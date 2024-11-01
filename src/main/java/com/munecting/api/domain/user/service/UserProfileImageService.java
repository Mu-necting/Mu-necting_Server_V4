package com.munecting.api.domain.user.service;

import com.munecting.api.domain.user.entity.User;
import com.munecting.api.domain.user.entity.Uuid;
import com.munecting.api.global.aws.s3.AmazonS3Manager;
import com.munecting.api.global.error.exception.InvalidValueException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

import static com.munecting.api.global.common.dto.response.Status.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class UserProfileImageService {

    private final UuidService uuidService;
    private final AmazonS3Manager s3Manager;

    private static final long MAX_FILE_SIZE = 3 * 1024 * 1024; // 3MB
    private static final String OVER_FILE_SIZE_ERROR_MESSAGE = "업로드 가능한 파일 사이즈는 최대 3MB입니다.";

    public String updateImage(User user, MultipartFile imgFile) {
        if (Objects.isNull(imgFile)) {
            return getImageUrl(user);
        }

        if (user.getProfileImageUrl() != null) {
            deleteOldImage(user);
        }

        String imageUrl = uploadNewImage(user.getId(), imgFile);
        return user.updateProfileImageUrl(imageUrl);
    }

    private String getImageUrl(User user) {
        if (user.getProfileImageUrl() == null) {
            // 프론트엔드 측 기본 이미지 사용
            return null;
        }

        return user.getProfileImageUrl();
    }

    private void deleteOldImage(User user) {
        Uuid uuid = uuidService.findByUserId(user.getId());
        String presentKeyName = s3Manager.generateProfileImageKeyName(uuid);
        s3Manager.deleteFile(presentKeyName);
        uuidService.delete(uuid);
    }

    private String uploadNewImage(Long userId, MultipartFile image) {
        validateImageSize(image);
        Uuid uuid = uuidService.createUuid(userId);
        String keyName = s3Manager.generateProfileImageKeyName(uuid);

        return s3Manager.uploadFile(keyName, image);
    }

    private void validateImageSize(MultipartFile image) {
        if (image.getSize() > MAX_FILE_SIZE) {
            throw new InvalidValueException(BAD_REQUEST, OVER_FILE_SIZE_ERROR_MESSAGE);
        }
    }
}
