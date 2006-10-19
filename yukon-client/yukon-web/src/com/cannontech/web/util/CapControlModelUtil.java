package com.cannontech.web.util;

import com.cannontech.database.db.point.calculation.CalcComponentTypes;

public class CapControlModelUtil {
    public static String PEAK_UPPER = "peak_upper";
    public static String PEAK_LOWER = "peak_lower";
    public static String PEAK_LAG = "peak_lagging";
    public static String PEAK_LEAD = "peak_leading";
    public static String OFFP_LOWER = "offpeak_lower";
    public static String OFFP_UPPER = "offpeak_upper";
    public static String OFFP_LAG = "offpeak_lagging";
    public static String OFFP_LEAD = "offpeak_leading";
    
    public static String PEAK_PF_POINT = "peak_pf_point";
    public static String OFFP_PF_POINT = "offpeak_pf_point";
    
    
    public CapControlModelUtil() {
        super();
    }

    public static boolean isVoltStrat(String algo) {
        if (algo.equalsIgnoreCase(CalcComponentTypes.LABEL_MULTI_VOLT))
            return true;
        else if (algo.equalsIgnoreCase(CalcComponentTypes.LABEL_VOLTS))
            return true;
        else
            return false;
    }
    
    public static boolean isVarStrat(String algo) {
        return algo.equalsIgnoreCase(CalcComponentTypes.LABEL_KVAR);
    }

    public static boolean isVoltVar (String algo) {
        return algo.equalsIgnoreCase(CalcComponentTypes.LABEL_MULTI_VOLT_VAR);
    }
    
    public static boolean isPFactor(String algo) {
        return algo.equalsIgnoreCase(CalcComponentTypes.PFACTOR_KW_KVAR_FUNCTION);
    }
}
