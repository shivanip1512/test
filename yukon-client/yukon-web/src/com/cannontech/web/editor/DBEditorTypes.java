package com.cannontech.web.editor;

import com.cannontech.database.data.pao.TypeBase;

/**
 * @author ryan
 *
 */
public interface DBEditorTypes {

	public final static int INVALID = -1;

	//editor types
	public final static int EDITOR_POINT = 1;
	public final static int EDITOR_CAPCONTROL = 2;
	public final static int EDITOR_SCHEDULE = 3;

	//delete types
	public final static int DELETE_PAO = 100;
	public final static int DELETE_POINT = 101;


	//misc types
	public final static int PAO_SCHEDULE = 0 + TypeBase.MISC_OFFSET;

}
