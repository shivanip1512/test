package com.cannontech.graph.model;

/**
 * Insert the type's description here.
 * Creation date: (7/10/2001 5:01:43 PM)
 * @author: 
 */
public final class TrendModelType
{
	//model type less then 0 yields previous model type (in code). 6/7/02 SN
	public final static int DONT_CHANGE_VIEW = -1;
	public final static int LINE_VIEW = 0;
	public final static int BAR_VIEW = 1;
	public final static int BAR_3D_VIEW = 2;
	public final static int STEP_VIEW = 3;
	public final static int SHAPES_LINE_VIEW = 4;
	public final static int TABULAR_VIEW = 5;
	public final static int SUMMARY_VIEW = 6;

	public final static String DONT_CHANGE_VIEW_STRING = "Invalid";
	public final static String LINE_VIEW_STRING = "Line Graph";
	public final static String BAR_VIEW_STRING = "Bar Graph";
	public final static String BAR_3D_VIEW_STRING = "3D Bar Graph";
	public final static String STEP_VIEW_STRING = "Step Graph";
	public final static String SHAPES_LINE_VIEW_STRING = "Shapes/Line Graph";
	public final static String TABULAR_VIEW_STRING = "Tabular";
	public final static String SUMMARY_VIEW_STRING = "Summary";
	
	// ** NOTE ** Add more int model values here with the addition of new models.	

	// ** OPTIONS ** Optional model/chart choices.
	// Additional Graph Plotting
	public final static int NONE_MASK = 0x0000;
	public final static int BASIC_MASK= 0x0001;
	public final static int LOAD_DURATION_MASK= 0x0002;
	public final static int GRAPH_MULTIPLIER = 0x0004;
	public final static int PLOT_MIN_MAX_MASK = 0x0008;
	
	// Legend Display
	public final static int LEGEND_LOAD_FACTOR_MASK = 0x0010;
	public final static int LEGEND_MIN_MAX_MASK = 0x0020;


	public final static String getViewString(int viewID)
	{
		String returnString = "";
		switch(viewID)
		{
			case LINE_VIEW:
				returnString =  LINE_VIEW_STRING;
				break;
			case BAR_VIEW:
				returnString =  BAR_VIEW_STRING;
				break;
			case BAR_3D_VIEW:
				returnString =  BAR_3D_VIEW_STRING;
				break;
			case STEP_VIEW:
				returnString =  STEP_VIEW_STRING;
				break;
			case SHAPES_LINE_VIEW:
				returnString =  SHAPES_LINE_VIEW_STRING;
				break;		
			case TABULAR_VIEW:
				returnString = TABULAR_VIEW_STRING;
				break;
			case SUMMARY_VIEW:
				returnString = SUMMARY_VIEW_STRING;
				break;
			default:
				returnString = LINE_VIEW_STRING;
		}
		return returnString;
	}
	
	public final static int getViewID(String viewString)
	{
		int returnInt = DONT_CHANGE_VIEW;
		if( viewString.equalsIgnoreCase(LINE_VIEW_STRING))
			returnInt = LINE_VIEW;
		else if (viewString.equalsIgnoreCase(BAR_VIEW_STRING))
			returnInt = BAR_VIEW;
		else if (viewString.equalsIgnoreCase(BAR_3D_VIEW_STRING))
			returnInt = BAR_3D_VIEW;
		else if (viewString.equalsIgnoreCase(STEP_VIEW_STRING))
			returnInt = STEP_VIEW;
		else if (viewString.equalsIgnoreCase(SHAPES_LINE_VIEW_STRING))
			returnInt = SHAPES_LINE_VIEW;
		else if (viewString.equalsIgnoreCase(TABULAR_VIEW_STRING))
			returnInt = TABULAR_VIEW;
		else if (viewString.equalsIgnoreCase(SUMMARY_VIEW_STRING))
			returnInt = SUMMARY_VIEW;
		else
			returnInt = LINE_VIEW;
		
		return returnInt;
	}
		
}

