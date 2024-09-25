package com.munecting.api.global.aop.aspect;

import com.munecting.api.global.util.CustomSpringELParser;
import com.munecting.api.global.aop.annotation.DistributedLock;
import com.munecting.api.global.error.exception.DistributedLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;

import static com.munecting.api.global.common.dto.response.Status.DISTRIBUTED_LOCK_ACQUISITION_FAILURE;
import static com.munecting.api.global.common.dto.response.Status.DISTRIBUTED_LOCK_TEMP_ERROR;

/**
 * @DistributedLock 선언 시 수행되는 Aop class
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAop {

    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(com.munecting.api.global.aop.annotation.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);
        String key = createKeyFrom(joinPoint, signature, distributedLock);
        RLock rLock = redissonClient.getLock(key);
        try {
            checkAvailableLock(distributedLock, rLock, key, method);
            return aopForTransaction.proceed(joinPoint);
        } finally {
            releaseLockSafely(method, key, rLock);
        }
    }

    private String createKeyFrom(ProceedingJoinPoint joinPoint, MethodSignature signature, DistributedLock distributedLock) {
        return REDISSON_LOCK_PREFIX + CustomSpringELParser.getDynamicValue(
                signature.getParameterNames(), joinPoint.getArgs(), distributedLock.key());
    }

    private void checkAvailableLock(DistributedLock distributedLock, RLock rLock, String key, Method method) {
        try {
            boolean available = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
            if (!available) {
                logAcquireLockFailure(method, key);
                throw new DistributedLockException(DISTRIBUTED_LOCK_ACQUISITION_FAILURE);
            }
        } catch (InterruptedException e) {
            logThreadInterruption(method, key, e);
            throw new DistributedLockException(DISTRIBUTED_LOCK_TEMP_ERROR);
        }
    }

    private void logAcquireLockFailure(Method method, String key) {
        log.warn("Failed to acquire lock for method: {} with key: {}", method.getName(), key);
    }

    private void logThreadInterruption(Method method, String key, InterruptedException e) {
        log.warn("Thread interrupted while attempting to acquire lock for method: {} with key: {}", method.getName(), key, e);
    }

    private void releaseLockSafely(Method method, String key, RLock rLock) {
        try {
            releaseLock(method, key, rLock);
        } catch (IllegalMonitorStateException e) {
            logReleaseLockFailure(method, key, e);
        }
    }

    private void releaseLock(Method method, String key, RLock rLock) {
        if (rLock.isHeldByCurrentThread()) {
            rLock.unlock();
            log.info("Lock released for method: {} with key: {}", method.getName(), key);
        }
    }

    private void logReleaseLockFailure(Method method, String key, IllegalMonitorStateException e) {
        log.warn("Failed to release lock for method: {} with key: {}. Lock may have already been released.", method.getName(), key, e);
    }
}