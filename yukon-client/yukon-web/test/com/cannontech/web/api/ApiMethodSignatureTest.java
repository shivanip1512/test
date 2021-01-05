package com.cannontech.web.api;

import static org.junit.Assert.assertFalse;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import javax.validation.Valid;

import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.reflections.Reflections;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.clientutils.YukonLogManager;

public class ApiMethodSignatureTest {
	private Logger log = YukonLogManager.getLogger(ApiMethodSignatureTest.class);

	@Test
	public void testValidMethodSignature() {
		Reflections reflections = new Reflections("com.cannontech.web");
		Set<Class<?>> classes = reflections.getTypesAnnotatedWith(RestController.class);
		for (Class aClass : classes) {
			try {
				for (final Method method : aClass.getDeclaredMethods()) {
					Annotation[][] Arrayannotations = method.getParameterAnnotations();
					boolean validAnnotationFound = false;
					for (Annotation[] annotationRow : Arrayannotations) {
						for (Annotation annotation : annotationRow) {
							if (!validAnnotationFound) {
								validAnnotationFound = (annotation.annotationType() == Valid.class);
							}
							if (validAnnotationFound) {
								boolean pathVariableFound = (annotation.annotationType() == PathVariable.class);
								if(pathVariableFound) {
									log.error("Path variable must be first parameter: "+ aClass.getName() + " Method " + method);
									assertFalse("Path variable must be first parameter", pathVariableFound);
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
