package com.cannontech.tdc.roweditor;

import com.cannontech.tdc.alarms.gui.AlarmingRow;

/**
 * Insert the type's description here.
 * Creation date: (11/14/2001 2:38:51 PM)
 * @author: 
 */
public abstract class ManualEntryJPanel extends javax.swing.JPanel
{
	// an alarmRow is only present when we are alarming
	private AlarmingRow alarmRow = null;
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
public ManualEntryJPanel( EditorDialogData data, Object currentValue, AlarmingRow alarmRow_ ) 
{
	super();

	alarmRow = alarmRow_;
	editorData = data;
	startingValue = currentValue;
}
/**
 * EditDataPanel constructor comment.
 */
public ManualEntryJPanel( EditorDialogData data, Object currentValue ) 
{
	this( data, currentValue, null );
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
 * Creation date: (11/14/2001 2:39:29 PM)
 * @return java.lang.String
 */
public abstract String getPanelTitle();
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 2:55:54 PM)
 * @return com.cannontech.message.dispatch.message.Signal
 */
protected AlarmingRow getAlarmRow() {
	return alarmRow;
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
	return getAlarmRow() != null;
}

}