/*
 * Created on Dec 2, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.model;
import com.cannontech.common.gui.tree.CheckNode;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.pao.PAOGroups;

/**
 * @author bjonasson
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CommChannelCheckBoxTreeModel extends DeviceCheckBoxTreeModel
{
	public static String TITLE_STRING = "Comm Channels";
	
	public CommChannelCheckBoxTreeModel()
	{
		super( new CheckNode(TITLE_STRING) );
	}

	public CommChannelCheckBoxTreeModel( boolean showPointNodes ) 
	{
		super( showPointNodes, new CheckNode(TITLE_STRING) );
	}
	
	public boolean isDeviceValid( int category_, int class_, int type_ )
	{
		return ( class_ == PAOGroups.CLASS_PORT && category_ == PAOGroups.CAT_PORT );
	}
	
	public synchronized java.util.List getCacheList( DefaultDatabaseCache cache ) 
	{
		return cache.getAllPorts();
	}
	
	public String toString()
	{
		return TITLE_STRING;
	}
}