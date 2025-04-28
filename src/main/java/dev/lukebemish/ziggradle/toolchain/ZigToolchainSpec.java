package dev.lukebemish.ziggradle.toolchain;

import org.gradle.api.provider.Property;

public abstract class ZigToolchainSpec {
    public abstract Property<ZigVersion> getVersion();
}
