package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
import javax.swing.tree.TreePath;

import com.cannontech.common.gui.tree.CheckNode;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteTypes;


/* *******
 * This model is good now, it was slow.
 * 
 * 
*/
public class EnergyCompanyCheckBoxTreeModel extends CheckBoxDBTreeModel 
{
	private static String TITLE_STRING = "Energy Company";
	/**
	 * EnergyCompanyCheckBoxTreeModel constructor comment.
	 * @param root javax.swing.tree.TreeNode
	 */
	public EnergyCompanyCheckBoxTreeModel()
	{
		super( new CheckNode(TITLE_STRING) );
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/22/2002 2:05:03 PM)
	 * @return com.cannontech.database.data.lite.LiteBase[]
	 */
	public boolean isLiteTypeSupported( int liteType )
	{
		return ( liteType == LiteTypes.ENERGY_COMPANY );
	}
	
	
	public synchronized void treePathWillExpand(javax.swing.tree.TreePath path)
	{
		//Watch out, this reloads the contacts every TIME!!!
		DBTreeNode node = (DBTreeNode)path.getLastPathComponent();
	
		if( node.willHaveChildren() &&
			 node.getUserObject() instanceof LiteEnergyCompany )
		{
			LiteEnergyCompany liteEC = (LiteEnergyCompany)node.getUserObject();
			
			node.removeAllChildren();
			//TODO FIX - Add LiteCustomers as the children?!
	//		for( int i = 0; i < liteEC.getgetAdditionalContacts().size(); i++ )
	//			node.add( new DBTreeNode(ciCust.getAdditionalContacts().get(i)) );
		}
		node.setWillHaveChildren(false);
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 */
	public String toString()
	{
		return TITLE_STRING;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (4/17/2002 1:58:45 PM)
	 * @param lite com.cannontech.database.data.lite.LiteBase
	 */
	public boolean updateTreeObject(LiteBase lb) 
	{
		if( lb == null || !isLiteTypeSupported(lb.getLiteType()) )
			return false;
	
		DBTreeNode node = findLiteObject( null, lb );
	
		if( node != null )
		{
			//slightyly different from the SUPER
			node.setWillHaveChildren( true );
			treePathWillExpand( new TreePath(node) );
			nodeStructureChanged( node );
	
			return true;			
		}
	
		return false;
	}
	/**
	 * This method was created in VisualAge.
	 */
	public void update() 
	{
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	
		synchronized(cache)
		{
			java.util.List energyCompanies = cache.getAllEnergyCompanies();
			java.util.Collections.sort( energyCompanies, LiteComparators.liteStringComparator );
			
			DBTreeNode rootNode = (DBTreeNode) getRoot();
			rootNode.removeAllChildren();
			for( int i = 0; i < energyCompanies.size(); i++ )
			{
				DBTreeNode ecNode = new CheckNode( energyCompanies.get(i) );
				//If this model was loaded previously...recheck the checked nodes, stored in vector checkedNodes
				if( getCheckedNodes().contains(ecNode.getUserObject()))
					((CheckNode)ecNode).setSelected(true);
				rootNode.add( ecNode );
			}
		}
	
		reload();
	}
}
