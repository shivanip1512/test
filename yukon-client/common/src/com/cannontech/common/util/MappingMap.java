package com.cannontech.common.util;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.bulk.mapper.ObjectMappingException;

public class MappingMap<J, K, I> extends AbstractMap<J, I> {

    private final class MapEntryConverter implements Map.Entry<J, I> {
        private final Entry<J, K> from;

        private MapEntryConverter(Entry<J, K> from) {
            this.from = from;
        }

        public J getKey() {
            return from.getKey();
        }

        public I getValue() {
            return objectMapper.map(from.getValue());
        }

        public I setValue(I value) {
            throw new UnsupportedOperationException();
        }
    }

    private final Map<J,K> base;
    private final ObjectMapper<K, I> objectMapper;
    
    public MappingMap(Map<J, K> base, ObjectMapper<K, I> objectMapper) {
        this.base = base;
        this.objectMapper = objectMapper;
    }

    @Override
    public Set<java.util.Map.Entry<J, I>> entrySet() {
        return new MappingSet<Entry<J,K>, Entry<J,I>>(base.entrySet(), new ObjectMapper<Entry<J,K>, Entry<J,I>>() {
            public Map.Entry<J, I> map(final Map.Entry<J, K> from) throws ObjectMappingException {
                return new MapEntryConverter(from);
            }
            
        });
    }

}
