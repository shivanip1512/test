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
	// ** NOTE ** Add more int model values here with the addition of new models.	

	// Optional model/chart choices.
	public final static int SHOW_YESTERDAY_MASK = 0x0001;
	public final static int SHOW_MULTIPLE_DAY_MASK = 0x0002;
	public final static int MULTIPLIER_MASK = 0x0004;
	public final static int DWELL_LABELS_MASK = 0x0008;
	public final static int LOAD_FACTOR_MASK = 0x0010;
}
