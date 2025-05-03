package dev.lukebemish.ziggradle;

import org.apache.commons.io.FileUtils;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileCollection;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;

@CacheableTask
public abstract class ZigCompileTask extends BaseZigTask<ZigCompileOptions> {
    @Inject
    public ZigCompileTask() {
        super();
    }

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

    @Override
    @Nested
    public abstract ZigCompileOptions getOptions();

    @Input
    public abstract Property<String> getBaseArtifactName();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory();

    @TaskAction
    protected void run() throws IOException {
        var dir = getOutputDirectory().getAsFile().get();
        if (dir.exists()) {
            FileUtils.deleteDirectory(dir);
        }
        dir.mkdirs();

        executeZig(spec -> {
            spec.workingDir(getOutputDirectory().get());
            var args = new ArrayList<String>();
            args.add(switch (getOptions().getArtifactType().get()) {
                case LIBRARY -> "build-lib";
                case EXECUTABLE -> "build-exe";
                case OBJECT -> "build-obj";
            });

            args.addAll(getOptions().getCacheDirArgs());

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

            // If not set, use building system's target (zig automatically detects this, no need to go through BuildPlatform)
            if (getOptions().getTargetTriple().isPresent()) {
                var target = getOptions().getTargetTriple().get();
                args.add("-target");
                args.add(target.getName());
            }

            args.addAll(getOptions().resolveCompilerArgs());

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
