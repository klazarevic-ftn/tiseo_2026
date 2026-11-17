package com.fin.tiseo.file_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

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
        return read("/api/users/me/access", jwt);
    }

    public Access user(Long userId, Jwt jwt) {
        return read("/api/users/" + userId + "/access", jwt);
    }

    private Access read(String path, Jwt jwt) {
        try {
            RestClient.RequestHeadersSpec<?> request = client.get().uri(path);

            request.header(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + jwt.getTokenValue()
            );

            Access access = request.retrieve().body(Access.class);

            if (access == null || access.userId() == null) {
                throw new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "Invalid response from User service"
                );
            }

            return access;
        } catch (RestClientResponseException exception) {
            int status = exception.getStatusCode().value();

            if (status == 401 || status == 403 || status == 404) {
                throw new ResponseStatusException(
                        exception.getStatusCode(),
                        "User access denied or user not found"
                );
            }

            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "User service is unavailable"
            );
        } catch (RestClientException exception) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "User service is unavailable"
            );
        }
    }

    public record Access(
            Long userId,
            boolean student,
            boolean administrator
    ) {
    }
}