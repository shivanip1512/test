package com.cannontech.database.model;

import com.cannontech.common.gui.tree.CheckNode;

/*
 * Created on Dec 2, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */


/**
 * @author bjonasson
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MCTCheckBoxTreeModel extends DeviceCheckBoxTreeModel
{

	public MCTCheckBoxTreeModel() {
		super( new CheckNode("MCTs") );
}
public MCTCheckBoxTreeModel( boolean showPointNodes ) 
{
	super( showPointNodes, new CheckNode("MCT") );
}

public boolean isDeviceValid( int category_, int class_, int type_ )
{

	return ( com.cannontech.database.data.device.DeviceTypesFuncs.isMCT(type_)
			  && category_ == com.cannontech.database.data.pao.PAOGroups.CAT_DEVICE );
			 
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "MCT";
}
}

