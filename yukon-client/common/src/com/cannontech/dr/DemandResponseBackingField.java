package com.cannontech.dr;

import java.util.Comparator;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.user.YukonUserContext;

/**
 * Interface for demand response fields 
 * @param <T>
 */
public interface DemandResponseBackingField<T> {
    
    public String getFieldName();

    /**
     * Method to get this field's value from the object passed in
     * @param object - Object to get field value for
     * @param userContext - Current userContext
     * @return Value of this field
     */
    public Object getValue(T object, YukonUserContext userContext);

    
    /** 
     * This is implemented by ProgramBackingFieldBase to pass along the scenario ID
     * idBits is a raw string list where the pattern in each item is either 1234/TAG or 1234/TAG/1234
     *\/delimited. The first group is the program or pao id. The second is the identifier specific to the dao
     *retrieval ie. PROGRAM or SCENARIO. The third group is a conditional group in the event the identifier specifies
     *a dao that requires two fields in order to retrieve a result. 
     */
    default public Object getValue(T object, int objectIdentifier, YukonUserContext userContext)  {
        return getValue(object, userContext);
    }
    
    /**
     * Method to get a DisplayablePao Comparator for this field 
     * @param userContext - Current userContext
     * @return Comparator for this field
     */
    public Comparator<DisplayablePao> getSorter(YukonUserContext userContext);
}
