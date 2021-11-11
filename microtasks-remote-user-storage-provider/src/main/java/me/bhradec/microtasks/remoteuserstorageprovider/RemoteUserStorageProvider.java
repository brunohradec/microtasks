package me.bhradec.microtasks.remoteuserstorageprovider;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.adapter.AbstractUserAdapter;
import org.keycloak.storage.user.UserLookupProvider;

public class RemoteUserStorageProvider implements
        UserStorageProvider,
        UserLookupProvider,
        CredentialInputValidator {

    private final KeycloakSession keycloakSession;
    private final ComponentModel componentModel;
    private final RemoteUserService remoteUserService;

    public RemoteUserStorageProvider(
            KeycloakSession keycloakSession,
            ComponentModel componentModel,
            RemoteUserService remoteUserService) {

        this.keycloakSession = keycloakSession;
        this.componentModel = componentModel;
        this.remoteUserService = remoteUserService;
    }

    private UserModel createUserModel(UserDto userDto, RealmModel realmModel) {
        return new AbstractUserAdapter(keycloakSession, realmModel, componentModel) {
            @Override
            public String getUsername() {
                return userDto.getUsername();
            }
        };
    }

    @Override
    public void close() {}

    @Override
    public UserModel getUserById(String id, RealmModel realm) {
        return null;
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realmModel) {
        UserDto userDto = remoteUserService.getUserByUsername(username);
        if (userDto == null) { return null; }

        return createUserModel(userDto, realmModel);
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realmModel) {
        return null;
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return false;
    }

    @Override
    public boolean isConfiguredFor(RealmModel realmModel, UserModel userModel, String credentialType) {
        return false;
    }

    @Override
    public boolean isValid(RealmModel realmModel, UserModel userModel, CredentialInput credentialInput) {
        return false;
    }
}
