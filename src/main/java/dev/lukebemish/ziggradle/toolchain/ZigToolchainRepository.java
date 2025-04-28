package dev.lukebemish.ziggradle.toolchain;

import org.gradle.api.Action;
import org.gradle.api.Named;
import org.gradle.api.artifacts.repositories.AuthenticationContainer;
import org.gradle.api.artifacts.repositories.PasswordCredentials;
import org.gradle.api.credentials.Credentials;
import org.gradle.api.provider.Property;

import java.net.URI;

public interface ZigToolchainRepository extends Named {
    void credentials(Action<? super PasswordCredentials> action);

    <T extends Credentials> void credentials(Class<T> credentialsType, Action<? super T> action);

    void credentials(Class<? extends Credentials> credentialsType);

    void authentication(Action<? super AuthenticationContainer> action);
    
    Property<Class<? extends ZigToolchainProvider>> getProviderClass();
    
    Property<URI> getRootUri();
}
