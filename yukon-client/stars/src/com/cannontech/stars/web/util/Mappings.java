package com.cannontech.stars.web.util;

import java.util.Hashtable;

import com.cannontech.database.db.starsappliance.ApplianceCategory;
import com.cannontech.database.db.starshardware.LMHardwareBase;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Mappings {

    private static Hashtable appNameMap = new Hashtable();
    private static Hashtable appImageMap = new Hashtable();
    private static Hashtable invNameMap = new Hashtable();

    static {
        appNameMap.put(ApplianceCategory.CATEGORY_AC, "Air Conditioner");
        appNameMap.put(ApplianceCategory.CATEGORY_WH, "Water Heater");

        appImageMap.put(ApplianceCategory.CATEGORY_AC, "AC.gif");
        appImageMap.put(ApplianceCategory.CATEGORY_WH, "WaterHeater.gif");

        invNameMap.put(LMHardwareBase.DEVICETYPE_LCR1000, "LCR 1000");
        invNameMap.put(LMHardwareBase.DEVICETYPE_LCR2000, "LCR 2000");
        invNameMap.put(LMHardwareBase.DEVICETYPE_LCR3000, "LCR 3000");
        invNameMap.put(LMHardwareBase.DEVICETYPE_LCR4000, "LCR 4000");
        invNameMap.put(LMHardwareBase.DEVICETYPE_LCR5000, "LCR 5000");
    }

    public Mappings() {
    }

    public static String getApplianceName(String category) {
        return (String) appNameMap.get(category);
    }

    public static String getApplianceImage(String category) {
        return (String) appImageMap.get(category);
    }

    public static String getLMHardwareName(String type) {
        return (String) invNameMap.get(type);
    }
}