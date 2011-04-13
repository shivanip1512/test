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
		super(TreeModelEnum.getForClass(EditableLCRSerialModel.class).getTreeModelName());
	}
	/**
	 * EditableLCRSerialModel constructor comment.
	 * @param root javax.swing.tree.TreeNode
	 */
	public EditableLCRSerialModel(String sortByTitle) {
		super(sortByTitle);
	}
}