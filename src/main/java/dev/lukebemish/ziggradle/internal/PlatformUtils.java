package dev.lukebemish.ziggradle.internal;

import dev.lukebemish.ziggradle.ZigArchitectureTarget;
import dev.lukebemish.ziggradle.ZigOperatingSystemTarget;
import dev.lukebemish.ziggradle.ZigTargetTriple;
import org.apache.commons.lang3.SystemUtils;
import org.gradle.platform.Architecture;
import org.gradle.platform.BuildPlatform;
import org.gradle.platform.BuildPlatformFactory;
import org.gradle.platform.OperatingSystem;

public final class PlatformUtils {
    private PlatformUtils() {}

    @SuppressWarnings("UnstableApiUsage")
    public static ZigArchitectureTarget getCurrentArchitecture() {
        return switch (getBuildPlatform().getArchitecture()) {
            case X86 -> ZigArchitectureTarget.X86;
            case X86_64 -> ZigArchitectureTarget.X86_64;
            case AARCH64 -> ZigArchitectureTarget.AARCH64;
        };
    }

    public static ZigOperatingSystemTarget getCurrentOperatingSystem() {
        return switch (getBuildPlatform().getOperatingSystem()) {
            case WINDOWS -> ZigOperatingSystemTarget.WINDOWS;
            case MAC_OS -> ZigOperatingSystemTarget.MACOS;
            case LINUX -> ZigOperatingSystemTarget.LINUX;
            case SOLARIS -> ZigOperatingSystemTarget.SOLARIS;
            case FREE_BSD -> ZigOperatingSystemTarget.FREEBSD;
            case UNIX -> new DefaultZigOperatingSystemTarget("unix"); // Invalid target, but that is for whoever is having to deal with this to handle
        };
    }

    public static ZigTargetTriple getCurrentTriple() {
        return ZigTargetTriple.of(getCurrentArchitecture(), getCurrentOperatingSystem(), null);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static BuildPlatform getBuildPlatform() {
        OperatingSystem os;
        if (SystemUtils.IS_OS_WINDOWS) {
            os = OperatingSystem.WINDOWS;
        } else if (SystemUtils.IS_OS_MAC) {
            os = OperatingSystem.MAC_OS;
        } else if (SystemUtils.IS_OS_SOLARIS) {
            os = OperatingSystem.SOLARIS;
        } else if (SystemUtils.IS_OS_FREE_BSD) {
            os = OperatingSystem.FREE_BSD;
        } else if (SystemUtils.IS_OS_LINUX) {
            os = OperatingSystem.LINUX;
        } else if (SystemUtils.IS_OS_UNIX) {
            os = OperatingSystem.UNIX;
        } else {
            throw new IllegalStateException("Unsupported OS: " + SystemUtils.OS_NAME);
        }

        Architecture arch = null;
        String osArch = System.getProperty("os.arch");
        boolean is64Bit = osArch.contains("64") || osArch.startsWith("armv8");
        if (osArch.startsWith("aarch") || osArch.startsWith("arm")) {
            if (is64Bit) {
                arch = Architecture.AARCH64;
            }
        } else if (!osArch.startsWith("ppc") && !osArch.startsWith("riscv")) {
            arch = is64Bit ? Architecture.X86_64 : Architecture.X86;
        }
        if (arch == null) {
            throw new IllegalStateException("Unsupported architecture: " + osArch);
        }

        return BuildPlatformFactory.of(arch, os);
    }
}
