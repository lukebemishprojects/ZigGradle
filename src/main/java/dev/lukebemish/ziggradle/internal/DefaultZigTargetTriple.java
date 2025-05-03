package dev.lukebemish.ziggradle.internal;

import dev.lukebemish.ziggradle.ZigAbiTarget;
import dev.lukebemish.ziggradle.ZigArchitectureTarget;
import dev.lukebemish.ziggradle.ZigOperatingSystemTarget;
import dev.lukebemish.ziggradle.ZigTargetTriple;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class DefaultZigTargetTriple implements ZigTargetTriple {
    private final ZigArchitectureTarget arch;
    private final ZigOperatingSystemTarget os;
    @Nullable
    private final ZigAbiTarget abi;

    public DefaultZigTargetTriple(ZigArchitectureTarget arch, ZigOperatingSystemTarget os, @Nullable ZigAbiTarget abi) {
        this.arch = arch;
        this.os = os;
        this.abi = abi;
    }

    @Override
    public String getName() {
        if (abi == null) {
            return arch.getName() + "-" + os.getName();
        } else {
            return arch.getName() + "-" + os.getName() + "-" + abi.getName();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ZigTargetTriple that)) return false;
        return Objects.equals(arch, that.getArch()) && Objects.equals(os, that.getOs()) && Objects.equals(abi, that.getAbi());
    }

    @Override
    public int hashCode() {
        return Objects.hash(arch, os, abi);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public ZigArchitectureTarget getArch() {
        return arch;
    }

    @Override
    public ZigOperatingSystemTarget getOs() {
        return os;
    }

    @Override
    public @Nullable ZigAbiTarget getAbi() {
        return abi;
    }
}
