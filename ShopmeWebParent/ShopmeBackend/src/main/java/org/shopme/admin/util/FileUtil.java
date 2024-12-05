package org.shopme.admin.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Slf4j
public class FileUtil {
    private FileUtil() {
    }

    public static String uploadFile(MultipartFile file, String lastPart) {

        try {

            // Create file path
            File baseFolder = new File("F:\\ShopmeProject\\image", lastPart);
            if (!baseFolder.exists()) {
                boolean mkdirs = baseFolder.mkdirs();
                if (mkdirs) {
                    log.info("{} is created!", baseFolder.getAbsolutePath());
                }
            }

            var nameArr = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");
            var extension = nameArr[nameArr.length - 1];
            var fileName = UUID.randomUUID() + "." + extension;
            var filePath = new File(baseFolder, fileName);

            // Upload
            Files.copy(file.getInputStream(), filePath.toPath(), StandardCopyOption.REPLACE_EXISTING);
            log.info("File :: {} is created.", fileName);
            return fileName;
        } catch (IOException e) {
            log.error("FileUtil.uploadFile :: {}", e.getMessage());
            return "";
        }
    }

    public static void deleteFile(String lastPart, String fileName) {
        try {
            if (!StringUtils.hasText(lastPart) || !StringUtils.hasText(fileName)) {
                return;
            }

            Path basePath = Paths.get("F:\\ShopmeProject\\image");
            File baseFolder = basePath.resolve(lastPart).normalize().toFile();
            var filePath = new File(baseFolder, fileName);

            if (Files.exists(filePath.toPath())) {
                Files.delete(filePath.toPath());
                log.info("File :: {} is deleted.", fileName);
            }
        } catch (IOException e) {
            log.error("FileUtil.deleteFile :: {}", e.getMessage());
        }
    }
}
