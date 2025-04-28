package dev.lukebemish.ziggradle.toolchain.internal;

import org.gradle.api.Transformer;
import org.gradle.api.invocation.Gradle;

import javax.inject.Inject;
import java.io.File;

public abstract class ToolchainUnpackTransform implements Transformer<File, File> {
    private final ResolvedZigToolchainInfo zigInfo;

    @Inject
    public ToolchainUnpackTransform(ResolvedZigToolchainInfo zigInfo) {
        this.zigInfo = zigInfo;
    }

    @Inject
    protected abstract Gradle getGradle();

    @Override
    public File transform(File file) {
        var unpackingService = (ToolchainUnpackingService) getGradle().getSharedServices().getRegistrations()
            .getByName(ToolchainUnpackingService.TOOLCHAIN_UNPACKING_SERVICE_NAME).getService().get();
        return unpackingService.unpack(file, zigInfo);
    }
}
