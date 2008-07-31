package com.cannontech.web.security;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;

import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.annotation.CheckRoleProperty;


public class WebSecurityAnnotationProcessor {
    private WebSecurityChecker webSecurityChecker;

    public void process(Object bean) throws Exception {
        boolean isProxy = isProxy(bean);
        Class<?> clazz = (isProxy) ?
                AopUtils.getTargetClass(bean) : bean.getClass();
                
        boolean hasCheckRole = hasCheckRole(clazz);
        if (hasCheckRole) {
            doHasCheckRole(getCheckRole(clazz));
        }
        
        boolean hasCheckRoleProperty = hasCheckRoleProperty(clazz);
        if (hasCheckRoleProperty) {
            doHasCheckRoleProperty(getCheckRoleProperty(clazz));
        }
    }
    
    private void doHasCheckRole(CheckRole checkRole) throws Exception {
        int[] roleIds = checkRole.value();
        webSecurityChecker.checkRole(roleIds);
    }

    private void doHasCheckRoleProperty(CheckRoleProperty checkRoleProperty) throws Exception {
        int[] rolePropertyIds = checkRoleProperty.value();
        webSecurityChecker.checkRoleProperty(rolePropertyIds);
    }
    
    private CheckRole getCheckRole(Class<?> clazz) {
        CheckRole checkRole = AnnotationUtils.findAnnotation(clazz, CheckRole.class);
        return checkRole;
    }
    
    private CheckRoleProperty getCheckRoleProperty(Class<?> clazz) {
        CheckRoleProperty checkRoleProperty = AnnotationUtils.findAnnotation(clazz, CheckRoleProperty.class);
        return checkRoleProperty;
    }
    
    private boolean hasCheckRole(Class<?> clazz) {
        CheckRole checkRole = getCheckRole(clazz);
        boolean hasCheckRole = checkRole != null;
        return hasCheckRole;
    }

    private boolean hasCheckRoleProperty(Class<?> clazz) {
        CheckRoleProperty checkRoleProperty = getCheckRoleProperty(clazz);
        boolean hasCheckRoleProperty = checkRoleProperty != null;
        return hasCheckRoleProperty;
    }

    private boolean isProxy(Object bean) {
        if (AopUtils.isAopProxy(bean) || 
                AopUtils.isCglibProxy(bean) || 
                AopUtils.isJdkDynamicProxy(bean)) {
            return true;
        }
        return false;
    }
    
    @Autowired
    public void setWebSecurityChecker(WebSecurityChecker webSecurityChecker) {
        this.webSecurityChecker = webSecurityChecker;
    }

}
