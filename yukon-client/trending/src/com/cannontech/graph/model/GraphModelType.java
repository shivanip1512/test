package com.cannontech.graph.model;

/**
 * Insert the type's description here.
 * Creation date: (7/10/2001 5:01:43 PM)
 * @author: 
 */
public interface GraphModelType
{
	//model type less then 0 yields previous model type (in code). 6/7/02 SN
	public static final int DONT_CHANGE_MODEL = -1;
	public static final int DATA_VIEW_MODEL = 0;
	public static final int BAR_GRAPH_MODEL = 1;
	public static final int LOAD_DURATION_CURVE_MODEL= 2;
	//public static final int MULTI_DAY_LD_MODEL = 3;
	// ** NOTE ** Add more int model values here with the addition of new models.
}
