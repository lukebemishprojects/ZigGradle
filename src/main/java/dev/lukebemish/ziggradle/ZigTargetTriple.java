package dev.lukebemish.ziggradle;

import dev.lukebemish.ziggradle.internal.DefaultZigTargetTriple;
import org.gradle.api.Named;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.Serializable;

public interface ZigTargetTriple extends Named, Serializable {
    // @formatter:off
    ZigTargetTriple ARC_LINUX_GNU             = of(ZigArchitectureTarget.ARC        , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNU       );
    ZigTargetTriple ARM_LINUX_GNUEABI         = of(ZigArchitectureTarget.ARM        , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNUEABI   );
    ZigTargetTriple ARM_LINUX_GNUEABIHF       = of(ZigArchitectureTarget.ARM        , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNUEABIHF );
    ZigTargetTriple ARM_LINUX_MUSLEABI        = of(ZigArchitectureTarget.ARM        , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.MUSLEABI  );
    ZigTargetTriple ARM_LINUX_MUSLEABIHF      = of(ZigArchitectureTarget.ARM        , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.MUSLEABIHF);
    ZigTargetTriple ARMEB_LINUX_GNUEABI       = of(ZigArchitectureTarget.ARMEB      , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNUEABI   );
    ZigTargetTriple ARMEB_LINUX_GNUEABIHF     = of(ZigArchitectureTarget.ARMEB      , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNUEABIHF );
    ZigTargetTriple ARMEB_LINUX_MUSLEABI      = of(ZigArchitectureTarget.ARMEB      , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.MUSLEABI  );
    ZigTargetTriple ARMEB_LINUX_MUSLEABIHF    = of(ZigArchitectureTarget.ARMEB      , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.MUSLEABIHF);
    ZigTargetTriple THUMB_LINUX_MUSLEABI      = of(ZigArchitectureTarget.THUMB      , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.MUSLEABI  );
    ZigTargetTriple THUMB_LINUX_MUSLEABIHF    = of(ZigArchitectureTarget.THUMB      , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.MUSLEABIHF);
    ZigTargetTriple THUMB_WINDOWS_GNU         = of(ZigArchitectureTarget.THUMB      , ZigOperatingSystemTarget.WINDOWS, ZigAbiTarget.GNU       );
    ZigTargetTriple THUMBEB_LINUX_MUSLEABI    = of(ZigArchitectureTarget.THUMBEB    , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.MUSLEABI  );
    ZigTargetTriple THUMBEB_LINUX_MUSLEABIHF  = of(ZigArchitectureTarget.THUMBEB    , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.MUSLEABIHF);
    ZigTargetTriple AARCH64_LINUX_GNU         = of(ZigArchitectureTarget.AARCH64    , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNU       );
    ZigTargetTriple AARCH64_LINUX_MUSL        = of(ZigArchitectureTarget.AARCH64    , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.MUSL      );
    ZigTargetTriple AARCH64_MACOS_NONE        = of(ZigArchitectureTarget.AARCH64    , ZigOperatingSystemTarget.MACOS  , ZigAbiTarget.NONE      );
    ZigTargetTriple AARCH64_WINDOWS_GNU       = of(ZigArchitectureTarget.AARCH64    , ZigOperatingSystemTarget.WINDOWS, ZigAbiTarget.GNU       );
    ZigTargetTriple AARCH64_BE_LINUX_GNU      = of(ZigArchitectureTarget.AARCH64_BE , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNU       );
    ZigTargetTriple AARCH64_BE_LINUX_MUSL     = of(ZigArchitectureTarget.AARCH64_BE , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.MUSL      );
    ZigTargetTriple CSKY_LINUX_GNUEABI        = of(ZigArchitectureTarget.CSKY       , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNUEABI   );
    ZigTargetTriple CSKY_LINUX_GNUEABIHF      = of(ZigArchitectureTarget.CSKY       , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNUEABIHF );
    ZigTargetTriple LOONGARCH64_LINUX_GNU     = of(ZigArchitectureTarget.LOONGARCH64, ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNU       );
    ZigTargetTriple LOONGARCH64_LINUX_GNUSF   = of(ZigArchitectureTarget.LOONGARCH64, ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNUSF     );
    ZigTargetTriple LOONGARCH64_LINUX_MUSL    = of(ZigArchitectureTarget.LOONGARCH64, ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.MUSL      );
    ZigTargetTriple M68K_LINUX_GNU            = of(ZigArchitectureTarget.M68K       , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNU       );
    ZigTargetTriple M68K_LINUX_MUSL           = of(ZigArchitectureTarget.M68K       , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.MUSL      );
    ZigTargetTriple MIPS_LINUX_GNUEABI        = of(ZigArchitectureTarget.MIPS       , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNUEABI   );
    ZigTargetTriple MIPS_LINUX_GNUEABIHF      = of(ZigArchitectureTarget.MIPS       , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNUEABIHF );
    ZigTargetTriple MIPS_LINUX_MUSLEABI       = of(ZigArchitectureTarget.MIPS       , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.MUSLEABI  );
    ZigTargetTriple MIPS_LINUX_MUSLEABIHF     = of(ZigArchitectureTarget.MIPS       , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.MUSLEABIHF);
    ZigTargetTriple MIPSEL_LINUX_GNUEABI      = of(ZigArchitectureTarget.MIPSEL     , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNUEABI   );
    ZigTargetTriple MIPSEL_LINUX_GNUEABIHF    = of(ZigArchitectureTarget.MIPSEL     , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNUEABIHF );
    ZigTargetTriple MIPSEL_LINUX_MUSLEABI     = of(ZigArchitectureTarget.MIPSEL     , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.MUSLEABI  );
    ZigTargetTriple MIPSEL_LINUX_MUSLEABIHF   = of(ZigArchitectureTarget.MIPSEL     , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.MUSLEABIHF);
    ZigTargetTriple MIPS64_LINUX_GNUABI64     = of(ZigArchitectureTarget.MIPS64     , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNUABI64  );
    ZigTargetTriple MIPS64_LINUX_GNUABIN32    = of(ZigArchitectureTarget.MIPS64     , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNUABIN32 );
    ZigTargetTriple MIPS64_LINUX_MUSLABI64    = of(ZigArchitectureTarget.MIPS64     , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.MUSLABI64 );
    ZigTargetTriple MIPS64_LINUX_MUSLABIN32   = of(ZigArchitectureTarget.MIPS64     , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.MUSLABIN32);
    ZigTargetTriple MIPS64EL_LINUX_GNUABI64   = of(ZigArchitectureTarget.MIPS64EL   , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNUABI64  );
    ZigTargetTriple MIPS64EL_LINUX_GNUABIN32  = of(ZigArchitectureTarget.MIPS64EL   , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNUABIN32 );
    ZigTargetTriple MIPS64EL_LINUX_MUSLABI64  = of(ZigArchitectureTarget.MIPS64EL   , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.MUSLABI64 );
    ZigTargetTriple MIPS64EL_LINUX_MUSLABIN32 = of(ZigArchitectureTarget.MIPS64EL   , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.MUSLABIN32);
    ZigTargetTriple POWERPC_LINUX_GNUEABI     = of(ZigArchitectureTarget.POWERPC    , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNUEABI   );
    ZigTargetTriple POWERPC_LINUX_GNUEABIHF   = of(ZigArchitectureTarget.POWERPC    , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNUEABIHF );
    ZigTargetTriple POWERPC_LINUX_MUSLEABI    = of(ZigArchitectureTarget.POWERPC    , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.MUSLEABI  );
    ZigTargetTriple POWERPC_LINUX_MUSLEABIHF  = of(ZigArchitectureTarget.POWERPC    , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.MUSLEABIHF);
    ZigTargetTriple POWERPC64_LINUX_GNU       = of(ZigArchitectureTarget.POWERPC64  , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNU       );
    ZigTargetTriple POWERPC64_LINUX_MUSL      = of(ZigArchitectureTarget.POWERPC64  , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.MUSL      );
    ZigTargetTriple POWERPC64LE_LINUX_GNU     = of(ZigArchitectureTarget.POWERPC64LE, ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNU       );
    ZigTargetTriple POWERPC64LE_LINUX_MUSL    = of(ZigArchitectureTarget.POWERPC64LE, ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.MUSL      );
    ZigTargetTriple RISCV32_LINUX_GNU         = of(ZigArchitectureTarget.RISCV32    , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNU       );
    ZigTargetTriple RISCV32_LINUX_MUSL        = of(ZigArchitectureTarget.RISCV32    , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.MUSL      );
    ZigTargetTriple RISCV64_LINUX_GNU         = of(ZigArchitectureTarget.RISCV64    , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNU       );
    ZigTargetTriple RISCV64_LINUX_MUSL        = of(ZigArchitectureTarget.RISCV64    , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.MUSL      );
    ZigTargetTriple S390X_LINUX_GNU           = of(ZigArchitectureTarget.S390X      , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNU       );
    ZigTargetTriple S390X_LINUX_MUSL          = of(ZigArchitectureTarget.S390X      , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.MUSL      );
    ZigTargetTriple SPARC_LINUX_GNU           = of(ZigArchitectureTarget.SPARC      , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNU       );
    ZigTargetTriple SPARC64_LINUX_GNU         = of(ZigArchitectureTarget.SPARC64    , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNU       );
    ZigTargetTriple WASM32_WASI_MUSL          = of(ZigArchitectureTarget.WASM32     , ZigOperatingSystemTarget.WASI   , ZigAbiTarget.MUSL      );
    ZigTargetTriple X86_LINUX_GNU             = of(ZigArchitectureTarget.X86        , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNU       );
    ZigTargetTriple X86_LINUX_MUSL            = of(ZigArchitectureTarget.X86        , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.MUSL      );
    ZigTargetTriple X86_WINDOWS_GNU           = of(ZigArchitectureTarget.X86        , ZigOperatingSystemTarget.WINDOWS, ZigAbiTarget.GNU       );
    ZigTargetTriple X86_64_LINUX_GNU          = of(ZigArchitectureTarget.X86_64     , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNU       );
    ZigTargetTriple X86_64_LINUX_GNUX32       = of(ZigArchitectureTarget.X86_64     , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.GNUX32    );
    ZigTargetTriple X86_64_LINUX_MUSL         = of(ZigArchitectureTarget.X86_64     , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.MUSL      );
    ZigTargetTriple X86_64_LINUX_MUSLX32      = of(ZigArchitectureTarget.X86_64     , ZigOperatingSystemTarget.LINUX  , ZigAbiTarget.MUSLX32   );
    ZigTargetTriple X86_64_MACOS_NONE         = of(ZigArchitectureTarget.X86_64     , ZigOperatingSystemTarget.MACOS  , ZigAbiTarget.NONE      );
    ZigTargetTriple X86_64_WINDOWS_GNU        = of(ZigArchitectureTarget.X86_64     , ZigOperatingSystemTarget.WINDOWS, ZigAbiTarget.GNU       );
    // @formatter:on

    @Nullable
    ZigArchitectureTarget getArch();

    @Nullable
    ZigOperatingSystemTarget getOs();

    @Nullable
    ZigAbiTarget getAbi();

    static ZigTargetTriple of(ZigArchitectureTarget arch, ZigOperatingSystemTarget os, @Nullable ZigAbiTarget abi) {
        return new DefaultZigTargetTriple(arch, os, abi);
    }
}
