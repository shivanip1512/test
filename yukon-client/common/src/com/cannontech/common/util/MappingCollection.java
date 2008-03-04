package com.cannontech.common.util;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

public class MappingCollection<J,K> extends AbstractCollection<K> {
    private final Collection<? extends J> base;
    private final ObjectMapper<J, K> objectMapper;

    public MappingCollection(Collection<? extends J> base, ObjectMapper<J, K> mapper) {
        this.base = base;
        this.objectMapper = mapper;
    }

    @Override
    public Iterator<K> iterator() {
        MappingIterator<J, K> iterator = new MappingIterator<J, K>(base.iterator(), objectMapper);
        return iterator;
    }

    @Override
    public int size() {
        return base.size();
    }


}
