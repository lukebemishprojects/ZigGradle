package dev.lukebemish.ziggradle.toolchain.internal;

import dev.lukebemish.ziggradle.toolchain.ZigToolchainProvider;
import dev.lukebemish.ziggradle.toolchain.ZigToolchainRepository;
import org.gradle.api.Action;
import org.gradle.api.artifacts.repositories.AuthenticationContainer;
import org.gradle.api.artifacts.repositories.AuthenticationSupported;
import org.gradle.api.artifacts.repositories.PasswordCredentials;
import org.gradle.api.credentials.Credentials;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import javax.inject.Inject;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public abstract class ZigToolchainRepositoryInternal implements ZigToolchainRepository {
    private Class<? extends Credentials> credentialsType = PasswordCredentials.class;
    private final List<Action<?>> credentialsActions = new ArrayList<>();
    private final List<Class<? extends Credentials>> credentialsActionsTypes = new ArrayList<>();
    private final List<Action<? super AuthenticationContainer>> authenticationActions = new ArrayList<>();
    private final String name;

    @Inject
    protected abstract ObjectFactory getObjectFactory();

    @Inject
    public ZigToolchainRepositoryInternal(String name) {
        this.name = name;
    }
    
    @SuppressWarnings("unchecked")
    public void apply(AuthenticationSupported supported) {
        if (!credentialsActions.isEmpty()) {
            supported.credentials(credentialsType, credentials -> {
                for (var action : credentialsActions) {
                    ((Action<Credentials>) action).execute(credentials);
                }
            });
        }
        if (!authenticationActions.isEmpty()) {
            supported.authentication(auth -> {
                for (var action : authenticationActions) {
                    action.execute(auth);
                }
            });
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void credentials(Action<? super PasswordCredentials> action) {
        if (!PasswordCredentials.class.isAssignableFrom(credentialsType)) {
            throw new IllegalStateException("Credentials type is not PasswordCredentials");
        }
        credentialsActions.add(action);
        credentialsActionsTypes.add(PasswordCredentials.class);
    }

    @Override
    public <T extends Credentials> void credentials(Class<T> credentialsType, Action<? super T> action) {
        credentials(credentialsType);
        if (!credentialsType.isAssignableFrom(this.credentialsType)) {
            throw new IllegalStateException("Credentials type is not " + credentialsType.getName());
        }
        credentialsActions.add(action);
        credentialsActionsTypes.add(credentialsType);
    }

    @Override
    public void credentials(Class<? extends Credentials> credentialsType) {
        this.credentialsType = credentialsType;
        for (var type : credentialsActionsTypes) {
            if (!credentialsType.isAssignableFrom(type)) {
                throw new IllegalStateException("Credentials type is not " + type.getName());
            }
        }
    }

    @Override
    public void authentication(Action<? super AuthenticationContainer> action) {
        this.authenticationActions.add(action);
    }

    @Override
    public abstract Property<Class<? extends ZigToolchainProvider>> getProviderClass();

    @Override
    public abstract Property<URI> getRootUri();
}
