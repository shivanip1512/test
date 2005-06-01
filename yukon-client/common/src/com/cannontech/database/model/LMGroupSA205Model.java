/*
 * Created on May 17, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.model;

import com.cannontech.database.data.pao.PAOGroups;
/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LMGroupSA205Model extends DeviceTreeModel 
{
/**
 * LMGroupVersacomModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public LMGroupSA205Model() {
	super(new DBTreeNode("SA-205 Groups"));
}
/**
 * Insert the method's description here.
 * Creation date: (4/24/2002 9:24:01 AM)
 * @return java.util.List
 */
public synchronized java.util.List getCacheList(
		com.cannontech.database.cache.DefaultDatabaseCache cache ) 
{
	return cache.getAllLoadManagement();
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 4:11:23 PM)
 * @param deviceType int
 */
public boolean isDeviceValid( int category_, int class_, int type_ )
{
	return( type_ == PAOGroups.LM_GROUP_SA205
			  && class_ == com.cannontech.database.data.pao.DeviceClasses.GROUP
			  && category_ == PAOGroups.CAT_DEVICE );
}
}
