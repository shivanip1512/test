package com.cannontech.database.model;

import com.cannontech.common.gui.tree.CheckNode;

/**
 * @author bjonasson
 *	Created on Feb 9, 2004
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MCTDisconnectCheckBoxTreeModel extends DeviceCheckBoxTreeModel
{

	public MCTDisconnectCheckBoxTreeModel() {
		super( new CheckNode("Disconnects") );
}
public MCTDisconnectCheckBoxTreeModel( boolean showPointNodes ) 
{
	super( showPointNodes, new CheckNode("Disconnect") );
}

public boolean isDeviceValid( int category_, int class_, int type_ )
{

	return ( com.cannontech.database.data.device.DeviceTypesFuncs.isDisconnectMCT(type_)
			  && category_ == com.cannontech.database.data.pao.PAOGroups.CAT_DEVICE );
			 
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "Disconnect";
}
}

