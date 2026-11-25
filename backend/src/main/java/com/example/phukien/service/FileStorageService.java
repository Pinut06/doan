package com.example.phukien.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private static final Set<String> DUOI_HOP_LE = Set.of("jpg", "jpeg", "png", "gif", "webp");

    private final Path thuMuc = Paths.get("uploads").toAbsolutePath().normalize();

    public FileStorageService() throws IOException {
        Files.createDirectories(thuMuc);
    }

    public Path getThuMuc() {
        return thuMuc;
    }

    public String luu(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String goc = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        int cham = goc.lastIndexOf('.');
        String duoi = cham >= 0 ? goc.substring(cham + 1).toLowerCase(Locale.ROOT) : "";
        if (!DUOI_HOP_LE.contains(duoi)) {
            throw new IllegalArgumentException("Chỉ chấp nhận ảnh .jpg, .png, .gif, .webp");
        }
        String ten = UUID.randomUUID() + "." + duoi;
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, thuMuc.resolve(ten));
        }
        return ten;
    }

    public void xoa(String ten) {
        if (ten == null || ten.isBlank()) {
            return;
        }
        try {
            Path file = thuMuc.resolve(Paths.get(ten).getFileName().toString()).normalize();
            if (file.startsWith(thuMuc)) {
                Files.deleteIfExists(file);
            }
        } catch (IOException ignored) {

        }
    }
}
