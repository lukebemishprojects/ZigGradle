package dev.lukebemish.ziggradle;

import dev.lukebemish.ziggradle.internal.PlatformUtils;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;

import javax.inject.Inject;

public abstract class ZigCompileOptions {
    @Input
    public abstract ListProperty<String> getCompilerArgs();

    @Input
    public abstract Property<ZigArchitectureTarget> getTargetArchitecture();

    @Input
    public abstract Property<ZigOperatingSystemTarget> getTargetOperatingSystem();

    @Input
    public abstract Property<ZigArtifactType> getArtifactType();

    @Internal
    public abstract DirectoryProperty getZigCache();

    @Internal
    public abstract DirectoryProperty getGlobalZigCache();

    @Input
    @Optional
    public abstract Property<Boolean> getDynamic();

    @Inject
    protected abstract Gradle getGradle();

    @Inject
    protected abstract ObjectFactory getObjectFactory();

    @Inject
    public ZigCompileOptions() {
        getTargetArchitecture().convention(PlatformUtils.getCurrentArchitecture());
        getTargetOperatingSystem().convention(PlatformUtils.getCurrentOperatingSystem());

        var dirProperty = getObjectFactory().directoryProperty();
        dirProperty.set(getGradle().getGradleUserHomeDir().toPath().resolve("caches").resolve("dev.lukebemish.ziggradle").resolve("zig-cache").toFile());

        getGlobalZigCache().convention(dirProperty);
    }
}
