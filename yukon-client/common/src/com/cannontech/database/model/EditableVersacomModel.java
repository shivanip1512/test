package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */

public class EditableVersacomModel extends EditableTextModel
{
	private static String title = "Versacom Serial";
	/**
	 * EditableLCRSerialModel constructor comment.
	 * @param root javax.swing.tree.TreeNode
	 */
	public EditableVersacomModel() {
		super(title);
	}
	/**
	 * EditableLCRSerialModel constructor comment.
	 * @param root javax.swing.tree.TreeNode
	 */
	public EditableVersacomModel(String sortByTitle) {
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
