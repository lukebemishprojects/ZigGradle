package dev.lukebemish.ziggradle;

import dev.lukebemish.ziggradle.toolchain.ZigCompiler;
import org.apache.commons.io.FileUtils;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileCollection;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecOperations;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;

public abstract class ZigCompileTask extends DefaultTask {
    @Inject
    public ZigCompileTask() {
        getOptions().getZigCache().convention(getProject().getLayout().getBuildDirectory().dir("zig-cache/"+getName()));
    }

    @Nested
    public abstract Property<ZigCompiler> getZigCompiler();

    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    @SkipWhenEmpty
    public abstract ConfigurableFileCollection getSourceFiles();

    @InputFiles
    @PathSensitive(PathSensitivity.NAME_ONLY)
    public abstract ConfigurableFileCollection getHeaders();

    @PathSensitive(PathSensitivity.NAME_ONLY)
    @InputFiles
    protected FileCollection getHeaderFiles() {
        return getHeaders().getAsFileTree();
    }

    @Nested
    public abstract ZigCompileOptions getOptions();

    @Input
    public abstract Property<String> getBaseArtifactName();

    public void options(Action<? super ZigCompileOptions> action) {
        action.execute(getOptions());
    }

    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory();

    @Inject
    protected abstract ExecOperations getExecOperations();

    @TaskAction
    protected void run() throws IOException {
        var dir = getOutputDirectory().getAsFile().get();
        if (dir.exists()) {
            FileUtils.deleteDirectory(dir);
        }
        dir.mkdirs();

        getExecOperations().exec(spec -> {
            spec.workingDir(dir);
            spec.setErrorOutput(System.err);
            spec.setStandardOutput(System.out);

            spec.setExecutable(getZigCompiler().get().getExecutablePath());
            var args = new ArrayList<String>();
            args.add(switch (getOptions().getArtifactType().get()) {
                case LIBRARY -> "build-lib";
                case EXECUTABLE -> "build-exe";
                case OBJECT -> "build-obj";
            });

            // Set up cache directories

            if (getOptions().getZigCache().isPresent()) {
                args.add("--cache-dir");
                args.add(getOptions().getZigCache().get().getAsFile().getAbsolutePath());
            }

            if (getOptions().getGlobalZigCache().isPresent()) {
                args.add("--global-cache-dir");
                args.add(getOptions().getGlobalZigCache().get().getAsFile().getAbsolutePath());
            }

            args.add("--name");
            args.add(getBaseArtifactName().get());

            // Module settings

            if (getOptions().getDynamic().isPresent()) {
                if (getOptions().getDynamic().get()) {
                    args.add("-dynamic");
                } else {
                    args.add("-static");
                }
            }

            var target = getOptions().getTargetArchitecture().get() + "-"
                    + getOptions().getTargetOperatingSystem().get();
            // TODO: ABI

            args.add("-target");
            args.add(target);

            args.addAll(getOptions().getCompilerArgs().get());

            for (var headerDir : getHeaders().getFiles()) {
                args.add("-I"+headerDir.getAbsolutePath());
            }

            for (var sourceFile : getSourceFiles().getFiles()) {
                args.add(sourceFile.getAbsolutePath());
            }

            spec.setArgs(args);
        }).rethrowFailure().assertNormalExitValue();
    }
}
