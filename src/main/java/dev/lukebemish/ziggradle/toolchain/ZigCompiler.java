package dev.lukebemish.ziggradle.toolchain;

import org.gradle.api.file.RegularFile;
import org.gradle.api.tasks.Internal;

public interface ZigCompiler {
    @Internal
    RegularFile getExecutablePath();
}
