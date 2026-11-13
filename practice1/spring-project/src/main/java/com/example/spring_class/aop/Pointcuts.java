package com.example.spring_class.aop;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {
    @Pointcut("execution(* com.example.spring_class..service..*.*(..))")
    public void serviceLayer(){}
}
