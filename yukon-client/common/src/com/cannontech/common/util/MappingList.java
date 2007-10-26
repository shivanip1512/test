package com.cannontech.common.util;

import java.util.AbstractList;
import java.util.List;

import com.cannontech.common.bulk.mapper.ObjectMappingException;

public class MappingList<J, K> extends AbstractList<K> {

    private final List<? extends J> base;
    private final ObjectMapper<J, K> objectMapper;

    public MappingList(List<? extends J> base, ObjectMapper<J, K> mapper) {
        this.base = base;
        this.objectMapper = mapper;
    }

    @Override
    public K get(int index) {
        J j = base.get(index);
        K k;
        try {
            k = objectMapper.map(j);
        } catch (ObjectMappingException e) {
            throw new RuntimeException(e);
        }
        return k;
    }

    @Override
    public int size() {
        return base.size();
    }

}
