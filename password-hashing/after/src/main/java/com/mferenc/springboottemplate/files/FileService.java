package com.mferenc.springboottemplate.files;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileService {
    private final Path uploadDir = Paths.get("uploads")
            .toAbsolutePath()
            .normalize();

    public Resource loadFile(String filename) {
        try {
            Path filePath = uploadDir.resolve(filename).normalize();

            if (!filePath.startsWith(uploadDir)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
            }

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new FileErrorException("File not found or not readable");
            }
        } catch (MalformedURLException e) {
            throw new FileErrorException("Invalid file path");
        }
    }

    public void saveFile(MultipartFile file, String dir) {
        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            String fileName = file.getOriginalFilename();
            if (fileName == null) {
                return;
            }
            Path directory = uploadDir.resolve(String.valueOf(dir));
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            Path targetPath = directory.resolve(file.getOriginalFilename()).normalize();
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileErrorException("Błąd podczas zapisywania pliku");
        }
    }
}


class FileErrorException extends RuntimeException {
    public FileErrorException(String message) {
        super(message);
    }
}
