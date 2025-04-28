package dev.lukebemish.ziggradle.toolchain.internal;

import dev.lukebemish.ziggradle.internal.PlatformUtils;
import dev.lukebemish.ziggradle.toolchain.ZigCompiler;
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

    public abstract static class DefaultZigCompiler implements ZigCompiler {
        private final ZigToolchain toolchain;

        @Inject
        public DefaultZigCompiler(ZigToolchain toolchain) {
            this.toolchain = toolchain;
        }

        @Internal
        public ZigToolchain getToolchain() {
            return toolchain;
        }

        @SuppressWarnings("UnstableApiUsage")
        @Override
        public RegularFile getExecutablePath() {
            var os = PlatformUtils.getBuildPlatform().getOperatingSystem();
            if (os == OperatingSystem.WINDOWS) {
                return toolchain.directory.file("zig.exe");
            }
            return toolchain.directory.file("zig");
        }
    }
}
