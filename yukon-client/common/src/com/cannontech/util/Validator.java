package com.cannontech.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Attempts to validate different types of data.
 *
 */
public class Validator
{

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
		int x = (int)c;

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

		return true;
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

	public static boolean isPhoneNumber(String phoneNumber)
	{
		return isNumber(PhoneNumber.strip(phoneNumber));
	}

}