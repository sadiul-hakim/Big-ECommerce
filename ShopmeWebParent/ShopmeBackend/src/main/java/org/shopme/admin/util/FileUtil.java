package org.shopme.admin.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtil {
	private FileUtil() {
	}

	public static File getFile(String lastPart) throws FileNotFoundException {
		return ResourceUtils.getFile("classpath:static/" + lastPart);
	}

	public static String uploadFile(MultipartFile file, String lastPart) {

		try {

			// Create file path
			File baseFolder = getFile(lastPart);
			var nameArr = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");
			var extension = nameArr[nameArr.length - 1];
			var fileName = UUID.randomUUID() + "." + extension;
			var filePath = new File(baseFolder, fileName);

			// Upload
			Files.copy(file.getInputStream(), filePath.toPath(), StandardCopyOption.REPLACE_EXISTING);
			return fileName;
		} catch (IOException e) {
			log.error("FileUtil.uploadFile :: {}", e.getMessage());
			return "";
		}
	}

	public static void deleteFile(String folderPath, String fileName) {
		try {
			var base = getFile(folderPath);
			var filePath = new File(base, fileName);

			if (Files.exists(filePath.toPath())) {
				Files.delete(filePath.toPath());
			}
		} catch (IOException e) {
			log.error("FileUtil.deleteFile :: {}", e.getMessage());
		}
	}
}
