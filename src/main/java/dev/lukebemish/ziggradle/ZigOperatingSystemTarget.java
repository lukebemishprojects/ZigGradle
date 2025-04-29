package dev.lukebemish.ziggradle;

import dev.lukebemish.ziggradle.internal.DefaultZigOperatingSystemTarget;
import org.gradle.api.Named;

import java.io.Serializable;

public interface ZigOperatingSystemTarget extends Named, Serializable {
    ZigOperatingSystemTarget LINUX = new DefaultZigOperatingSystemTarget("linux");
    ZigOperatingSystemTarget MACOS = new DefaultZigOperatingSystemTarget("macos");
    ZigOperatingSystemTarget WINDOWS = new DefaultZigOperatingSystemTarget("windows");
    ZigOperatingSystemTarget SOLARIS = new DefaultZigOperatingSystemTarget("solaris");
    ZigOperatingSystemTarget FREEBSD = new DefaultZigOperatingSystemTarget("freebsd");
}
