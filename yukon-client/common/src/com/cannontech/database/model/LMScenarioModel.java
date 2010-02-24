/*
 * Created on Mar 1, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.model;

import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.yukon.IDatabaseCache;
/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LMScenarioModel extends AbstractDeviceTreeModel  
{
	/**
	 * LMScenarioModel constructor comment.
	 */
	public LMScenarioModel() {
		super(new DBTreeNode("Control Scenario"));
		
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (4/24/2002 9:24:01 AM)
	 * @return java.util.List
	 */
	public synchronized java.util.List getCacheList(IDatabaseCache cache ) 
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
		return( type_ == PAOGroups.LM_SCENARIO
				  && category_ == PAOGroups.CAT_LOADCONTROL );
	}
}