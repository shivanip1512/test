package com.cannontech.database.model;

import com.cannontech.common.gui.tree.CheckNode;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.pao.PAOGroups;

/**
 * @author bjonasson
 *	Created on Feb 9, 2004
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MCTDisconnectCheckBoxTreeModel extends DeviceCheckBoxTreeModel
{
	public static String TITLE_STRING = "Disconnect";

	public MCTDisconnectCheckBoxTreeModel()
	{
		super( new CheckNode(TITLE_STRING) );
	}
	public MCTDisconnectCheckBoxTreeModel( boolean showPointNodes ) 
	{
		super( showPointNodes, new CheckNode(TITLE_STRING) );
	}
	
	public boolean isDeviceValid( int category_, int class_, int type_ )
	{
		return ( DeviceTypesFuncs.isDisconnectMCT(type_)
				  && category_ == PAOGroups.CAT_DEVICE );
	}

	public String toString()
	{
		return TITLE_STRING;
	}
}

