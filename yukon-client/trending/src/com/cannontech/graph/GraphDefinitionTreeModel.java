package com.cannontech.graph;

/**
 * A tree model that contains GraphDefinitions
 * Creation date: (6/21/00 4:53:19 PM)
 * @author:  Aaron Lauinger 
 */
public class GraphDefinitionTreeModel extends com.cannontech.database.model.DBTreeModel {
/**
 * GraphDefinitionTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public GraphDefinitionTreeModel() {
	super( new com.cannontech.database.model.DBTreeNode(GraphClient.TREE_PARENT_LABEL) );
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 2:05:03 PM)
 * @return com.cannontech.database.data.lite.LiteBase[]
 */
public boolean isLiteTypeSupported( int liteType )
{
	return ( liteType == com.cannontech.database.data.lite.LiteTypes.GRAPHDEFINITION );
}
/**
 * Insert the method's description here.
 * Creation date: (6/21/00 4:54:15 PM)
 * @return java.lang.String
 */
public String toString() {
	return GraphClient.TREE_PARENT_LABEL;
}
/**
 * update method comment.
 */
public void update() 
{
	com.cannontech.database.model.DBTreeNode rootNode = (com.cannontech.database.model.DBTreeNode) getRoot();
	rootNode.removeAllChildren();
	
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
		java.util.List gDefs = cache.getAllGraphDefinitions();
		java.util.Iterator iter = gDefs.iterator();

		while( iter.hasNext() )
			rootNode.add( new com.cannontech.database.model.DBTreeNode(iter.next()));

	}
		 
	reload();
}
}
