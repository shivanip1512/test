package com.cannontech.amr.rfn.service.pointmapping.icd;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import org.junit.Assert;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * Groups points from the yukonPointMappingIcd.yaml file by associating coincident points with their preceding base point.  
 */
public class CoincidentGroupingCollector implements Collector<Named<? extends PointDefinition>, Map<PointMapping, NameScale>, Map<PointMapping, NameScale>> {

    BasePointDefinition basePoint = null;
    
    @Override
    public Supplier<Map<PointMapping, NameScale>> supplier() {
        return Maps::newHashMap;
    }

    @Override
    public BiConsumer<Map<PointMapping, NameScale>, Named<? extends PointDefinition>> accumulator() {
        return (map, npd) -> {
            if (!npd.getValue().isCoincident()) {
                basePoint = npd.getValue();
                map.put(new PointMapping(npd.getValue(), null), new NameScale(npd.getName(), 0));
            } else if (basePoint != null) {
                map.put(new PointMapping(npd.getValue(), basePoint), new NameScale(npd.getName(), 0));
            } else {
                Assert.fail("Base point is null for " + npd);
            }
        };
    }

    @Override
    public BinaryOperator<Map<PointMapping, NameScale>> combiner() {
        return (map1, map2) -> {
            map1.putAll(map2);
            return map1;
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return ImmutableSet.of(Characteristics.IDENTITY_FINISH);
    }

    @Override
    public Function<Map<PointMapping, NameScale>, Map<PointMapping, NameScale>> finisher() {
        return Function.identity();
    }
    
}