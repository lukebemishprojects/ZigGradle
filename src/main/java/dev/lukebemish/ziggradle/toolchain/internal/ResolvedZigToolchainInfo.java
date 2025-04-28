package dev.lukebemish.ziggradle.toolchain.internal;

import dev.lukebemish.ziggradle.toolchain.ZigToolchainSpec;
import dev.lukebemish.ziggradle.toolchain.ZigVersion;
import org.gradle.platform.BuildPlatform;

public record ResolvedZigToolchainInfo(ZigVersion zigVersion, BuildPlatform buildPlatform) {
    public boolean matches(ZigToolchainSpec spec) {
        if (spec.getVersion().isPresent()) {
            if (!spec.getVersion().get().equals(zigVersion)) {
                return false;
            }
        }
        return true;
    }
}
