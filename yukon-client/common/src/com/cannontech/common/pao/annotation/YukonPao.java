package com.cannontech.common.pao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.model.CompleteCapControlArea;
import com.cannontech.common.pao.model.CompleteYukonPao;
import com.cannontech.common.pao.service.PaoPersistenceService;

/**
 * This annotation is used for specifying which model objects in the PaoPersistenceDao/Service 
 * structure are used to represent actual pao types.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface YukonPao {
    String AUTO_DETECT = ":auto";
    String UNSPECIFIED = ":unspecified";

    /**
     * Returns whether or not the annotated model object has a table representing its data. Defaults to true.
     * @return true if the annotated model object has a backing table, false otherwise.
     */
    boolean tableBacked() default true;
    
    /**
     * Get the name of the table that backs the annotated model object if it is table backed. 
     * If the object is table backed but a table name isn't specified in the annotation, this 
     * method returns {@link YukonPao#AUTO_DETECT}. In such a case, the {@link PaoPersistenceService}
     * will automatically use the name of the annotated class minus the "Complete" prefix.
     * 
     * <p>Examples of both cases:</p>
     * 
     * <p>{@link CompleteCapControlArea} is table backed and has no specified table name in its annotation.
     * As a result, this method will return {@link YukonPao#AUTO_DETECT} when called, and the 
     * {@link PaoPersistenceService} will determine its table to be "CapControlArea" because of the removal
     * of "Complete" from the class's name.</p>
     * <p>{@link CompleteYukonPao} is table backed and has a specified table name in its annotation. As a 
     * result, this method will return that string and the {@link PaoPersistenceService} will use that 
     * its table name in queries.</p>
     * @return the name of the table the annotated model object.
     */
    String tableName() default AUTO_DETECT;
    
    /**
     * Get the name of the column that is used as the identifier for the table that backs the annotated
     * model object. If the object is table backed but identifier column name isn't specified in the 
     * annotation, this method returns {@link YukonPao#UNSPECIFIED}. In such a case, the {@link PaoPersistenceService}
     * will throw an exception on startup, as a table-backed class cannot have no identifier column specified.
     * @return the name of the identifier column for the table that backs the class.
     */
    String idColumnName() default UNSPECIFIED;
    
    /**
     * Get the pao types that are represented by the annotated class.
     * @return an array of the pao types that are represented by the annotated class.
     */
    PaoType[] paoTypes() default {};
}
