package com.cannontech.common.gui.util;

/**
 * Insert the type's description here.
 * Creation date: (7/17/2001 4:32:19 PM)
 * @author: 
 */
public class JTextFieldComboEditor extends javax.swing.JTextField implements javax.swing.ComboBoxEditor {
/**
 * JtextFieldComboEditor constructor comment.
 */
public JTextFieldComboEditor() {
	super();
	initialize();
}
/**
 * JtextFieldComboEditor constructor comment.
 * @param columns int
 */
public JTextFieldComboEditor(int columns) {
	super(columns);
	initialize();

}
/**
 * JtextFieldComboEditor constructor comment.
 * @param text java.lang.String
 */
public JTextFieldComboEditor(String text) {
	super(text);
	initialize();

}
/**
 * JtextFieldComboEditor constructor comment.
 * @param text java.lang.String
 * @param columns int
 */
public JTextFieldComboEditor(String text, int columns) {
	super(text, columns);
	initialize();
}
/**
 * JtextFieldComboEditor constructor comment.
 * @param doc javax.swing.text.Document
 * @param text java.lang.String
 * @param columns int
 */
public JTextFieldComboEditor(javax.swing.text.Document doc, String text, int columns) {
	super(doc, text, columns);
	initialize();

}
 /** Return the component that should be added to the tree hierarchy for
	* this editor
	*/
public java.awt.Component getEditorComponent() {
	return this;
}
 /** Return the edited item **/
public Object getItem() {
	return getText();
}
/**
 * Insert the method's description here.
 * Creation date: (7/17/2001 4:43:21 PM)
 */
private void initialize() 
{
	setBorder( javax.swing.BorderFactory.createEmptyBorder() );
}
 /** Set the item that should be edited. Cancel any editing if necessary **/
public void setItem(Object anObject) 
{
	setText( anObject == null ? "" : anObject.toString() );
}
}
