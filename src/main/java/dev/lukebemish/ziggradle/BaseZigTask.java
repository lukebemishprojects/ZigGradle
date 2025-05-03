package dev.lukebemish.ziggradle;

import dev.lukebemish.ziggradle.toolchain.ZigCompiler;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;
import org.gradle.process.ExecOperations;
import org.gradle.process.ExecResult;
import org.gradle.process.ExecSpec;

import javax.inject.Inject;

public abstract class BaseZigTask<T extends BaseZigOptions> extends DefaultTask {
    @Inject
    public BaseZigTask() {
        getOptions().getZigCache().convention(getProject().getLayout().getBuildDirectory().dir("zig-cache/"+getName()));
    }

    @Nested
    public abstract Property<ZigCompiler> getZigCompiler();

    @Inject
    protected abstract ExecOperations getExecOperations();

    protected abstract T getOptions();

    public void options(Action<? super T> action) {
        action.execute(getOptions());
    }

    protected ExecResult executeZig(Action<ExecSpec> callback) {
        return getExecOperations().exec(spec -> {
            spec.setErrorOutput(System.err);
            spec.setStandardOutput(System.out);

            spec.setExecutable(getZigCompiler().get().getExecutablePath());

            if (getOptions().getReplaceEnv().isPresent() && getOptions().getReplaceEnv().get()) {
                spec.setEnvironment(getOptions().getEnv().get());
            } else {
                spec.environment(getOptions().getEnv().get());
            }

            callback.execute(spec);
        });
    }
}
