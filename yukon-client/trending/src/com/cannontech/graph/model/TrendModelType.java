package com.cannontech.graph.model;

/**
 * Insert the type's description here.
 * Creation date: (7/10/2001 5:01:43 PM)
 * @author: 
 */
public interface TrendModelType
{
	//model type less then 0 yields previous model type (in code). 6/7/02 SN
	public final static int DONT_CHANGE_MODEL = -1;
	public final static int LINE_MODEL = 0;
	public final static int BAR_MODEL = 1;
	public final static int BAR_3D_MODEL = 2;
	public final static int STEP_MODEL = 3;

	public final static int LOAD_DURATION_LINE_MODEL = 4;
	public final static int LOAD_DURATION_STEP_MODEL = 5;

	public final static int MULTIPLE_DAYS_LINE_MODEL = 6;
	
	public final static int SHAPES_LINE_MODEL = 7;

	// ** NOTE ** Add more int model values here with the addition of new models.	

	// Optional model/chart choices.
		// Additional Graph Plotting
	public final static int PLOT_YESTERDAY_MASK = 0x0001;
	public final static int PLOT_MULTIPLE_DAY_MASK = 0x0002;
	public final static int PLOT_MIN_MAX_MASK = 0x0004;
		// Legend Display
	public final static int SHOW_LOAD_FACTOR_LEGEND_MASK = 0x0008;
	public final static int SHOW_MIN_MAX_LEGEND_MASK = 0x0010;
		// Extra stuff.
	public final static int MULTIPLIER_MASK = 0x0020;
	public final static int DWELL_LABELS_MASK = 0x0040;
	
}
