package com.multipart.test.domain.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
	String uploadImage(MultipartFile image) throws Exception;
	
	String uploadVideo (MultipartFile video) throws Exception;
}
