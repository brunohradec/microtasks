package me.bhradec.microtasks.remoteuserstorageprovider;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

public class RemoteUserStorageProviderFactory implements UserStorageProviderFactory<RemoteUserStorageProvider> {
    public static final String PROVIDER_NAME = "microtasks-remote-user-storage-provider";

    private RemoteUserService buildHttpClient(String uri) {
        ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
        ResteasyWebTarget resteasyWebTarget = resteasyClient.target(uri);

        return resteasyWebTarget
                .proxyBuilder(RemoteUserService.class)
                .classloader(RemoteUserService.class.getClassLoader())
                .build();
    }

    @Override
    public RemoteUserStorageProvider create(KeycloakSession keycloakSession, ComponentModel componentModel) {
        return new RemoteUserStorageProvider(
                keycloakSession,
                componentModel,
                buildHttpClient("http://localhost/5050"));
    }

    @Override
    public String getId() {
        return PROVIDER_NAME;
    }
}
