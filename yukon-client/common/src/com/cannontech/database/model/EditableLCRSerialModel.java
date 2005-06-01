package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */

public class EditableLCRSerialModel extends EditableTextModel
{
//	private static String title = "LCR Serial #";
	/**
	 * EditableLCRSerialModel constructor comment.
	 * @param root javax.swing.tree.TreeNode
	 */
	public EditableLCRSerialModel() {
		super(ModelFactory.getModelString(ModelFactory.EDITABLE_LCR_SERIAL));
	}
	/**
	 * EditableLCRSerialModel constructor comment.
	 * @param root javax.swing.tree.TreeNode
	 */
	public EditableLCRSerialModel(String sortByTitle) {
		super(sortByTitle);
	}
}