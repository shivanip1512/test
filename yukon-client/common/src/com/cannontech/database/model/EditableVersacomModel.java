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
		super(TreeModelEnum.getForClass(EditableVersacomModel.class).getTreeModelName());
	}
	/**
	 * EditableLCRSerialModel constructor comment.
	 * @param root javax.swing.tree.TreeNode
	 */
	public EditableVersacomModel(String sortByTitle) {
		super(sortByTitle);
	}
}
