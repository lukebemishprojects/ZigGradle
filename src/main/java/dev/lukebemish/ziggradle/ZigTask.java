package dev.lukebemish.ziggradle;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;

public abstract class ZigTask extends BaseZigTask<ZigOptions> {
    @OutputDirectory
    @Optional
    public abstract DirectoryProperty getWorkingDirectory();

    @Inject
    public ZigTask() {
        super();
    }

    @Override
    @Nested
    public abstract ZigOptions getOptions();

    @TaskAction
    protected void run() {
        executeZig(spec -> {
            if (getWorkingDirectory().isPresent()) {
                spec.workingDir(getWorkingDirectory().get());
            }
            spec.setArgs(getOptions().resolveCompilerArgs());
        }).rethrowFailure().assertNormalExitValue();
    }
}
