package dev.lukebemish.ziggradle.toolchain.internal;

import dev.lukebemish.ziggradle.internal.PlatformUtils;
import dev.lukebemish.ziggradle.toolchain.ZigCompiler;
import dev.lukebemish.ziggradle.toolchain.ZigInstallationMetadata;
import dev.lukebemish.ziggradle.toolchain.ZigVersion;
import org.gradle.api.file.Directory;
import org.gradle.api.file.RegularFile;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;
import org.gradle.platform.OperatingSystem;

import javax.inject.Inject;

public class ZigToolchain {
    private final Directory directory;
    private final ResolvedZigToolchainInfo spec;

    @Inject
    public ZigToolchain(Directory directory, ResolvedZigToolchainInfo spec) {
        this.directory = directory;
        this.spec = spec;
    }

    @Nested
    protected ResolvedZigToolchainInfo getSpec() {
        return spec;
    }

    @Internal
    public Directory getInstallationPath() {
        return directory;
    }

    public abstract static class DefaultInstallationMetadata implements ZigInstallationMetadata {
        private final ZigVersion version;
        private final Directory installationPath;

        @Inject
        public DefaultInstallationMetadata(ZigToolchain toolchain) {
            this.version = toolchain.getSpec().zigVersion();
            this.installationPath = toolchain.getInstallationPath();
        }

        @Override
        public ZigVersion getVersion() {
            return version;
        }

        @Override
        public Directory getInstallationPath() {
            return installationPath;
        }
    }

    public abstract static class DefaultZigCompiler implements ZigCompiler {
        private final ZigInstallationMetadata metadata;

        @Inject
        public DefaultZigCompiler(ZigInstallationMetadata metadata) {
            this.metadata = metadata;
        }

        @Override
        public ZigInstallationMetadata getInstallationMetadata() {
            return metadata;
        }

        @SuppressWarnings("UnstableApiUsage")
        @Override
        public RegularFile getExecutablePath() {
            var os = PlatformUtils.getBuildPlatform().getOperatingSystem();
            if (os == OperatingSystem.WINDOWS) {
                return metadata.getInstallationPath().file("zig.exe");
            }
            return metadata.getInstallationPath().file("zig");
        }
    }
}
