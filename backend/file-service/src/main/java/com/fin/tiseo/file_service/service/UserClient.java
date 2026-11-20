package com.fin.tiseo.file_service.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class UserClient {

    private final RestClient client;

    public UserClient(
            RestClient.Builder builder,
            @Value("${services.user.url}") String url
    ) {
        client = builder.baseUrl(url).build();
    }

    public Access currentUser(Jwt jwt) {
        Profile profile = read("/api/users/me", jwt);

        return new Access(
                profile.user().id(),
                hasRole(jwt, "STUDENT"),
                hasRole(jwt, "ADMINISTRATOR")
        );
    }

    public Access user(Long userId, Jwt jwt) {
        Profile profile = read("/api/users/" + userId, jwt);

        return new Access(
                profile.user().id(),
                profile.students() != null && !profile.students().isEmpty(),
                profile.administrator() != null
        );
    }

    private Profile read(String path, Jwt jwt) {
        RestClient.RequestHeadersSpec<?> request =
                client.get().uri(path);

        request.headers(headers ->
                headers.setBearerAuth(jwt.getTokenValue()));

        return request.retrieve().body(Profile.class);
    }

    private boolean hasRole(Jwt jwt, String role) {
        Map<String, Object> realmAccess =
                jwt.getClaimAsMap("realm_access");

        return realmAccess != null
                && realmAccess.get("roles") instanceof List<?> roles
                && roles.contains(role);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Profile(
            UserData user,
            List<StudentData> students,
            AdministratorData administrator
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record UserData(Long id) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record StudentData(Long id) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AdministratorData(Long id) {
    }

    public record Access(
            Long userId,
            boolean student,
            boolean administrator
    ) {
    }
}