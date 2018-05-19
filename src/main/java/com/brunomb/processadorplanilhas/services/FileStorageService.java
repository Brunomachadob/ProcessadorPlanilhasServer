package com.brunomb.processadorplanilhas.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import com.brunomb.processadorplanilhas.exception.FileStorageException;
import com.brunomb.processadorplanilhas.exception.MyFileNotFoundException;

@Service
public class FileStorageService {

	private final Path fileStorageLocation;

	@Autowired
	public FileStorageService() {
		try {
			this.fileStorageLocation = Files.createTempDirectory("uploads").toAbsolutePath().normalize();
		} catch (Exception ex) {
			throw new FileStorageException("Could not create the directory where the uploaded files will be stored.",
					ex);
		}
	}

	public String storeFile(String originalFileName, byte[] bytes) {
		try {
			
			Path tempFile = Files.createTempFile(fileStorageLocation, originalFileName.replace(".xlsx", "") + "_", ".xlsx");

			Files.copy(new ByteArrayInputStream(bytes), tempFile, StandardCopyOption.REPLACE_EXISTING);

			return tempFile.toFile().getName();
		} catch (IOException ex) {
			throw new FileStorageException("Could not store file. Please try again!", ex);
		}
	}

	public Resource loadFileAsResource(String fileName) {
		try {
			Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			
			if (resource.exists()) {
				return resource;
			} else {
				throw new MyFileNotFoundException("File not found " + fileName);
			}
		} catch (MalformedURLException ex) {
			throw new MyFileNotFoundException("File not found " + fileName, ex);
		}
	}
}