package com.multipart.test.domain.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
	String upload(MultipartFile image) throws Exception;
}
