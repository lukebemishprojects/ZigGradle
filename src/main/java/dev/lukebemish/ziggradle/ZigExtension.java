package dev.lukebemish.ziggradle;

import dev.lukebemish.ziggradle.toolchain.ZigToolchainsExtension;
import dev.lukebemish.ziggradle.toolchain.internal.ZigToolchainsExtensionInternal;
import org.gradle.api.Action;
import org.gradle.api.model.ObjectFactory;

import javax.inject.Inject;

public abstract class ZigExtension {
    private final ZigToolchainsExtension toolchains;

    @Inject
    protected abstract ObjectFactory getObjectFactory();
    
    @Inject
    public ZigExtension() {
        this.toolchains = getObjectFactory().newInstance(ZigToolchainsExtensionInternal.class);
    }

    public ZigToolchainsExtension getToolchains() {
        return toolchains;
    }
    
    public void toolchains(Action<? super ZigToolchainsExtension> action) {
        action.execute(toolchains);
    }
}
