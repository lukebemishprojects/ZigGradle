package dev.lukebemish.ziggradle.internal;

import dev.lukebemish.ziggradle.ZigOperatingSystemTarget;

import java.util.Objects;

public class DefaultZigOperatingSystemTarget implements ZigOperatingSystemTarget {
    private final String name;

    public DefaultZigOperatingSystemTarget(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ZigOperatingSystemTarget that)) return false;
        return Objects.equals(name, that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public String toString() {
        return getName();
    }
}
