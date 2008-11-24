package com.cannontech.yukon.api.util;

import org.jdom.input.DOMBuilder;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.util.ObjectMapper;

public class NodeToElementMapperWrapper<E> implements ObjectMapper<Node, E> {

    private ObjectMapper<org.jdom.Element, E> delegate;
    
    public NodeToElementMapperWrapper(ObjectMapper<org.jdom.Element, E> delegate) {
        super();
        this.delegate = delegate;
    }

    @Override
    public E map(Node from) throws ObjectMappingException {
        DOMBuilder domBuilder = new DOMBuilder();
        Element w3cElement = (Element)from;
        org.jdom.Element jdomElement = domBuilder.build(w3cElement);
        return delegate.map(jdomElement);
    }

}
