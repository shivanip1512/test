package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */

public class EditableVersacomModel extends EditableTextModel
{
	/**
	 * EditableLCRSerialModel constructor comment.
	 * @param root javax.swing.tree.TreeNode
	 */
	public EditableVersacomModel() {
		super(ModelFactory.getModelString(ModelFactory.EDITABLE_VERSACOM_SERIAL));
	}
	/**
	 * EditableLCRSerialModel constructor comment.
	 * @param root javax.swing.tree.TreeNode
	 */
	public EditableVersacomModel(String sortByTitle) {
		super(sortByTitle);
	}
}
