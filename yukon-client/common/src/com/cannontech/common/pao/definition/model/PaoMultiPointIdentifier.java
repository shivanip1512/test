package com.cannontech.common.pao.definition.model;

import java.util.Iterator;
import java.util.Set;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * This class represents a Set of points which all pertain to the same PAO.
 */
public final class PaoMultiPointIdentifier {
    private final PaoIdentifier paoIdentifier;
    private final Set<PointIdentifier> pointIdentifiers;
    private final Set<PaoPointIdentifier> paoPointIdentifiers; // this object is a redundant copy of above
    
    public PaoMultiPointIdentifier(YukonPao pao, Iterable<PointIdentifier> points) {
        this.paoIdentifier = pao.getPaoIdentifier();
        this.pointIdentifiers = ImmutableSet.copyOf(points);
        Builder<PaoPointIdentifier> builder = ImmutableSet.builder();
        for (PointIdentifier pointIdentifier : points) {
            PaoPointIdentifier paoPointIdentifier = new PaoPointIdentifier(paoIdentifier, pointIdentifier);
            builder.add(paoPointIdentifier);
        }
        this.paoPointIdentifiers = builder.build();
    }

    public PaoMultiPointIdentifier(Iterable<PaoPointIdentifier> paoPoints) {
        Builder<PointIdentifier> pointBuilder = ImmutableSet.builder();
        Builder<PaoPointIdentifier> paoPointBuilder = ImmutableSet.builder();
        Iterator<PaoPointIdentifier> iterator = paoPoints.iterator();
        if (!iterator.hasNext()) {
            throw new IllegalArgumentException("must pass at least one point");
        }
        PaoPointIdentifier next = iterator.next();
        this.paoIdentifier = next.getPaoIdentifier();
        pointBuilder.add(next.getPointIdentifier());
        paoPointBuilder.add(next);
        while (iterator.hasNext()) {
            next = iterator.next();
            if (!this.paoIdentifier.equals(next.getPaoIdentifier())) {
                throw new IllegalArgumentException("all points must be on the same PAO");
            }
            pointBuilder.add(next.getPointIdentifier());
            paoPointBuilder.add(next);
        }
        this.pointIdentifiers = pointBuilder.build();
        this.paoPointIdentifiers = paoPointBuilder.build();
    }
    
    public PaoIdentifier getPao() {
        return paoIdentifier;
    }
    
    public Set<PointIdentifier> getPointIdentifiers() {
        return pointIdentifiers;
    }
    
    public Set<PaoPointIdentifier> getPaoPointIdentifiers() {
        return paoPointIdentifiers;
    }
    
    @Override
    public String toString() {
        return paoIdentifier.toString() + pointIdentifiers.toString();
    }
    
}
