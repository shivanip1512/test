package com.cannontech.tdc.roweditor;

/**
 * Insert the type's description here.
 * Creation date: (11/14/2001 2:38:51 PM)
 * @author: 
 */
import com.cannontech.message.dispatch.message.Signal;

public abstract class ManualEntryJPanel extends javax.swing.JPanel 
{
	// a Signal() is only present when we are alarming
	private Signal signal = null;
	private boolean isRowAlarmed = false;
	private java.util.Observable observingData = null;
	private Object startingValue = null;
	private EditorDialogData editorData = null;

/**
 * ManualEntryJPanel constructor comment.
 */
protected ManualEntryJPanel() {
	super();
}
/**
 * EditDataPanel constructor comment.
 */
public ManualEntryJPanel( EditorDialogData data, com.cannontech.tdc.ObservableRow obsValue, Object currentValue, Signal signalData ) 
{
	super();

	isRowAlarmed = true;
	signal = signalData;
	editorData = data;
	observingData = obsValue;
	startingValue = currentValue;
}
/**
 * EditDataPanel constructor comment.
 */
public ManualEntryJPanel( EditorDialogData data, java.util.Observable obsValue, Object currentValue ) 
{
	super();

	isRowAlarmed = false;
	editorData = data;
	observingData = obsValue;
	startingValue = currentValue;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 2:55:54 PM)
 * @return com.cannontech.tdc.roweditor.EditorDialogData
 */
protected EditorDialogData getEditorData() {
	return editorData;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 2:55:54 PM)
 * @return java.util.Observable
 */
protected java.util.Observable getObservingData() {
	return observingData;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 2:39:29 PM)
 * @return java.lang.String
 */
public abstract String getPanelTitle();
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 2:55:54 PM)
 * @return com.cannontech.message.dispatch.message.Signal
 */
protected com.cannontech.message.dispatch.message.Signal getSignal() {
	return signal;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 2:55:54 PM)
 * @return java.lang.Object
 */
protected java.lang.Object getStartingValue() {
	return startingValue;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 2:55:54 PM)
 * @return boolean
 */
protected boolean isRowAlarmed() {
	return isRowAlarmed;
}
}
