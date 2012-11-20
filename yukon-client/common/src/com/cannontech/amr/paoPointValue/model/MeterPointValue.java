package com.cannontech.amr.paoPointValue.model;

import java.util.Comparator;
import java.util.Date;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.core.dynamic.PointValueHolder;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;

public class MeterPointValue implements YukonPao {

    private final Meter meter;
    private final PaoPointIdentifier paoPointIdentifier;
    private final BuiltInAttribute attribute;
    private final PointValueHolder pointValueHolder;
    private final String pointName;
    private final String formattedValue;

    public MeterPointValue(Meter meter, PaoPointIdentifier paoPointIdentifier,
                           BuiltInAttribute attribute,
                           PointValueHolder pointValueHolder, String pointName,
                           String formattedValue) {
        this.meter = meter;
        this.paoPointIdentifier = paoPointIdentifier;
        this.attribute = attribute;
        this.pointValueHolder = pointValueHolder;
        this.pointName = pointName;
        this.formattedValue = formattedValue;
    }

    public Meter getMeter() {
        return meter;
    }

    public PaoPointIdentifier getPaoPointIdentifier() {
        return paoPointIdentifier;
    }

    public BuiltInAttribute getAttribute() {
        return attribute;
    }

    public PointValueHolder getPointValueHolder() {
        return pointValueHolder;
    }

    public String getPointName() {
        return pointName;
    }

    public String getFormattedValue() {
        return formattedValue;
    }

    @Override
    public PaoIdentifier getPaoIdentifier() {
        return paoPointIdentifier.getPaoIdentifier();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
        result = prime * result + ((formattedValue == null) ? 0 : formattedValue.hashCode());
        result =
            prime * result + ((paoPointIdentifier == null) ? 0 : paoPointIdentifier.hashCode());
        result = prime * result + ((pointName == null) ? 0 : pointName.hashCode());
        result = prime * result + ((pointValueHolder == null) ? 0 : pointValueHolder.hashCode());
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
        MeterPointValue other = (MeterPointValue) obj;
        if (attribute != other.attribute)
            return false;
        if (formattedValue == null) {
            if (other.formattedValue != null)
                return false;
        } else if (!formattedValue.equals(other.formattedValue))
            return false;
        if (paoPointIdentifier == null) {
            if (other.paoPointIdentifier != null)
                return false;
        } else if (!paoPointIdentifier.equals(other.paoPointIdentifier))
            return false;
        if (pointName == null) {
            if (other.pointName != null)
                return false;
        } else if (!pointName.equals(other.pointName))
            return false;
        if (pointValueHolder == null) {
            if (other.pointValueHolder != null)
                return false;
        } else if (!pointValueHolder.equals(other.pointValueHolder))
            return false;
        return true;
    }

    public static Comparator<MeterPointValue> getMeterNameComparator() {
        return getMeterNameOrdering();
    }

    public static Comparator<MeterPointValue> getDateMeterNameComparator() {
        return getDateOrdering().compound(getMeterNameOrdering()).compound(getPointNameOrdering().reverse());
    }

    public static Comparator<MeterPointValue> getPointNameMeterNameComparator() {
        return getPointNameOrdering().compound(getMeterNameOrdering());
    }

    public static Comparator<MeterPointValue> getFormattedValueComparator() {
        Ordering<String> normalStringComparer = Ordering.natural().nullsLast();
        Ordering<MeterPointValue> formattedValueOrdering = normalStringComparer
            .onResultOf(new Function<MeterPointValue, String>() {
                @Override
                public String apply(MeterPointValue from) {
                    return from.getFormattedValue();
                }
            });
        return formattedValueOrdering;
    }

    public static Comparator<MeterPointValue> getMeterNumberComparator() {
        Ordering<String> normalStringComparer = Ordering.natural().nullsLast();
        Ordering<MeterPointValue> meterNumberOrdering = normalStringComparer
            .onResultOf(new Function<MeterPointValue, String>() {
                @Override
                public String apply(MeterPointValue from) {
                    return from.getMeter().getMeterNumber();
                }
            });
        return meterNumberOrdering;
    }

    public static Comparator<MeterPointValue> getDeviceTypeComparator() {
        Ordering<String> normalStringComparer = Ordering.natural().nullsLast();
        Ordering<MeterPointValue> typeOrdering = normalStringComparer
            .onResultOf(new Function<MeterPointValue, String>() {
                @Override
                public String apply(MeterPointValue from) {
                    return from.getMeter().getPaoType().getDbString();
                }
            });
        return typeOrdering;
    }

    /* Commonly used Ordering methods */
    public static Ordering<MeterPointValue> getPaoIdOrdering() {
        return Ordering.natural().nullsLast()
            .onResultOf(new Function<MeterPointValue, Integer>() {
                @Override
                public Integer apply(MeterPointValue from) {
                    return from.getPaoIdentifier().getPaoId();
                }
            });
    }

    public static Ordering<MeterPointValue> getPointIdOrdering() {
        return Ordering.natural().nullsLast()
            .onResultOf(new Function<MeterPointValue, Integer>() {
                @Override
                public Integer apply(MeterPointValue from) {
                    return from.getPointValueHolder().getId();
                }
            });
    }

    public static Ordering<MeterPointValue> getMeterNameOrdering() {
        return Ordering.natural().nullsLast()
            .onResultOf(new Function<MeterPointValue, String>() {
                @Override
                public String apply(MeterPointValue from) {
                    return from.getMeter().getName();
                }
            });
    }

    public static Ordering<MeterPointValue> getDateOrdering() {
        return Ordering.natural().nullsLast()
            .onResultOf(new Function<MeterPointValue, Date>() {
                @Override
                public Date apply(MeterPointValue from) {
                    return from.getPointValueHolder().getPointDataTimeStamp();
                }
            });
    }

    public static Ordering<MeterPointValue> getPointNameOrdering() {
        return Ordering.natural().nullsLast()
            .onResultOf(new Function<MeterPointValue, String>() {
                @Override
                public String apply(MeterPointValue from) {
                    return from.getPointName();
                }
            });
    }
}
