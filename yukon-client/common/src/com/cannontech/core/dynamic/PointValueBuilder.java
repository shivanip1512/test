package com.cannontech.core.dynamic;

import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.lang.Validate;

import com.cannontech.common.point.PointQuality;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.data.point.PointType;

public class PointValueBuilder {
    private Integer pointId = null;
    private Double value = null;
    private PointQuality pointQuality = PointQuality.Normal;
    private Date timeStamp = new Date();;
    private PointType pointType = null;

    private PointValueBuilder() {
    }
    
    public static PointValueBuilder create() {
        return new PointValueBuilder();
    }
    
    /**
     * This gets the pointId, timeStamp, quality, and value from the ResultSet. 
     * Type must be supplied separately. 
     * @param rs
     * @return
     * @throws SQLException
     */
    public PointValueBuilder withResultSet(YukonResultSet rs) throws SQLException {
        pointId = rs.getInt("pointid");
        timeStamp = rs.getDate("timestamp");
        pointQuality = PointQuality.getPointQuality(rs.getInt("Quality"));
        value = rs.getDouble("value");
        return this;
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
        this.pointType = PointType.getForId(type);
        return this;
    }
    
    public PointValueBuilder withType(String type) {
    	this.pointType = PointType.getForString(type);
    	return this;
    }
    
    public PointValueBuilder withType(PointType type) {
    	this.pointType = type;
    	return this;
    }
    
    public PointValueQualityHolder build() {
        Validate.notNull(pointId);
        Validate.notNull(value);
        Validate.notNull(pointType);
        
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
                return pointType.getPointTypeId();
            }
            
            @Override
            public PointType getPointType() {
            	return pointType;
            }

            @Override
            public double getValue() {
                return value;
            }

			@Override
			public boolean equals(Object obj) {
				
				PointValueQualityHolder other = (PointValueQualityHolder)obj;
				
				return this.getId() == other.getId() && 
					   this.getValue() == other.getValue() && 
					   this.getPointDataTimeStamp().equals(other.getPointDataTimeStamp());
			}
        };
    }
    

}
