package com.multipart.test.domain.controller;

import com.multipart.test.domain.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class FileController {
	
	private final FileService fileService;
	
	@PostMapping(value = "/image", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<String> uploadImage(@RequestPart MultipartFile image) throws Exception {
		
		return ResponseEntity.status(HttpStatus.CREATED).body(fileService.uploadImage(image));
		
	}
	
	@PostMapping(value = "video", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<String> uploadVideo(@RequestPart MultipartFile video) throws Exception {
		
		return ResponseEntity.status(HttpStatus.CREATED).body(fileService.uploadVideo(video));
		
	}
}
