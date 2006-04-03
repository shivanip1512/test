package com.cannontech.esub.editor.element;

import com.cannontech.common.editor.PropertyPanel;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.DataInputPanelListener;

public class FunctionElementEditor  extends PropertyPanel implements DataInputPanelListener {

    private String[] tabs = { "General" };
    private FunctionElementEditorPanel functionPanel = new FunctionElementEditorPanel();
    private DataInputPanel[] inputPanels = { functionPanel };
    
    public FunctionElementEditor() {
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
    public FunctionElementEditorPanel getFunctionPanel() {
        return functionPanel;
    }

    /**
     * @param panel
     */
    public void setFunctionPanel(FunctionElementEditorPanel panel) {
        functionPanel = panel;
    }

    /* (non-Javadoc)
     * @see com.cannontech.common.gui.util.DataInputPanel#getValue(java.lang.Object)
     */
    public Object getValue(Object o) {
        return functionPanel.getValue(o);
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