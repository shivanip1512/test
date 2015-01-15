package com.cannontech.database.data.point;

import com.cannontech.database.data.pao.TypeBase;

public final class PointTypes implements TypeBase {
    
    // System point id constants - these mirror the #defines
    // in pointtypes.h
    public static final int SYS_PID_SYSTEM = 0;
    public static final int SYS_PID_PORTER = -1;
    public static final int SYS_PID_SCANNER = -2;
    public static final int SYS_PID_DISPATCH = -3;
    public static final int SYS_PID_MACS = -4;
    public static final int SYS_PID_CAPCONTROL = -5;
    public static final int SYS_PID_NOTIFCATION = -6;
    public static final int SYS_PID_LOADMANAGEMENT = -10;

    // System point id constants - client
    public static final int SYS_PID_THRESHOLD = -100;
    public static final int SYS_PID_MULTISPEAK = -110;

    // The following constants are keys into the private
    // Array of strings (not related to any c/c++ defines!)
    // The Point Types
    public static final int STATUS_POINT = 0;
    public static final int ANALOG_POINT = 1;
    public static final int PULSE_ACCUMULATOR_POINT = 2;
    public static final int DEMAND_ACCUMULATOR_POINT = 3;
    public static final int CALCULATED_POINT = 4;
    public static final int STATUS_OUTPUT_POINT = 5;
    public static final int ANALOG_OUTPUT_POINT = 6;
    public static final int SYSTEM_POINT = 7;
    public static final int CALCULATED_STATUS_POINT = 8;

    public static final int INVALID_POINT = 9;

    // All the strings associated with points and the database
    private static final String[] pointStrings = {
            // Point Types
            "Status", 
            "Analog", 
            "PulseAccumulator", 
            "DemandAccumulator",
            "CalcAnalog", 
            "StatusOutput", // 5
            "AnalogOutput", 
            "System", 
            "CalcStatus", 
            "INVALID", // 9
    };

    // point update types
    public static final String UPDATE_FIRST_CHANGE = "On First Change";
    public static final String UPDATE_ALL_CHANGE = "On All Change";
    public static final String UPDATE_TIMER = "On Timer";
    public static final String UPDATE_TIMER_CHANGE = "On Timer+Change";
    public static final String UPDATE_HISTORICAL = "Historical";

    public final static String getType(int typeEnum) {

        if (typeEnum < 0 || typeEnum > pointStrings.length - 1)
            throw new Error("PointTypes::getType(int) - received unknow type: "
                    + typeEnum);
        else
            return pointStrings[typeEnum];

    }

    public final static int getType(String typeStr) {

        // Go through the point strings array and return it's index
        // when we find it
        for (int i = 0; i < pointStrings.length; i++) {
            if (pointStrings[i].equalsIgnoreCase(typeStr)) {
                return i;
            }
        }

        // Must not have found it
        throw new Error("PointTypes::getType(String) - Unrecognized type:  '"
                + typeStr + "'");
    }

    public final static boolean isValidPointType(int ptType) {
        return (ptType == com.cannontech.database.data.point.PointTypes.ANALOG_POINT
                || ptType == com.cannontech.database.data.point.PointTypes.STATUS_POINT
                || ptType == com.cannontech.database.data.point.PointTypes.PULSE_ACCUMULATOR_POINT
                || ptType == com.cannontech.database.data.point.PointTypes.DEMAND_ACCUMULATOR_POINT
                || ptType == com.cannontech.database.data.point.PointTypes.CALCULATED_POINT || ptType == com.cannontech.database.data.point.PointTypes.CALCULATED_STATUS_POINT);
    }
    
}
