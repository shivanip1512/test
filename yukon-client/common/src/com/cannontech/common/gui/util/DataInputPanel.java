package com.cannontech.common.gui.util;

/**
 * This type was created in VisualAge.
 */
import java.util.Vector;

public abstract class DataInputPanel extends javax.swing.JPanel 
{
	private Vector listeners = new Vector();

	//set this string to the message of an error when isInputValid() returns false, 
	//  and it will print to the screen.
	private String errorString = " ";
/**
 * DataInputPanel constructor comment.
 */
public DataInputPanel() {
	super();
	initialize();
}
/**
 * This method was created in VisualAge.
 * @param listener com.cannontech.common.gui.util.DataInputPanelListener
 */
public void addDataInputPanelListener(DataInputPanelListener listener) {
	
	if( !this.listeners.contains( listener ) )
	{
		this.listeners.addElement( listener );
	}	
}
/**
 * This method was created in VisualAge.
 */
public void fireInputUpdate() {

	for( int i = this.listeners.size()-1; i >= 0; i-- )
	{
		((DataInputPanelListener) this.listeners.elementAt(i)).inputUpdate(new DataInputPanelEvent(this));
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 9:54:34 AM)
 * @return java.lang.String
 */
public java.lang.String getErrorString() {
	return errorString;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public abstract Object getValue(Object o);
/**
 * This method was created in VisualAge.
 */
private void initialize() {
	
}
/**
 * This method must be implemented if a notion of data validity needs to be supported.
 * @return boolean
 */
/* This method should be overriden all the time */
/* IN THE FUTURE THIS METHOD WILL BE MADE abstract */
public boolean isInputValid()
{
	return true;
}
/**
 * This method was created in VisualAge.
 * @param listener com.cannontech.common.gui.util.DataInputPanelListener
 */
public void removeDataInputPanelListener(DataInputPanelListener listener ) {

	if( this.listeners.contains( listener ) )
	{
		this.listeners.removeElement( listener );
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 9:54:34 AM)
 * @param newErrorString java.lang.String
 */
public void setErrorString(java.lang.String newErrorString) {
	errorString = newErrorString;
}
/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public abstract void setValue(Object o);
}
