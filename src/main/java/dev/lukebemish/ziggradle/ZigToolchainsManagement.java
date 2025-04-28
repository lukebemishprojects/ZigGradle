package dev.lukebemish.ziggradle;

import dev.lukebemish.ziggradle.toolchain.ZigToolchainRepository;
import dev.lukebemish.ziggradle.toolchain.internal.ZigToolchainRepositoryInternal;
import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.model.ObjectFactory;

import javax.inject.Inject;

public abstract class ZigToolchainsManagement {
    private final NamedDomainObjectContainer<ZigToolchainRepository> zigRepositories;

    @Inject
    protected abstract ObjectFactory getObjectFactory(); 
    
    @Inject
    public ZigToolchainsManagement() {
        this.zigRepositories = getObjectFactory().domainObjectContainer(ZigToolchainRepository.class, name -> getObjectFactory().newInstance(ZigToolchainRepositoryInternal.class, name));
    }

    public NamedDomainObjectContainer<ZigToolchainRepository> getZigRepositories() {
        return zigRepositories;
    }
    
    public void zigRepositories(Action<? super NamedDomainObjectContainer<ZigToolchainRepository>> action) {
        action.execute(getZigRepositories());
    }
}
