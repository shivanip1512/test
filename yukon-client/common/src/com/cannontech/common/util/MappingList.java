package com.cannontech.common.util;

import java.util.AbstractList;
import java.util.List;

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
        K k = objectMapper.map(j);

        return k;
    }

    @Override
    public int size() {
        return base.size();
    }

}
