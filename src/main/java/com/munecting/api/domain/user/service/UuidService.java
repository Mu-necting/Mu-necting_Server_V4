package com.munecting.api.domain.user.service;

import com.munecting.api.domain.user.entity.Uuid;
import com.munecting.api.domain.user.dao.UuidRepository;
import com.munecting.api.global.error.exception.InternalServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UuidService {

    private final UuidRepository uuidRepository;

    public Uuid createUuid(Long userId) {
        Uuid uuid = Uuid.toEntity(userId);

        return uuidRepository.save(uuid);
    }

    public Uuid findByUserId(Long userId) {
        return uuidRepository.findByUserId(userId).orElseThrow(()-> {
            log.warn("userId {}과 매핑된 UUID 객체가 존재하지 않습니다.",userId);
            throw new InternalServerException();
        });
    }

    public void delete(Uuid uuid) {
        uuidRepository.delete(uuid);
    }
}
