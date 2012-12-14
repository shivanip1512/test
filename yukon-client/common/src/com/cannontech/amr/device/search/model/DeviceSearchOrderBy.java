package com.cannontech.amr.device.search.model;

import java.util.Collections;
import java.util.Comparator;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;

public enum DeviceSearchOrderBy {
    NAME,
    TYPE(new Comparator<LiteYukonPAObject>() {
        public int compare(LiteYukonPAObject o1, LiteYukonPAObject o2) {
            String thisVal = null, anotherVal = null;
            
            thisVal = o1.getPaoType().getDbString();
            anotherVal = o2.getPaoType().getDbString();
            
            if (thisVal.equalsIgnoreCase(anotherVal)) {
                //if the types are equal, we need to sort by deviceName
                thisVal = o1.getPaoName();
                anotherVal = o2.getPaoName();
            }
            
            return (thisVal.compareToIgnoreCase(anotherVal));
        }
        
        public boolean equals(Object obj) {
            return false;
        }
    }),
    ADDRESS(new Comparator<LiteYukonPAObject>() {
        public int compare(LiteYukonPAObject o1, LiteYukonPAObject o2) {
            String thisVal = null, anotherVal = null;
            
            thisVal = String.valueOf(o1.getAddress());
            anotherVal = String.valueOf(o2.getAddress());
            
            if (thisVal.equalsIgnoreCase(anotherVal)) {
                //if the types are equal, we need to sort by deviceName
                thisVal = o1.getPaoName();
                anotherVal = o2.getPaoName();
            }

            return (thisVal.compareToIgnoreCase(anotherVal));
        }

        public boolean equals(Object obj) {
            return false;
        }
    }),
    METERNUMBER(new Comparator<LiteYukonPAObject>() {
        public int compare(LiteYukonPAObject o1, LiteYukonPAObject o2) {
            String thisVal = null, anotherVal = null;
            
            LiteDeviceMeterNumber ldmn1 = DaoFactory.getDeviceDao().getLiteDeviceMeterNumber(o1.getYukonID());
            LiteDeviceMeterNumber ldmn2 = DaoFactory.getDeviceDao().getLiteDeviceMeterNumber(o2.getYukonID());
            
            thisVal = (ldmn1 != null ? ldmn1.getMeterNumber() : NULL_OBJECT_STRING);
            anotherVal = (ldmn2 != null ? ldmn2.getMeterNumber() : NULL_OBJECT_STRING);
            
            if (thisVal.equalsIgnoreCase(anotherVal)) {
                //if the types are equal, we need to sort by deviceName
                thisVal = o1.getPaoName();
                anotherVal = o2.getPaoName();
            }
            return (thisVal.compareToIgnoreCase(anotherVal));
        }
        
        public boolean equals(Object obj) {
            return false;
        }
    }),
    ROUTE(new Comparator<LiteYukonPAObject>() {
        public int compare(LiteYukonPAObject o1, LiteYukonPAObject o2) {
            String thisVal = null, anotherVal = null;
            
            if (PAOGroups.INVALID == o1.getRouteID()) {
                thisVal = NULL_OBJECT_STRING;
            } else {
                thisVal = DaoFactory.getPaoDao().getYukonPAOName(o1.getRouteID());
            }
            
            if (PAOGroups.INVALID == o2.getRouteID()) {
                anotherVal = NULL_OBJECT_STRING;
            } else {
                anotherVal = DaoFactory.getPaoDao().getYukonPAOName(o2.getRouteID());
            }
            
            if (thisVal.equalsIgnoreCase(anotherVal)) {
                //if the types are equal, we need to sort by deviceName
                thisVal = o1.getPaoName();
                anotherVal = o2.getPaoName();
            }
            
            return (thisVal.compareToIgnoreCase(anotherVal));
        }
        
        public boolean equals(Object obj) {
            return false;
        }
    }),
    COMM_CHANNEL,
    LOAD_GROUP,
    LMGROUP_ROUTE,
    LMGROUP_SERIAL,
    LMGROUP_CAPACITY,
    LMGROUP_TYPE,
    CBC_SERIAL;
    
    private static final String NULL_OBJECT_STRING = "---";
    
    private Comparator<LiteYukonPAObject> comparator = null;
    
    private DeviceSearchOrderBy() {}
    
    private DeviceSearchOrderBy(Comparator<LiteYukonPAObject> comparator) {
        this.comparator = comparator;
    }
    
    public Comparator<LiteYukonPAObject> getComparator() {
        return comparator;
    }
    
    public Comparator<LiteYukonPAObject> getReverseComparator() {
        return (comparator == null) ? Collections.<LiteYukonPAObject>reverseOrder() : Collections.reverseOrder(comparator);
    }
}
