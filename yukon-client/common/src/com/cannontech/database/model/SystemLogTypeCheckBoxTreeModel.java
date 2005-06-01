/*
 * Created on Jun 7, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.model;

import com.cannontech.common.gui.tree.CheckNode;
import com.cannontech.database.db.point.SystemLog;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SystemLogTypeCheckBoxTreeModel extends CheckBoxDBTreeModel
{
	/**
	 * @param root
	 */
	public SystemLogTypeCheckBoxTreeModel()
	{
		super(new CheckNode(ModelFactory.getModelString(ModelFactory.SYSTEMLOG_TYPES_CHECKBOX)));
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.database.model.LiteBaseTreeModel#isLiteTypeSupported(int)
	 */
	public boolean isLiteTypeSupported(int liteType)
	{
		//No liteType exists for SystemLog types.
		return false;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.model.LiteBaseTreeModel#update()
	 */
	public void update()
	{
		String availableSystemLogTypesArray[] = SystemLog.LOG_TYPE_STRINGS;
		
		CheckNode rootNode = (CheckNode) getRoot();
		rootNode.removeAllChildren();
	
		for (int i = 0; i <  availableSystemLogTypesArray.length; i++)
		{
			CheckNode cycleGroupNode = new CheckNode( availableSystemLogTypesArray[i] );
			rootNode.add(cycleGroupNode);
		}
	
		reload();
	}
}
