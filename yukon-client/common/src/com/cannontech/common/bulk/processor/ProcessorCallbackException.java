package com.cannontech.common.bulk.processor;

/**
 * Empty interface used to group exceptions that are thrown within a Processor
 * and added to a callback's exception list.
 * @author m_peterson
 *
 */
public interface ProcessorCallbackException {
    public String getMessage();
}
