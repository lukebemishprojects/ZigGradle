package dev.lukebemish.ziggradle.toolchain;

import org.gradle.api.file.Directory;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;

public interface ZigInstallationMetadata {
    @Input
    ZigVersion getVersion();

    @Internal
    Directory getInstallationPath();
}
