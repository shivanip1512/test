/*
 * Created on Mar 1, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.model;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LMScenarioModel extends DBTreeModel 
{
	/**
	 * LMScenarioModel constructor comment.
	 */
	public LMScenarioModel() {
		super(new DBTreeNode("Scenario Control"));
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (4/22/2002 2:05:03 PM)
	 * @return com.cannontech.database.data.lite.LiteBase[]
	 */
	public boolean isLiteTypeSupported( int liteType )
	{
		return ( liteType == com.cannontech.database.data.lite.LiteTypes.LMCONSTRAINT );
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (8/24/2001 11:27:44 AM)
	 * @return java.lang.String
	 */
	public String toString() {
		return "Scenario";
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (8/24/2001 10:45:51 AM)
	 */
	public void update()
	{

		com.cannontech.database.cache.DefaultDatabaseCache cache =
			com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

		synchronized (cache)
		{
			java.util.List theLMScenarios = cache.getAllLMScenarios();

			java.util.Collections.sort(theLMScenarios, com.cannontech.database.data.lite.LiteComparators.liteStringComparator);

			DBTreeNode rootNode = (DBTreeNode) getRoot();
			rootNode.removeAllChildren();

			for (int i = 0; i < theLMScenarios.size(); i++)
			{
				DBTreeNode scenarioNode = new DBTreeNode(theLMScenarios.get(i));
			
				/*if(((com.cannontech.database.data.lite.LiteLMScenario)theLMScenarios.get(i)).getScenarioID() == 0)
					scenarioNode.setIsSystemReserved(true);*/

				rootNode.add(scenarioNode);
			}
		}

		reload();
	}


}
