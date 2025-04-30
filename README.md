# zig-gradle

A Gradle plugin for setting up and using zig toolchains. It is recommended to apply this as a settings plugin to set up
automatic default toolchain sources, and then apply in individual subprojects as desired. To set up a default toolchain:

```gradle
zig {
    toolchain {
        version = dev.lukebemish.ziggradle.toolchain.ZigVersion.of('0.14.0')
    }
}
```

Specific toolchains can also be acquired using `zig.compilerFor`. This compiler can be used as-is for `Exec` tasks, or
with some presets in the `ZigCompileTask` task.
