package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */

public class EditableLCRSerialModel extends EditableTextModel
{
	private static String title = "LCR Serial #";
	/**
	 * EditableLCRSerialModel constructor comment.
	 * @param root javax.swing.tree.TreeNode
	 */
	public EditableLCRSerialModel() {
		super(title);
	}
	/**
	 * EditableLCRSerialModel constructor comment.
	 * @param root javax.swing.tree.TreeNode
	 */
	public EditableLCRSerialModel(String sortByTitle) {
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
