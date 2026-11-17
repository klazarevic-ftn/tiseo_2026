package com.fin.tiseo.user_service.service;

import com.fin.tiseo.user_service.entity.User;
import jakarta.ws.rs.core.Response;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final Keycloak keycloak;

    @Value("${keycloak.admin.realm}")
    private String realm;

    public String create(
            User user,
            String username,
            String password,
            String roleName
    ) {
        UserRepresentation account = new UserRepresentation();
        account.setUsername(username);
        account.setFirstName(user.getFirstName());
        account.setLastName(user.getLastName());
        account.setEmail(user.getEmail());
        account.setEnabled(true);

        CredentialRepresentation credential =
                new CredentialRepresentation();

        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        account.setCredentials(List.of(credential));

        String id;

        try (Response response =
                     keycloak.realm(realm).users().create(account)) {

            if (response.getStatus() == 409) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Username or email already exists"
                );
            }

            if (response.getStatus() != 201) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Keycloak could not create the account"
                );
            }

            id = CreatedResponseUtil.getCreatedId(response);
        }

        try {
            var role = keycloak.realm(realm)
                    .roles()
                    .get(roleName)
                    .toRepresentation();

            keycloak.realm(realm)
                    .users()
                    .get(id)
                    .roles()
                    .realmLevel()
                    .add(List.of(role));

            return id;
        } catch (RuntimeException error) {
            try {
                removeCreatedAccount(id);
            } catch (RuntimeException cleanupError) {
                error.addSuppressed(cleanupError);
            }

            throw error;
        }
    }

    public void update(User user) {
        UserRepresentation account = new UserRepresentation();
        account.setFirstName(user.getFirstName());
        account.setLastName(user.getLastName());
        account.setEmail(user.getEmail());

        keycloak.realm(realm)
                .users()
                .get(user.getKeycloakSubject())
                .update(account);
    }

    public void disable(String subject) {
        var resource = keycloak.realm(realm).users().get(subject);
        UserRepresentation account = new UserRepresentation();

        account.setEnabled(false);

        resource.update(account);
        resource.logout();
    }

    public void removeCreatedAccount(String subject) {
        keycloak.realm(realm).users().get(subject).remove();
    }
}