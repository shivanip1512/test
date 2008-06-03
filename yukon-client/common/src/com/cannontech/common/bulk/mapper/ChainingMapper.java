package com.cannontech.common.bulk.mapper;

import com.cannontech.common.util.ObjectMapper;

public class ChainingMapper<J,K> implements ObjectMapper<J, K> {
    ObjectMapper<J, K> delegate;
    
    public ChainingMapper(ObjectMapper<J,K> one) {
        delegate = one;
    }

    public <A> ChainingMapper(final ObjectMapper<J, A> one, final ObjectMapper<A, K> two) {
        delegate = new ObjectMapper<J, K>() {
            @Override
            public K map(J from) throws ObjectMappingException {
                return two.map(one.map(from));
            }
        };
    }
    
    public <A,B> ChainingMapper(final ObjectMapper<J, A> one, final ObjectMapper<A, B> two, final ObjectMapper<B, K> three) {
        delegate = new ObjectMapper<J, K>() {
            @Override
            public K map(J from) throws ObjectMappingException {
                return three.map(two.map(one.map(from)));
            }
        };
    }
    
    @Override
    public K map(J from) throws ObjectMappingException {
        return delegate.map(from);
    }

}
