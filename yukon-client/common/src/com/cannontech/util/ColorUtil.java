package com.cannontech.util;

import java.awt.Color;

/**
 * @author ryan
 *
 * Util to convert colors to different formats.
 *
 */
public class ColorUtil
{
	private static final String _KEY = "0123456789ABCDEF";


	public static String getHex(int[] rgb)
	{
		StringBuffer sb = new StringBuffer();

		sb.append("#");

		sb.append(_KEY.substring((int)Math.floor(rgb[0] / 16), (int)Math.floor(rgb[0] / 16) + 1));

		sb.append(_KEY.substring(rgb[0] % 16, (rgb[0] % 16) + 1));

		sb.append(_KEY.substring((int)Math.floor(rgb[1] / 16), (int)Math.floor(rgb[1] / 16) + 1));

		sb.append(_KEY.substring(rgb[1] % 16, (rgb[1] % 16) + 1));

		sb.append(_KEY.substring((int)Math.floor(rgb[2] / 16), (int)Math.floor(rgb[2] / 16) + 1));

		sb.append(_KEY.substring(rgb[2] % 16, (rgb[2] % 16) + 1));

		return sb.toString();
	}

	public static int[] getRGB(String hex)
	{
		if (hex.startsWith("#"))
		{
			hex = hex.substring(1, hex.length()).toUpperCase();
		}
		else
		{
			hex = hex.toUpperCase();
		}

		int[] hexArray = new int[6];

		if (hex.length() == 6)
		{
			char[] c = hex.toCharArray();

			for (int i = 0; i < hex.length(); i++)
			{
				if (c[i] == 'A')
				{
					hexArray[i] = 10;
				}
				else if (c[i] == 'B')
				{
					hexArray[i] = 11;
				}
				else if (c[i] == 'C')
				{
					hexArray[i] = 12;
				}
				else if (c[i] == 'D')
				{
					hexArray[i] = 13;
				}
				else if (c[i] == 'E')
				{
					hexArray[i] = 14;
				}
				else if (c[i] == 'F')
				{
					hexArray[i] = 15;
				}
				else
				{
					hexArray[i] = GetterUtil.getInteger(new Character(c[i]).toString());
				}
			}
		}

		int[] rgb = new int[3];
		rgb[0] = (hexArray[0] * 16) + hexArray[1];
		rgb[1] = (hexArray[2] * 16) + hexArray[3];
		rgb[2] = (hexArray[4] * 16) + hexArray[5];

		return rgb;
	}
	
	/**
	 * Convert Color object to HTML string rapresentation (#RRGGBB).
	 * @param c color to convert
	 * @return html string rapresentation (#RRGGBB)
	*/
	public static String getHTMLColor(Color c) {
		String colorR = "0" + Integer.toHexString(c.getRed());
		colorR = colorR.substring(colorR.length() - 2);
		String colorG = "0" + Integer.toHexString(c.getGreen());
		colorG = colorG.substring(colorG.length() - 2);
		String colorB = "0" + Integer.toHexString(c.getBlue());
		colorB = colorB.substring(colorB.length() - 2);
		String html_color = "#" + colorR + colorG + colorB;
		return html_color;
	}
	
	public static String getHTMLColor(int color) {
	    return getHTMLColor(new Color(color));
	}
	
}