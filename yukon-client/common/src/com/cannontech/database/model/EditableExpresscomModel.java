package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */

public class EditableExpresscomModel extends EditableTextModel
{
	private static String title = "Expresscom Serial";
	/**
	 * EditableLCRSerialModel constructor comment.
	 * @param root javax.swing.tree.TreeNode
	 */
	public EditableExpresscomModel() {
		super(title);
	}
	/**
	 * EditableLCRSerialModel constructor comment.
	 * @param root javax.swing.tree.TreeNode
	 */
	public EditableExpresscomModel(String sortByTitle) {
		super(sortByTitle);
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 */
	public String toString() {
		return title;
	}	
}
