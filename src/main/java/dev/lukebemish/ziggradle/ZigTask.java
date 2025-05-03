package dev.lukebemish.ziggradle;

import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.util.ArrayList;

public abstract class ZigTask extends BaseZigTask<ZigOptions> {
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
            var args = new ArrayList<String>();
            args.addAll(resolveCompilerArgs());
            spec.setArgs(args);
        }).rethrowFailure().assertNormalExitValue();
    }
}
