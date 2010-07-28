package com.cannontech.core.dao.support;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class IdentifiableUtils {

    public static <T extends Identifiable> ImmutableMap<Integer,T> getMap(Iterable<T> iterable) {
        return Maps.uniqueIndex(iterable, new Function<T,Integer>() {
            @Override
            public Integer apply(T model) {
                return model.getId();
            }
        });
    }
}
