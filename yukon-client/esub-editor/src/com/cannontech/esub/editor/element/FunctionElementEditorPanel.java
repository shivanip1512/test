package com.cannontech.esub.editor.element;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.*;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;

import com.cannontech.common.gui.tree.*;
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.cache.functions.YukonImageFuncs;
import com.cannontech.database.data.lite.*;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.model.DeviceCheckBoxTreeModel;
import com.cannontech.esub.editor.Util;
import com.cannontech.esub.element.FunctionElement;
import com.cannontech.esub.element.StateImage;
//import com.cannontech.esub.element.FunctionElement;

public class FunctionElementEditorPanel  extends com.cannontech.common.gui.util.DataInputPanel implements TreeSelectionListener{
    
    private JLabel functionLabel = null;
    private JLabel rawStateLabel = null;
    private JComboBox functionComboBox = null;
    private JComboBox rawStateComboBox = null;
    private PointSelectionPanel pointSelectionPanel = null;
//    private JScrollPane deviceJScrollPane = null;
//    private JTree deviceJTree = null;
//    private CheckNodeSelectionListener deviceNodeListener = null;
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
                
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return pointSelectionPanel;
    }
    
//    private JScrollPane getDeviceScrollPane() {
//        if ( deviceJScrollPane == null ) 
//        {
//            try 
//            {
//                deviceJScrollPane = new javax.swing.JScrollPane();
//                deviceJScrollPane.setName( "DeviceScrollPane" );
//                getDeviceScrollPane().setViewportView(getDeviceJTree());
//                deviceJScrollPane.setPreferredSize(new Dimension (250, 250));
//            } catch ( java.lang.Throwable ivjExc ) 
//            {
//                handleException( ivjExc );
//            }
//        }
//        return deviceJScrollPane;
//    }
    
//    private JTree getDeviceJTree() {
//        if (deviceJTree == null) {
//            try {
//                deviceJTree = new javax.swing.JTree();
//                deviceJTree.setName("DeviceJTree");
//                deviceJTree.setBounds(0, 0, 300, 400);
//                // user code begin {1}
//                
//                DefaultMutableTreeNode root = new DefaultMutableTreeNode("Devices/Points");
//                
//                deviceJTree.setModel( new DeviceCheckBoxTreeModel(true) );
//                deviceJTree.setCellRenderer( new CheckRenderer() );
//                deviceJTree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
//                getDeviceJTreeModel().update();
//                
//                deviceJTree.addMouseListener( getDeviceNodeListener());
//                
//                deviceJTree.addMouseListener( new MouseAdapter()
//                {
//                    public void mouseClicked(MouseEvent e)
//                    {
//                        deviceValueChanged( null );
//                    }
//                });
//
//            } catch (java.lang.Throwable ivjExc) {
//                handleException(ivjExc);
//            }
//        }
//        return deviceJTree;
//    }
//    
    
//    private DeviceCheckBoxTreeModel getDeviceJTreeModel() 
//    {
//        return (DeviceCheckBoxTreeModel)getDeviceJTree().getModel();
//    }
//    
//    private CheckNodeSelectionListener getDeviceNodeListener()
//    {
//        if( deviceNodeListener == null )
//        {
//            deviceNodeListener = new CheckNodeSelectionListener( getDeviceJTree(), true);
//        }
//        return deviceNodeListener;
//    }
    
//    /**
//     * This method checks for extra work to do like checking or unchecking parents after a tree
//     * selection and then updates the panel.
//     */
//    public void deviceValueChanged(TreeSelectionEvent e) 
//    {
//        int selRow = getDeviceJTree().getMaxSelectionRow();
//        if( selRow != -1) 
//        {
//            CheckNode node = ( CheckNode )getDeviceJTree().getPathForRow( selRow ).getLastPathComponent();
//            if( !node.isSelected( )) // we are doing an uncheck
//            {
//                CheckNode parent = (CheckNode)node.getParent();
//                
//                // only uncheck parents if they infact checked currently
//                if( parent != null  && parent.isSelected() )  
//                {
//                    //uncheck our parents until we hit the root
//                    while( node.getParent() != null ) 
//                    {
//                        getDeviceNodeListener().uncheckParent(node);
//                        if( parent.getLevel() == 0 )
//                        {
//                            break;
//                        }
//                        node = (CheckNode)node.getParent();
//                    }
//                }
//                
//            }else if ( (CheckNode)node.getParent() != null ) // we don't care if the root got clicked
//            {
//                //  Here we check to see if we need to set our parent as checked and if we do, we continue to check are 
//                //  parent's parent until we either find an unchecked child or we get to the root, confusing as hell.
//                
//                boolean cont = true;
//                while(cont)
//                {
//                    cont = checkParent(node);
//                    node = (CheckNode)node.getParent();
//                }
//                
//            }
//            
//        }
//        
//        fireInputUpdate();
//    }
    
//    /**
//     * This methdod looks at all the siblings of "node" to see whether we need to set node's parent as checked,
//     * returning true if we another round of parent checking is needed, false if our parent is actually the root.
//     * Return the ret property value.
//     * @return boolean ret
//     */
//    private boolean checkParent(CheckNode node)
//    {
//        boolean ret = true;
//        
//        //  since we're doing a set checked on this guy, see if all our siblings are also checked, if so check the parent
//        int children = node.getSiblingCount();
//        CheckNode parent = (CheckNode)node.getParent();
//        CheckNode check = (CheckNode)parent.getFirstChild();
//        
//        for ( int i = 0; i < children; i++ )
//        {
//            if ( !check.isSelected() )
//            {
//                // at least one of our siblings isn't checked so we don't care anymore
//                return false;
//            }else 
//            {
//                if ( check.getNextSibling() == null )
//                {
//                    // we are the last node and we are checked so now we can set the parent as checked
//                    parent.setSelected( true );
//                    if( parent.getLevel() == 0 )                
//                    {
//                        // the parent is the root and we are done
//                        return false;
//                    }else
//                    {
//                        // we've set our parent and we can return for more checking fun
//                        break;
//                    }
//                    
//                }
//            }
//            check = (CheckNode) check.getNextSibling();
//            
//        }
//        
//        return ret;
//    }
    
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
        
        ArrayList argList = new ArrayList();
        String functionName = getFunctionComboBox().getSelectedItem().toString();
        argList.add(functionName);
        String rawState = getRawStateComboBox().getSelectedItem().toString();
        argList.add(rawState);
        String pointID = new Integer(getPointSelectionPanel().getSelectedPoint().getLiteID()).toString();
        argList.add(pointID);
        functionElement.setFunctionID(0);
        functionElement.setArgList(argList);
        System.out.println("script: "+functionElement.getScript());
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
        ArrayList argList = functionElement.getArgList();
        if(argList != null ) {
            
            try {
                functionName = (String)argList.get(0);
                rawState = (String)argList.get(1);
                pointID = new Integer((String)argList.get(2)).intValue();
                if( pointID != -1 && functionName != null && rawState != null)
                {
                    getFunctionComboBox().setSelectedItem(functionName);
                    getRawStateComboBox().setSelectedItem(rawState);
                    getPointSelectionPanel().selectPoint(PointFuncs.getLitePoint(pointID));
                }
            } catch (Exception e) {
                System.out.println("error with function string data");
                e.printStackTrace();
            } 
        }
        
        LiteYukonImage img = YukonImageFuncs.getLiteYukonImage(1);
        functionElement.setYukonImage(img);
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
