package com.smallv.stock.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class ServiceAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Around("execution(* com.smallv.stock.service..*(..))")
    public Object calcPerformanceAdvice(ProceedingJoinPoint pjp){
        logger.info("--- 성능 측정 시작 ---");

        StopWatch sw = new StopWatch();
        sw.start();
        Object result;

        try {
            result = pjp.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        sw.stop();

        logger.info("--- 성능 측정 종료 ---");
        logger.info("소요시간 : {} ms", sw.getLastTaskTimeMillis());
        return result;
    }
}
