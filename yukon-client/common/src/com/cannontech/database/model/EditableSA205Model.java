package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */

public class EditableSA205Model extends EditableTextModel
{
	/**
	 * EditableLCRSerialModel constructor comment.
	 * @param root javax.swing.tree.TreeNode
	 */
	public EditableSA205Model() {
		super(TreeModelEnum.getForClass(EditableSA205Model.class).getTreeModelName());
	}
	/**
	 * EditableLCRSerialModel constructor comment.
	 * @param root javax.swing.tree.TreeNode
	 */
	public EditableSA205Model(String sortByTitle) {
		super(sortByTitle);
	}
}
