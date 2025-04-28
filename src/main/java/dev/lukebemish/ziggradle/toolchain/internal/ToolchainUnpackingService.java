package dev.lukebemish.ziggradle.toolchain.internal;

import dev.lukebemish.ziggradle.internal.PlatformUtils;
import dev.lukebemish.ziggradle.toolchain.ZigToolchainRequest;
import dev.lukebemish.ziggradle.toolchain.ZigToolchainSpec;
import dev.lukebemish.ziggradle.toolchain.ZigVersion;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.gradle.api.Project;
import org.gradle.api.Transformer;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;
import org.gradle.api.services.ServiceReference;
import org.gradle.platform.Architecture;
import org.gradle.platform.BuildPlatform;
import org.gradle.platform.BuildPlatformFactory;
import org.gradle.platform.OperatingSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ToolchainUnpackingService implements BuildService<ToolchainUnpackingService.Parameters> {
    public static final String TOOLCHAIN_UNPACKING_SERVICE_NAME = "dev.lukebemish.ziggradle.internal.toolchain.unpackingService";
    private static final Logger LOGGER = LoggerFactory.getLogger(ToolchainUnpackingService.class);

    public static final String ZIG_TOOLCHAIN_PROVIDER_SERVICE_PREFIX = "dev.lukebemish.ziggradle.internal.toolchain.providers.";

    public interface Parameters extends BuildServiceParameters {
        DirectoryProperty getGradleUserHome();
    }

    @Inject
    public ToolchainUnpackingService() {}

    @Inject
    protected abstract ObjectFactory getObjectFactory();

    private final List<ResolvedZigToolchain> toolchains = new ArrayList<>();
    private final AtomicBoolean discovered = new AtomicBoolean(false);

    private void discoverToolchains() {
        // TODO: support local toolchains?
        if (!discovered.compareAndExchange(false, true)) {
            // We search the relevant directory for toolchain properties files
            var cacheDir = getParameters().getGradleUserHome().getAsFile().get().toPath().resolve("caches")
                .resolve("dev.lukebemish.ziggradle").resolve("toolchains.1");
            if (cacheDir.toFile().exists()) {
                try (var files = Files.list(cacheDir)) {
                    files.filter(f -> f.getFileName().toString().endsWith(".properties")).forEach(f -> {
                        var name = f.getFileName().toString().substring(0, f.getFileName().toString().length() - ".properties".length());
                        var properties = new Properties();
                        try (var reader = Files.newBufferedReader(f)) {
                            properties.load(reader);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                        var version = properties.getProperty("version", null);
                        var os = properties.getProperty("os", null);
                        var arch = properties.getProperty("arch", null);
                        if (version != null && os != null && arch != null) {
                            var unpackDir = cacheDir.resolve(name);
                            if (Files.exists(unpackDir)) {
                                var toolchainDirectory = this.determineStructure(unpackDir);
                                var fileProperty = getObjectFactory().fileProperty();
                                fileProperty.set(toolchainDirectory);
                                var info = new ResolvedZigToolchainInfo(
                                    ZigVersion.of(version),
                                    BuildPlatformFactory.of(Architecture.valueOf(arch.toUpperCase(Locale.ROOT)), OperatingSystem.valueOf(os.toUpperCase(Locale.ROOT)))
                                );
                                var toolchain = fileProperty.getAsFile().map(getObjectFactory().newInstance(ToolchainCreatingTransformer.class, info));
                                var resolvedToolchain = new ResolvedZigToolchain(info, toolchain);
                                toolchains.add(resolvedToolchain);
                            }
                        }
                    });
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }
    }

    public static abstract class ToolchainCreatingTransformer implements Transformer<ZigToolchain, File> {
        private final ResolvedZigToolchainInfo spec;

        @Inject
        public ToolchainCreatingTransformer(ResolvedZigToolchainInfo spec) {
            this.spec = spec;
        }

        @Inject
        protected abstract ObjectFactory getObjectFactory();

        @Override
        public ZigToolchain transform(File file) {
            var directoryProperty = getObjectFactory().directoryProperty();
            directoryProperty.set(file);
            return getObjectFactory().newInstance(ZigToolchain.class, directoryProperty.get(), spec);
        }
    }

    public Provider<ZigToolchain> forInfo(ResolvedZigToolchainInfo info, Project project) {
        // TODO: this still fails conf cache on the first time downloading a toolchain -- this may be unavoidable without bypassing gradle's dependency downloading
        var property = getObjectFactory().property(ResolvedZigToolchainInfo.class);
        property.set(info);
        return property.map(project.getObjects().newInstance(ServiceTransformer.class));
    }

    public Provider<ZigToolchain> toolchainFor(ZigToolchainSpec spec, Project project, Provider<List<ZigToolchainProviderInfo>>  toolchainProviderInfo) {
        discoverToolchains();

        for (var existing : toolchains) {
            if (existing.info().matches(spec) && existing.info().buildPlatform().equals(PlatformUtils.getBuildPlatform())) {
                return forInfo(existing.info(), project);
            }
        }

        for (var providerInfo : toolchainProviderInfo.get()) {
            var name = providerInfo.info().name();
            var provider = providerInfo.provider().get();

            var result = provider.resolve(new ZigToolchainRequest() {
                @Override
                public ZigToolchainSpec getJavaToolchainSpec() {
                    return spec;
                }

                @Override
                public BuildPlatform getBuildPlatform() {
                    return PlatformUtils.getBuildPlatform();
                }
            });

            if (result.isPresent()) {
                var toolchainDownload = result.get();

                var uri = toolchainDownload.getUri().toString();
                var rootUri = providerInfo.info().rootUri().toString();
                if (!uri.startsWith(rootUri)) {
                    throw new IllegalStateException("Toolchain URI " + uri + " does not start with root URI " + rootUri);
                }
                var rest = uri.substring(rootUri.length());
                if (rest.startsWith("/")) {
                    rest = rest.substring(1);
                }

                var dep = (ModuleDependency) project.getDependencies().create(
                    ZIG_TOOLCHAIN_PROVIDER_SERVICE_PREFIX + name + ":zig:" + rest
                );

                var resolvedSpec = new ResolvedZigToolchainInfo(
                    toolchainDownload.getVersion(),
                    PlatformUtils.getBuildPlatform()
                );

                // Is there a better option than a detached config here? Or is this fine?
                var config = project.getConfigurations().detachedConfiguration(dep);

                var toolchainDirectory = config.getIncoming().artifactView(artifactView -> {}).getArtifacts().getResolvedArtifacts().map(set -> {
                    if (set.size() != 1) {
                        throw new IllegalStateException("Expected exactly one artifact, but got " + set.size());
                    }
                    var artifact = set.iterator().next();
                    return artifact.getFile();
                }).map(project.getObjects().newInstance(ToolchainUnpackTransform.class, resolvedSpec));

                var toolchainProvider = toolchainDirectory.map(getObjectFactory().newInstance(ToolchainCreatingTransformer.class, resolvedSpec));
                toolchains.add(new ResolvedZigToolchain(resolvedSpec, toolchainProvider));
                return forInfo(resolvedSpec, project);
            }
        }

        throw new IllegalStateException("No toolchain provider found for " + spec);
    }

    public static abstract class ServiceTransformer implements Transformer<ZigToolchain, ResolvedZigToolchainInfo> {
        @ServiceReference(TOOLCHAIN_UNPACKING_SERVICE_NAME)
        protected abstract Property<ToolchainUnpackingService> getService();

        @Override
        public ZigToolchain transform(ResolvedZigToolchainInfo info) {
            return getService().get().existingToolchain(info);
        }
    }

    public ZigToolchain existingToolchain(ResolvedZigToolchainInfo info) {
        discoverToolchains();
        for (var existing : toolchains) {
            if (existing.info().equals(info)) {
                return existing.toolchain().get();
            }
        }
        throw new IllegalArgumentException("No toolchain matching "+info);
    }

    File unpack(File input, ResolvedZigToolchainInfo info) {
        var version = info.zigVersion();
        var arch = info.buildPlatform().getArchitecture().name().toLowerCase(Locale.ROOT);
        var os = info.buildPlatform().getOperatingSystem().name().toLowerCase(Locale.ROOT);

        var key = String.format(
            "%s-%s-%s",
            version,
            arch,
            os
        );
        var cacheDir = getParameters().getGradleUserHome().get().getAsFile().toPath().resolve("caches")
            .resolve("dev.lukebemish.ziggradle").resolve("toolchains.1");
        var outputDir = cacheDir.resolve(key);
        var lockFile = cacheDir.resolve(key + ".lock");
        var existsFile = cacheDir.resolve(key + ".properties");
        try (var ignored = lock(lockFile)) {
            if (Files.exists(existsFile)) {
                var properties = new Properties();
                try (var reader = Files.newBufferedReader(existsFile)) {
                    properties.load(reader);
                }
                if (properties.getProperty("version").equals(version.toString())
                    && properties.getProperty("os").equals(os)
                    && properties.getProperty("arch").equals(arch)
                ) {
                    return determineStructure(outputDir);
                }
            }

            if (Files.exists(outputDir)) {
                FileUtils.deleteDirectory(outputDir.toFile());
            }
            decompress(input, outputDir.toFile());
            var properties = new Properties();
            properties.put("version", info.zigVersion().toString());
            properties.put("os", os);
            properties.put("arch", arch);
            try (var writer = Files.newBufferedWriter(existsFile)) {
                properties.store(writer, "Zig toolchain properties");
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            return determineStructure(outputDir);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void decompress(File input, File outputDir) {
        try (
            var fis = new FileInputStream(input);
            var bis = new BufferedInputStream(fis);
            XZCompressorInputStream xzis = new XZCompressorInputStream(bis);
            TarArchiveInputStream tais = new TarArchiveInputStream(xzis)
        ) {
            TarArchiveEntry entry;
            while ((entry = tais.getNextEntry()) != null) {
                Path outputPath = outputDir.toPath().resolve(entry.getName()).normalize();
                if (!outputPath.startsWith(outputDir.toPath())) {
                    throw new IOException("Entry is outside of the target directory: "
                        + entry.getName());
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(outputPath);
                } else {
                    Files.createDirectories(outputPath.getParent());
                    Files.copy(tais, outputPath);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File determineStructure(Path outputDir) {
        var filesInDirectory = outputDir.toFile().listFiles();
        if (filesInDirectory == null || filesInDirectory.length == 0) {
            throw new IllegalStateException("No directories found in toolchain directory " + outputDir);
        }

        // If there's one file, we go down one level. Otherwise, zig is here
        if (filesInDirectory.length > 1) {
            return outputDir.toFile();
        } else {
            return filesInDirectory[0];
        }
    }

    private Lock lock(Path lockFile) {
        try {
            Files.createDirectories(lockFile.getParent());
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to create cache directory " + lockFile, e);
        }
        LOGGER.debug("Acquiring lock at {}", lockFile);

        // Try 5 times to get a file channel -- this doesn't block anything yet
        FileChannel channel = null;
        IOException last = null;
        for (int attempt = 0; attempt < 5; attempt++) {
            try {
                channel = FileChannel.open(lockFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                break;
            } catch (AccessDeniedException e) {
                last = e;
                try {
                    // Wait one second, try again
                    Thread.sleep(1000L);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            } catch (IOException e) {
                last = e;
                break;
            }
        }
        if (channel == null) {
            throw new UncheckedIOException("Failed to create lock-file " + lockFile, last);
        }

        // Now we try to get a lock on the file which will block other precesses
        FileLock fileLock;
        long startTime = System.currentTimeMillis();
        while (true) {
            try {
                fileLock = channel.tryLock();
                if (fileLock != null) {
                    break;
                }
            } catch (OverlappingFileLockException ignored) {
                // The lock is held by this process already, in another thread
            } catch (IOException e) {
                try {
                    channel.close();
                } catch (IOException ignored) {
                }
            }

            try {
                if (System.currentTimeMillis() - startTime > 1000 * 60 * 5) {
                    // If we've waited more than two minutes, fail
                    throw new RuntimeException("Failed to acquire lock on " + lockFile + "; timed out after 5 minutes");
                }
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }

        LOGGER.debug("Acquired lock at {}", lockFile);

        return new Lock(fileLock, lockFile);
    }

    private static final class Lock implements AutoCloseable {
        private final FileLock fileLock;
        private final Path lockFile;

        private Lock(FileLock fileLock, Path lockFile) {
            this.fileLock = fileLock;
            this.lockFile = lockFile;
        }

        @Override
        public void close() {
            LOGGER.debug("Releasing lock on {}", lockFile);
            try {
                fileLock.release();
            } catch (IOException e) {
                LOGGER.error("Failed to release lock on {}", fileLock.channel().toString(), e);
            }
            try {
                fileLock.channel().close();
            } catch (IOException ignored) {
            }
        }
    }
}
