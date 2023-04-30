package com.multipart.test.domain.controller;

import com.multipart.test.domain.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class FileController {
	
	private final FileService fileService;
	
	@PostMapping("/upload")
	public ResponseEntity<String> upload(@RequestPart MultipartFile image) throws Exception {
		
		return ResponseEntity.status(HttpStatus.CREATED).body(fileService.upload(image));
		
	}
}
