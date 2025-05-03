package dev.lukebemish.ziggradle;

import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;

import javax.inject.Inject;

public abstract class ZigCompileOptions extends BaseZigOptions {
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
