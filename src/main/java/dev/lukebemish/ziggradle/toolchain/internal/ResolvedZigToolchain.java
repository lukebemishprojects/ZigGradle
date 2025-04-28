package dev.lukebemish.ziggradle.toolchain.internal;

import org.gradle.api.provider.Provider;

public record ResolvedZigToolchain(ResolvedZigToolchainInfo info, Provider<ZigToolchain> toolchain) {
}
