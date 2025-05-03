package dev.lukebemish.ziggradle;

import dev.lukebemish.ziggradle.internal.DefaultZigAbiTarget;
import org.gradle.api.Named;

import java.io.Serializable;

public interface ZigAbiTarget extends Named, Serializable {
    // @formatter:off
    ZigAbiTarget NONE        = of("none"       );
    ZigAbiTarget GNU         = of("gnu"        );
    ZigAbiTarget GNUABIN32   = of("gnuabin32"  );
    ZigAbiTarget GNUABI64    = of("gnuabi64"   );
    ZigAbiTarget GNUEABI     = of("gnueabi"    );
    ZigAbiTarget GNUEABIHF   = of("gnueabihf"  );
    ZigAbiTarget GNUF32      = of("gnuf32"     );
    ZigAbiTarget GNUSF       = of("gnusf"      );
    ZigAbiTarget GNUX32      = of("gnux32"     );
    ZigAbiTarget GNUILP32    = of("gnuilp32"   );
    ZigAbiTarget CODE16      = of("code16"     );
    ZigAbiTarget EABI        = of("eabi"       );
    ZigAbiTarget EABIHF      = of("eabihf"     );
    ZigAbiTarget ILP32       = of("ilp32"      );
    ZigAbiTarget ANDROID     = of("android"    );
    ZigAbiTarget ANDROIDEABI = of("androideabi");
    ZigAbiTarget MUSL        = of("musl"       );
    ZigAbiTarget MUSLABIN32  = of("muslabin32" );
    ZigAbiTarget MUSLABI64   = of("muslabi64"  );
    ZigAbiTarget MUSLEABI    = of("musleabi"   );
    ZigAbiTarget MUSLEABIHF  = of("musleabihf" );
    ZigAbiTarget MUSLX32     = of("muslx32"    );
    ZigAbiTarget MSVC        = of("msvc"       );
    ZigAbiTarget ITANIUM     = of("itanium"    );
    ZigAbiTarget CYGNUS      = of("cygnus"     );
    ZigAbiTarget SIMULATOR   = of("simulator"  );
    ZigAbiTarget MACABI      = of("macabi"     );
    ZigAbiTarget OHOS        = of("ohos"       );
    ZigAbiTarget OHOSEABI    = of("ohoseabi"   );
    // @formatter:on

    static ZigAbiTarget of(String abi) {
        return new DefaultZigAbiTarget(abi);
    }
}
