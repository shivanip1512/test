package com.cannontech.web.api;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import javax.validation.Valid;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.clientutils.YukonLogManager;

public class ApiMethodSignatureTest {
    private Logger log = YukonLogManager.getLogger(ApiMethodSignatureTest.class);

    /*
     * According to best practice defined in https://confluence-prod.tcc.etn.com/display/EEST/API+Development+Design+Practices#APIDevelopmentDesignPractices MethodSignature, 
       path variable annotation should be before valid annotation. If not then the test case will fail. 
       This is done so that if path variables have any issues (like String passed instead of Integer) then this will be handled by spring
       and validators will not be called. Ref - YUK-23141
     */
    @Test
    public void testValidMethodSignature() {
        Reflections reflections = new Reflections("com.cannontech.web");
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(RestController.class);
        for (Class aClass : classes) {
            try {
                for (final Method method : aClass.getDeclaredMethods()) {
                    Annotation[][] arrayAnnotations = method.getParameterAnnotations();
                    boolean validAnnotationFound = false;
                    for (Annotation[] annotationRow : arrayAnnotations) {
                        for (Annotation annotation : annotationRow) {
                            if (!validAnnotationFound) {
                                validAnnotationFound = (annotation.annotationType() == Valid.class);
                            }
                            if (validAnnotationFound) {
                                boolean pathVariableFound = (annotation.annotationType() == PathVariable.class);
                                if (pathVariableFound) {
                                    log.error("Path variable must be first parameter: " + aClass.getName() + " Method " + method);
                                    assertFalse(pathVariableFound, "Path variable must be first parameter");
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
