/*
 * Created on Dec 2, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.model;
import com.cannontech.common.gui.tree.CheckNode;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.PAOGroups;
/**
 * @author bjonasson
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TransmitterCheckBoxTreeModel extends DeviceCheckBoxTreeModel
{
	public static String TITLE_STRING = "Transmitter";
	public TransmitterCheckBoxTreeModel()
	{
		super( new CheckNode(TITLE_STRING) );
	}

	public TransmitterCheckBoxTreeModel( boolean showPointNodes ) 
	{
		super( showPointNodes, new CheckNode(TITLE_STRING) );
	}
	
	public boolean isDeviceValid( int category_, int class_, int type_ )
	{
		return( DeviceTypesFuncs.isTransmitter(type_)
				  && DeviceClasses.isMeterClass(class_)
				  && category_ == PAOGroups.CAT_DEVICE );
	}
	public String toString()
	{
		return TITLE_STRING;
	}
}