package dev.lukebemish.ziggradle;

import dev.lukebemish.ziggradle.toolchain.ZigCompiler;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecOperations;
import org.gradle.process.ExecResult;
import org.gradle.process.ExecSpec;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class ZigTask extends DefaultTask {
    @Inject
    public ZigTask() {
        getOptions().getZigCache().convention(getProject().getLayout().getBuildDirectory().dir("zig-cache/"+getName()));
    }

    @Nested
    public abstract Property<ZigCompiler> getZigCompiler();

    @Inject
    protected abstract ExecOperations getExecOperations();

    @Nested
    public abstract ZigOptions getOptions();

    @TaskAction
    protected void run() throws IOException {
        executeZig(spec -> {
            var args = new ArrayList<String>();
            args.addAll(getCacheDirArgs());
            args.addAll(getOptions().getCompilerArgs().get());
            spec.setArgs(args);
        });
    }

    protected List<String> getCacheDirArgs() {
        final var options = getOptions();
        final var zigCache = options.getZigCache();
        final var globalZigCache = options.getGlobalZigCache();
        final var args = new ArrayList<String>();
        // Set up cache directories
        if (zigCache.isPresent()) {
            args.add("--cache-dir");
            args.add(zigCache.get().getAsFile().getAbsolutePath());
        }

        if (globalZigCache.isPresent()) {
            args.add("--global-cache-dir");
            args.add(globalZigCache.get().getAsFile().getAbsolutePath());
        }
        return args;
    }

    protected ExecResult executeZig(Action<ExecSpec> callback) {
        return getExecOperations().exec(spec -> {
            spec.setErrorOutput(System.err);
            spec.setStandardOutput(System.out);

            spec.setExecutable(getZigCompiler().get().getExecutablePath());

            callback.execute(spec);
        });
    }
}
