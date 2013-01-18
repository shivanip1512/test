package com.cannontech.web.common.pao.service;

import java.util.Comparator;

import com.cannontech.amr.meter.model.PointSortField;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.google.common.collect.ImmutableMap;

public class YukonPoint {
    
    private final String pointName;
    private final int pointId;
    private final PaoPointIdentifier paoPointIdentifier;
    private final BuiltInAttribute attribute;
    
    public static Comparator<YukonPoint> ATTRIBUTE_COMPARATOR = new Comparator<YukonPoint>() {
        @Override
        public int compare(YukonPoint o1, YukonPoint o2) {
            if (o1.attribute == null) {
                return 1;
            } else if (o2.attribute == null) {
                return -1;
            } else {
                return o1.attribute.getDescription().compareTo(o2.attribute.getDescription());
            }
        }
    };
    
    public static Comparator<YukonPoint> POINTNAME_COMPARATOR = new Comparator<YukonPoint>() {
        @Override
        public int compare(YukonPoint o1, YukonPoint o2) {
            if (o1.pointName == null) {
                return 1;
            } else if (o2.pointName == null) {
                return -1;
            } else {
                return o1.pointName.compareToIgnoreCase(o2.pointName);
            }
        }
    };
    
    public static Comparator<YukonPoint> POINTTYPE_COMPARATOR = new Comparator<YukonPoint>() {
        @Override
        public int compare(YukonPoint o1, YukonPoint o2) {
            if (o1.paoPointIdentifier == null ||
                o1.paoPointIdentifier.getPointIdentifier() == null) {
                return 1;
            } else if (o2.paoPointIdentifier == null ||
                       o2.paoPointIdentifier.getPointIdentifier() == null) {
                return -1;
            } else {
                String o1Type = o1.paoPointIdentifier.getPointIdentifier().getPointType().name();
                String o2Type = o2.paoPointIdentifier.getPointIdentifier().getPointType().name();
                return o1Type.compareToIgnoreCase(o2Type);
            }
        }
    };
    
    public static Comparator<YukonPoint> POINTOFFSET_COMPARATOR = new Comparator<YukonPoint>() {
        @Override
        public int compare(YukonPoint o1, YukonPoint o2) {
            if (o1.paoPointIdentifier == null || 
                o1.paoPointIdentifier.getPointIdentifier() == null) {
                return 1;
            } else if (o2.paoPointIdentifier == null ||
                       o2.paoPointIdentifier.getPointIdentifier() == null) {
                return -1;
            } else {
                return o1.paoPointIdentifier.getPointIdentifier().getOffset() -
                       o2.paoPointIdentifier.getPointIdentifier().getOffset();
            }
        }
    };
    
    private final static ImmutableMap<PointSortField, Comparator<YukonPoint>> orderByToComparatorMap;
    
    static {
        ImmutableMap.Builder<PointSortField, Comparator<YukonPoint>> mapBuilder = ImmutableMap.builder();
        mapBuilder.put(PointSortField.ATTRIBUTE, ATTRIBUTE_COMPARATOR);
        mapBuilder.put(PointSortField.POINTNAME, POINTNAME_COMPARATOR);
        mapBuilder.put(PointSortField.POINTOFFSET, POINTOFFSET_COMPARATOR);
        mapBuilder.put(PointSortField.POINTTYPE, POINTTYPE_COMPARATOR);
        orderByToComparatorMap = mapBuilder.build();
    }
    
    private YukonPoint(PaoPointIdentifier paoPointId, BuiltInAttribute attribute, String name, int pointId) {
        this.paoPointIdentifier = paoPointId;
        this.attribute = attribute;
        this.pointName = name;
        this.pointId = pointId;
    }
    
    public String getPointName() {
        return pointName;
    }
    
    public int getPointId() {
        return pointId;
    }
    
    public BuiltInAttribute getAttribute() {
        return attribute;
    }
    
    public PaoPointIdentifier getPaoPointIdentifier() {
        return paoPointIdentifier;
    }
    
    public static YukonPoint of(PaoPointIdentifier paoPointId, BuiltInAttribute attribute, String name, int pointId) {
        return new YukonPoint(paoPointId, attribute, name, pointId);
    }
    
    public static Comparator<YukonPoint> getComparatorForSortField(PointSortField pointSortField) {
        return orderByToComparatorMap.get(pointSortField);
    }
}