package dev.lukebemish.ziggradle;

import org.apache.commons.io.FileUtils;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.util.ArrayList;

public abstract class ZigBuildTask extends BaseZigTask<ZigBuildOptions> {
    @Override
    @Nested
    public abstract ZigBuildOptions getOptions();

    @OutputDirectory
    public abstract DirectoryProperty getWorkingDirectory();

    @OutputDirectory
    public abstract DirectoryProperty getPrefixDirectory();

    /**
     * This exists so that gradle can detect changes. Should match the contents of build.zig.zon -> paths
     */
    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    public abstract ConfigurableFileCollection getSourceFiles();

    @InputFile
    @Optional
    @PathSensitive(PathSensitivity.RELATIVE)
    public abstract RegularFileProperty getBuildFile();

    @InputFile
    @Optional
    @PathSensitive(PathSensitivity.NONE)
    public abstract RegularFileProperty getLibcFile();

    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    public abstract ConfigurableFileCollection getHeaders();

    public ZigBuildTask() {
        super();
        getWorkingDirectory().convention(getProject().getLayout().getProjectDirectory());
    }

    @TaskAction
    protected void run() throws IOException {
        var dir = getPrefixDirectory().getAsFile().get();
        if (dir.exists()) {
            FileUtils.deleteDirectory(dir);
        }

        executeZig(spec -> {
            spec.workingDir(getWorkingDirectory().get());
            var args = new ArrayList<String>();

            args.add("build");

            args.addAll(getOptions().getSteps().get());

            args.addAll(getOptions().getCacheDirArgs());

            args.add("--prefix");
            args.add(getPrefixDirectory().get().getAsFile().getAbsolutePath());

            if (getBuildFile().isPresent()) {
                args.add("--build-file");
                args.add(getBuildFile().get().getAsFile().getAbsolutePath());
            }

            if (getLibcFile().isPresent()) {
                args.add("--libc");
                args.add(getLibcFile().get().getAsFile().getAbsolutePath());
            }

            getHeaders().forEach(headerDir -> {
                args.add("--search-prefix");
                args.add(headerDir.getAbsolutePath());
            });

            if (getOptions().getTarget().isPresent()) {
                args.add("-Dtarget=" + getOptions().getTarget().get().getName());
            }

            if (getOptions().getOptimize().isPresent()) {
                args.add("-Doptimize=" + getOptions().getOptimize().get().name());
            }

            args.addAll(getOptions().resolveCompilerArgs());

            spec.setArgs(args);
        }).rethrowFailure().assertNormalExitValue();
    }
}
