package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */

public class EditableVersacomSerialModel extends DBTreeModel
{
	public static java.util.Vector serNumVector = null;

/**
 * LMGroupVersacomSerialModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public EditableVersacomSerialModel() {
	super(new DBTreeNode("VERSACOM Serial #"));
}
/**
 * Insert the method's description here.
 * Creation date: (2/26/2002 10:40:12 AM)
 * @return java.util.Vector
 */
public static java.util.Vector getSerialNumberVector()
{
	return serNumVector;
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 2:05:03 PM)
 * @return com.cannontech.database.data.lite.LiteBase[]
 */
public boolean isLiteTypeSupported( int liteType )
{
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (2/26/2002 10:38:34 AM)
 * @param newSerNumVector java.util.Vector
 */
public static void setSerialNumberVector(java.util.Vector newSerNumVector)
{
	serNumVector = newSerNumVector;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "VERSACOM Serial #";
}
/**
 * update method comment.
 */
public void update()
{
	DBTreeNode rootNode = (DBTreeNode) getRoot();
	rootNode.removeAllChildren();

	int vectorSize = serNumVector.size();
	String [] vectorString = new String [vectorSize];
	serNumVector.copyInto(vectorString);
	java.util.Arrays.sort(vectorString);
	
	for ( int i = 0; i < vectorSize; i++ )
	{

		DBTreeNode deviceNode = new DBTreeNode ( vectorString[i]);
		rootNode.add(deviceNode);
		
	}

	reload();
}
}
