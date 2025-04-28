package dev.lukebemish.ziggradle.toolchain.internal;

import org.gradle.api.artifacts.ComponentMetadataContext;
import org.gradle.api.artifacts.ComponentMetadataRule;
import org.gradle.api.attributes.Attribute;

public abstract class ZigToolchainComponentRule implements ComponentMetadataRule {
    public static final Attribute<Boolean> ZIG_TOOLCHAIN_BUNDLING_ATTRIBUTE = Attribute.of("dev.lukebemish.ziggradle.internal.bundled", Boolean.class);
    
    @Override
    public void execute(ComponentMetadataContext componentMetadataContext) {
        componentMetadataContext.getDetails().allVariants(variant -> {
            variant.attributes(attributes -> {
                attributes.attribute(ZIG_TOOLCHAIN_BUNDLING_ATTRIBUTE, true);
            });
        });
    }
}
