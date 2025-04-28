package dev.lukebemish.ziggradle.toolchain.internal;

import dev.lukebemish.ziggradle.toolchain.ZigToolchainProvider;
import org.gradle.api.provider.Provider;
import org.jspecify.annotations.Nullable;

import java.net.URI;

public record ZigToolchainProviderInfo(SerializedInfo info, Provider<? extends ZigToolchainProvider> provider) {
    public record SerializedInfo(String name, @Nullable URI rootUri) {}
}
