package com.cannontech.database.data.pao;


/**
 * @author ryan
 *
 */
public interface DBEditorTypes {

	public static final String PAGE_TYPE_EDITOR = "editor";
	public static final String PAGE_TYPE_DELETE = "delete";
	public static final String PAGE_TYPE_COPY = "copy";

	public static final String EDTYPE_POINT = "point";
	public static final String EDTYPE_CAPCONTROL = "capcontrol";
	public static final String EDTYPE_SCHEDULE = "schedule";
	public static final String EDTYPE_LIST_SCHEDULE = "list_schedule";



	public final static int INVALID = -1;

	//editor types
	public final static int EDITOR_POINT = 1;
	public final static int EDITOR_CAPCONTROL = 2;
	public final static int EDITOR_SCHEDULE = 3;
	public final static int LIST_SCHEDULE = 4;


	//delete types
//	public static final String DELETE_PAO = "pao";
//	public static final String DELETE_POINT = "point";
//	public final static int DELETE_PAO = 100;
//	public final static int DELETE_POINT = 101;


	//misc types
	public final static int PAO_SCHEDULE = 0 + TypeBase.MISC_OFFSET;


}