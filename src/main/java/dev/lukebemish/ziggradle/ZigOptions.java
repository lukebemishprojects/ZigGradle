package dev.lukebemish.ziggradle;

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

public abstract class ZigOptions {

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
    public ZigOptions() {
        var dirProperty = getObjectFactory().directoryProperty();
        dirProperty.set(getGradle().getGradleUserHomeDir().toPath().resolve("caches").resolve("dev.lukebemish.zig-gradle").resolve("zig-cache").toFile());

        getGlobalZigCache().convention(dirProperty);
    }
}
