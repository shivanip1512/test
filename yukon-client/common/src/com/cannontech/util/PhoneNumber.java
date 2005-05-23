package com.cannontech.util;

/**
 * Contains validation functions and valid characters for a
 * phone number.
 *
 */
public class PhoneNumber
{

	/**
	 * Puts the given phone number string into a readable format.
	 * 
	 */
	public static String format(String phoneNumber)
	{
		if (phoneNumber == null)
		{
			return "";
		}

		if (phoneNumber.length() > 10)
		{
			return "(" + phoneNumber.substring(0, 3) + ") " + phoneNumber.substring(3, 6) + "-" + phoneNumber.substring(6, 10) + " x" + phoneNumber.substring(10, phoneNumber.length());
		}
		else if (phoneNumber.length() == 10)
		{
			return "(" + phoneNumber.substring(0, 3) + ") " + phoneNumber.substring(3, 6) + "-" + phoneNumber.substring(6, 10);
		}
		else if (phoneNumber.length() == 7)
		{
			return phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3, 7);
		}
		else
		{
			return phoneNumber;
		}
	}

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