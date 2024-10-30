package com.munecting.api.global.aws.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.munecting.api.domain.user.entity.Uuid;
import com.munecting.api.global.config.AmazonConfig;
import com.munecting.api.global.error.exception.InternalServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.munecting.api.global.common.dto.response.Status.AWS_S3_UPLOAD_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager {

    private final AmazonS3 amazonS3;

    private final AmazonConfig amazonConfig;

    public String generateProfileImageKeyName(Uuid uuid) {
        return amazonConfig.getProfileImagePath() + '/' + uuid.getUuid();
    }

    public String uploadFile(String keyName, MultipartFile file){
        upload(keyName, file);
        return getFileUrl(keyName);
    }

    private void upload(String keyName, MultipartFile file) {
        try {
            ObjectMetadata metadata = createMetadata(file);
            PutObjectRequest request = createPutObjectRequest(keyName, file, metadata);
            amazonS3.putObject(request);

        } catch (IOException e) {
            log.error("error at AmazonS3Manager uploadFile : {}", (Object) e.getStackTrace());
            throw new InternalServerException(AWS_S3_UPLOAD_ERROR);
        }
    }

    private ObjectMetadata createMetadata(MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType("image/jpeg");
        return metadata;
    }

    private PutObjectRequest createPutObjectRequest(String keyName, MultipartFile file, ObjectMetadata metadata) throws IOException {
        return new PutObjectRequest(amazonConfig.getBucket(), keyName, file.getInputStream(), metadata);
    }

    private String getFileUrl(String keyName) {
        return amazonS3.getUrl(amazonConfig.getBucket(), keyName).toString();
    }

    public void deleteFile(String keyName) {
        amazonS3.deleteObject(amazonConfig.getBucket(), keyName);
    }
}
