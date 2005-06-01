package com.cannontech.database.model;

import com.cannontech.common.gui.tree.CheckNode;

/**
 * @author snebben
 *
 */
public class CBCOrderByTreeModel extends CheckBoxDBTreeModel
{
	public static final String[] ORDER_TYPE_STRINGS =
	{
		"Order by CapBank",
		"Order by Feeder",
		"Order by Substation",
		"Order by Size"
	};	
	
	/**
	 * @param root
	 */
	public CBCOrderByTreeModel()
	{
		super(new CheckNode(ModelFactory.getModelString(ModelFactory.CBC_ORDER_BY)));
		
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.database.model.LiteBaseTreeModel#isLiteTypeSupported(int)
	 */
	public boolean isLiteTypeSupported(int liteType)
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.model.LiteBaseTreeModel#update()
	 */
	public void update()
	{
		CheckNode rootNode = (CheckNode) getRoot();
		rootNode.removeAllChildren();
	
		for (int i = 0; i <  ORDER_TYPE_STRINGS.length; i++)
		{
			CheckNode cycleGroupNode = new CheckNode( ORDER_TYPE_STRINGS[i] );
			rootNode.add(cycleGroupNode);
		}
	
		reload();
	}
}
