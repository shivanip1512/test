package com.cannontech.database.db.graph;

/**
 * Insert the type's description here.
 * Creation date: (7/10/2001 5:01:43 PM)
 * @author: 
 */
public final class GraphRenderers
{
	//model type less then 0 yields previous model type (in code). 6/7/02 SN
	//Bars are _no longer_ drawn, rather, a step or line graph is filled (_AREA) to provide the same _bar look_. 6/24/04 SN
	public final static int DONT_CHANGE = -1;
	public final static int LINE = 0;
	public final static int LINE_SHAPES = 1;
	public final static int LINE_AREA = 2;
	public final static int LINE_AREA_SHAPES = 3;
	public final static int STEP = 4;
	public final static int STEP_SHAPES = 5;
	public final static int STEP_AREA = 6;
	public final static int STEP_AREA_SHAPES = 7;
	public final static int BAR = 8;
	public final static int BAR_3D = 9;
	
	public final static int TABULAR = 10;
	public final static int SUMMARY = 11;
	public final static int DEFAULT = 12;	//Use the GDS database column
	
	public final static String DONT_CHANGE_STRING = "Invalid";
	public final static String LINE_STRING = "Line Graph";
	public final static String LINE_SHAPES_STRING = "Line/Shapes Graph";
	public final static String LINE_AREA_STRING = "Line/Area Graph";
	public final static String LINE_AREA_SHAPES_STRING = "Line/Area/Shapes Graph";
	public final static String STEP_STRING = "Step Graph";
	public final static String STEP_SHAPES_STRING = "Step Graph";
	public final static String STEP_AREA_STRING = "Step/Area (Bar Look) Graph";
	public final static String STEP_AREA_SHAPES_STRING = "Step/Area/Shapes (Bar Look) Graph";
	public final static String BAR_STRING = "Bar Graph";
	public final static String BAR_3D_STRING = "3D Bar Graph";
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
			case LINE_SHAPES:
				return LINE_SHAPES_STRING;
			case LINE_AREA:
				return LINE_AREA_STRING;
			case LINE_AREA_SHAPES:
				return LINE_AREA_SHAPES_STRING;

			case STEP:
				return STEP_STRING;
			case STEP_SHAPES:
				return STEP_SHAPES_STRING;
			case STEP_AREA:
				return STEP_AREA_STRING;
			case STEP_AREA_SHAPES:
				return STEP_AREA_SHAPES_STRING;

			case BAR:
				return BAR_STRING;
			case BAR_3D:
				return BAR_3D_STRING;
								
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
	public final static boolean isAreaGraph(int viewID)
	{
		if( viewID == LINE_AREA || viewID == LINE_AREA_SHAPES ||
			viewID == STEP_AREA || viewID == STEP_AREA_SHAPES)
			return true;
		return false;
	}
	
	public final static int getRendererID(String rendString)
	{
		int returnInt = DONT_CHANGE;
		
		if( rendString.equalsIgnoreCase(LINE_STRING))
			returnInt = LINE;
		else if (rendString.equalsIgnoreCase(LINE_SHAPES_STRING))
			returnInt = LINE_SHAPES;
		else if (rendString.equalsIgnoreCase(LINE_AREA_STRING))
			returnInt = LINE_AREA;
		else if (rendString.equalsIgnoreCase(LINE_AREA_SHAPES_STRING))
			returnInt = LINE_AREA_SHAPES;
			
		else if (rendString.equalsIgnoreCase(STEP_STRING))
			returnInt = STEP;
		else if (rendString.equalsIgnoreCase(STEP_SHAPES_STRING))
			returnInt = STEP_SHAPES;
		else if (rendString.equalsIgnoreCase(STEP_AREA_STRING))
			returnInt = STEP_AREA;
		else if (rendString.equalsIgnoreCase(STEP_AREA_SHAPES_STRING))
			returnInt = STEP_AREA_SHAPES;

		else if (rendString.equalsIgnoreCase(BAR_STRING))
			returnInt = BAR;
		else if (rendString.equalsIgnoreCase(BAR_3D_STRING))
			returnInt = BAR_3D;
						
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

