package com.cydeo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.cydeo.controller.ProjectController.*(..))")
    public void projectControllerMethodsPC() {
    }

    @Before("projectControllerMethodsPC()")
    public void beforeProjectControllerMethods(JoinPoint joinPoint) {
        log.info(
                "Method: {} \n Arguments : {}",
                joinPoint.getSignature().toShortString(),
                joinPoint.getArgs()
        );
    }

    @AfterReturning(pointcut = "projectControllerMethodsPC()", returning = "results")
    public void afterProjectControllerMethods(JoinPoint joinPoint, Object results){
        log.info(
                "Method : {} \n Arguments: {}\nResult: {}",
                joinPoint.getSignature().toShortString(),
                joinPoint.getArgs(),
                results.toString()
        );
    }


    @AfterThrowing(pointcut = "projectControllerMethodsPC()", throwing = "exception")
    public void afterProjectControllerMethods(JoinPoint joinPoint, Exception exception){
        log.info(
                "Method : {} \n Arguments: {}\nException: {}",
                joinPoint.getSignature().toShortString(),
                joinPoint.getArgs(),
                exception.getMessage()
        );
    }

    @Pointcut("within(com.cydeo.controller.TaskController)")
    public void taskControllerPC(){}

    @Around("taskControllerPC()")
    public Object aroundTaskControllerMethods(ProceedingJoinPoint proceedingJoinPoint){
        log.info(
                "Method : {} \n Arguments: {}",
                proceedingJoinPoint.getSignature().toShortString(),
                proceedingJoinPoint.getArgs()
        );

        Object result = null;
        try {
            result = proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        log.info(
                "Method : {} \n Arguments: {}\nResult: {}",
                proceedingJoinPoint.getSignature().toShortString(),
                proceedingJoinPoint.getArgs(),
                result.toString()
        );

        return result;

    }

}
