package com.cannontech.stars.web.util;

import java.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class CommonUtils {

    private static java.text.DecimalFormat decFormat = new java.text.DecimalFormat("0.#");

    public CommonUtils() {
    }

    public static String getDurationString(int sec) {
        String durationStr = null;

        if (sec >= 3600)
            durationStr = decFormat.format(1.0 * sec / 3600) + " Hours";
        else
            durationStr = String.valueOf(sec / 60) + " Minutes";

        return durationStr;
    }
}