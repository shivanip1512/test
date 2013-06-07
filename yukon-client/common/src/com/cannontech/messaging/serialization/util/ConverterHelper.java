package com.cannontech.messaging.serialization.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class ConverterHelper {
    private static final long MAX_UINT_VALUE = 0xFFFFFFFFL;

    public static long dateToMillisec(Date date) {
        return date.getTime();
    }

    public static long calendarToMillisec(GregorianCalendar calendar) {
        return calendar.getTimeInMillis();
    }

    public static Date millisecToDate(long millisec) {
        return new Date(millisec);
    }

    public static GregorianCalendar millisecToCalendar(long millisec) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(millisec);
        return cal;
    }

    public static <T> List<T> toList(final T[] array) {
        List<T> list = new ArrayList<T>(array.length);
        for (T element : array) {
            list.add(element);
        }
        return list;
    }

    public static List<Integer> toList(final int[] array) {
        List<Integer> list = new ArrayList<Integer>(array.length);

        for (int element : array) {
            list.add(element);
        }
        return list;
    }

    public static int[] toIntArray(final List<Integer> intList) {
        if (intList == null) {
            return null;
        }

        int[] array = new int[intList.size()];

        for (int i = array.length - 1; i > 0; i--) {
            array[i] = intList.get(i).intValue();
        }
        return array;
    }

    public static double[] toDoubleArray(final List<Double> doubleList) {
        if (doubleList == null) {
            return null;
        }

        double[] array = new double[doubleList.size()];

        for (int i = array.length - 1; i > 0; i--) {
            array[i] = doubleList.get(i).doubleValue();
        }
        return array;
    }

    public static boolean intToBool(int intBool) {
        return intBool != 0;
    }

    public static int boolToInt(boolean bool) {
        return bool ? 1 : 0;
    }

    public static int UnsignedToInt(long unsigned) {
        return (int) (MAX_UINT_VALUE & unsigned);
    }

    public static long IntToUnsigned(int i) {
        return MAX_UINT_VALUE & i;
    }
}
