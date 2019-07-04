package com.cannontech.common.dr.setup;

import com.cannontech.common.i18n.DisplayableEnum;

public enum Loads implements DisplayableEnum {
    Load_1(1),
    Load_2(2),
    Load_3(3),
    Load_4(4),
    Load_5(5),
    Load_6(6),
    Load_7(7),
    Load_8(8);

    private Integer loadNumber;
    
    Loads(Integer loadNumber) {
        this.loadNumber = loadNumber;
    }
    public Integer getLoadNumber() {
        return loadNumber;
    }
    
    public static Loads getDisplayValue(Integer loadNumber) {
        for (Loads load : Loads.values()) {
            if (load.getLoadNumber() == loadNumber) {
                return load;
            }
        }
        return null;
    }
    
    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dr.setup.loadGroup." + name();
    }
}