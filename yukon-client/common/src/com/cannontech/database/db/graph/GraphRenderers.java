package com.cannontech.database.db.graph;

/**
 * Insert the type's description here.
 * Creation date: (7/10/2001 5:01:43 PM)
 * @author: 
 */
public final class GraphRenderers
{
	//model type less then 0 yields previous model type (in code). 6/7/02 SN
	public final static int DONT_CHANGE = -1;
	public final static int LINE = 0;
	public final static int BAR = 1;
	public final static int BAR_3D = 2;
	public final static int STEP = 3;
	public final static int SHAPES_LINE = 4;
	public final static int TABULAR = 5;
	public final static int SUMMARY = 6;
	public final static int DEFAULT = 7;	//Use the GDS database column
	
	public final static String DONT_CHANGE_STRING = "Invalid";
	public final static String LINE_STRING = "Line Graph";
	public final static String BAR_STRING = "Bar Graph";
	public final static String BAR_3D_STRING = "3D Bar Graph";
	public final static String STEP_STRING = "Step Graph";
	public final static String SHAPES_LINE_STRING = "Shapes/Line Graph";
	public final static String TABULAR_STRING = "Tabular";
	public final static String SUMMARY_STRING = "Summary";
	public final static String DEFAULT_STRING = "Default";
	
	// ** NOTE ** Add more int model values here with the addition of new models.	

	// ** OPTIONS ** Optional model/chart choices.
	// Additional Graph Plotting
	public final static int NONE_MASK = 0x0000;
	public final static int BASIC_MASK= 0x0001;
	public final static int LOAD_DURATION_MASK= 0x0002;
	public final static int GRAPH_MULTIPLIER_MASK = 0x0004;
	public final static int PLOT_MIN_MAX_MASK = 0x0008;
	
	// Legend Display
	public final static int LEGEND_LOAD_FACTOR_MASK = 0x0010;
	public final static int LEGEND_MIN_MAX_MASK = 0x0020;

	public final static String getRendererString(int rendID)
	{
		String returnString = "";
		switch(rendID)
		{
			case LINE:
				return LINE_STRING;
			case BAR:
				return BAR_STRING;
			case BAR_3D:
				return BAR_3D_STRING;
			case STEP:
				return STEP_STRING;
			case SHAPES_LINE:
				return SHAPES_LINE_STRING;
			case TABULAR:
				return TABULAR_STRING;
			case SUMMARY:
				return SUMMARY_STRING;
			case DEFAULT:
				return DEFAULT_STRING;
			default:
				return LINE_STRING;
		}
	}
	public final static String getViewString(int viewID)
	{
		return getRendererString(viewID);
	}
	
	public final static int getRendererID(String rendString)
	{
		int returnInt = DONT_CHANGE;
		if( rendString.equalsIgnoreCase(LINE_STRING))
			returnInt = LINE;
		else if (rendString.equalsIgnoreCase(BAR_STRING))
			returnInt = BAR;
		else if (rendString.equalsIgnoreCase(BAR_3D_STRING))
			returnInt = BAR_3D;
		else if (rendString.equalsIgnoreCase(STEP_STRING))
			returnInt = STEP;
		else if (rendString.equalsIgnoreCase(SHAPES_LINE_STRING))
			returnInt = SHAPES_LINE;
		else if (rendString.equalsIgnoreCase(TABULAR_STRING))
			returnInt = TABULAR;
		else if (rendString.equalsIgnoreCase(SUMMARY_STRING))
			returnInt = SUMMARY;
		else if (rendString.equalsIgnoreCase(DEFAULT_STRING))
			returnInt = DEFAULT;
		else
			returnInt = LINE;
		
		return returnInt;
	}
	
	public final static int getViewID(String viewString)
	{	
		return getRendererID(viewString);
	}
}

