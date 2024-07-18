package com.cydeo.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class PerformanceAspect {

    @Pointcut("@annotation(com.cydeo.annotation.ExecutionTime)")
    private void executionTimePC(){}

    @Around("executionTimePC()")
    public Object aroundUserControllerExecutionTimePC(ProceedingJoinPoint proceedingJoinPoint){

        Object result = null;
        Long before = System.currentTimeMillis();

        try {
            result = proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        Long after = System.currentTimeMillis();
        result = after - before;

        log.info(
                "Method: {} \n Execution Time: {}",
                proceedingJoinPoint.getSignature().toShortString(),
                result.toString()
        );


        return result;
    }


}
