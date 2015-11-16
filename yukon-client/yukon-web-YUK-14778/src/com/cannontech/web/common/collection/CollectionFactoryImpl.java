package com.cannontech.web.common.collection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


public class CollectionFactoryImpl<E extends Enum<E>, T> implements CollectionFactory<T> {
    
    private Class<E> enumType;
    
    public CollectionFactoryImpl(Class<E> enumType) {
        super();
        this.enumType = enumType;
    }
    
    private Map<E, CollectionProducer<E, T>> collectionProducerMap = new HashMap<E, CollectionProducer<E, T>>();
    
    public void setCollectionProducerList(List<CollectionProducer<E, T>> producers) {
        
        for (CollectionProducer<E, T> producer : producers) {
            collectionProducerMap.put(producer.getSupportedType(), producer);
        }
    }
    
    public T createCollection(HttpServletRequest req) throws CollectionCreationException {
        
        String type = req.getParameter("collectionType");
        
        E collectionType = Enum.valueOf(enumType, type);
        
        if (collectionProducerMap.containsKey(collectionType)) {
            CollectionProducer<E, T> producer = collectionProducerMap.get(collectionType);
            return producer.createCollection(req);
        }
        
        throw new CollectionCreationException("collectionType: " + type + " is not supported.");
    }
}