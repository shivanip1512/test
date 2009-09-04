package com.cannontech.core.dynamic;

import java.util.Date;

import org.apache.commons.lang.Validate;

import com.cannontech.common.point.PointQuality;

public class PointValueBuilder {
    private Integer pointId = null;
    private Double value = null;
    private PointQuality pointQuality = PointQuality.Normal;
    private Date timeStamp = new Date();;
    private Integer type = null;

    private PointValueBuilder() {
    }
    
    public static PointValueBuilder create() {
        return new PointValueBuilder();
    }

    public PointValueBuilder withPointId(int pointId) {
        this.pointId = pointId;
        return this;
    }

    public PointValueBuilder withValue(double value) {
        this.value = value;
        return this;
    }

    public PointValueBuilder withPointQuality(PointQuality pointQuality) {
        this.pointQuality = pointQuality;
        return this;
    }
    
    public PointValueBuilder withTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public PointValueBuilder withType(int type) {
        this.type = type;
        return this;
    }
    
    public PointValueQualityHolder build() {
        Validate.notNull(pointId);
        Validate.notNull(value);
        Validate.notNull(type);
        
        return new PointValueQualityHolder() {

            @Override
            public PointQuality getPointQuality() {
                return pointQuality;
            }

            @Override
            public int getId() {
                return pointId;
            }

            @Override
            public Date getPointDataTimeStamp() {
                return timeStamp;
            }

            @Override
            public int getType() {
                return type;
            }

            @Override
            public double getValue() {
                return value;
            }
            
        };
    }
    

}
