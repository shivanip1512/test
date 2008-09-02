package com.cannontech.stars.core.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.util.StarsUtils;

@Aspect
public class AccountCheckerServiceAdvice {
    
    @Around("bean(accountCheckerService)")
    public Object doOperatorCheck(ProceedingJoinPoint pjp) throws Throwable {
        LiteYukonUser user = getYukonUser(pjp);
        
        boolean isOperator = StarsUtils.isOperator(user);
        if (!isOperator) {
            pjp.proceed();
        }
        
        return null;
    }
    
    private LiteYukonUser getYukonUser(ProceedingJoinPoint pjp) {
        Object[] args = pjp.getArgs();
        LiteYukonUser user = (LiteYukonUser) args[0];
        return user;
    }
    
}
