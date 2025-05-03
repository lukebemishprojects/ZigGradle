package dev.lukebemish.ziggradle;

import dev.lukebemish.ziggradle.internal.DefaultZigArchitectureTarget;
import org.gradle.api.Named;

import java.io.Serializable;

public interface ZigArchitectureTarget extends Named, Serializable {
    // @formatter:off
    ZigArchitectureTarget AMDGCN      = of("amdgcn"     );
    ZigArchitectureTarget ARC         = of("arc"        );
    ZigArchitectureTarget ARM         = of("arm"        );
    ZigArchitectureTarget ARMEB       = of("armeb"      );
    ZigArchitectureTarget THUMB       = of("thumb"      );
    ZigArchitectureTarget THUMBEB     = of("thumbeb"    );
    ZigArchitectureTarget AARCH64     = of("aarch64"    );
    ZigArchitectureTarget AARCH64_BE  = of("aarch64_be" );
    ZigArchitectureTarget AVR         = of("avr"        );
    ZigArchitectureTarget BPFEL       = of("bpfel"      );
    ZigArchitectureTarget BPFEB       = of("bpfeb"      );
    ZigArchitectureTarget CSKY        = of("csky"       );
    ZigArchitectureTarget HEXAGON     = of("hexagon"    );
    ZigArchitectureTarget KALIMBA     = of("kalimba"    );
    ZigArchitectureTarget LANAI       = of("lanai"      );
    ZigArchitectureTarget LOONGARCH32 = of("loongarch32");
    ZigArchitectureTarget LOONGARCH64 = of("loongarch64");
    ZigArchitectureTarget M68K        = of("m68k"       );
    ZigArchitectureTarget MIPS        = of("mips"       );
    ZigArchitectureTarget MIPSEL      = of("mipsel"     );
    ZigArchitectureTarget MIPS64      = of("mips64"     );
    ZigArchitectureTarget MIPS64EL    = of("mips64el"   );
    ZigArchitectureTarget MSP430      = of("msp430"     );
    ZigArchitectureTarget NVPTX       = of("nvptx"      );
    ZigArchitectureTarget NVPTX64     = of("nvptx64"    );
    ZigArchitectureTarget POWERPC     = of("powerpc"    );
    ZigArchitectureTarget POWERPCLE   = of("powerpcle"  );
    ZigArchitectureTarget POWERPC64   = of("powerpc64"  );
    ZigArchitectureTarget POWERPC64LE = of("powerpc64le");
    ZigArchitectureTarget PROPELLER   = of("propeller"  );
    ZigArchitectureTarget RISCV32     = of("riscv32"    );
    ZigArchitectureTarget RISCV64     = of("riscv64"    );
    ZigArchitectureTarget S390X       = of("s390x"      );
    ZigArchitectureTarget SPARC       = of("sparc"      );
    ZigArchitectureTarget SPARC64     = of("sparc64"    );
    ZigArchitectureTarget SPIRV       = of("spirv"      );
    ZigArchitectureTarget SPIRV32     = of("spirv32"    );
    ZigArchitectureTarget SPIRV64     = of("spirv64"    );
    ZigArchitectureTarget VE          = of("ve"         );
    ZigArchitectureTarget WASM32      = of("wasm32"     );
    ZigArchitectureTarget WASM64      = of("wasm64"     );
    ZigArchitectureTarget X86         = of("x86"        );
    ZigArchitectureTarget X86_64      = of("x86_64"     );
    ZigArchitectureTarget XCORE       = of("xcore"      );
    ZigArchitectureTarget XTENSA      = of("xtensa"     );
    // @formatter:on

    static ZigArchitectureTarget of(String arch) {
        return new DefaultZigArchitectureTarget(arch);
    }
}
