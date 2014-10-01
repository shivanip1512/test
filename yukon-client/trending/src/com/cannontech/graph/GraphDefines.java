package com.cannontech.graph;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import com.cannontech.common.util.Pair;

public interface GraphDefines {
    public static java.util.Comparator timeAndValuePair_ValueComparator = new java.util.Comparator() {
        @Override
        public int compare(Object o1, Object o2) {
            Double thisVal = (Double) ((Pair) o1).getSecond();
            Double anotherVal = (Double) ((Pair) o2).getSecond();
            return (thisVal.doubleValue() > anotherVal.doubleValue() ? -1
                : (thisVal.doubleValue() == anotherVal.doubleValue() ? 0 : 1));
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    };

    public String TITLE_HEADER_BGCOLOR = "#FFFFFF";
    public String HEADER_CELL_BGCOLOR = "#999966";
    public String TABLE_CELL_BGCOLOR = "#CCCC99";

    public static final String HELP_FILE = "Yukon_Trending_Help.chm";

    // old GraphDataFormats interface removed and fields moved into here.
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    // datePopupFormat

    public static SimpleDateFormat multipleDaystimedateFormat = new SimpleDateFormat("MM/dd HH:mm");
    public static SimpleDateFormat timeFormat_HH_mm = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat timeFormat_HH_mm_ss = new SimpleDateFormat("HH:mm:ss");
    public static SimpleDateFormat timeFormat_HH_mm_ss_SSS = new SimpleDateFormat("HH:mm:ss.SSS");
    public static SimpleDateFormat extendedTimeFormat = new SimpleDateFormat("HH:mm:ss");

    public static SimpleDateFormat dateTimeformat = new SimpleDateFormat("M/dd/yyyy  HH:mm:ss");
    public static SimpleDateFormat extendedDateTimeformat = new SimpleDateFormat("EEE MMM d HH:mm:ss a z yyyy");

    public static DecimalFormat valueFormat = new DecimalFormat("0.000");
    public static DecimalFormat percentFormat = new DecimalFormat(" ##0.0% ");

    public final Character[] axisChars = new Character[] { new Character('L'), new Character('R') };
    public final int PRIMARY_AXIS = 0; // LEFT yAxis
    public final int SECONDARY_AXIS = 1; // RIGHT yAxis

    public static java.text.SimpleDateFormat LEGEND_FORMAT = new java.text.SimpleDateFormat("MMM dd");
    public static java.text.SimpleDateFormat CATEGORY_FORMAT_MMM_dd_HH_mm = new java.text.SimpleDateFormat(
        " MMM dd HH:mm ");
    public static java.text.SimpleDateFormat CATEGORY_FORMAT_MMM_dd_HH_mm_ss = new java.text.SimpleDateFormat(
        " MMM dd HH:mm:ss ");
    public static java.text.SimpleDateFormat CATEGORY_FORMAT_MMM_dd_HH_mm_ss_SSS = new java.text.SimpleDateFormat(
        " MMM dd HH:mm:ss.SSS ");
}
