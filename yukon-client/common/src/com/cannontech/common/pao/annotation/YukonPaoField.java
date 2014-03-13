package com.cannontech.common.pao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.cannontech.common.pao.service.PaoPersistenceService;

/**
 * This annotation is used to identify database column fields within a model object which are 
 * annotated by either {@link YukonPao} or {@link YukonPaoPart}. This annotation should be placed on pertinent 
 * getter methods, and must never be placed on both the getter AND the setter for the same property descriptor.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface YukonPaoField {
    
    /**
     * Returns the name of the column that the method represents. If the column name is absent in the 
     * annotation, this will return {@link YukonPao#AUTO_DETECT}, in which case the
     * {@link PaoPersistenceService} will automatically use the name of the property descriptor for the
     * method as the name of the column.
     * @return the name of the database column the annotated method represents.
     */
    String columnName() default YukonPao.AUTO_DETECT;
}
