package org.winey.server.external.client.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.winey.server.exception.Error;
import org.winey.server.exception.model.BadRequestException;
import org.winey.server.exception.model.NotFoundException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @PostConstruct
    public AmazonS3Client amazonS3Client() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }

    public String uploadImage(MultipartFile multipartFile, String folder) {
        String fileName = createFileName(multipartFile.getOriginalFilename());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        try(InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket+"/"+ folder + "/image", fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            return amazonS3.getUrl(bucket+"/"+ folder + "/image", fileName).toString();
        } catch(IOException e) {
            throw new NotFoundException(Error.NOT_FOUND_IMAGE_EXCEPTION, Error.NOT_FOUND_IMAGE_EXCEPTION.getMessage());
        }
    }

    public List<String> uploadImages(List<MultipartFile> multipartFileList, String folder) {
        // 스스로 실습해봅시다 ㅋㅋ
        ObjectMetadata objectMetadata = new ObjectMetadata();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < multipartFileList.size(); i++) {
            String fileName = createFileName(multipartFileList.get(i).getOriginalFilename());
            objectMetadata.setContentLength(multipartFileList.get(i).getSize());
            objectMetadata.setContentType(multipartFileList.get(i).getContentType());

            try(InputStream inputStream = multipartFileList.get(i).getInputStream()) {
                amazonS3.putObject(new PutObjectRequest(bucket+"/"+ folder + "/image", fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
                list.add(amazonS3.getUrl(bucket+"/"+ folder + "/image", fileName).toString());
            } catch (IOException e) {
                throw new NotFoundException(Error.NOT_FOUND_IMAGE_EXCEPTION, Error.NOT_FOUND_IMAGE_EXCEPTION.getMessage());
            }
        }
        return list;
    }

    // 파일명 (중복 방지)
    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    // 파일 유효성 검사
    private String getFileExtension(String fileName) {
        if (fileName.length() == 0) {
            throw new NotFoundException(Error.NOT_FOUND_IMAGE_EXCEPTION, Error.NOT_FOUND_IMAGE_EXCEPTION.getMessage());
        }
        ArrayList<String> fileValidate = new ArrayList<>();
        fileValidate.add(".jpg");
        fileValidate.add(".jpeg");
        fileValidate.add(".png");
        fileValidate.add(".JPG");
        fileValidate.add(".JPEG");
        fileValidate.add(".PNG");
        String idxFileName = fileName.substring(fileName.lastIndexOf("."));
        if (!fileValidate.contains(idxFileName)) {
            throw new BadRequestException(Error.NOT_FOUND_IMAGE_EXCEPTION, Error.NOT_FOUND_IMAGE_EXCEPTION.getMessage());
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    // 이미지 삭제
    public void deleteFile(String imageUrl) {
        String imageKey = imageUrl.substring(49);
        amazonS3.deleteObject(bucket, imageKey);
    }
}
