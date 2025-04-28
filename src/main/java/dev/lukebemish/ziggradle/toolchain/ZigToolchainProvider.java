package dev.lukebemish.ziggradle.toolchain;

import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;

import java.util.Optional;

public interface ZigToolchainProvider extends BuildService<BuildServiceParameters.None> {
    Optional<ZigToolchainDownload> resolve(ZigToolchainRequest request);
}
