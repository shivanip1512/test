package com.cannontech.database.model;

/**
 * Insert the type's description here.
 * Creation date: (4/23/2002 10:23:03 AM)
 * @author: 
 */
public interface LiteBaseTreeModel extends javax.swing.tree.TreeModel
{
/**
 * Insert the method's description here.
 * Creation date: (4/23/2002 10:31:35 AM)
 * @param lb com.cannontech.database.data.lite.LiteBase
    returns weather we inserted the LiteBase node object
 */
boolean insertTreeObject(com.cannontech.database.data.lite.LiteBase lb);
/**
 * Insert the method's description here.
 * Creation date: (4/23/2002 10:31:35 AM)
 * @param lb com.cannontech.database.data.lite.LiteBase
 */
	//A check for LiteBase types that are allowed in the DBTreeModel,
	// all subclasses should define this method
boolean isLiteTypeSupported(int liteType);
/**
 * Insert the method's description here.
 * Creation date: (4/23/2002 10:31:35 AM)
 * @param lb com.cannontech.database.data.lite.LiteBase
	returns weather we removed the LiteBase node object
 */
boolean removeTreeObject(com.cannontech.database.data.lite.LiteBase lb);
/**
 * Insert the method's description here.
 * Creation date: (4/25/2002 12:35:32 PM)
 * @param path javax.swing.tree.TreePath
 */
void treePathWillExpand(javax.swing.tree.TreePath path);
/**
 * Insert the method's description here.
 * Creation date: (4/23/2002 10:31:35 AM)
 */
void update();
/**
 * Insert the method's description here.
 * Creation date: (4/23/2002 10:31:35 AM)
 * @param lb com.cannontech.database.data.lite.LiteBase
   returns weather we updated the LiteBase node object
 */
boolean updateTreeObject(com.cannontech.database.data.lite.LiteBase lb);
}
