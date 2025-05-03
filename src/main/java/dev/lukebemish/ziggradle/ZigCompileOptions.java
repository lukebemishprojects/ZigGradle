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

public abstract class ZigCompileOptions extends ZigOptions {
    @Input
    @Optional
    public abstract Property<ZigTargetTriple> getTargetTriple();

    @Input
    public abstract Property<ZigArtifactType> getArtifactType();

    @Input
    @Optional
    public abstract Property<Boolean> getDynamic();

    @Inject
    public ZigCompileOptions() {
        super();
    }
}
