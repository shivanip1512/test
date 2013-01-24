package com.cannontech.common.util;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.WordUtils;

public class EnumUtils {
    
    /**
     * Converts the supplied enum from our standard enum format to a more user friendly format. <br><br>
     * ABC_DEF_GHI > Abc Def Ghi <br>
     * WEEKDAY_WEEKEND > Weekday Weekend
     */
    public static <E extends Enum<E>> String convertToUserFriendlyEnumFormat(E enumObject) {
        Validate.notNull(enumObject);
        
        String enumString = enumObject.name();
        return WordUtils.capitalize(enumString.replace('_', ' '));
    }
    
    /**
     * Converts the supplied enum string into our standard enum format <br><br>
     * Abc Def Ghi > ABC_DEF_GHI <br>
     * Weekday Weekend > WEEKDAY_WEEKEND
     */
    public static String convertToEnumFormat(String enumString) {
        Validate.notNull(enumString);
        
        return enumString.toUpperCase().replaceAll(" ", "_");
    }
}