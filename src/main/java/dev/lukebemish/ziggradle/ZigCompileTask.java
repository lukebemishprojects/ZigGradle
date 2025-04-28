package dev.lukebemish.ziggradle;

import dev.lukebemish.ziggradle.toolchain.ZigCompiler;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;

import javax.inject.Inject;

public abstract class ZigCompileTask extends DefaultTask {
    @Inject
    public ZigCompileTask() {}

    @Input
    public abstract Property<ZigCompiler> getZigCompiler();

    @InputFiles
    public abstract ConfigurableFileCollection getSourceFiles();
}
