package com.cannontech.common.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.google.common.collect.Lists;

public final class StringUtils {
    
    private StringUtils() {}
    
    public static String percent(int total, int count, int maxFractionDigits) {
        
        double percent = count / (double)total;
        
        DecimalFormat decimalFormat = new DecimalFormat("###.##%");
        decimalFormat.setMaximumFractionDigits(maxFractionDigits);
        decimalFormat.setMinimumFractionDigits(0);
        
        return decimalFormat.format(percent);
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
}