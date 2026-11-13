package com.example.spring_class.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class ServiceLoggingAspect {
    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceLayer(){}

    @Around("serviceLayer()")
    public Object logAndTime(ProceedingJoinPoint pjp) throws Throwable{
        String method = pjp.getSignature().toShortString();
        log.info("{} args = {}", method, Arrays.toString(pjp.getArgs()));

        long start = System.nanoTime();
        try {
            Object result = pjp.proceed();
            long ms = (System.nanoTime() - start) / 1_000_000;
            log.info("{} returned in {} ms", method, ms);
            return result;
        } catch(Throwable ex){
            long ms = (System.nanoTime() - start) / 1_000_000;
            log.error("{} threw {} after {} ms", method, ex.getClass().getSimpleName(), ms);
            throw ex;
        }
    }
}
