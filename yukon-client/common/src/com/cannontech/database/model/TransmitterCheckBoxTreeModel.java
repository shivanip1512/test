/*
 * Created on Dec 2, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.model;
import com.cannontech.common.gui.tree.CheckNode;
/**
 * @author bjonasson
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TransmitterCheckBoxTreeModel extends DeviceCheckBoxTreeModel
{

	public TransmitterCheckBoxTreeModel() {
		super( new CheckNode("Transmitters") );
}

public TransmitterCheckBoxTreeModel( boolean showPointNodes ) 
{
	super( showPointNodes, new CheckNode("Transmitter") );
}

public boolean isDeviceValid( int category_, int class_, int type_ )
{
	return( com.cannontech.database.data.device.DeviceTypesFuncs.isTransmitter(type_)
			  && com.cannontech.database.data.pao.DeviceClasses.isMeterClass(class_)
			  && category_ == com.cannontech.database.data.pao.PAOGroups.CAT_DEVICE );
}
public String toString() {
	return "Transmitter";
}
}