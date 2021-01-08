package com.cannontech.common.bulk.processor;

import com.cannontech.common.bulk.collection.device.model.CollectionActionDetail;

/**
 * Empty interface used to group exceptions that are thrown within a Processor
 * and added to a callback's exception list.
 * @author m_peterson
 *
 */
public interface ProcessorCallbackException {    
        
    public default CollectionActionDetail getDetail() {
        return CollectionActionDetail.FAILURE;
    }
    
    public String getMessage();
}
