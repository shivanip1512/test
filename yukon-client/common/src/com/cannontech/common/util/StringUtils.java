package com.cannontech.common.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.cannontech.common.gui.util.TextFieldDocument;

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
     * Parase a comma separated string into an List<Integer>
     * @param s
     * @return List<Integer>
     */
    public static List<Integer> parseIntStringForList(String s) {
        List<Integer> integerList = new ArrayList<Integer>();
        if(s == null) {
            return integerList;
        }
        StringTokenizer tok = new StringTokenizer(s, ",", false);
        while(tok.hasMoreTokens()) {
            Integer newInteger = Integer.parseInt(tok.nextToken());
            integerList.add(newInteger);
        }
        return integerList;        
    }
    
    /**
	 * Parse a comma separated string into an int[]
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

	/**
	 * Parse a delimited string into List of Strings using the following delimiters:
	 * ,\t\n\r\f (comma, tab, new-line, carriage return, and form-feed characters)
	 * Each token is trimmed and does not include _blank_ tokens from string. (See parseStringsForList(String string, String delimiters)). 
	 * @param string
	 * @return
	 */
	public static List<String> parseStringsForList(String string) {
		return parseStringsForList(string, ",\t\n\r\f");
	}
	
	/**
	 * Parse a delimited string into List of Strings using delimiters.
	 * Each token is trimmed and does not include _blank_ tokens from string.
	 * @param string
	 * @param delimiters
	 * @return
	 */
	public static List<String> parseStringsForList(String string, String delimiters) {
        StringTokenizer st = new StringTokenizer(string, delimiters);
	    final List<String> tokenStrings = new ArrayList<String>(st.countTokens());
	    while (st.hasMoreTokens()) {
	    	String token = st.nextToken().trim();
	    	if (org.apache.commons.lang.StringUtils.isNotBlank(token)) {
	    		tokenStrings.add(token);
	    	}
	    }
	    return tokenStrings;
	}
	
    /**
     * First remove any whitespace, then parse the comma separated string into an int[].
     * @param s
     * @return
     */
    public static int[] parseIntStringAfterRemovingWhitespace(String s) {
        s = org.apache.commons.lang.StringUtils.deleteWhitespace(s);
        return parseIntString(s);
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

    public static String toCamelCase(String string){
        String[] words = string.split(" ");
        String resultString = "";
        for (String word : words) {
            resultString += word.substring(0,1).toUpperCase() + 
                            word.substring(1).toLowerCase();
        }
        return resultString.substring(0,1).toLowerCase() + resultString.substring(1);
    }
    
    public static String elideCenter(String input, int maxSize) {
        String ellipsis = "\u2026";
        if (input.length() <= maxSize) {
            return input;
        }
        
        int charactersToRemove = input.length() - maxSize + ellipsis.length();
        int startPoint = (input.length() - charactersToRemove + 1) / 2; // if uneven, extra character should be on left side
        StringBuilder result = new StringBuilder(maxSize);
        result.append(input.substring(0, startPoint));
        result.append(ellipsis);
        result.append(input.substring(startPoint + charactersToRemove, input.length()));
        return result.toString();
    }
    
    public static String stripNone (String value) {
        return CtiUtilities.STRING_NONE.equals(value) ? "" : value;
    }
    
    public static String intListToCommaDelimitedString(List<Integer> intList) {
        StringBuffer buffer = new StringBuffer();
        Iterator<Integer> iterator = intList.iterator();
        while(iterator.hasNext()) {
            buffer.append(iterator.next().toString());
            if(iterator.hasNext()) {
                buffer.append(",");
            }
        }
        return buffer.toString();
    }
    
    /**
     * Returns a string that has invalid DeviceGroup name characters removed and 
     * replaced with underscores.
     * @param string
     * @return
     */
    public static String removeInvalidDeviceGroupNameCharacters(String string) {
        String result = string;
        // what follows is not a perfect solution, it could produce duplicates
        // but this is unlikely in practice and a better solution would probably
        // involve generating really ugly group names (8.3 window's file names???)
        for (char badCharacter : TextFieldDocument.INVALID_CHARS_DEVICEGROUPNAME) {
            result = result.replace(badCharacter, '_');
        }
        return result;
    }
}