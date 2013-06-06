package com.cannontech.esub.editor.element;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.esub.element.FunctionElement;
//import com.cannontech.esub.element.FunctionElement;
import com.cannontech.spring.YukonSpringHook;

public class FunctionElementEditorPanel  extends com.cannontech.common.gui.util.DataInputPanel implements TreeSelectionListener{
    
    private JLabel functionLabel = null;
    private JLabel rawStateLabel = null;
    private JComboBox functionComboBox = null;
    private JComboBox rawStateComboBox = null;
    private PointSelectionPanel pointSelectionPanel = null;
    private static final String[] functions = { "Control by Point" };
    private static final String[] states = { "Open", "Close" };
    private FunctionElement functionElement;
    
    /**
     * FunctionElementEditorPanel constructor.
     */
    public FunctionElementEditorPanel() {
        super();
        initialize();
    }
    
    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("FunctionElementEditorPanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(300, 400);
            
            java.awt.GridBagConstraints constraintsFunctionLabel = new java.awt.GridBagConstraints();
            constraintsFunctionLabel.gridx = 0; constraintsFunctionLabel.gridy = 0;
            constraintsFunctionLabel.gridwidth = 1;
            constraintsFunctionLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsFunctionLabel.insets = new java.awt.Insets(4, 4, 4, 4);
            add(getFunctionLabel(), constraintsFunctionLabel);
            
            java.awt.GridBagConstraints constraintsFunctionComboBox = new java.awt.GridBagConstraints();
            constraintsFunctionComboBox.gridx = 1; constraintsFunctionComboBox.gridy = 0;
            constraintsFunctionComboBox.gridwidth = 1;
            constraintsFunctionComboBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsFunctionComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
            add(getFunctionComboBox(), constraintsFunctionComboBox);
            
            java.awt.GridBagConstraints constraintsRawStateLabel = new java.awt.GridBagConstraints();
            constraintsRawStateLabel.gridx = 0; constraintsRawStateLabel.gridy = 1;
            constraintsRawStateLabel.gridwidth = 1;
            constraintsRawStateLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRawStateLabel.insets = new java.awt.Insets(4, 4, 4, 4);
            add(getRawStateLabel(), constraintsRawStateLabel);
            
            java.awt.GridBagConstraints constraintsRawStateComboBox = new java.awt.GridBagConstraints();
            constraintsRawStateComboBox.gridx = 1; constraintsRawStateComboBox.gridy = 1;
            constraintsRawStateComboBox.gridwidth = 1;
            constraintsRawStateComboBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRawStateComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
            add(getRawStateComboBox(), constraintsRawStateComboBox);
            
            java.awt.GridBagConstraints constraintsPointSelectionPanel = new java.awt.GridBagConstraints();
            constraintsPointSelectionPanel.gridx = 0; constraintsPointSelectionPanel.gridy = 2;
            constraintsPointSelectionPanel.gridwidth = 2;
            constraintsPointSelectionPanel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsPointSelectionPanel.fill = java.awt.GridBagConstraints.BOTH;
            constraintsPointSelectionPanel.insets = new java.awt.Insets(4, 4, 4, 4);
            add(getPointSelectionPanel(), constraintsPointSelectionPanel);
            
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        
        getPointSelectionPanel().getIvjDevicePointTree().addTreeSelectionListener(this);
        
    }
    
    private JLabel getFunctionLabel()
    {
        if( functionLabel == null )
        {
            try {
                functionLabel = new javax.swing.JLabel();
                functionLabel.setName("FunctionLabel");
                functionLabel.setText("Function:");
                functionLabel.setPreferredSize( new Dimension( 60, 23 ) );
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return functionLabel;
    }
    
    private JLabel getRawStateLabel()
    {
        if( rawStateLabel == null )
        {
            try {
                rawStateLabel = new javax.swing.JLabel();
                rawStateLabel.setName("RawStateLabel");
                rawStateLabel.setText("Raw State:");
                rawStateLabel.setPreferredSize( new Dimension( 60, 23 ) );
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return rawStateLabel;
    }
    
    private JComboBox getFunctionComboBox()
    {
        if( functionComboBox == null )
        {
            try{
                functionComboBox = new JComboBox(functions);
                functionComboBox.setName("FunctionComboBox");
                functionComboBox.setPreferredSize(new java.awt.Dimension(150, 23));
                functionComboBox.setMinimumSize(new Dimension( 100, 23 ));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return functionComboBox;
    }
    
    private JComboBox getRawStateComboBox()
    {
        if( rawStateComboBox == null )
        {
            try{
                rawStateComboBox = new JComboBox(states);
                rawStateComboBox.setName("RawStateComboBox");
                rawStateComboBox.setPreferredSize(new java.awt.Dimension(150, 23));
                rawStateComboBox.setMinimumSize(new Dimension( 100, 23 ));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return rawStateComboBox;
    }
    
    /**
     * Return the PointSelectionPanel property value.
     * @return com.cannontech.esub.editor.element.PointSelectionPanel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private PointSelectionPanel getPointSelectionPanel() 
    {
        if (pointSelectionPanel == null) {
            try {
                pointSelectionPanel = com.cannontech.esub.editor.Util.getPointSelectionPanel();
                pointSelectionPanel.setName("PointSelectionPanel");
                pointSelectionPanel.setPreferredSize(new Dimension(220, 361));
                pointSelectionPanel.setMinimumSize(new Dimension(220, 200));
                
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return pointSelectionPanel;
    }
    
    /**
     * Called whenever the part throws an exception.
     * @param exception java.lang.Throwable
     */
    private void handleException(java.lang.Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }
    
    public Object getValue(Object o) {
        
        ArrayList<String> argList = new ArrayList<String>();
        String functionName = getFunctionComboBox().getSelectedItem().toString();
        argList.add(functionName);
        String rawState = String.valueOf(getRawStateComboBox().getSelectedIndex());
        String pointID = new Integer(getPointSelectionPanel().getSelectedPoint().getLiteID()).toString();
        
        argList.add(pointID);
        argList.add(rawState);
        
        if(pointID.equals("") )
        {
            argList = null;
        }
        
        functionElement.setFunctionID(0);
        functionElement.setArgList(argList);
        
        return functionElement;
    }

    public void setValue(Object o)
    {
        functionElement = (FunctionElement) o;
        
        String functionName = null;
        String rawState = null;
        int pointID = -1;;
        
        // hardcoded till we get new functions
        getPointSelectionPanel().refresh();
        ArrayList<String> argList = functionElement.getArgList();
        if(argList != null ) {
            if(argList.size() >= 3) {
                functionName = argList.get(0);
                pointID = new Integer(argList.get(1)).intValue();
                rawState = argList.get(2);
                
                if( pointID != -1 && functionName != null && rawState != null) {
                    getFunctionComboBox().setSelectedItem(functionName);
                    getRawStateComboBox().setSelectedIndex(new Integer(rawState).intValue());
                    LitePoint litePoint = null;
                    try {
                        YukonSpringHook.getBean(PointDao.class).getLitePoint(pointID);
                    }catch(NotFoundException nfe) {
                        CTILogger.error("The point (pointId:"+ pointID + ") for this Function might have been deleted!", nfe);
                    }
                    if(litePoint != null) {
                        getPointSelectionPanel().selectPoint(litePoint);
                    }
                }
            }
        }
    }
    
    public boolean isInputValid() {
        LitePoint pt = getPointSelectionPanel().getSelectedPoint();
        return ( pt != null );
    }

    public void valueChanged(TreeSelectionEvent evt) {
        com.cannontech.database.data.lite.LitePoint p = getPointSelectionPanel().getSelectedPoint();    
        fireInputUpdate();
    }
    
}
