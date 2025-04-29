package dev.lukebemish.ziggradle.toolchain;

import org.gradle.api.file.RegularFile;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;

public interface ZigCompiler {
    @Nested
    ZigInstallationMetadata getInstallationMetadata();

    @Internal
    RegularFile getExecutablePath();
}
