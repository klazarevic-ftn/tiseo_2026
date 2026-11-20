package com.fin.tiseo.file_service.service;

import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final S3Client client;

    @Value("${storage.bucket}")
    private String bucket;

    public void upload(String key, MultipartFile file) {
        String originalName = URLEncoder.encode(
                file.getOriginalFilename(),
                StandardCharsets.UTF_8
        );

        try (InputStream input = file.getInputStream()) {
            client.putObject(
                    request -> request
                            .bucket(bucket)
                            .key(key)
                            .metadata(Map.of(
                                    "original-filename", originalName
                            ))
                            .contentType(
                                    file.getContentType() != null
                                            ? file.getContentType()
                                            : "application/octet-stream"
                            ),
                    RequestBody.fromInputStream(input, file.getSize())
            );
        } catch (IOException | SdkException exception) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Document could not be uploaded",
                    exception
            );
        }
    }

    public byte[] download(String key) {
        try {
            return client.getObjectAsBytes(
                    request -> request.bucket(bucket).key(key)
            ).asByteArray();
        } catch (SdkException exception) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Document could not be downloaded",
                    exception
            );
        }
    }
}