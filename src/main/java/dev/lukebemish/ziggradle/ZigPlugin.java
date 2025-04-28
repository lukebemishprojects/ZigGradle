package dev.lukebemish.ziggradle;

import dev.lukebemish.ziggradle.toolchain.ZigToolchainRepository;
import dev.lukebemish.ziggradle.toolchain.internal.DefaultToolchainProvider;
import dev.lukebemish.ziggradle.toolchain.internal.ToolchainUnpackTransform;
import dev.lukebemish.ziggradle.toolchain.internal.ZigToolchainComponentRule;
import dev.lukebemish.ziggradle.toolchain.internal.ZigToolchainProviderInfo;
import dev.lukebemish.ziggradle.toolchain.internal.ZigToolchainRepositoryInternal;
import dev.lukebemish.ziggradle.toolchain.internal.ZigToolchainsExtensionInternal;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.ComponentMetadataHandler;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.IvyArtifactRepository;
import org.gradle.api.artifacts.type.ArtifactTypeDefinition;
import org.gradle.api.initialization.Settings;
import org.gradle.api.initialization.resolve.RepositoriesMode;
import org.gradle.api.initialization.resolve.RulesMode;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.model.ObjectFactory;

import javax.inject.Inject;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

public abstract class ZigPlugin implements Plugin<Object> {
    @Inject
    public ZigPlugin() {}
    
    @Inject
    protected abstract ObjectFactory getObjectFactory();
    
    @Override
    public void apply(Object target) {
        if (target instanceof Project project) {
            var zigExtension = project.getExtensions().create("zig", ZigExtension.class);
        } else if (target instanceof Settings settings) {
            settings.getGradle().getPluginManager().apply(ZigPlugin.class);
            
            var repositories = settings.getToolchainManagement().getExtensions().create("zig", ZigToolchainsManagement.class).getZigRepositories();
            
            // Add the default provider
            repositories.register("zig-default", zigRepo -> {
                zigRepo.getRootUri().set(DefaultToolchainProvider.ZIG_DOWNLOAD_URL);
                zigRepo.getProviderClass().set(DefaultToolchainProvider.class);
            });
            
            settings.getGradle().settingsEvaluated(s -> {
                var failOnProjectRepos = s.getDependencyResolutionManagement().getRepositoriesMode()
                        .map(mode -> mode == RepositoriesMode.FAIL_ON_PROJECT_REPOS);

                var failOnProjectRules = s.getDependencyResolutionManagement().getRulesMode()
                        .map(mode -> mode == RulesMode.FAIL_ON_PROJECT_RULES);

                var services = new ArrayList<ZigToolchainProviderInfo.SerializedInfo>();
                for (var toolchainRepo : repositories) {
                    settings.getGradle().getSharedServices().registerIfAbsent(ZigToolchainsExtensionInternal.ZIG_TOOLCHAIN_PROVIDER_SERVICE_PREFIX + toolchainRepo.getName(), toolchainRepo.getProviderClass().get());
                    services.add(new ZigToolchainProviderInfo.SerializedInfo(toolchainRepo.getName(), toolchainRepo.getRootUri().get()));
                }

                s.getGradle().getLifecycle().beforeProject(project -> {
                    if (!failOnProjectRepos.get()) {
                        applyToolchainRepos(project.getRepositories(), repositories);
                    }
                    if (!failOnProjectRules.get()) {
                        applyToolchainModules(project.getDependencies().getComponents(), repositories);
                    }
                    var zigExtension = project.getExtensions().findByType(ZigExtension.class);
                    if (zigExtension != null) {
                        ((ZigToolchainsExtensionInternal) zigExtension.getToolchains()).setProviders(services);
                    }
                    project.getExtensions().add(ZigToolchainsExtensionInternal.ZIG_TOOLCHAIN_PROVIDER_PROXY_EXTENSION, services);

                    project.getDependencies().getAttributesSchema().attribute(ZigToolchainComponentRule.ZIG_TOOLCHAIN_BUNDLING_ATTRIBUTE);
                    project.getDependencies().registerTransform(ToolchainUnpackTransform.class, action -> {
                        action.getFrom()
                                .attribute(ZigToolchainComponentRule.ZIG_TOOLCHAIN_BUNDLING_ATTRIBUTE, true);
                        action.getTo()
                                .attribute(ZigToolchainComponentRule.ZIG_TOOLCHAIN_BUNDLING_ATTRIBUTE, false);
                    });
                });
                
                applyToolchainRepos(s.getDependencyResolutionManagement().getRepositories(), repositories);
                applyToolchainModules(s.getDependencyResolutionManagement().getComponents(), repositories);
            });
        } else if (target instanceof Gradle gradle) {
            // Do nothing
        } else {
            throw new IllegalArgumentException("ZigPlugin can only be applied to a Project or Settings.");
        }
    }

    private static void applyToolchainRepos(RepositoryHandler repositoryHandler, Collection<ZigToolchainRepository> repos) {
        for (var toolchainRepo : repos) {
            repositoryHandler.exclusiveContent(exclusiveContent -> {
                exclusiveContent.forRepository(() -> repositoryHandler.ivy(repo -> {
                    repo.setName("zig-gradle-toolchain-repository" + toolchainRepo.getName());
                    ((ZigToolchainRepositoryInternal) toolchainRepo).apply(repo);
                    repo.setUrl(toolchainRepo.getRootUri());
                    repo.patternLayout(layout ->
                            layout.artifact("[revision]")
                    );
                    repo.metadataSources(IvyArtifactRepository.MetadataSources::artifact);
                }));
                exclusiveContent.filter(content -> {
                    content.includeModule(ZigToolchainsExtensionInternal.ZIG_TOOLCHAIN_PROVIDER_SERVICE_PREFIX + toolchainRepo.getName(), "zig");
                });
            });
        }
    }
    
    private static void applyToolchainModules(ComponentMetadataHandler components, Collection<ZigToolchainRepository> repos) {
        for (var toolchainRepo : repos) {
            components.withModule(ZigToolchainsExtensionInternal.ZIG_TOOLCHAIN_PROVIDER_SERVICE_PREFIX + toolchainRepo.getName() + ":zig", ZigToolchainComponentRule.class);
        }
    }
}
