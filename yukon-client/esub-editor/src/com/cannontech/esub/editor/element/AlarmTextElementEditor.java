/*
 * Created on May 9, 2003
 */
package com.cannontech.esub.editor.element;

import com.cannontech.common.editor.PropertyPanel;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.DataInputPanelListener;

/**
 * @author alauinger
 */
public class AlarmTextElementEditor
	extends PropertyPanel
	implements DataInputPanelListener {

	private String[] tabs = { "General" };
	private AlarmTextElementEditorPanel alarmIndicatorPanel = new AlarmTextElementEditorPanel();
	private DataInputPanel[] inputPanels = {
		alarmIndicatorPanel	
	};
	
	public AlarmTextElementEditor() {
		super();
		checkValidity();
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.common.editor.PropertyPanel#getInputPanels()
	 */
	protected DataInputPanel[] getInputPanels() {
		return inputPanels;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.common.editor.PropertyPanel#getTabNames()
	 */
	protected String[] getTabNames() {
		return tabs;
	}
	
	private void checkValidity() 
	{
		getPropertyButtonPanel().getOkJButton().setEnabled(isInputValid());
	}

	/**
	 * @return
	 */
	public AlarmTextElementEditorPanel getAlarmIndicatorPanel() {
		return alarmIndicatorPanel;
	}

	/**
	 * @param panel
	 */
	public void setAlarmIndicatorPanel(AlarmTextElementEditorPanel panel) {
		alarmIndicatorPanel = panel;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.common.gui.util.DataInputPanel#getValue(java.lang.Object)
	 */
	public Object getValue(Object o) {
		return alarmIndicatorPanel.getValue(o);
	}

	/* (non-Javadoc)
	 * @see com.cannontech.common.gui.util.DataInputPanel#setValue(java.lang.Object)
	 */
	public void setValue(Object val) {
		super.setValue(val);
	}
	
	public void inputUpdate(PropertyPanelEvent evt) {
		checkValidity();
	}

}
