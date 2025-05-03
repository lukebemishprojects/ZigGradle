package dev.lukebemish.ziggradle;

import dev.lukebemish.ziggradle.internal.DefaultZigOperatingSystemTarget;
import org.gradle.api.Named;

import java.io.Serializable;

public interface ZigOperatingSystemTarget extends Named, Serializable {
    // @formatter:off
    ZigOperatingSystemTarget FREESTANDING = of("freestanding");
    ZigOperatingSystemTarget OTHER        = of("other"       );
    ZigOperatingSystemTarget CONTIKI      = of("contiki"     );
    ZigOperatingSystemTarget ELFIAMCU     = of("elfiamcu"    );
    ZigOperatingSystemTarget FUCHSIA      = of("fuchsia"     );
    ZigOperatingSystemTarget HERMIT       = of("hermit"      );
    ZigOperatingSystemTarget AIX          = of("aix"         );
    ZigOperatingSystemTarget HAIKU        = of("haiku"       );
    ZigOperatingSystemTarget HURD         = of("hurd"        );
    ZigOperatingSystemTarget LINUX        = of("linux"       );
    ZigOperatingSystemTarget PLAN9        = of("plan9"       );
    ZigOperatingSystemTarget RTEMS        = of("rtems"       );
    ZigOperatingSystemTarget SERENITY     = of("serenity"    );
    ZigOperatingSystemTarget ZOS          = of("zos"         );
    ZigOperatingSystemTarget DRAGONFLY    = of("dragonfly"   );
    ZigOperatingSystemTarget FREEBSD      = of("freebsd"     );
    ZigOperatingSystemTarget NETBSD       = of("netbsd"      );
    ZigOperatingSystemTarget OPENBSD      = of("openbsd"     );
    ZigOperatingSystemTarget DRIVERKIT    = of("driverkit"   );
    ZigOperatingSystemTarget IOS          = of("ios"         );
    ZigOperatingSystemTarget MACOS        = of("macos"       );
    ZigOperatingSystemTarget TVOS         = of("tvos"        );
    ZigOperatingSystemTarget VISIONOS     = of("visionos"    );
    ZigOperatingSystemTarget WATCHOS      = of("watchos"     );
    ZigOperatingSystemTarget ILLUMOS      = of("illumos"     );
    ZigOperatingSystemTarget SOLARIS      = of("solaris"     );
    ZigOperatingSystemTarget WINDOWS      = of("windows"     );
    ZigOperatingSystemTarget UEFI         = of("uefi"        );
    ZigOperatingSystemTarget PS3          = of("ps3"         );
    ZigOperatingSystemTarget PS4          = of("ps4"         );
    ZigOperatingSystemTarget PS5          = of("ps5"         );
    ZigOperatingSystemTarget EMSCRIPTEN   = of("emscripten"  );
    ZigOperatingSystemTarget WASI         = of("wasi"        );
    ZigOperatingSystemTarget AMDHSA       = of("amdhsa"      );
    ZigOperatingSystemTarget AMDPAL       = of("amdpal"      );
    ZigOperatingSystemTarget CUDA         = of("cuda"        );
    ZigOperatingSystemTarget MESA3D       = of("mesa3d"      );
    ZigOperatingSystemTarget NVCL         = of("nvcl"        );
    ZigOperatingSystemTarget OPENCL       = of("opencl"      );
    ZigOperatingSystemTarget OPENGL       = of("opengl"      );
    ZigOperatingSystemTarget VULKAN       = of("vulkan"      );
    // @formatter:on

    static ZigOperatingSystemTarget of(String os) {
        return new DefaultZigOperatingSystemTarget(os);
    }
}
