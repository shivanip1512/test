package com.cannontech.graph.model;

/**
 * Insert the type's description here.
 * Creation date: (7/10/2001 5:01:43 PM)
 * @author: 
 */
public interface TrendModelType
{
	//model type less then 0 yields previous model type (in code). 6/7/02 SN
	public final static int DONT_CHANGE_VIEW = -1;
	public final static int LINE_VIEW = 0;
	public final static int BAR_VIEW = 1;
	public final static int BAR_3D_VIEW = 2;
	public final static int STEP_VIEW = 3;
	public final static int SHAPES_LINE_VIEW = 4;
		
	// ** NOTE ** Add more int model values here with the addition of new models.	

	// Optional model/chart choices.
	// Additional Graph Plotting
	public final static int BASIC_MASK= 0x0001;
	public final static int LOAD_DURATION_MASK= 0x0002;
	public final static int GRAPH_MULTIPLIER = 0x0004;
	public final static int PLOT_MIN_MAX_MASK = 0x0008;
	
	// Legend Display
	public final static int LEGEND_LOAD_FACTOR_MASK = 0x0010;
	public final static int LEGEND_MIN_MAX_MASK = 0x0020;
		// Extra stuff.
//	public final static int DWELL_LABELS_MASK = 0x0040;

}
