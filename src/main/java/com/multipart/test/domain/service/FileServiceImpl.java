package com.multipart.test.domain.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;
import com.multipart.test.global.util.ImageProcess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
	
	private final AmazonS3Client amazonS3Client;
	
	private final ImageProcess imageProcess;
	
	@Value("${S3_BUCKET}")
	private String S3Bucket;
	
	@Override
	public String uploadImage(MultipartFile image) throws ImageProcessingException, IOException, MetadataException {
		long startTime = System.currentTimeMillis();
		
//		MultipartFile resizedImage = imageProcess.resizeImage(image, 620);
//		String originalName = image.getOriginalFilename(); // 파일 이름
//		System.out.println("파일 이름 : " + originalName);
		String uuid = UUID.randomUUID().toString();
//		long size = resizedImage.getSize(); // 파일 크기
		long size = image.getSize(); // 파일 크기
		System.out.println("UUID 발급 : " + uuid);
		
		log.info("AWS 오브젝트 메타데이터 설정 중..");
		ObjectMetadata objectMetaData = new ObjectMetadata();
//		objectMetaData.setContentType(resizedImage.getContentType());
		objectMetaData.setContentType(image.getContentType());
		objectMetaData.setContentLength(size);
//		log.info("content type : " + resizedImage.getContentType());
		log.info("content type : " + image.getContentType());
		log.info("size : " + size);
		log.info("AWS 오브젝트 메타데이터 설정 완료");
		
		// S3에 업로드
		amazonS3Client.putObject(
				new PutObjectRequest(S3Bucket, uuid, image.getInputStream(), objectMetaData)
						.withCannedAcl(CannedAccessControlList.PublicRead)
		);
		log.info("업로드 완료");
		
		String path = amazonS3Client.getUrl(S3Bucket, uuid).toString(); // 접근가능한 URL 가져오기
		log.info("URL 발급 완료");
		long endTime = System.currentTimeMillis();
		System.out.println("업로드 시간 : " + (endTime - startTime));
		return path;
	}
	
	@Override
	public String uploadVideo (MultipartFile video) throws Exception {
		TransferManager tm = TransferManagerBuilder.standard()
				.withS3Client(amazonS3Client)
				.build();
		
		// TransferManager processes all transfers asynchronously,
		// so this call returns immediately.
		String key = "will/video/" + UUID.randomUUID();
		File file = convert(video).orElseThrow(() -> new IOException("파일 변환 실패"));
		Upload upload = tm.upload(S3Bucket, key, file);
		log.info("Object upload started");
		
		// Optionally, wait for the upload to finish before continuing.
		upload.waitForCompletion();
		log.info("Object upload complete");
		
		String path = amazonS3Client.getUrl(S3Bucket, key).toString(); // 접근가능한 URL 가져오기
		log.info("URL 발급 완료 : " + path);
		
		if (file.delete()) log.info("File delete success");
		else log.warn("File delete fail");
		
		return path;
	}
	
	// 로컬에 파일 업로드 하기
	private Optional<File> convert(MultipartFile file) throws IOException {
		File convertFile = new File(file.getOriginalFilename());
		if (convertFile.createNewFile()) { // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
			try (FileOutputStream fos = new FileOutputStream(convertFile)) { // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
				fos.write(file.getBytes());
			}
			return Optional.of(convertFile);
		}
		return Optional.empty();
	}
	
}
