package dev.lukebemish.ziggradle.toolchain.internal;

import dev.lukebemish.ziggradle.toolchain.ZigCompiler;
import org.gradle.api.file.Directory;
import org.gradle.api.file.RegularFile;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;

import javax.inject.Inject;

public class ZigToolchain {
    private final Directory directory;
    private final ResolvedZigInfo spec;

    @Inject
    public ZigToolchain(Directory directory, ResolvedZigInfo spec) {
        this.directory = directory;
        this.spec = spec;
    }

    @Nested
    protected ResolvedZigInfo getSpec() {
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

        @Override
        public RegularFile getExecutablePath() {
            return toolchain.directory.file("zig");
        }
    }
}
