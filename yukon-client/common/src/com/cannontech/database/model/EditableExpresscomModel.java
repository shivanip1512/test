package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */

public class EditableExpresscomModel extends EditableTextModel
{
	/**
	 * EditableLCRSerialModel constructor comment.
	 * @param root javax.swing.tree.TreeNode
	 */
	public EditableExpresscomModel() {
		super(TreeModelEnum.getForClass(EditableExpresscomModel.class).getTreeModelName());
	}
	/**
	 * EditableLCRSerialModel constructor comment.
	 * @param root javax.swing.tree.TreeNode
	 */
	public EditableExpresscomModel(String sortByTitle) {
		super(sortByTitle);
	}	
}
