package com.cannontech.common.bulk.field;

import com.cannontech.common.util.ObjectMapper;
import com.cannontech.web.input.InputSource;

/**
 * 
 * @author m_peterson
 *
 * @param <O> The type of object the identifier mapper returns based on an identifier value.
 * @param <T> Type of value that the field is.
 */
public interface BulkField<T, O> {
    
   /**
    * Should this field show as one that is available to make mass changes to?
    * @return boolean
    */
   public boolean getMassChangable();
   
   public InputSource<T> getInputSource();
   
   public ObjectMapper<T, O> getIdentifierMapper();
}
