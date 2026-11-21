package com.example.spring_class.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.slf4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Order(0)
public class ExecutionTimeAspect {

    private static final Logger log = LoggerFactory.getLogger("timing");
    @Around("com.example.spring_class.aop.Pointcuts.serviceLayer()")
    public Object measure(ProceedingJoinPoint pjp) throws Throwable{
        String method = pjp.getSignature().toShortString();
        log.debug("{} args = {}", method, Arrays.toString(pjp.getArgs()));

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
    @AfterThrowing(
            pointcut = "com.example.spring_class.aop.Pointcuts.serviceLayer()",
            throwing= "ex"
    )
    public void onError(JoinPoint jp, Throwable ex){
        log.warn("x {} threw {}", jp.getSignature().toShortString(), ex.getClass().getSimpleName());
    }
}
