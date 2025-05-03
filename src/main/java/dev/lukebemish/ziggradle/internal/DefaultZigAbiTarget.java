package dev.lukebemish.ziggradle.internal;

import dev.lukebemish.ziggradle.ZigAbiTarget;

import java.util.Objects;

public class DefaultZigAbiTarget implements ZigAbiTarget {
    private final String name;

    public DefaultZigAbiTarget(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ZigAbiTarget that)) return false;
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
