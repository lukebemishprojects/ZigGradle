package dev.lukebemish.ziggradle;

import dev.lukebemish.ziggradle.internal.DefaultZigArchitectureTarget;
import org.gradle.api.Named;

import java.io.Serializable;

public interface ZigArchitectureTarget extends Named, Serializable {
    ZigArchitectureTarget X86_64 = new DefaultZigArchitectureTarget("x86_64");
    ZigArchitectureTarget AARCH64 = new DefaultZigArchitectureTarget("aarch64");
    ZigArchitectureTarget X86 = new DefaultZigArchitectureTarget("x86");
}
