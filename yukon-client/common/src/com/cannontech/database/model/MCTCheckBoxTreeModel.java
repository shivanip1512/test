package com.cannontech.database.model;

import com.cannontech.common.gui.tree.CheckNode;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.pao.PAOGroups;

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
	public MCTCheckBoxTreeModel()
	{
		super( new CheckNode("MCTs") );
	}

	public MCTCheckBoxTreeModel( boolean showPointNodes ) 
	{
		super( showPointNodes, new CheckNode("MCT") );
	}
	public boolean isDeviceValid( int category_, int class_, int type_ )
	{
		return ( DeviceTypesFuncs.isMCT(type_) && category_ == PAOGroups.CAT_DEVICE );
	}

	public String toString()
	{
		return "MCT";
	}
}

