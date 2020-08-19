package com.cannontech.web.common.pao.service;

import java.util.Comparator;
import java.util.List;

import com.cannontech.amr.meter.model.PointSortField;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.point.YukonPoint;
import com.google.common.collect.ImmutableMap;

public class LiteYukonPoint implements YukonPao, YukonPoint {
    
    private final String pointName;
    private final int pointId;
    private final PaoPointIdentifier paoPointIdentifier;
    //first attribute
    private final Attribute attribute;
    //all attributes associated with a point
    private final List<Attribute> allAttributes;
    
    public static Comparator<LiteYukonPoint> POINTNAME_COMPARATOR = new Comparator<LiteYukonPoint>() {
        @Override
        public int compare(LiteYukonPoint o1, LiteYukonPoint o2) {
            if (o1.pointName == null) {
                return 1;
            } else if (o2.pointName == null) {
                return -1;
            } else {
                return o1.pointName.compareToIgnoreCase(o2.pointName);
            }
        }
    };
    
    public static Comparator<LiteYukonPoint> POINTTYPE_COMPARATOR = new Comparator<LiteYukonPoint>() {
        @Override
        public int compare(LiteYukonPoint o1, LiteYukonPoint o2) {
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
    
    public static Comparator<LiteYukonPoint> POINTOFFSET_COMPARATOR = new Comparator<LiteYukonPoint>() {
        @Override
        public int compare(LiteYukonPoint o1, LiteYukonPoint o2) {
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
    
    private final static ImmutableMap<PointSortField, Comparator<LiteYukonPoint>> orderByToComparatorMap;
    
    static {
        ImmutableMap.Builder<PointSortField, Comparator<LiteYukonPoint>> mapBuilder = ImmutableMap.builder();
        mapBuilder.put(PointSortField.POINTNAME, POINTNAME_COMPARATOR);
        mapBuilder.put(PointSortField.POINTOFFSET, POINTOFFSET_COMPARATOR);
        mapBuilder.put(PointSortField.POINTTYPE, POINTTYPE_COMPARATOR);
        orderByToComparatorMap = mapBuilder.build();
    }
    
    private LiteYukonPoint(PaoPointIdentifier paoPointId, Attribute attribute, List<Attribute> allAttributes, String name, int pointId) {
        this.paoPointIdentifier = paoPointId;
        this.attribute = attribute;
        this.pointName = name;
        this.pointId = pointId;
        this.allAttributes = allAttributes;
    }
    
    public String getPointName() {
        return pointName;
    }
    
    public int getPointId() {
        return pointId;
    }
    
    public Attribute getAttribute() {
        return attribute;
    }
    
    public PaoPointIdentifier getPaoPointIdentifier() {
        return paoPointIdentifier;
    }
    
    public static LiteYukonPoint of(PaoPointIdentifier paoPointId, Attribute attribute, List<Attribute> allAttributes, String name, int pointId) {
        return new LiteYukonPoint(paoPointId, attribute, allAttributes, name, pointId);
    }
    
    public static Comparator<LiteYukonPoint> getComparatorForSortField(PointSortField pointSortField) {
        return orderByToComparatorMap.get(pointSortField);
    }

    @Override
    public PointIdentifier getPointIdentifier() {
        return paoPointIdentifier.getPointIdentifier();
    }

    @Override
    public PaoIdentifier getPaoIdentifier() {
        return paoPointIdentifier.getPaoIdentifier();
    }

    public List<Attribute> getAllAttributes() {
        return allAttributes;
    }
    
    public boolean isDisplayPopup() {
        return allAttributes.size() > 1;
    }
    
}