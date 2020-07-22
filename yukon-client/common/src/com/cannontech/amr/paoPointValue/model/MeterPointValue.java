package com.cannontech.amr.paoPointValue.model;

import java.util.Comparator;
import java.util.Date;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;

public class MeterPointValue implements YukonPao {

    private final String deviceName;
    private final String meterNumber;
    private final PaoPointIdentifier paoPointIdentifier;
    private final PointValueHolder pointValueHolder;
    private final String pointName;
    private String formattedValue;
    private String formattedRawValue;

    public MeterPointValue(YukonMeter meter, PaoPointIdentifier paoPointIdentifier, PointValueHolder pointValueHolder,
            String pointName) {
        this.deviceName = meter.getName();
        this.meterNumber = meter.getMeterNumber();
        this.paoPointIdentifier = paoPointIdentifier;
        this.pointValueHolder = pointValueHolder;
        this.pointName = pointName;
    }

    public MeterPointValue(RfnDevice rfnDevice, PaoPointIdentifier paoPointIdentifier, PointValueHolder pointValueHolder,
            String pointName) {
        this.deviceName = rfnDevice.getName();
        this.meterNumber = rfnDevice.getRfnIdentifier().getSensorSerialNumber();
        this.paoPointIdentifier = paoPointIdentifier;
        this.pointValueHolder = pointValueHolder;
        this.pointName = pointName;
    }

    public PaoPointIdentifier getPaoPointIdentifier() {
        return paoPointIdentifier;
    }

    public PointValueHolder getPointValueHolder() {
        return pointValueHolder;
    }

    public String getPointName() {
        return pointName;
    }

    public String getFormattedValue(final PointFormattingService pointFormattingService, final YukonUserContext userContext) {
        if (formattedValue == null) {
            this.formattedValue = pointFormattingService.getValueString(pointValueHolder, Format.VALUE, userContext);
        }
        return formattedValue;
    }

    public String getFormattedRawValue(final PointFormattingService pointFormattingService, final YukonUserContext userContext) {
        if (formattedRawValue == null) {
            this.formattedRawValue = pointFormattingService.getValueString(pointValueHolder, Format.RAWVALUE, userContext);
        }
        return formattedRawValue;
    }

    @Override
    public PaoIdentifier getPaoIdentifier() {
        return paoPointIdentifier.getPaoIdentifier();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((formattedValue == null) ? 0 : formattedValue.hashCode());
        result =
            prime * result + ((paoPointIdentifier == null) ? 0 : paoPointIdentifier.hashCode());
        result = prime * result + ((pointName == null) ? 0 : pointName.hashCode());
        result = prime * result + ((pointValueHolder == null) ? 0 : pointValueHolder.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MeterPointValue other = (MeterPointValue) obj;
        if (formattedValue == null) {
            if (other.formattedValue != null) {
                return false;
            }
        } else if (!formattedValue.equals(other.formattedValue)) {
            return false;
        }
        if (paoPointIdentifier == null) {
            if (other.paoPointIdentifier != null) {
                return false;
            }
        } else if (!paoPointIdentifier.equals(other.paoPointIdentifier)) {
            return false;
        }
        if (pointName == null) {
            if (other.pointName != null) {
                return false;
            }
        } else if (!pointName.equals(other.pointName)) {
            return false;
        }
        if (pointValueHolder == null) {
            if (other.pointValueHolder != null) {
                return false;
            }
        } else if (!pointValueHolder.equals(other.pointValueHolder)) {
            return false;
        }
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

    public static Comparator<MeterPointValue> getFormattedValueComparator(final PointFormattingService pointFormattingService, final YukonUserContext userContext) {
        Ordering<String> normalStringComparer = Ordering.natural().nullsLast();
        Ordering<MeterPointValue> formattedValueOrdering = normalStringComparer
            .onResultOf(new Function<MeterPointValue, String>() {
                @Override
                public String apply(MeterPointValue from) {
                    return from.getFormattedValue(pointFormattingService, userContext);
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
                    return from.getMeterNumber();
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
                    return from.getPaoIdentifier().getPaoType().getDbString();
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
                    return from.getDeviceName();
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

    public String getDeviceName() {
        return deviceName;
    }

    public String getMeterNumber() {
        return meterNumber;
    }
}
