package com.cannontech.esub.editor.element;

import com.cannontech.common.editor.PropertyPanel;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.DataInputPanelListener;

/**
 * The Editor for DrawingMetaElement elements.
 * @author alauinger
 */
public class DrawingMetaElementEditor
	extends PropertyPanel
	implements DataInputPanelListener {

	private String[] tabs = { "General" };
	
	private DrawingMetaElementEditorPanel drawingMetaElementEditorPanel = 	
		new DrawingMetaElementEditorPanel();
		
	private DataInputPanel[] inputPanels = { 
			drawingMetaElementEditorPanel
	};
	
	public DrawingMetaElementEditor() {
		super();
		setName("DynamicMetaElementEditor");
		setPreferredSize(new java.awt.Dimension(300, 300));
		setSize(300,300);
		getDrawingMetaElementEditorPanel().addDataInputPanelListener(this);
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
		return getDrawingMetaElementEditorPanel().getValue(o);
	}

	/**
	 * @see com.cannontech.common.gui.util.DataInputPanel#setValue(Object)
	 */
	public void setValue(Object o) {
		super.setValue(o);
	}

	/**
	 * Returns the drawingMetaElementEditorPanel.
	 * @return DrawingMetaElementEditorPanel
	 */
	public DrawingMetaElementEditorPanel getDrawingMetaElementEditorPanel() {
		return drawingMetaElementEditorPanel;
	}

	/**
	 * Sets the drawingMetaElementEditorPanel.
	 * @param drawingMetaElementEditorPanel The drawingMetaElementEditorPanel to set
	 */
	public void setDrawingMetaElementEditorPanel(DrawingMetaElementEditorPanel drawingMetaElementEditorPanel) {
		this.drawingMetaElementEditorPanel = drawingMetaElementEditorPanel;
	}

}
