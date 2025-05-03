package dev.lukebemish.ziggradle;

import dev.lukebemish.ziggradle.toolchain.ZigCompiler;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;
import org.gradle.process.ExecOperations;
import org.gradle.process.ExecResult;
import org.gradle.process.ExecSpec;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseZigTask<T extends ZigOptions> extends DefaultTask {
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

    public void addCacheDirArgs() {
        getOptions().getCompilerArgs().add(CacheDirMarker.INSTANCE);
    }

    @Internal
    protected List<String> resolveCompilerArgs() {
        var result = new ArrayList<String>();
        var theArgs = getOptions().getCompilerArgs().get();
        for (var arg: theArgs) {
            if (arg instanceof CacheDirMarker) {
                result.addAll(getCacheDirArgs());
            } else {
                result.add(arg.toString());
            }
        }
        return result;
    }

    @Internal
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

            if (getOptions().getReplaceEnv().isPresent() && getOptions().getReplaceEnv().get()) {
                spec.setEnvironment(getOptions().getEnv().get());
            } else {
                spec.environment(getOptions().getEnv().get());
            }

            callback.execute(spec);
        });
    }

    private static class CacheDirMarker implements Serializable {
        public static final CacheDirMarker INSTANCE = new CacheDirMarker();
        private CacheDirMarker() {}
    }
}
