package com.cannontech.util;

/**
 * Contains validation functions and valid characters for a
 * phone number.
 *
 */
public class PhoneNumber
{

    /**
	 * Returns the given digits from a phone number
	 * 
	 */
	public static String strip(String phoneNumber)
	{
		return extractDigits(phoneNumber);
	}

	public static String extractDigits(String s)
	{
		if (s == null)
		{
			return "";
		}

		char[] c = s.toCharArray();

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < c.length; i++)
		{
			if (Validator.isDigit(c[i]))
			{
				sb.append(c[i]);
			}
		}

		return sb.toString();
	}

}