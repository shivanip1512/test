package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.database.data.pao.CustomerTypes;
import com.cannontech.database.data.lite.LiteCustomerContact;
import com.cannontech.database.data.lite.LiteBase;


/* This model is terrible and reloads the entire thing from cache
  when ever there is any change(Update,delete,add).
  It was left this way because it is so specialized.
  It is very scottish for CRRRRAAAAAAPPPP!
*/
public class CICustomerTableModel extends DBTreeModel 
{
/**
 * MCTTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public CICustomerTableModel() {
	super( new DBTreeNode("Customers") );
}
/**
 * Insert the method's description here.
 * Creation date: (4/17/2002 1:58:45 PM)
 * @param lite com.cannontech.database.data.lite.LiteBase
 */
public boolean insertTreeObject( LiteBase lb ) 
{
	update();

	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 4:11:23 PM)
 * @param deviceType int
 */
public boolean isDeviceValid( int category_, int class_, int type_ )
{
	return( class_ == com.cannontech.database.data.pao.PAOGroups.CLASS_CUSTOMER
			  && type_ == CustomerTypes.CI_CUSTOMER
			  && category_ == com.cannontech.database.data.pao.PAOGroups.CAT_CUSTOMER );
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 2:05:03 PM)
 * @return com.cannontech.database.data.lite.LiteBase[]
 */
public boolean isLiteTypeSupported( int liteType )
{
	return ( liteType == com.cannontech.database.data.lite.LiteTypes.YUKON_PAOBJECT
		  		|| liteType == com.cannontech.database.data.lite.LiteTypes.CUSTOMER_CONTACT );
}
/**
 * Insert the method's description here.
 * Creation date: (4/17/2002 1:58:45 PM)
 * @param lite com.cannontech.database.data.lite.LiteBase
 */
public boolean removeTreeObject(LiteBase lb) 
{
	update();

	return false;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "Customer";
}
/**
 * This method was created in VisualAge.
 */
public void update() 
{
	com.cannontech.database.cache.DefaultDatabaseCache cache =
					com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
		java.util.List customers = cache.getAllCustomers();
		java.util.Collections.sort( customers, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
		
		java.util.List customerContacts = cache.getAllCustomerContacts();
		java.util.Collections.sort( customerContacts, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );

		DBTreeNode rootNode = (DBTreeNode) getRoot();
		rootNode.removeAllChildren();
		
		int custID, custClass;
		int custType, custCat;
		
		for( int i = 0; i < customers.size(); i++ )
		{
			custClass = ((com.cannontech.database.data.lite.LiteYukonPAObject)customers.get(i)).getPaoClass();
			custType = ((com.cannontech.database.data.lite.LiteYukonPAObject)customers.get(i)).getType();
			custCat = ((com.cannontech.database.data.lite.LiteYukonPAObject)customers.get(i)).getCategory();

			if( isDeviceValid(custCat, custClass, custType)  )
			{
				DBTreeNode custNode = new DBTreeNode( customers.get(i));
				rootNode.add( custNode );

				custID = ((com.cannontech.database.data.lite.LiteYukonPAObject)customers.get(i)).getYukonID();

				for( int j = 0; j < customerContacts.size(); j++ )
				{
					if( ((com.cannontech.database.data.lite.LiteCustomerContact)customerContacts.get(j)).getDeviceID() == custID )
					{
						custNode.add( new DBTreeNode( customerContacts.get(j)) );
					}
				}

			}
		}
	}

	reload();
}
/**
 * Insert the method's description here.
 * Creation date: (4/17/2002 1:58:45 PM)
 * @param lite com.cannontech.database.data.lite.LiteBase
 */
public boolean updateTreeObject(LiteBase lb) 
{
	update();

	return false;
}
}
