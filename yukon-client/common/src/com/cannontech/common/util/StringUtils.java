package com.cannontech.common.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

public final class StringUtils {
    
    /** Returns the percentage formatted to the pattern provided or ###.##% if pattern is null. */
    public static String percent(int total, int count, int maxFractionDigits, String pattern) {
        
        pattern = pattern == null ? "###.##%" : pattern;
        double percent = count / (double)total;
        
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        decimalFormat.setMaximumFractionDigits(maxFractionDigits);
        decimalFormat.setMinimumFractionDigits(0);
        
        return decimalFormat.format(percent);
    }
    
    /** Returns the percentage formatted as ###.##% */
    public static String percent(int total, int count, int maxFractionDigits) {
        return percent(total, count, maxFractionDigits, "###.##%");
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

    /**
     * Separates camelCase str with addedChar. Opposite of removeChars method.
     * Example: str = "RampOutRandomRest", addedChar = " " 
     * Result: "Ramp Out Random Rest"
     */
    public static String addCharBetweenWords(char addedChar, String str) {
        return org.apache.commons.lang3.StringUtils.join(org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase(str), addedChar).trim();
    }

    /** 
     * Removes deletedChar from str. Opposite of addCharBetweenWords method.
     * Example: str = Ramp Out Random Rest, deletedChar = " " 
     * Result: "RampOutRandomRest"
     */
    public static String removeChars(char deletedChar, String str) {
        return org.apache.commons.lang3.StringUtils.remove(str, deletedChar);
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
	    	if (org.apache.commons.lang3.StringUtils.isNotBlank(token)) {
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
        s = org.apache.commons.lang3.StringUtils.deleteWhitespace(s);
        return parseIntString(s);
    }
    
    public static String toStringList(Object... args) {
        return org.apache.commons.lang3.StringUtils.join(args, ",");
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

    public static String listAsJsSafeString(List<String> input) {

        Object[] listeElems = new Object[input.size()];

        int index = 0;
        for (String item : input) {
            listeElems[index] = "'" + escapeXmlAndJavascript(item) + "'";
            index++;
        }
        String result =  "[" + toStringList(listeElems) + "]";
        return result;
    }

    public static List<String> restoreJsSafeList(String input) {
        List<String> result = Lists.newArrayList();

        if( org.apache.commons.lang3.StringUtils.isEmpty(input)) {
            return result;
        }
        if( input.equals("[]")) {
            return result;
        }

        input = input.replaceAll("(^\\[')|('\\]$)", "");
        String[] items = input.split("','");

        for(String item : items){
            String f = item.replaceAll("\\\\'", "\\'");
            f = f.replaceAll("\\\\\"", "\"");
            result.add(f);
        }
        return result;
    }

    public static String escapeXmlAndJavascript (String input) {
        String result = input;
        result = result.replaceAll("\\\\", "\\\\\\\\");
        result = result.replaceAll("\t", "\\t");
        result = result.replaceAll("\n", "\\n");
        result = result.replaceAll("&", "&#x26");
        result = result.replaceAll("'", "\\\\&apos;");
        result = result.replaceAll("\"", "\\\\&quot;");
        result = result.replaceAll("<", "&lt;");
        result = result.replaceAll(">", "&gt;");
        return result;
    }
    
    public static String colonizeMacAddress(String mac) {
        
        Pattern regex = Pattern.compile("(..)(..)(..)(..)(..)(..)");
        Matcher m = regex.matcher(mac);
        if (m.matches()) {
            return String.format("%s:%s:%s:%s:%s:%s",
                    m.group(1), m.group(2), m.group(3), m.group(4), m.group(5), m.group(6));
        } else {
            return mac;
        }
        
    }
    
    /**
     * Return true if value contain elements only from pattern else false.
     * @param isRepetationAllowed : Set true if str can contain elements more than one (element frequency > 1) else false.
     */
    public static boolean isStringMatchesWithPattern(String str, String pattern, boolean isRepetationAllowed) {
        String newString = org.apache.commons.lang3.StringUtils.EMPTY;
        if (str.equals(pattern)) {
            return true;
        } else if (str.length() > pattern.length() && !isRepetationAllowed) {
            // Invalid Value.
            return false;
        } else {
            for (int i = 0; i < str.length(); i++) {
                if (pattern.contains(Character.toString(str.charAt(i)))) {
                    if (!isRepetationAllowed) {
                        if (newString.contains(Character.toString(str.charAt(i)))) {
                            return false;
                        }
                        newString += Character.toString(str.charAt(i));
                    }
                } else {
                    return false;
                }
            }
        }
        // If you are here, that mean you have crossed all the levels hence return true.
        return true;
    }
    
    /**
     * Arrange the elements of str as per the elements order in pat.
     * Example : If str = "ADC" and pat = "ABCD". Return ACD (Elements order as per the pat)
     */
    public static String formatStringWithPattern(String str, String pat) {
        String formatedString = org.apache.commons.lang3.StringUtils.EMPTY;
        for (int i = 0; i < pat.length(); i++) {
            if (str.contains(Character.toString(pat.charAt(i)))) {
                formatedString += Character.toString(pat.charAt(i));
            }
        }
        return formatedString;
    }
    
    /**
     * Converts integer value to binary value.
     */
    public static String convertIntegerToBinary(Integer value) {
        StringBuffer binaryValue = new StringBuffer();
        for (int i = 0; i < 16; i++) {
            if ((value & 1) == 0) {
                binaryValue.append(0);
            } else {
                binaryValue.append(1);
            }
            value >>= 1;
        }
        return binaryValue.toString();
    }

    /**
     * Converts binary value to integer.
     */
    public static Integer convertBinaryToInteger(String binaryValue) {
        int val = 0;
        for (int i = binaryValue.length() - 1; i >= 0; i--) {
            val <<= 1;
            if (binaryValue.charAt(i) == '1') {
                val += 1;
            }
        }
        return val;
    }
    
}