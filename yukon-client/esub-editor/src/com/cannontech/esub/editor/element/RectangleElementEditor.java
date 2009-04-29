package com.cannontech.esub.editor.element;

import com.cannontech.common.editor.PropertyPanel;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.DataInputPanelListener;

/**
 * @author asolberg
 */
public class RectangleElementEditor extends PropertyPanel implements DataInputPanelListener {

    private String[] tabs = { "General" };
    private RectangleElementEditorPanel rectangleElementEditorPanel = new RectangleElementEditorPanel(this);
    private DataInputPanel[] inputPanels = { rectangleElementEditorPanel };
    
    /**
     * Contructor for RectangleElementEditor
     */
    public RectangleElementEditor() {
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
    
    /**
     * This method checks the validity of the data on the panel 
     * and sets the OK button to enabled if it currently holds valid data.
     */
    private void checkValidity() {
        getPropertyButtonPanel().getOkJButton().setEnabled(isInputValid());
    }

    /**
     * Returns the RectangleElementEditorPanel
     * @return RectangleElementEditorPanel
     */
    public RectangleElementEditorPanel getRectangleElementEditorPanel() {
        return rectangleElementEditorPanel;
    }

    /**
     * Set the RectangleElementEditorPanel
     * @param RectangleElementEditorPanel
     */
    public void setRectangleElementEditorPanel(RectangleElementEditorPanel panel) {
        rectangleElementEditorPanel = panel;
    }

    /* (non-Javadoc)
     * @see com.cannontech.common.gui.util.DataInputPanel#getValue(java.lang.Object)
     */
    public Object getValue(Object o) {
        return rectangleElementEditorPanel.getValue(o);
    }

    /* (non-Javadoc)
     * @see com.cannontech.common.gui.util.DataInputPanel#setValue(java.lang.Object)
     */
    public void setValue(Object val) {
        super.setValue(val);
    }
    
    /**
     * If something is changed or altered on the panel the method is 
     * called and notifies codes that checks validity of the panel in its current state.
     */
    public void inputUpdate(PropertyPanelEvent evt) {
        checkValidity();
    }
    
    public void selectionPerformed() {
        getRectangleElementEditorPanel().selectionPerformed();
    }
}
