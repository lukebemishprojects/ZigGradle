package dev.lukebemish.ziggradle;

import dev.lukebemish.ziggradle.toolchain.ZigCompiler;
import dev.lukebemish.ziggradle.toolchain.ZigToolchainSpec;
import org.gradle.api.Action;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Provider;

import javax.inject.Inject;

public abstract class ZigExtension {
    private final ZigToolchainSpec toolchains;

    @Inject
    protected abstract ObjectFactory getObjectFactory();

    @Inject
    public ZigExtension() {
        this.toolchains = getObjectFactory().newInstance(ZigToolchainSpec.class);
    }

    public ZigToolchainSpec getToolchain() {
        return toolchains;
    }

    public void toolchain(Action<? super ZigToolchainSpec> action) {
        action.execute(toolchains);
    }

    public Provider<ZigCompiler> compilerFor(Action<? super ZigToolchainSpec> config) {
        var spec = getObjectFactory().newInstance(ZigToolchainSpec.class);
        spec.getVersion().convention(getToolchain().getVersion());
        config.execute(spec);
        return compilerFor(spec);
    }

    public abstract Provider<ZigCompiler> compilerFor(ZigToolchainSpec spec);
}
