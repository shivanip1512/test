package com.cannontech.amr.crf.service;

import static com.cannontech.amr.crf.message.MeterReadingType.*;
import static com.cannontech.common.pao.PaoType.CRF_AL;
import static com.cannontech.common.pao.PaoType.CRF_AX;
import static com.cannontech.common.pao.attribute.model.BuiltInAttribute.BLINK_COUNT;
import static com.cannontech.common.pao.attribute.model.BuiltInAttribute.DEMAND;
import static com.cannontech.common.pao.attribute.model.BuiltInAttribute.LOAD_PROFILE;
import static com.cannontech.common.pao.attribute.model.BuiltInAttribute.USAGE;
import static com.cannontech.common.pao.attribute.model.BuiltInAttribute.VOLTAGE;
import static com.cannontech.common.pao.attribute.model.BuiltInAttribute.VOLTAGE_PROFILE;

import java.util.Set;

import com.cannontech.amr.crf.message.MeterReadingType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableMap.Builder;

public class CrfAttributeLookupService {
    private final ImmutableMap<LookupKey, AttributeConverter> map;
    
    {
        Builder<LookupKey, AttributeConverter> b = ImmutableMap.builder();
        
        add(b, CRF_AL, BILLING,  USAGE,           "Wh", .001);
        add(b, CRF_AL, CURRENT,  USAGE,           "Wh", .001);
        add(b, CRF_AL, INTERVAL, USAGE,           "Wh", .001);
        add(b, CRF_AL, BILLING,  USAGE,           "Wh", .001, "Quadrant 1", "Quadrant 4");
        add(b, CRF_AL, CURRENT,  USAGE,           "Wh", .001, "Quadrant 1", "Quadrant 4");
        add(b, CRF_AL, INTERVAL, USAGE,           "Wh", .001, "Quadrant 1", "Quadrant 4");
        add(b, CRF_AL, BILLING,  VOLTAGE,         "V", .001, "Primary", "milli");
        add(b, CRF_AL, CURRENT,  VOLTAGE,         "V", .001, "Primary", "milli");
        add(b, CRF_AL, INTERVAL, VOLTAGE_PROFILE, "V", .001, "Primary", "milli");
        add(b, CRF_AL, BILLING,  BLINK_COUNT,     "Outage Count", 1);
        add(b, CRF_AL, CURRENT,  BLINK_COUNT,     "Outage Count", 1);
        add(b, CRF_AL, INTERVAL, BLINK_COUNT,     "Outage Count", 1);

        add(b, CRF_AX, BILLING,  USAGE,           "Wh", .001, "Quadrant 1", "Quadrant 4");
        add(b, CRF_AX, CURRENT,  USAGE,           "Wh", .001, "Quadrant 1", "Quadrant 4");
        add(b, CRF_AX, INTERVAL, USAGE,           "Wh", .001, "Quadrant 1", "Quadrant 4");
        add(b, CRF_AX, BILLING,  DEMAND,          "W", .001, "Quadrant 1", "Quadrant 4");
        add(b, CRF_AX, CURRENT,  DEMAND,          "W", .001, "Quadrant 1", "Quadrant 4");
        add(b, CRF_AX, INTERVAL, LOAD_PROFILE,    "W", .001, "Quadrant 1", "Quadrant 4");
        add(b, CRF_AX, BILLING,  VOLTAGE,         "V", .001, "Phase A", "milli");
        add(b, CRF_AX, CURRENT,  VOLTAGE,         "V", .001, "Phase A", "milli");
        add(b, CRF_AX, INTERVAL, VOLTAGE_PROFILE, "V", .001, "Phase A", "milli");
        add(b, CRF_AX, BILLING,  BLINK_COUNT,     "Outage Count", 1);
        add(b, CRF_AX, CURRENT,  BLINK_COUNT,     "Outage Count", 1);
        add(b, CRF_AX, INTERVAL, BLINK_COUNT,     "Outage Count", 1);
        
        map = b.build();
    }

    public AttributeConverter findMatch(PaoType paoType, MeterReadingType readingType, String unitOfMeasure, Set<String> unitOfMeasureModifiers) {
        LookupKey lookupKey = new LookupKey();
        lookupKey.paoType = paoType;
        lookupKey.readingType = readingType;
        lookupKey.unitOfMeasure = unitOfMeasure;
        lookupKey.unitOfMeasureModifiers = unitOfMeasureModifiers;
        
        return map.get(lookupKey);
    }

    private static void add(Builder<LookupKey, AttributeConverter> builder, PaoType type, MeterReadingType readingType, final BuiltInAttribute attribute, String unitOfMeasure, final double multiplier, String... modifiers) {
        LookupKey lookupKey = new LookupKey();
        lookupKey.paoType = type;
        lookupKey.readingType = readingType;
        lookupKey.unitOfMeasure = unitOfMeasure;
        lookupKey.unitOfMeasureModifiers = ImmutableSet.of(modifiers);
        
        AttributeConverter value = new AttributeConverter() {
            
            @Override
            public BuiltInAttribute getAttribute() {
                return attribute;
            }
            
            @Override
            public double convertValue(double value) {
                return value * multiplier;
            }
        };
        builder.put(lookupKey, value);
    }
    
    private static class LookupKey {
        PaoType paoType;
        String unitOfMeasure;
        Set<String> unitOfMeasureModifiers;
        MeterReadingType readingType;
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((paoType == null) ? 0 : paoType.hashCode());
            result = prime * result + ((readingType == null) ? 0 : readingType.hashCode());
            result = prime * result + ((unitOfMeasure == null) ? 0 : unitOfMeasure.hashCode());
            result = prime * result + ((unitOfMeasureModifiers == null) ? 0
                    : unitOfMeasureModifiers.hashCode());
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            LookupKey other = (LookupKey) obj;
            if (paoType == null) {
                if (other.paoType != null)
                    return false;
            } else if (!paoType.equals(other.paoType))
                return false;
            if (readingType == null) {
                if (other.readingType != null)
                    return false;
            } else if (!readingType.equals(other.readingType))
                return false;
            if (unitOfMeasure == null) {
                if (other.unitOfMeasure != null)
                    return false;
            } else if (!unitOfMeasure.equals(other.unitOfMeasure))
                return false;
            if (unitOfMeasureModifiers == null) {
                if (other.unitOfMeasureModifiers != null)
                    return false;
            } else if (!unitOfMeasureModifiers.equals(other.unitOfMeasureModifiers))
                return false;
            return true;
        }
        
        
    }

}
