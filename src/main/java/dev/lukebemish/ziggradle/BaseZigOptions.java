package dev.lukebemish.ziggradle;

import org.gradle.api.Named;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseZigOptions {

    @Input
    public abstract MapProperty<String, String> getEnv();

    @Input
    @Optional
    public abstract Property<Boolean> getReplaceEnv();

    @Input
    public abstract ListProperty<Object> getCompilerArgs();

    @Internal
    public abstract DirectoryProperty getZigCache();

    @Internal
    public abstract DirectoryProperty getGlobalZigCache();

    @Inject
    protected abstract Gradle getGradle();

    @Inject
    protected abstract ObjectFactory getObjectFactory();

    @Inject
    public BaseZigOptions() {
        var dirProperty = getObjectFactory().directoryProperty();
        dirProperty.set(getGradle().getGradleUserHomeDir().toPath().resolve("caches").resolve("dev.lukebemish.zig-gradle").resolve("zig-cache").toFile());

        getGlobalZigCache().convention(dirProperty);
    }

    @Internal
    public List<String> resolveCompilerArgs() {
        var result = new ArrayList<String>();
        var theArgs = getCompilerArgs().get();
        for (var arg: theArgs) {
            resolveCompilerArg(arg, result);
        }
        return result;
    }

    protected void resolveCompilerArg(Object arg, List<String> result) {
        if (arg instanceof String str) {
            result.add(str);
        } else if (arg instanceof Named named){
            result.add(named.getName());
        } else {
            result.add(arg.toString());
        }
    }

    @Internal
    public List<String> getCacheDirArgs() {
        final var zigCache = getZigCache();
        final var globalZigCache = getGlobalZigCache();
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
}
