package com.cannontech.web.widget.accountInformation;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.msp.beans.v4.ConfiguredReadingType;
import com.cannontech.msp.beans.v4.GPSLocation;
import com.cannontech.msp.beans.v4.MeterID;
import com.cannontech.msp.beans.v4.Money;
import com.cannontech.msp.beans.v4.ObjectRef;
import com.cannontech.msp.beans.v4.gml.PointType;



public enum MspAccountInformationInfoV4 {
    
    INTEGER {
        @Override
        public String getValue(Object value) {
            return value.toString();
        }
        
        @Override
        public boolean isInstance(Object value) {
            return (value instanceof Integer || value instanceof Long) ? true: false; 
        }
    },
    NUMBER {
        @Override
        public String getValue(Object value) {
            DecimalFormat formatter = new DecimalFormat("#.###");
            return formatter.format(value);
        }
        
        @Override
        public boolean isInstance(Object value) {
            return (value instanceof Number) ? true: false; 
        }
    },
    DATE {
        @Override
        public String getValue(Object value) {
            return StringUtils.EMPTY;
        }
        
        @Override
        public boolean isInstance(Object value) {
            return (value instanceof Date) ? true: false; 
        }
    },
    CALENDAR {
        @Override
        public String getValue(Object value) {
            return StringUtils.EMPTY;
        }
        
        @Override
        public boolean isInstance(Object value) {
            return (value instanceof Calendar) ? true: false; 
        }
    },
    XMLGREGORIANCALENDAR {
        @Override
        public String getValue(Object value) {
            return StringUtils.EMPTY;
        }

        @Override
        public boolean isInstance(Object value) {
            return (value instanceof XMLGregorianCalendar) ? true : false;
        }
    },
    OBJECTREF {
        @Override
        public String getValue(Object value) {
            return ((ObjectRef) value).getValue();
        }
        
        @Override
        public boolean isInstance(Object value) {
            return (value instanceof ObjectRef) ? true: false; 
        }
    },

    MONEY {
        @Override
        public String getValue(Object value) {
            if (((Money) value).getCurrencyCode() != null) {
                return ((Money) value).getValue() + StringUtils.SPACE + ((Money) value).getCurrencyCode().value();
            } else {
                return StringUtils.EMPTY;
            }
        }
        
        @Override
        public boolean isInstance(Object value) {
            return (value instanceof Money) ? true: false; 
        }
    },
    METERID {
        @Override
        public String getValue(Object value) {
            MeterID meterID = (MeterID) value;
            return makeMeterInfo(meterID);
        }
        
        @Override
        public boolean isInstance(Object value) {
            return (value instanceof MeterID) ? true: false; 
        }
    },
    CONFIGUREDREADINGTYPE {
        @Override
        public String getValue(Object value) {
            ConfiguredReadingType readingType = (ConfiguredReadingType) value;
            return getReadingTypeDetails(readingType);
        }
        
        @Override
        public boolean isInstance(Object value) {
            return (value instanceof ConfiguredReadingType) ? true: false; 
        }
    },
    GPSLOCATION {
        @Override
        public String getValue(Object value) {
            GPSLocation gpsLocation = (GPSLocation) value;
            return makeMapLocation(gpsLocation);
        }
        
        @Override
        public boolean isInstance(Object value) {
            return (value instanceof GPSLocation) ? true: false; 
        }
    },
    POINTTYPE {
        @Override
        public String getValue(Object value) {
            PointType pointType = (PointType) value;
            return makeMapLocation(pointType);
        }
        
        @Override
        public boolean isInstance(Object value) {
            return (value instanceof PointType) ? true: false; 
        }
    },
    STRINGARRAY {
        @Override
        public String getValue(Object value) {
            return StringUtils.join((String[]) value, ", ");
        }
        
        @Override
        public boolean isInstance(Object value) {
            return (value instanceof String[]) ? true: false; 
        }
    },

    BOOLEAN {
        @Override
        public String getValue(Object value) {
            return ((Boolean) value ? "Yes" : "No");

        }

        @Override
        public boolean isInstance(Object value) {
            return (value instanceof Boolean) ? true : false;
        }
    },
    OTHER {
        @Override
        public String getValue(Object value) {
            return value.toString();
        }
        
        @Override
        public boolean isInstance(Object value) {
            return false; 
        }
    };
    
    // Create meter information from MeterID object
    private static String makeMeterInfo(MeterID meterID) {
        StringBuffer meterInfo = new StringBuffer();
        meterInfo.append("Meter Name=" + meterID.getMeterNo());
        meterInfo.append(", Service Type=" + meterID.getServiceType());

        return meterInfo.toString();
    }
    
    // Returns the reading type code
    private static String getReadingTypeDetails(ConfiguredReadingType configuredReadingType) {

        StringBuffer readingType = new StringBuffer();
        readingType.append("Reading Type=" + configuredReadingType.getReadingType());
        if (configuredReadingType.getReadingTypeCode() != null) {
            readingType.append(" Reading Type Code=" + configuredReadingType.getReadingTypeCode().getName());
        }
        return readingType.toString();
    }
    
    // Make the location information for a point type
    private static String makeMapLocation(PointType pointType) {

        StringBuffer location = new StringBuffer();
        if (pointType.getCoord() != null) {
            location.append("X=" + pointType.getCoord().getX()  + StringUtils.SPACE);
            location.append("Y=" + pointType.getCoord().getY()  + StringUtils.SPACE);
            location.append("Z=" + pointType.getCoord().getZ()  + StringUtils.SPACE);
            location.append("Buldge=" + pointType.getCoord().getBulge());
        }

        if (pointType.getCoordinates() != null) {
            location.append("Value=" + pointType.getCoordinates().getValue()  + StringUtils.SPACE);
            location.append("CS=" + pointType.getCoordinates().getCs()  + StringUtils.SPACE);
            location.append("TS=" + pointType.getCoordinates().getTs()  + StringUtils.SPACE);
        }
        return location.toString();
    }
    
    // Create map location details from gps location
    private static String makeMapLocation(GPSLocation gpsLocation) {

        StringBuffer location = new StringBuffer();
        location.append("Latitude=" + gpsLocation.getLatitude()  + StringUtils.SPACE);
        location.append("Longitude=" + gpsLocation.getLongitude()  + StringUtils.SPACE);
        location.append("Altitude=" + gpsLocation.getAltitude()  + StringUtils.SPACE);

        return location.toString();
    }
    
    public abstract String getValue(Object value);

    public abstract boolean isInstance(Object value);
}