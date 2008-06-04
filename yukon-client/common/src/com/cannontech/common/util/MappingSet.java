package com.cannontech.common.util;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

public class MappingSet<J, K> extends AbstractSet<K> {
    private final Set<? extends J> base;
    private final ObjectMapper<J, K> objectMapper;

    public MappingSet(Set<? extends J> base, ObjectMapper<J, K> mapper) {
        this.base = base;
        this.objectMapper = mapper;
    }
    
    @Override
    public Iterator<K> iterator() {
        return new MappingIterator<J, K>(base.iterator(), objectMapper);
    }

    @Override
    public int size() {
        return base.size();
    }

}
