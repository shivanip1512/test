package com.cannontech.esub.editor.element;

import com.cannontech.common.editor.PropertyPanel;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.DataInputPanelListener;

/**
 * The Editor for DrawingMetaElement elements.
 * @author alauinger
 */
public class CurrentAlarmsTableEditor
	extends PropertyPanel
	implements DataInputPanelListener {

	private String[] tabs = { "General" };
	
	private CurrentAlarmsTableEditorPanel currentAlarmsTableEditorPanel =
		new CurrentAlarmsTableEditorPanel();
				
	private DataInputPanel[] inputPanels = { 
			currentAlarmsTableEditorPanel
	};
	
	public CurrentAlarmsTableEditor() {
		super();
		setName("CurrentAlarmsTableEditor");
		setPreferredSize(new java.awt.Dimension(300, 200));
		setSize(300,200);
		
		getCurrentAlarmsTableEditorPanel().addDataInputPanelListener(this);
		checkValidity();
	}
	
	/**
	 * @see com.cannontech.common.editor.PropertyPanel#getInputPanels()
	 */
	protected DataInputPanel[] getInputPanels() {
		return inputPanels;
	}

	/**
	 * @see com.cannontech.common.editor.PropertyPanel#getTabNames()
	 */
	protected String[] getTabNames() {
		return tabs;
	}

	private void checkValidity() {
	getPropertyButtonPanel().getOkJButton().setEnabled(isInputValid());
	}
	
	public void inputUpdate(PropertyPanelEvent evt) {
		checkValidity();
	}
	/**
	 * @see com.cannontech.common.gui.util.DataInputPanel#getValue(Object)
	 */
	public Object getValue(Object o) {
		return getCurrentAlarmsTableEditorPanel().getValue(o);
	}

	/**
	 * @see com.cannontech.common.gui.util.DataInputPanel#setValue(Object)
	 */
	public void setValue(Object o) {
		super.setValue(o);
	}

	/**
	 * Returns the currentAlarmsTableEditorPanel.
	 * @return CurrentAlarmsTableEditorPanel
	 */
	public CurrentAlarmsTableEditorPanel getCurrentAlarmsTableEditorPanel() {
		return currentAlarmsTableEditorPanel;
	}

	/**
	 * Sets the currentAlarmsTableEditorPanel.
	 * @param currentAlarmsTableEditorPanel The currentAlarmsTableEditorPanel to set
	 */
	public void setCurrentAlarmsTableEditorPanel(CurrentAlarmsTableEditorPanel currentAlarmsTableEditorPanel) {
		this.currentAlarmsTableEditorPanel = currentAlarmsTableEditorPanel;
	}

}
