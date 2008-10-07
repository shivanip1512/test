package com.cannontech.common.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

public class MappingIterator<J,K> implements Iterator<K>, Closeable {
    private final Iterator<? extends J> base;
    private final ObjectMapper<J, K> objectMapper;

    public MappingIterator(Iterator<? extends J> base, ObjectMapper<J, K> mapper) {
        this.base = base;
        this.objectMapper = mapper;
    }
    @Override
    public boolean hasNext() {
        return base.hasNext();
    }

    @Override
    public K next() {
        J j = base.next();
        K k = objectMapper.map(j);

        return k;
    }

    @Override
    public void remove() {
        base.remove();
    }
    
    @Override
    public void close() throws IOException {
        
        if (this.base instanceof Closeable) {
            Closeable closeable = (Closeable) this.base;
            closeable.close();
        }
    }

}
