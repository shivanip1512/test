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
     * Converts an array of integer into a comma-separated list that is compatible 
     * with an SQL statement of the form "id in (<your list>)". The only special 
     * thing this function does is return the word "null" if the list is empty.
     * @param ids a Integer[]
     * @return a comma separated list or the word "null"
     */
    public static String convertToSqlLikeList(Integer[] ids) {
        String groupIdStr;
        if (ids.length > 0) {
            groupIdStr = org.apache.commons.lang.StringUtils.join(ids, ",");
        } else {
            groupIdStr = "null";
        }
        return groupIdStr;
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

}