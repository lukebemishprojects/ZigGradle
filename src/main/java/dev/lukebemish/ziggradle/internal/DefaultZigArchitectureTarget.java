package dev.lukebemish.ziggradle.internal;

import dev.lukebemish.ziggradle.ZigArchitectureTarget;

import java.util.Objects;

public class DefaultZigArchitectureTarget implements ZigArchitectureTarget {
    private final String name;

    public DefaultZigArchitectureTarget(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ZigArchitectureTarget that)) return false;
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
