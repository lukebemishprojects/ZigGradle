package dev.lukebemish.ziggradle.toolchain;

import org.gradle.platform.BuildPlatform;

public interface ZigToolchainRequest {
    ZigToolchainSpec getJavaToolchainSpec();

    BuildPlatform getBuildPlatform();
}
