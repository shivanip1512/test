package com.cannontech.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Attempts to validate different types of data.
 */
public class Validator
{
    //"standard" 48-bit, 6-octet MAC Address. E.g. xx:xx:xx:xx:xx:xx
    private static final Pattern shortMacPattern = Pattern.compile("([\\da-fA-F]{2}:){5}[\\da-fA-F]{2}");
    //IPv6, 64-bit, 8-octet MAC Address. E.g. xx:xx:xx:xx:xx:xx:xx:xx
    private static final Pattern longMacPattern = Pattern.compile("([\\da-fA-F]{2}:){7}[\\da-fA-F]{2}");
    
	public static boolean isAddress(String address)
	{
		if (isNull(address))
		{
			return false;
		}

		char[] c = address.toCharArray();

		for (int i = 0; i < c.length; i++)
		{
			if ((!isChar(c[i])) && (!isDigit(c[i])) && (!Character.isWhitespace(c[i])))
			{
				return false;
			}
		}

		return true;
	}

	public static boolean isChar(char c)
	{
		return Character.isLetter(c);
	}

	public static boolean isChar(String s)
	{
		if (isNull(s))
		{
			return false;
		}

		char[] c = s.toCharArray();

		for (int i = 0; i < c.length; i++)
		{
			if (!isChar(c[i]))
			{
				return false;
			}
		}

		return true;
	}

	public static boolean isDigit(char c)
	{
		int x = c;

		if ((x >= 48) && (x <= 57))
		{
			return true;
		}

		return false;
	}

	public static boolean isDigit(String s)
	{
		if (isNull(s))
		{
			return false;
		}

		char[] c = s.toCharArray();

		for (int i = 0; i < c.length; i++)
		{
			if (!isDigit(c[i]))
			{
				return false;
			}
		}

		return true;
	}

	public static boolean isHex(String s)
	{
		if (isNull(s))
		{
			return false;
		}
		
        String  regex = "[0-9a-fA-F]*";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(s);
        
		return m.matches();
	}

	public static boolean isHTML(String s)
	{
		if (isNull(s))
		{
			return false;
		}

		if (((s.indexOf("<html>") != -1) || (s.indexOf("<HTML>") != -1)) && ((s.indexOf("</html>") != -1) || (s.indexOf("</HTML>") != -1)))
		{

			return true;
		}

		return false;
	}

	public static boolean isEmailAddress(String ea)
	{

        if (ea.startsWith("postmaster@"))
        {
            return false;
        }
        if (ea.startsWith("root@"))
        {
            return false;
        }
        if( ea.startsWith(".") )
        {
            return false;
        }
        int at = ea.indexOf('@');
        if( at == 0 || at == -1 ){
            return false;
        }
        if( ea.charAt(at-1) == '.' )
        {
            return false;
        }
        
        String  regex = "\\b[a-zA-Z0-9.!#$%&'*+-/=?^_`{|}~]+@([a-zA-Z0-9-_]+\\.)+[a-zA-Z]{2,4}\\b";
        String in = ea;
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(in);
        
        return m.matches();
	}

	public static boolean isName(String name)
	{
		if (isNull(name))
		{
			return false;
		}

		char[] c = name.trim().toCharArray();

		for (int i = 0; i < c.length; i++)
		{
			if (((!isChar(c[i])) && (!Character.isWhitespace(c[i]))) || (c[i] == ','))
			{
				return false;
			}
		}

		return true;
	}

	public static boolean isNumber(String number)
	{
		if (isNull(number))
		{
			return false;
		}

		char[] c = number.toCharArray();

		for (int i = 0; i < c.length; i++)
		{
			if (!isDigit(c[i]))
			{
				return false;
			}
		}

		return true;
	}

	public static boolean isNull(String s)
	{
		if (s == null)
		{
			return true;
		}

		s = s.trim();

		if ((s.equals(StringPool.NULL)) || (s.equals(StringPool.BLANK)))
		{
			return true;
		}

		return false;
	}

	public static boolean isNotNull(String s)
	{
		return !isNull(s);
	}

	public static boolean isPassword(String password)
	{
		if (isNull(password))
		{
			return false;
		}

		if (password.length() < 4)
		{
			return false;
		}

		char[] c = password.toCharArray();

		for (int i = 0; i < c.length; i++)
		{
			if ((!isChar(c[i])) && (!isDigit(c[i])))
			{

				return false;
			}
		}

		return true;
	}

	/**
	 * Validate a EUI 48 MAC Address string (48-bytes, e.g. xx:xx:xx:xx:xx:xx).
	 * To allow 64-bit addresses, use <code>isMacAddress(String macAddress, boolean allowLongAddress)</code>
	 */
	public static boolean isMacAddress(String macAddress) {
	    return isMacAddress(macAddress, false);
	}
	
	/**
	 * Validate a MAC Address string - either 48 or 64 bytes
	 * (e.g. xx:xx:xx:xx:xx:xx or xx:xx:xx:xx:xx:xx:xx:xx)
	 * @param allowLongAddress Permit 48-byte and 64-byte address strings.
	 */
	public static boolean isMacAddress(String macAddress, boolean allowLongAddress) {
        Matcher shortMac = shortMacPattern.matcher(macAddress);
        Matcher longMac = longMacPattern.matcher(macAddress);
        
        return shortMac.matches() || (longMac.matches() && allowLongAddress);
	}
	
	public static boolean isInstallCode(String installCode) {
        String  regex = "([\\da-fA-F]{2}:){7}[\\da-fA-F]{2}";
        String in = installCode;
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(in);
        
        return m.matches();
	}
}