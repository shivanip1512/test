package com.cannontech.web.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/**
 * Ignores CSRF token checking. This is useful for our swing clients which do not have access to updated csrf tokens
 * 
 * For new controllers (@Controller annotated) this is only checked on the method level and is ignored
 * if applied to the type. For older controllers and request handlers this will only be checked if applied to the
 * type.
 * 
 * See WebSecurityInterceptor.
 */
public @interface IgnoreCsrfCheck {/*empty*/}
