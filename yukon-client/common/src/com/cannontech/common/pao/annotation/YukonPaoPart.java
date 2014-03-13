package com.cannontech.common.pao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.cannontech.common.pao.service.PaoPersistenceService;

/**
 * This annotation is used for representing classes that comprise various classes annotated by the 
 * {@link YukonPao} annotation. Classes with this annotation don't represent "complete" paos, but represent
 * a table that is used as a part of a representation of a complete pao. Where classes with the {@link YukonPao}
 * annotated are designed with an inheritance structure, classes with this annotation are always stand-alone 
 * objects that are used via composition within {@link YukonPao} annotated classes to complete the representation
 * of classes that represent PAO Types.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface YukonPaoPart {
    
    /**
     * Get the name of the table that backs the annotated model object. If a table name isn't specified in 
     * the annotation, this method returns {@link YukonPao#AUTO_DETECT}. In such a case, the 
     * {@link PaoPersistenceService} will automatically use the name of the annotated class minus 
     * the "Complete" prefix.
     */
    String tableName() default YukonPao.AUTO_DETECT;
    
    /**
     * Get the name of the column that is used as the identifier for the table that backs the annotated
     * model object. If the identifier column name isn't specified in the annotation, this method returns 
     * {@link YukonPao#UNSPECIFIED}. In such a case, the {@link PaoPersistenceService}
     * will throw an exception on startup, as a table-backed class cannot have no identifier column specified.
     * @return the name of the identifier column for the table that backs the class.
     */
    String idColumnName() default YukonPao.UNSPECIFIED;
}
