package com.cannontech.database.model;

import java.util.Vector;


/**
 * This type was created in VisualAge.
 */

public class EditableTextModel extends DBTreeModel
{
	public java.util.Vector entryVector = null;

	/**
	 * EditableLCRSerialModel constructor comment.
	 * @param root javax.swing.tree.TreeNode
	 */
	public EditableTextModel(String sortByText) {
		super(new DBTreeNode(sortByText));
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (2/26/2002 10:40:12 AM)
	 * @return java.util.Vector
	 */
	public java.util.Vector getEntryVector()
	{
		if (entryVector == null)
			entryVector = new Vector();
		return entryVector;
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
	public void setEntryVector(java.util.Vector newEntryVector)
	{
		entryVector = newEntryVector;
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 */
	//Use the super's method
//	public String toString() {
//		return "LCR Serial #";
//	}
	/**
	 * update method comment.
	 */
	public void update()
	{
		DBTreeNode rootNode = (DBTreeNode) getRoot();
		rootNode.removeAllChildren();
	
		int vectorSize = getEntryVector().size();
		String [] vectorString = new String [vectorSize];
		getEntryVector().copyInto(vectorString);
		java.util.Arrays.sort(vectorString);
		
		for ( int i = 0; i < vectorSize; i++ )
		{
	
			DBTreeNode deviceNode = new DBTreeNode ( vectorString[i]);
			rootNode.add(deviceNode);
		}
		reload();
	}
}