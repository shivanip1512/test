package com.cannontech.common.gui.util;

/**
 * Insert the type's description here.
 * Creation date: (4/19/2001 4:08:10 PM)
 * @author: 
 */
public class ConfirmationJPanel extends javax.swing.JPanel 
{
	public static final int CANCELED_PANEL = 0;
	public static final int CONFIRMED_PANEL = 1;
	public static final int UPDATE_PANEL = 2;

	private int choice = CANCELED_PANEL;
/**
 * ConfirmationJPanel constructor comment.
 */
public ConfirmationJPanel() {
	super();
	setLayout(null);
}
/**
 * ConfirmationJPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public ConfirmationJPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * ConfirmationJPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public ConfirmationJPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * ConfirmationJPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public ConfirmationJPanel(boolean isDoubleBuffered) 
{
	super(isDoubleBuffered);
	setLayout(null);
}
/**
 * Insert the method's description here.
 * Creation date: (4/19/2001 4:04:44 PM)
 */
// Override me please!
protected void disposePanel() 
{
	//do nothing!!
}
/**
 * Insert the method's description here.
 * Creation date: (4/19/2001 4:13:40 PM)
 * @return int
 */
public int getChoice() {
	return choice;
}
/**
 * Insert the method's description here.
 * Creation date: (4/19/2001 4:13:40 PM)
 * @param newChoice int
 */
protected void setChoice(int newChoice) {
	choice = newChoice;
}
}
