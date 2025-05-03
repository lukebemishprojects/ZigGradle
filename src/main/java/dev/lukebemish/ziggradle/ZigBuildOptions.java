package dev.lukebemish.ziggradle;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;

public abstract class ZigBuildOptions extends BaseZigOptions {
    @Input
    public abstract ListProperty<String> getSteps();

    @Input
    @Optional
    public abstract Property<ZigTargetTriple> getTarget();

    @Input
    @Optional
    public abstract Property<Optimization> getOptimize();

    public ZigBuildOptions() {
        super();
    }


    public enum Optimization {
        Debug,
        ReleaseSafe,
        ReleaseFast,
        ReleaseSmall;


        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
}
