package com.cannontech.tdc.utils;

/**
 * Insert the type's description here.
 * Creation date: (3/10/00 11:51:14 AM)
 * @author: 
 */
public class DialogRemovalTimer extends javax.swing.Timer implements java.awt.event.ActionListener 
{
	private javax.swing.JDialog dialog = null;
	private java.awt.event.ActionListener actionListener = null;

/**
 * DialogRemovalTimer constructor comment.
 * @param delay int
 * @param listener java.awt.event.ActionListener
 */
public DialogRemovalTimer(int delay, java.awt.event.ActionListener listener) {
	super(delay, listener);
}
/**
 * DialogRemovalTimer constructor comment.
 * @param delay int
 * @param listener java.awt.event.ActionListener
 */
public DialogRemovalTimer(int delay, java.awt.event.ActionListener listener, javax.swing.JDialog d) 
{
	super(delay, listener);

	actionListener = listener;
	dialog = d;
}
/**
 * actionPerformed method comment.
 */
public void actionPerformed(java.awt.event.ActionEvent e) 
{
	// Times up, kill the dialog
	dialog.dispose();	
}
/**
 * Insert the method's description here.
 * Creation date: (3/10/00 12:02:06 PM)
 */
public void setActionListener( java.awt.event.ActionListener l ) 
{
	super.removeActionListener( actionListener );
	super.addActionListener( l );
}
}
