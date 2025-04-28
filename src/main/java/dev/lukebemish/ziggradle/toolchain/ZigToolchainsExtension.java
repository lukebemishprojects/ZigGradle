package dev.lukebemish.ziggradle.toolchain;

import org.gradle.api.Action;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Provider;

import javax.inject.Inject;

public abstract class ZigToolchainsExtension {
    public Provider<ZigCompiler> compilerFor(Action<? super ZigToolchainSpec> config) {
        var spec = getObjectFactory().newInstance(ZigToolchainSpec.class);
        config.execute(spec);
        return compilerFor(spec);
    }

    public abstract Provider<ZigCompiler> compilerFor(ZigToolchainSpec spec);
    
    @Inject
    protected ZigToolchainsExtension() {}

    @Inject
    protected abstract ObjectFactory getObjectFactory();
}
