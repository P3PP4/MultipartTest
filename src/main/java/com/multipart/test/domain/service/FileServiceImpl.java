package com.multipart.test.domain.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;
import com.multipart.test.global.util.ImageProcess;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
	
	private final AmazonS3Client amazonS3Client;
	
	private final ImageProcess imageProcess;
	
	@Value("${S3_BUCKET}")
	private String S3Bucket;
	
	@Override
	public String upload(MultipartFile image) throws ImageProcessingException, IOException, MetadataException {
		
		MultipartFile resizedImage = imageProcess.resizeImage(image, 620);
		String originalName = image.getOriginalFilename(); // 파일 이름
		System.out.println("파일 이름 : " + originalName);
		String uuid = UUID.randomUUID().toString();
		long size = resizedImage.getSize(); // 파일 크기
		System.out.println("UUID 발급 : " + uuid);
		
		System.out.println("AWS 오브젝트 메타데이터 설정 중..");
		ObjectMetadata objectMetaData = new ObjectMetadata();
		objectMetaData.setContentType(resizedImage.getContentType());
		objectMetaData.setContentLength(size);
		System.out.println("content type : " + resizedImage.getContentType());
		System.out.println("size : " + size);
		System.out.println("AWS 오브젝트 메타데이터 설정 완료");
		
		// S3에 업로드
		amazonS3Client.putObject(
				new PutObjectRequest(S3Bucket, uuid, resizedImage.getInputStream(), objectMetaData)
						.withCannedAcl(CannedAccessControlList.PublicRead)
		);
		System.out.println("업로드 완료");
		
		String path = amazonS3Client.getUrl(S3Bucket, uuid).toString(); // 접근가능한 URL 가져오기
		System.out.println("URL 발급 완료");
		
		return path;
	}
}
