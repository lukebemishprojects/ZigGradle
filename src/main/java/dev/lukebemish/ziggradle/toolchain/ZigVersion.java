package dev.lukebemish.ziggradle.toolchain;

import java.io.Serializable;
import java.util.Objects;

public final class ZigVersion implements Serializable {
    private ZigVersion(String version) {
        this.version = version;
    }

    public static ZigVersion of(String version) {
        return new ZigVersion(version);
    }

    private final String version;

    public String toString() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ZigVersion that)) return false;
        return Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(version);
    }
}
