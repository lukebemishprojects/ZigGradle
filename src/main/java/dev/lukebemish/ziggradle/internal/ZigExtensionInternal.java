package dev.lukebemish.ziggradle.internal;

import dev.lukebemish.ziggradle.ZigExtension;
import dev.lukebemish.ziggradle.toolchain.ZigCompiler;
import dev.lukebemish.ziggradle.toolchain.ZigToolchainProvider;
import dev.lukebemish.ziggradle.toolchain.ZigToolchainSpec;
import dev.lukebemish.ziggradle.toolchain.internal.ToolchainUnpackingService;
import dev.lukebemish.ziggradle.toolchain.internal.ZigToolchain;
import dev.lukebemish.ziggradle.toolchain.internal.ZigToolchainProviderInfo;
import org.gradle.api.Project;
import org.gradle.api.Transformer;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;

import javax.inject.Inject;
import java.util.List;

public abstract class ZigExtensionInternal extends ZigExtension {
    public static final String ZIG_TOOLCHAIN_PROVIDER_PROXY_EXTENSION = "dev.lukebemish.ziggradle.internal.toolchain.providersExtension";

    @SuppressWarnings("unchecked")
    @Inject
    public ZigExtensionInternal() {

        var providerIntoList = getProject().getExtensions().findByName(ZIG_TOOLCHAIN_PROVIDER_PROXY_EXTENSION);
        if (providerIntoList != null) {
            setProviders((List<ZigToolchainProviderInfo.SerializedInfo>) providerIntoList);
        }
    }

    @Inject
    protected abstract Project getProject();

    protected abstract ListProperty<ZigToolchainProviderInfo> getToolchainProviderInfo();

    public void setProviders(List<ZigToolchainProviderInfo.SerializedInfo> providerServices) {
        for (var providerInfo : providerServices) {
            var providerService = getProject().getGradle().getSharedServices().getRegistrations().findByName(ToolchainUnpackingService.ZIG_TOOLCHAIN_PROVIDER_SERVICE_PREFIX + providerInfo.name());
            getToolchainProviderInfo().add(new ZigToolchainProviderInfo(providerInfo, providerService.getService().map(s -> (ZigToolchainProvider) s)));
        }
    }

    private Provider<ZigToolchain> toolchainFor(ZigToolchainSpec spec) {
        var service = (ToolchainUnpackingService) getProject().getGradle().getSharedServices().getRegistrations()
            .getByName(ToolchainUnpackingService.TOOLCHAIN_UNPACKING_SERVICE_NAME).getService().get();
        return service.toolchainFor(spec, getProject(), getToolchainProviderInfo());
    }

    public static abstract class CompilerCreatingTransformer implements Transformer<ZigCompiler, ZigToolchain> {
        @Inject
        protected abstract ObjectFactory getObjectFactory();

        @Inject
        public CompilerCreatingTransformer() {}

        @Override
        public ZigCompiler transform(ZigToolchain zigToolchain) {
            return getObjectFactory().newInstance(ZigToolchain.DefaultZigCompiler.class, zigToolchain);
        }
    }

    public Provider<ZigCompiler> compilerFor(ZigToolchainSpec spec) {
        return toolchainFor(spec).map(getObjectFactory().newInstance(CompilerCreatingTransformer.class));
    }
}
