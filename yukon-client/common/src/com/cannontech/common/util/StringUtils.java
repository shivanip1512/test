package com.cannontech.common.util;

import java.util.StringTokenizer;

public final class StringUtils {
    /**
     * StringUtils constructor comment.
     */
    private StringUtils() {
        super();
    }

    public static String addCharBetweenWords(char addedChar, String str) {
        if (str == null) {
            return null;
        }

        StringBuffer b = new StringBuffer(str);

        for (int i = 0; i < b.length(); i++) {
            if (Character.isUpperCase(b.charAt(i))) {
                b.insert(i++, addedChar);
            }
        }

        return b.toString().trim();
    }

    public static String trimSpaces(String s) {
        int len = s.length();
        int st = 0;
        char[] val = s.toCharArray(); /* avoid getfield opcode */

        while ((st < len) && (val[st] == ' ')) {
            st++;
        }
        while ((st < len) && (val[len - 1] == ' ')) {
            len--;
        }

        return ((st > 0) || (len < s.length())) ? s.substring(st, len) : s;
    }

    public static String removeChars(char deletedChar, String str) {
        if (str == null) {
            return null;
        }

        StringBuffer b = new StringBuffer(str);

        for (int indx = b.toString().indexOf(deletedChar); 
             indx >= 0; 
             indx = b.toString().indexOf(deletedChar)) {
            b.deleteCharAt(indx);
        }

        return b.toString();
    }

    /**
     * Takes an array of strings and try to convert and put each element into an
     * array of ints. If anything goes wrong, a zero length int array is
     * returned.
     */
    public static int[] toIntArray(String[] str) {
        int[] intArr = new int[0];
        if (str != null) {
            try {
                intArr = new int[str.length];
                for (int i = 0; i < str.length; i++) {
                    intArr[i] = Integer.parseInt(str[i]);
                }
            } catch (NumberFormatException nfe) {
                intArr = new int[0];
            }
        }

        return intArr;
    }
    /**
     * Takes an array of strings and try to convert and put each element into an
     * array of float. If anything goes wrong, a zero length float array is
     * returned.
     */
    public static float[] toFloatArray(String[] str) {
        float[] intArr = new float[0];
        if (str != null) {
            try {
                intArr = new float[str.length];
                for (int i = 0; i < str.length; i++) {
                    intArr[i] = Float.parseFloat(str[i]);
                }
            } catch (NumberFormatException nfe) {
                intArr = new float[0];
            }
        }

        return intArr;
    }
    /**
	 * Parase a comma separated string into an int[]
	 * @param s
	 * @return
	 */
	public static int[] parseIntString(String s) {
		if(s == null) {
			return new int[0];
		}
		
		StringTokenizer tok = new StringTokenizer(s, ",", false);

		int n = tok.countTokens();
		int[] intArr = new int[n];
		for(int i = 0; i < intArr.length; i++) {
			String intStr = tok.nextToken();
			intArr[i] = Integer.parseInt(intStr);
		}
		return intArr;
	}
    
    public static String toStringList(Object... args) {
        return org.apache.commons.lang.StringUtils.join(args, ",");
    }
    
    /**
     * Wrapper method for use with jstl
     * @param string1 - String to compare
     * @param string2 - String to compare
     * @return True if equalsIgnoreCase is true
     */
    public static boolean equalsIgnoreCase(String string1, String string2){
        if(string1 == null){
            return string2 == null;
        }
        
        return string1.equalsIgnoreCase(string2);
    }

}