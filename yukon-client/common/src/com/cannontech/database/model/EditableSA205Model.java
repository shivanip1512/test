package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */

public class EditableSA205Model extends EditableTextModel
{
	private static String title = "DCU-205 Serial";
	/**
	 * EditableLCRSerialModel constructor comment.
	 * @param root javax.swing.tree.TreeNode
	 */
	public EditableSA205Model() {
		super(title);
	}
	/**
	 * EditableLCRSerialModel constructor comment.
	 * @param root javax.swing.tree.TreeNode
	 */
	public EditableSA205Model(String sortByTitle) {
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
