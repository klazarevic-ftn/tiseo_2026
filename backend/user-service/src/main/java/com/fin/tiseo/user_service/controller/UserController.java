package com.fin.tiseo.user_service.controller;

import com.fin.tiseo.user_service.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping("/me")
    public UserService.Profile me(@AuthenticationPrincipal Jwt jwt) {
        return service.currentProfile(jwt.getSubject());
    }

    @PutMapping("/me")
    public UserService.Profile updateContact(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ContactUpdate request
    ) {
        return service.updateCurrentContact(
                jwt.getSubject(),
                request.email()
        );
    }

    @GetMapping
    public List<UserService.Profile> search(@RequestParam String search) {
        return service.findProfiles(search);
    }

    @GetMapping("/{id}")
    public UserService.Profile findById(@PathVariable Long id) {
        return service.profile(id);
    }

    @PostMapping
    public ResponseEntity<UserService.Profile> create(
            @Valid @RequestBody UserService.CreateRequest request
    ) {
        UserService.Profile profile = service.createProfile(request);

        return ResponseEntity
                .created(URI.create("/api/users/" + profile.user().getId()))
                .body(profile);
    }

    @PutMapping("/{id}")
    public UserService.Profile update(
            @PathVariable Long id,
            @Valid @RequestBody UserService.UpdateRequest request
    ) {
        return service.updateProfile(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteProfile(id);
        return ResponseEntity.noContent().build();
    }

    public record ContactUpdate(
            @NotBlank @Email String email
    ) {
    }
}