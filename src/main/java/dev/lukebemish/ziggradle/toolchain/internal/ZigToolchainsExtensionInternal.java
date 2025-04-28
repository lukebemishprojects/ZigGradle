package dev.lukebemish.ziggradle.toolchain.internal;

import dev.lukebemish.ziggradle.toolchain.ZigCompiler;
import dev.lukebemish.ziggradle.toolchain.ZigToolchainProvider;
import dev.lukebemish.ziggradle.toolchain.ZigToolchainRequest;
import dev.lukebemish.ziggradle.toolchain.ZigToolchainSpec;
import dev.lukebemish.ziggradle.toolchain.ZigToolchainsExtension;
import org.apache.commons.lang3.SystemUtils;
import org.gradle.api.Project;
import org.gradle.api.Transformer;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.platform.Architecture;
import org.gradle.platform.BuildPlatform;
import org.gradle.platform.BuildPlatformFactory;
import org.gradle.platform.OperatingSystem;

import javax.inject.Inject;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ZigToolchainsExtensionInternal extends ZigToolchainsExtension {
    public static final String ZIG_TOOLCHAIN_PROVIDER_SERVICE_PREFIX = "dev.lukebemish.ziggradle.internal.toolchain.providers.";
    public static final String ZIG_TOOLCHAIN_PROVIDER_PROXY_EXTENSION = "dev.lukebemish.ziggradle.internal.toolchain.providersExtension";

    @SuppressWarnings("unchecked")
    @Inject
    public ZigToolchainsExtensionInternal() {
        var providerIntoList = getProject().getExtensions().findByName(ZIG_TOOLCHAIN_PROVIDER_PROXY_EXTENSION);
        if (providerIntoList != null) {
            setProviders((List<ZigToolchainProviderInfo.SerializedInfo>) providerIntoList);
        }
    }

    @Inject
    protected abstract Project getProject();

    protected abstract ListProperty<ZigToolchainProviderInfo> getToolchainProviderInfo();

    public void setProviders(List<ZigToolchainProviderInfo.SerializedInfo> providerServices) {
        for (var providerInfo : providerServices) {
            var providerService = getProject().getGradle().getSharedServices().getRegistrations().findByName(ZIG_TOOLCHAIN_PROVIDER_SERVICE_PREFIX + providerInfo.name());
            getToolchainProviderInfo().add(new ZigToolchainProviderInfo(providerInfo, providerService.getService().map(s -> (ZigToolchainProvider) s)));
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    protected BuildPlatform getBuildPlatform() {
        OperatingSystem os;
        if (SystemUtils.IS_OS_WINDOWS) {
            os = OperatingSystem.WINDOWS;
        } else if (SystemUtils.IS_OS_MAC) {
            os = OperatingSystem.MAC_OS;
        } else if (SystemUtils.IS_OS_SOLARIS) {
            os = OperatingSystem.SOLARIS;
        } else if (SystemUtils.IS_OS_FREE_BSD) {
            os = OperatingSystem.FREE_BSD;
        } else if (SystemUtils.IS_OS_LINUX) {
            os = OperatingSystem.LINUX;
        } else if (SystemUtils.IS_OS_UNIX) {
            os = OperatingSystem.UNIX;
        } else {
            throw new IllegalStateException("Unsupported OS: " + SystemUtils.OS_NAME);
        }

        Architecture arch = null;
        String osArch = System.getProperty("os.arch");
        boolean is64Bit = osArch.contains("64") || osArch.startsWith("armv8");
        if (osArch.startsWith("aarch") || osArch.startsWith("arm")) {
            if (is64Bit) {
                arch = Architecture.AARCH64;
            }
        } else if (!osArch.startsWith("ppc") && !osArch.startsWith("riscv")) {
            arch = is64Bit ? Architecture.X86_64 : Architecture.X86;
        }
        if (arch == null) {
            throw new IllegalStateException("Unsupported architecture: " + osArch);
        }

        return BuildPlatformFactory.of(arch, os);
    }

    private final Map<ResolvedZigInfo, Provider<ZigToolchain>> toolchains = new HashMap<>();

    private Provider<ZigToolchain> toolchainFor(ZigToolchainSpec spec) {
        // TODO: support local toolchains?

        var resolvedSpec = new ResolvedZigInfo(spec.getVersion().get());

        if (toolchains.containsKey(resolvedSpec)) {
            return toolchains.get(resolvedSpec);
        }

        for (var providerInfo : getToolchainProviderInfo().get()) {
            var name = providerInfo.info().name();
            var provider = providerInfo.provider().get();

            var result = provider.resolve(new ZigToolchainRequest() {
                @Override
                public ZigToolchainSpec getJavaToolchainSpec() {
                    return spec;
                }

                @Override
                public BuildPlatform getBuildPlatform() {
                    return ZigToolchainsExtensionInternal.this.getBuildPlatform();
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

                var dep = (ModuleDependency) getProject().getDependencies().create(
                        ZIG_TOOLCHAIN_PROVIDER_SERVICE_PREFIX + name + ":zig:" + rest
                );

                // Is there a better option than a detached config here? Or is this fine?
                var config = getProject().getConfigurations().detachedConfiguration(dep);

                var toolchainDirectory = config.getIncoming().artifactView(artifactView -> {
                    artifactView.attributes(attributes ->
                            attributes.attribute(ZigToolchainComponentRule.ZIG_TOOLCHAIN_BUNDLING_ATTRIBUTE, false)
                    );
                }).getArtifacts().getResolvedArtifacts().map(set -> {
                    if (set.size() != 1) {
                        throw new IllegalStateException("Expected exactly one artifact, but got " + set.size());
                    }
                    var artifact = set.iterator().next();
                    return artifact.getFile();
                });

                var toolchainProvider = toolchainDirectory.map(getObjectFactory().newInstance(ToolchainCreatingTransformer.class, resolvedSpec));
                toolchains.put(resolvedSpec, toolchainProvider);
                return toolchainProvider;
            }
        }

        throw new IllegalStateException("No toolchain provider found for " + resolvedSpec);
    }

    public static abstract class ToolchainCreatingTransformer implements Transformer<ZigToolchain, File> {
        private final ResolvedZigInfo spec;

        @Inject
        public ToolchainCreatingTransformer(ResolvedZigInfo spec) {
            this.spec = spec;
        }

        @Inject
        protected abstract ObjectFactory getObjectFactory();

        @Override
        public ZigToolchain transform(File file) {
            var directoryProperty = getObjectFactory().directoryProperty();
            var filesInDirectory = file.listFiles();
            if (filesInDirectory == null || filesInDirectory.length == 0) {
                throw new IllegalStateException("No directories found in toolchain directory " + file);
            }
            // If there's one file, we go down one level. Otherwise, zig is here
            if (filesInDirectory.length > 1) {
                directoryProperty.set(file);
            } else {
                directoryProperty.set(filesInDirectory[0]);
            }
            return getObjectFactory().newInstance(ZigToolchain.class, directoryProperty.get(), spec);
        }
    }

    public static abstract class CompilerCreatingTransformer implements Transformer<ZigCompiler, ZigToolchain> {
        @Inject
        protected abstract ObjectFactory getObjectFactory();

        @Inject
        public CompilerCreatingTransformer() {}

        @Override
        public ZigCompiler transform(ZigToolchain zigToolchain) {
            return getObjectFactory().newInstance(ZigToolchain.DefaultZigCompiler.class, zigToolchain);
        }
    }

    public Provider<ZigCompiler> compilerFor(ZigToolchainSpec spec) {
        return toolchainFor(spec).map(getObjectFactory().newInstance(CompilerCreatingTransformer.class));
    }
}
