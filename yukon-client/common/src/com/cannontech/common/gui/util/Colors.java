package com.cannontech.common.gui.util;

import com.google.common.collect.ImmutableMap;

/**
 * Enumerates colors by integer and by string.
 * Provides static methods to obtain the java.awt.Color that the given
 * enumerated value represents.
 * Creation date: (10/4/00 1:54:49 PM)
 * @author: Aaron Lauinger
 */
public class Colors {

	/* 
		Add new colors to both the integer and string enumerations and
		update the two private arrays that do the mapping.
	*/
		
	public static final int GREEN_ID 	= 0;
	public static final int RED_ID 		= 1;
	public static final int WHITE_ID 	= 2;
	public static final int YELLOW_ID 	= 3;
	public static final int BLUE_ID 	= 4;
	public static final int CYAN_ID 	= 5;
	public static final int BLACK_ID 	= 6;
	public static final int ORANGE_ID 	= 7;
	public static final int MAGENTA_ID 	= 8;
	public static final int GRAY_ID 	= 9;
	public static final int PINK_ID 	= 10;

	public static final String GREEN_STR_ID 	= "Green";
	public static final String RED_STR_ID 		= "Red";
	public static final String WHITE_STR_ID 	= "White";
	public static final String YELLOW_STR_ID 	= "Yellow";
	public static final String BLUE_STR_ID 		= "Blue";
	public static final String CYAN_STR_ID 		= "Cyan";
	public static final String BLACK_STR_ID 	= "Black";
	public static final String ORANGE_STR_ID 	= "Orange";
	public static final String MAGENTA_STR_ID 	= "Magenta";
	public static final String GRAY_STR_ID 		= "Gray";
	public static final String PINK_STR_ID 		= "Pink";
	/*Map builder for conversion to web color*/
	private static ImmutableMap<Integer, String> colorPaletteToWeb = ImmutableMap.<Integer, String>builder()
	        .put(Colors.BLACK_ID, "#000000")
	        .put(Colors.BLUE_ID, "#0008FF")
	        .put(Colors.CYAN_ID, "#00D5FF")
	        .put(Colors.GRAY_ID, "#808080")
	        .put(Colors.GREEN_ID, "#15FF00")
	        .put(Colors.MAGENTA_ID, "#FF00AE")
	        .put(Colors.ORANGE_ID, "#FF9500")
	        .put(Colors.PINK_ID, "#FFB5E8")
	        .put(Colors.RED_ID, "#FF0000")
	        .put(Colors.YELLOW_ID, "#FFDD00")
	        .build();
	    
	public static String colorPaletteToWeb(int color) {
	    String retval = colorPaletteToWeb.get(color);
	    return retval == null ? "#FFFFFF" : retval;
	}
	// Mapping from integer enumerations into java.awt.Color
	private static final java.awt.Color[] IDToColorMapping =
	{
		java.awt.Color.green,
		java.awt.Color.red,
		java.awt.Color.white,
		java.awt.Color.yellow,
		java.awt.Color.blue,
		java.awt.Color.cyan,
		java.awt.Color.black,
		java.awt.Color.orange,
		java.awt.Color.magenta,
		java.awt.Color.gray,
		java.awt.Color.pink
	};	

	// Mapping from string enumerations into java.awt.Color
	private static final Object[][] IDStrToColorMapping =
	{
		{ GREEN_STR_ID, 	IDToColorMapping[GREEN_ID] 	},
		{ RED_STR_ID, 		IDToColorMapping[RED_ID] 	},
		{ WHITE_STR_ID, 	IDToColorMapping[WHITE_ID] 	},
		{ YELLOW_STR_ID, 	IDToColorMapping[YELLOW_ID] },
		{ BLUE_STR_ID, 		IDToColorMapping[BLUE_ID] 	},
		{ CYAN_STR_ID, 		IDToColorMapping[CYAN_ID] 	},
		{ BLACK_STR_ID, 	IDToColorMapping[BLACK_ID] 	},
		{ ORANGE_STR_ID, 	IDToColorMapping[ORANGE_ID] },
		{ MAGENTA_STR_ID, 	IDToColorMapping[MAGENTA_ID]},
		{ GRAY_STR_ID, 		IDToColorMapping[GRAY_ID] 	},
		{ PINK_STR_ID, 		IDToColorMapping[PINK_ID] 	}
	};
/**
 * ColorFactory constructor comment.
 */
private Colors() {
	super();
}
/**
 * Returns the java.awt.Color object is represented by the given color id.
 * Creation date: (10/4/00 2:07:05 PM)
 * @return java.awt.Color
 * @param colorID int
 */
public final static java.awt.Color getColor(int colorID) 
{
	return IDToColorMapping[colorID];
}
/**
 * Returns the java.awt.Color object is represented by the given color id string
 * Creation date: (10/4/00 2:06:50 PM)
 * @return java.awt.Color
 * @param colorStrID java.lang.String
 */
public static final java.awt.Color getColor(String colorStrID) 
{
	for( int i = 0; i < IDStrToColorMapping.length; i++ )
		if( IDStrToColorMapping[i][0].equals(colorStrID) )
			return (java.awt.Color) IDStrToColorMapping[i][1];

	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (11/6/00 11:04:48 AM)
 * @return int
 * @param c java.awt.Color
 */
public static final int getColorID(java.awt.Color c) {
	for( int i = 0; i < IDToColorMapping.length; i++ )
	{
		if( IDToColorMapping[i] == c )
			return i; 
	}

	return -1;
}
		/**
 * Insert the method's description here.
 * Creation date: (11/6/00 12:44:01 PM)
 * @return java.awt.Color[]
 */
public final static java.awt.Color[] getColors() {
	return IDToColorMapping;
}/**
 * Insert the method's description here.
 * Creation date: (11/6/00 11:43:45 AM)
 * @return java.lang.String
 * @param c int
 */
public static String getColorString(int c) {
	return (String) IDStrToColorMapping[c][0];
}
}
