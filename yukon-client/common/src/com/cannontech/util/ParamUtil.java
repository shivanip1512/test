package com.cannontech.util;

import java.text.DateFormat;
import java.util.Date;
import java.util.Enumeration;
import javax.servlet.ServletRequest;

/**
 * Util for getting parameters from the request object allowing for
 * a default value if the key is not found.
 *
 */
public class ParamUtil
{

	// Servlet Request

	public static boolean getBoolean(ServletRequest req, String param)
	{
		return GetterUtil.getBoolean(req.getParameter(param));
	}

	public static boolean getBoolean(ServletRequest req, String param, boolean defaultValue)
	{
		return get(req, param, defaultValue);
	}

	public static Date getDate(ServletRequest req, String param, DateFormat df)
	{
		return GetterUtil.getDate(req.getParameter(param), df);
	}

	public static Date getDate(ServletRequest req, String param, DateFormat df, Date defaultValue)
	{
		return get(req, param, df, defaultValue);
	}

	public static double getDouble(ServletRequest req, String param)
	{
		return GetterUtil.getDouble(req.getParameter(param));
	}

	public static double getDouble(ServletRequest req, String param, double defaultValue)
	{
		return get(req, param, defaultValue);
	}

	public static float getFloat(ServletRequest req, String param)
	{
		return GetterUtil.getFloat(req.getParameter(param));
	}

	public static float getFloat(ServletRequest req, String param, float defaultValue)
	{
		return get(req, param, defaultValue);
	}

	public static int getInteger(ServletRequest req, String param)
	{
		return GetterUtil.getInteger(req.getParameter(param));
	}

	public static int getInteger(ServletRequest req, String param, int defaultValue)
	{
		return get(req, param, defaultValue);
	}

	public static long getLong(ServletRequest req, String param)
	{
		return GetterUtil.getLong(req.getParameter(param));
	}

	public static long getLong(ServletRequest req, String param, long defaultValue)
	{
		return get(req, param, defaultValue);
	}

	public static short getShort(ServletRequest req, String param)
	{
		return GetterUtil.getShort(req.getParameter(param));
	}

	public static short getShort(ServletRequest req, String param, short defaultValue)
	{
		return get(req, param, defaultValue);
	}

	public static String getString(ServletRequest req, String param)
	{
		return GetterUtil.getString(req.getParameter(param));
	}

	public static String getString(ServletRequest req, String param, String defaultValue)
	{
		return get(req, param, defaultValue);
	}

	public static boolean get(ServletRequest req, String param, boolean defaultValue)
	{
		return GetterUtil.get(req.getParameter(param), defaultValue);
	}

	public static Date get(ServletRequest req, String param, DateFormat df, Date defaultValue)
	{
		return GetterUtil.get(req.getParameter(param), df, defaultValue);
	}

	public static double get(ServletRequest req, String param, double defaultValue)
	{
		return GetterUtil.get(req.getParameter(param), defaultValue);
	}

	public static float get(ServletRequest req, String param, float defaultValue)
	{
		return GetterUtil.get(req.getParameter(param), defaultValue);
	}

	public static int get(ServletRequest req, String param, int defaultValue)
	{
		return GetterUtil.get(req.getParameter(param), defaultValue);
	}

	public static long get(ServletRequest req, String param, long defaultValue)
	{
		return GetterUtil.get(req.getParameter(param), defaultValue);
	}

	public static short get(ServletRequest req, String param, short defaultValue)
	{
		return GetterUtil.get(req.getParameter(param), defaultValue);
	}

	public static String get(ServletRequest req, String param, String defaultValue)
	{
		String returnValue = GetterUtil.get(req.getParameter(param), defaultValue);

		if (returnValue != null)
		{
			return returnValue.trim();
		}

		return null;
	}

	public static void print(ServletRequest req)
	{
		Enumeration e = req.getParameterNames();

		while (e.hasMoreElements())
		{
			String param = (String)e.nextElement();

			String[] values = req.getParameterValues(param);

			for (int i = 0; i < values.length; i++)
			{
				System.out.println(param + "[" + i + "] = " + values[i]);
			}
		}
	}

}