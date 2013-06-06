package com.cannontech.esub.editor.element;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.esub.element.DrawingElement;
import com.cannontech.esub.element.DynamicText;
import com.cannontech.esub.element.LineElement;
import com.cannontech.spring.YukonSpringHook;

public class BlinkPointPanel extends DataInputPanel implements ActionListener, TreeSelectionListener {
    private javax.swing.JPanel buttonGroup = null;
    private PointSelectionPanel pointSelectionPanel = null;
    private DrawingElement elem;
    private HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
    private JButton okButton;
    private JLabel rawStateLabel;
    private JLabel blinkLabel;
    private JPanel previewPanel;
    private JCheckBox blinkCheckBox0;
    private JCheckBox blinkCheckBox1;
    private JCheckBox blinkCheckBox2;
    private JCheckBox blinkCheckBox3;
    private JCheckBox blinkCheckBox4;
    private JCheckBox blinkCheckBox5;
    private JCheckBox blinkCheckBox6;
    private JCheckBox blinkCheckBox7;
    private JCheckBox blinkCheckBox8;
    private JCheckBox blinkCheckBox9;
    private JCheckBox blinkCheckBox10;
    private JButton resetButton;
    private JLabel rawState0;
    private JLabel rawState1;
    private JLabel rawState2;
    private JLabel rawState3;
    private JLabel rawState4;
    private JLabel rawState5;
    private JLabel rawState6;
    private JLabel rawState7;
    private JLabel rawState8;
    private JLabel rawState9;
    private JLabel rawState10;
    private JCheckBox[] checkBoxes = new JCheckBox[12];
    private JLabel[] stateLabels = new JLabel[12];

/**
 * ArrowPointPanel constructor comment.
 */
public BlinkPointPanel() {
    super();
    initialize();
}

/**
 * Return the ButtonGroup JPanel.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getButtonGroup() {
    if (buttonGroup == null) {
        try {
            buttonGroup = new javax.swing.JPanel();
            buttonGroup.setName("ButtonGroup");
            buttonGroup.setLayout(new java.awt.GridBagLayout());
            
            java.awt.GridBagConstraints constraintsOKButton = new java.awt.GridBagConstraints();
            constraintsOKButton.gridx = 1; constraintsOKButton.gridy = 0;
            constraintsOKButton.insets = new java.awt.Insets(4, 4, 4, 4);
            getButtonGroup().add(getOkButton(), constraintsOKButton);
             
            java.awt.GridBagConstraints constraintsResetButton = new java.awt.GridBagConstraints();
            constraintsResetButton.gridx = 0; constraintsResetButton.gridy = 0;
            constraintsResetButton.insets = new java.awt.Insets(4, 4, 4, 4);
            getButtonGroup().add(getResetButton(), constraintsResetButton);

        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return buttonGroup;
}

private javax.swing.JPanel getPreviewPanel() 
{
    if (previewPanel == null) 
    {
        try
        {
            previewPanel = new javax.swing.JPanel();
            previewPanel.setName("PreviewPanel");
            previewPanel.setLayout(new java.awt.GridBagLayout());
            
            previewPanel.setPreferredSize(new Dimension( 180, 400 ));
            previewPanel.setSize(new Dimension( 180, 400 ));
            previewPanel.setMinimumSize(new Dimension( 180, 400 ));
            previewPanel.setBorder(new TitleBorder("Preview"));
            previewPanel.setAlignmentX(javax.swing.SwingConstants.TOP);
            
            java.awt.GridBagConstraints constraintsRawStateLabel = new java.awt.GridBagConstraints();
            constraintsRawStateLabel.gridx = 0; constraintsRawStateLabel.gridy = 0;
            constraintsRawStateLabel.insets = new java.awt.Insets(0, 4, 4, 4);
            getPreviewPanel().add(getRawStateLabel(), constraintsRawStateLabel);
            
            java.awt.GridBagConstraints constraintsRawStateLabel0 = new java.awt.GridBagConstraints();
            constraintsRawStateLabel0.gridx = 0; constraintsRawStateLabel0.gridy = 1;
            constraintsRawStateLabel0.insets = new java.awt.Insets(0, 4, 4, 4);
            constraintsRawStateLabel0.anchor = java.awt.GridBagConstraints.WEST;
            getPreviewPanel().add(getRawState0(), constraintsRawStateLabel0);
            
            java.awt.GridBagConstraints constraintsRawStateLabel1 = new java.awt.GridBagConstraints();
            constraintsRawStateLabel1.gridx = 0; constraintsRawStateLabel1.gridy = 2;
            constraintsRawStateLabel1.insets = new java.awt.Insets(0, 4, 4, 4);
            constraintsRawStateLabel1.anchor = java.awt.GridBagConstraints.WEST;
            getPreviewPanel().add(getRawState1(), constraintsRawStateLabel1);
            
            java.awt.GridBagConstraints constraintsRawStateLabel2 = new java.awt.GridBagConstraints();
            constraintsRawStateLabel2.gridx = 0; constraintsRawStateLabel2.gridy = 3;
            constraintsRawStateLabel2.insets = new java.awt.Insets(0, 4, 4, 4);
            constraintsRawStateLabel2.anchor = java.awt.GridBagConstraints.WEST;
            getPreviewPanel().add(getRawState2(), constraintsRawStateLabel2);
            
            java.awt.GridBagConstraints constraintsRawStateLabel3 = new java.awt.GridBagConstraints();
            constraintsRawStateLabel3.gridx = 0; constraintsRawStateLabel3.gridy = 4;
            constraintsRawStateLabel3.insets = new java.awt.Insets(0, 4, 4, 4);
            constraintsRawStateLabel3.anchor = java.awt.GridBagConstraints.WEST;
            getPreviewPanel().add(getRawState3(), constraintsRawStateLabel3);
            
            java.awt.GridBagConstraints constraintsRawStateLabel4 = new java.awt.GridBagConstraints();
            constraintsRawStateLabel4.gridx = 0; constraintsRawStateLabel4.gridy = 5;
            constraintsRawStateLabel4.insets = new java.awt.Insets(0, 4, 4, 4);
            constraintsRawStateLabel4.anchor = java.awt.GridBagConstraints.WEST;
            getPreviewPanel().add(getRawState4(), constraintsRawStateLabel4);
            
            java.awt.GridBagConstraints constraintsRawStateLabel5 = new java.awt.GridBagConstraints();
            constraintsRawStateLabel5.gridx = 0; constraintsRawStateLabel5.gridy = 6;
            constraintsRawStateLabel5.insets = new java.awt.Insets(0, 4, 4, 4);
            constraintsRawStateLabel5.anchor = java.awt.GridBagConstraints.WEST;
            getPreviewPanel().add(getRawState5(), constraintsRawStateLabel5);
            
            java.awt.GridBagConstraints constraintsRawStateLabel6 = new java.awt.GridBagConstraints();
            constraintsRawStateLabel6.gridx = 0; constraintsRawStateLabel6.gridy = 7;
            constraintsRawStateLabel6.insets = new java.awt.Insets(0, 4, 4, 4);
            constraintsRawStateLabel6.anchor = java.awt.GridBagConstraints.WEST;
            getPreviewPanel().add(getRawState6(), constraintsRawStateLabel6);
            
            java.awt.GridBagConstraints constraintsRawStateLabel7 = new java.awt.GridBagConstraints();
            constraintsRawStateLabel7.gridx = 0; constraintsRawStateLabel7.gridy = 8;
            constraintsRawStateLabel7.insets = new java.awt.Insets(0, 4, 4, 4);
            constraintsRawStateLabel7.anchor = java.awt.GridBagConstraints.WEST;
            getPreviewPanel().add(getRawState7(), constraintsRawStateLabel7);
            
            java.awt.GridBagConstraints constraintsRawStateLabel8 = new java.awt.GridBagConstraints();
            constraintsRawStateLabel8.gridx = 0; constraintsRawStateLabel8.gridy = 9;
            constraintsRawStateLabel8.insets = new java.awt.Insets(0, 4, 4, 4);
            constraintsRawStateLabel8.anchor = java.awt.GridBagConstraints.WEST;
            getPreviewPanel().add(getRawState8(), constraintsRawStateLabel8);
            
            java.awt.GridBagConstraints constraintsRawStateLabel9 = new java.awt.GridBagConstraints();
            constraintsRawStateLabel9.gridx = 0; constraintsRawStateLabel9.gridy = 10;
            constraintsRawStateLabel9.insets = new java.awt.Insets(0, 4, 4, 4);
            constraintsRawStateLabel9.anchor = java.awt.GridBagConstraints.WEST;
            getPreviewPanel().add(getRawState9(), constraintsRawStateLabel9);
            
            java.awt.GridBagConstraints constraintsRawStateLabel10 = new java.awt.GridBagConstraints();
            constraintsRawStateLabel10.gridx = 0; constraintsRawStateLabel10.gridy = 11;
            constraintsRawStateLabel10.insets = new java.awt.Insets(0, 4, 4, 4);
            constraintsRawStateLabel10.anchor = java.awt.GridBagConstraints.WEST;
            getPreviewPanel().add(getRawState10(), constraintsRawStateLabel10);
            
            java.awt.GridBagConstraints constraintsBlinkLabel = new java.awt.GridBagConstraints();
            constraintsBlinkLabel.gridx = 1; constraintsBlinkLabel.gridy = 0;
            constraintsBlinkLabel.insets = new java.awt.Insets(0, 4, 4, 4);
            getPreviewPanel().add(getBlinkLabel(), constraintsBlinkLabel);
            
            java.awt.GridBagConstraints constraintsBlinkCheckBox0 = new java.awt.GridBagConstraints();
            constraintsBlinkCheckBox0.gridx = 1; constraintsBlinkCheckBox0.gridy = 1;
            constraintsBlinkCheckBox0.insets = new java.awt.Insets(0, 4, 4, 4);
            constraintsBlinkCheckBox0.anchor = java.awt.GridBagConstraints.WEST;
            getPreviewPanel().add(getBlinkCheckBox0(), constraintsBlinkCheckBox0);
            
            java.awt.GridBagConstraints constraintsBlinkCheckBox1 = new java.awt.GridBagConstraints();
            constraintsBlinkCheckBox1.gridx = 1; constraintsBlinkCheckBox1.gridy = 2;
            constraintsBlinkCheckBox1.insets = new java.awt.Insets(0, 4, 4, 4);
            constraintsBlinkCheckBox1.anchor = java.awt.GridBagConstraints.WEST;
            getPreviewPanel().add(getBlinkCheckBox1(), constraintsBlinkCheckBox1);
            
            java.awt.GridBagConstraints constraintsBlinkCheckBox2 = new java.awt.GridBagConstraints();
            constraintsBlinkCheckBox2.gridx = 1; constraintsBlinkCheckBox2.gridy = 3;
            constraintsBlinkCheckBox2.insets = new java.awt.Insets(0, 4, 4, 4);
            constraintsBlinkCheckBox2.anchor = java.awt.GridBagConstraints.WEST;
            getPreviewPanel().add(getBlinkCheckBox2(), constraintsBlinkCheckBox2);
            
            java.awt.GridBagConstraints constraintsBlinkCheckBox3 = new java.awt.GridBagConstraints();
            constraintsBlinkCheckBox3.gridx = 1; constraintsBlinkCheckBox3.gridy = 4;
            constraintsBlinkCheckBox3.insets = new java.awt.Insets(0, 4, 4, 4);
            constraintsBlinkCheckBox3.anchor = java.awt.GridBagConstraints.WEST;
            getPreviewPanel().add(getBlinkCheckBox3(), constraintsBlinkCheckBox3);
            
            java.awt.GridBagConstraints constraintsBlinkCheckBox4 = new java.awt.GridBagConstraints();
            constraintsBlinkCheckBox4.gridx = 1; constraintsBlinkCheckBox4.gridy = 5;
            constraintsBlinkCheckBox4.insets = new java.awt.Insets(0, 4, 4, 4);
            constraintsBlinkCheckBox4.anchor = java.awt.GridBagConstraints.WEST;
            getPreviewPanel().add(getBlinkCheckBox4(), constraintsBlinkCheckBox4);
            
            java.awt.GridBagConstraints constraintsBlinkCheckBox5 = new java.awt.GridBagConstraints();
            constraintsBlinkCheckBox5.gridx = 1; constraintsBlinkCheckBox5.gridy = 6;
            constraintsBlinkCheckBox5.insets = new java.awt.Insets(0, 4, 4, 4);
            constraintsBlinkCheckBox5.anchor = java.awt.GridBagConstraints.WEST;
            getPreviewPanel().add(getBlinkCheckBox5(), constraintsBlinkCheckBox5);
            
            java.awt.GridBagConstraints constraintsBlinkCheckBox6 = new java.awt.GridBagConstraints();
            constraintsBlinkCheckBox6.gridx = 1; constraintsBlinkCheckBox6.gridy = 7;
            constraintsBlinkCheckBox6.insets = new java.awt.Insets(0, 4, 4, 4);
            constraintsBlinkCheckBox6.anchor = java.awt.GridBagConstraints.WEST;
            getPreviewPanel().add(getBlinkCheckBox6(), constraintsBlinkCheckBox6);
            
            java.awt.GridBagConstraints constraintsBlinkCheckBox7 = new java.awt.GridBagConstraints();
            constraintsBlinkCheckBox7.gridx = 1; constraintsBlinkCheckBox7.gridy = 8;
            constraintsBlinkCheckBox7.insets = new java.awt.Insets(0, 4, 4, 4);
            constraintsBlinkCheckBox7.anchor = java.awt.GridBagConstraints.WEST;
            getPreviewPanel().add(getBlinkCheckBox7(), constraintsBlinkCheckBox7);
            
            java.awt.GridBagConstraints constraintsBlinkCheckBox8 = new java.awt.GridBagConstraints();
            constraintsBlinkCheckBox8.gridx = 1; constraintsBlinkCheckBox8.gridy = 9;
            constraintsBlinkCheckBox8.insets = new java.awt.Insets(0, 4, 4, 4);
            constraintsBlinkCheckBox8.anchor = java.awt.GridBagConstraints.WEST;
            getPreviewPanel().add(getBlinkCheckBox8(), constraintsBlinkCheckBox8);
            
            java.awt.GridBagConstraints constraintsBlinkCheckBox9 = new java.awt.GridBagConstraints();
            constraintsBlinkCheckBox9.gridx = 1; constraintsBlinkCheckBox9.gridy = 10;
            constraintsBlinkCheckBox9.insets = new java.awt.Insets(0, 4, 4, 4);
            constraintsBlinkCheckBox9.anchor = java.awt.GridBagConstraints.WEST;
            getPreviewPanel().add(getBlinkCheckBox9(), constraintsBlinkCheckBox9);
            
            java.awt.GridBagConstraints constraintsBlinkCheckBox10 = new java.awt.GridBagConstraints();
            constraintsBlinkCheckBox10.gridx = 1; constraintsBlinkCheckBox10.gridy = 11;
            constraintsBlinkCheckBox10.insets = new java.awt.Insets(0, 4, 4, 4);
            constraintsBlinkCheckBox10.anchor = java.awt.GridBagConstraints.WEST;
            getPreviewPanel().add(getBlinkCheckBox10(), constraintsBlinkCheckBox10);
        } catch (java.lang.Throwable ivjExc) 
        {
            handleException(ivjExc);
        }
    }
    return previewPanel;
}

/**
 * Return the PointSelectionPanel property value.
 * @return com.cannontech.esub.editor.element.PointSelectionPanel
 */
public PointSelectionPanel getPointSelectionPanel() {
    if (pointSelectionPanel == null) {
        try {
            pointSelectionPanel = new PointSelectionPanel();
            pointSelectionPanel.setName("PointSelectionPanel");
            pointSelectionPanel.setPreferredSize(new Dimension(150,400));
            pointSelectionPanel.setMinimumSize(new Dimension(150,400));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return pointSelectionPanel;
}

public HashMap<Integer, Integer> getCustomBlinkMap() {
    return map;
}

/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) {
    if ( getPointSelectionPanel().getSelectedPoint() == null) {
        JOptionPane.showMessageDialog(this, "Please select a point from the device tree.", "Settings not done yet.", JOptionPane.WARNING_MESSAGE);
    }
    if( o instanceof LineElement) {
        ((LineElement)elem).setBlinkPointID(getPointSelectionPanel().getSelectedPoint().getLiteID());
        ((LineElement)elem).setCustomBlinkMap(map);
    }else if ( o instanceof DynamicText) {
        ((DynamicText)elem).setBlinkPointID(getPointSelectionPanel().getSelectedPoint().getLiteID());
        ((DynamicText)elem).setCustomBlinkMap(map);
    }
    return elem;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

    /* Uncomment the following lines to print uncaught exceptions to stdout */
     CTILogger.error("--------- UNCAUGHT EXCEPTION ---------", exception);
     
}

/**
 * Initialize the class.
 */
private void initialize() {
    try {
        setName("BlinkPointPanel");
        setLayout(new java.awt.GridBagLayout());

        java.awt.GridBagConstraints constraintsPointPanel = new java.awt.GridBagConstraints();
        constraintsPointPanel.gridx = 0; constraintsPointPanel.gridy = 0;
        constraintsPointPanel.gridwidth = 1;
        constraintsPointPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
        constraintsPointPanel.anchor = java.awt.GridBagConstraints.NORTH;
        constraintsPointPanel.insets = new java.awt.Insets(4, 4, 4, 4);
        constraintsPointPanel.weightx = .5;
        add(getPointSelectionPanel(), constraintsPointPanel);

        java.awt.GridBagConstraints constraintsPreviewPanel = new java.awt.GridBagConstraints();
        constraintsPreviewPanel.gridx = 1; constraintsPreviewPanel.gridy = 0;
        constraintsPreviewPanel.gridwidth = 1;
        constraintsPreviewPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
        constraintsPreviewPanel.weightx = .5;
        constraintsPreviewPanel.insets = new java.awt.Insets(4, 4, 4, 4);
        add(getPreviewPanel(), constraintsPreviewPanel);
        
        java.awt.GridBagConstraints constraintsButtonGroup = new java.awt.GridBagConstraints();
        constraintsButtonGroup.gridx = 1; constraintsButtonGroup.gridy = 1;
        constraintsButtonGroup.gridwidth = 2;
        constraintsButtonGroup.anchor = java.awt.GridBagConstraints.EAST;
        constraintsButtonGroup.insets = new java.awt.Insets(4, 4, 4, 50);
        add(getButtonGroup(), constraintsButtonGroup);

        checkBoxes[0] = getBlinkCheckBox0(); 
        checkBoxes[1] = getBlinkCheckBox1();
        checkBoxes[2] = getBlinkCheckBox2();
        checkBoxes[3] = getBlinkCheckBox3();
        checkBoxes[4] = getBlinkCheckBox4();
        checkBoxes[5] = getBlinkCheckBox5();
        checkBoxes[6] = getBlinkCheckBox6();
        checkBoxes[7] = getBlinkCheckBox7();
        checkBoxes[8] = getBlinkCheckBox8();
        checkBoxes[9] = getBlinkCheckBox9();
        checkBoxes[10] = getBlinkCheckBox10();
        
        getBlinkCheckBox0().addActionListener(this);
        getBlinkCheckBox1().addActionListener(this);
        getBlinkCheckBox2().addActionListener(this);
        getBlinkCheckBox3().addActionListener(this);
        getBlinkCheckBox4().addActionListener(this);
        getBlinkCheckBox5().addActionListener(this);
        getBlinkCheckBox6().addActionListener(this);
        getBlinkCheckBox7().addActionListener(this);
        getBlinkCheckBox8().addActionListener(this);
        getBlinkCheckBox9().addActionListener(this);
        getBlinkCheckBox10().addActionListener(this);

        getOkButton().addActionListener(this);
        getResetButton().addActionListener(this);
         
        stateLabels[0] = getRawState0(); 
        stateLabels[1] = getRawState1();
        stateLabels[2] = getRawState2();
        stateLabels[3] = getRawState3();
        stateLabels[4] = getRawState4();
        stateLabels[5] = getRawState5();
        stateLabels[6] = getRawState6();
        stateLabels[7] = getRawState7();
        stateLabels[8] = getRawState8();
        stateLabels[9] = getRawState9();
        stateLabels[10] = getRawState10();
         
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
    getPointSelectionPanel().getIvjDevicePointTree().addTreeSelectionListener(this);
}
/**
 * Creation date: (1/23/2002 11:12:06 AM)
 * @return boolean
 */
public boolean isInputValid() {
    LitePoint pt = getPointSelectionPanel().getSelectedPoint();
    return (pt != null && 
            (pt.getPointType() == PointTypes.STATUS_POINT ||
             pt.getPointType() == PointTypes.CALCULATED_STATUS_POINT ||
             pt.getPointType() == PointTypes.ANALOG_POINT));
}

/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
@SuppressWarnings("unchecked")
public void setValue(Object o) 
{
    if(o instanceof LineElement) {
        elem = (LineElement) o;
        map = new HashMap( ((LineElement)elem).getCustomBlinkMap() );
    
        getPointSelectionPanel().refresh();
    
        // Set selected point 
        int lpid = ((LineElement)elem).getBlinkPointID();
        LitePoint litePoint = null;
        try {
            litePoint = YukonSpringHook.getBean(PointDao.class).getLitePoint(lpid);
        }catch(NotFoundException nfe) {
            CTILogger.error("The blink point (pointId:"+ lpid + ") for this line might have been deleted!", nfe);
        }
        if(litePoint != null) {
            getPointSelectionPanel().selectPoint(litePoint);
        }
    }else if( o instanceof DynamicText) {
        elem = (DynamicText) o;
        map = new HashMap( ((DynamicText)elem).getCustomBlinkMap());
    
        getPointSelectionPanel().refresh();
    
        // Set selected point 
        int lpid = ((DynamicText)elem).getBlinkPointID();
        LitePoint litePoint = null;
        try {
            litePoint = YukonSpringHook.getBean(PointDao.class).getLitePoint(lpid);
        }catch(NotFoundException nfe) {
            CTILogger.error("The blink point (pointId:"+ lpid + ") for this DynamicText might have been deleted!", nfe);
        }
        if(litePoint != null) {
            getPointSelectionPanel().selectPoint(litePoint);
        }
    }
}
/**
 * Creation date: (12/18/2001 4:16:51 PM)
 * @param evt javax.swing.event.TreeSelectionEvent
 */
public void valueChanged(TreeSelectionEvent evt) 
{
    LitePoint p = getPointSelectionPanel().getSelectedPoint();  
    if(p != null)
    {
        setPreviewPanelValues(p);
    }
    fireInputUpdate();
}

@SuppressWarnings("unchecked")
public void resetPreviewPanelValues(LitePoint p)
{
    List values = new ArrayList(12);
    if(p != null)
    {
        LiteStateGroup lsg = YukonSpringHook.getBean(StateDao.class).getLiteStateGroup(p.getStateGroupID());
        List states = lsg.getStatesList();
        
        for(int i = 0; i < states.size(); i++) 
        {
            LiteState state = (LiteState)states.get(i);
            int rawStateNumber = state.getStateRawState();
           
            checkBoxes[rawStateNumber].setSelected(false);
            values.add(checkBoxes[rawStateNumber]);
            
            stateLabels[i].setText(state.getStateText());
            
            checkBoxes[i].setVisible(true);
            stateLabels[i].setVisible(true);
        }
        
        for(int i = 0; i < 11; i++)
        {
            
           if(!values.contains(checkBoxes[i]))
           {
               checkBoxes[i].setVisible(false);
               stateLabels[i].setVisible(false);
           }
        }
    }else
    {
        for(int i = 0; i < 11; i++)
        {
            
            checkBoxes[i].setVisible(false);
            stateLabels[i].setVisible(false);
        }
    }
}

@SuppressWarnings("unchecked")
public void setPreviewPanelValues(LitePoint p) 
{
    List values = new ArrayList(12);
    LiteStateGroup lsg = YukonSpringHook.getBean(StateDao.class).getLiteStateGroup(p.getStateGroupID());
    List states = lsg.getStatesList();
    
    for(int i = 0; i < states.size(); i++) 
    {
        
        LiteState state = (LiteState)states.get(i);
        int rawStateNumber = state.getStateRawState();
        
        if(!(map.get(rawStateNumber) == null))
        {
            checkBoxes[rawStateNumber].setSelected(true);
            
        }else {
       
            checkBoxes[rawStateNumber].setSelected(false);
        
        }
        values.add(checkBoxes[rawStateNumber]);
        
        stateLabels[i].setText(state.getStateText());
        checkBoxes[rawStateNumber].setVisible(true);
        stateLabels[rawStateNumber].setVisible(true);
    }
    
    for(int i = 0; i < 11; i++)
    {
       if(!values.contains(checkBoxes[i]))
       {
           checkBoxes[i].setVisible(false);
           stateLabels[i].setVisible(false);
       }
    }
}

private JLabel getBlinkLabel()
{
    if (blinkLabel == null)
    {
        try
        {
            blinkLabel = new JLabel();
            blinkLabel.setName("BlinkLabel");
            blinkLabel.setText("Blink:");
        }catch( java.lang.Throwable ivjExc )
        {
            handleException(ivjExc);
        }
    }
    return blinkLabel;
}

private JLabel getRawStateLabel()
{
    if (rawStateLabel == null)
    {
        try
        {
            rawStateLabel = new JLabel();
            rawStateLabel.setName("RawStateLabel");
            rawStateLabel.setText("Raw State");
            rawStateLabel.setAlignmentX(javax.swing.SwingConstants.TOP);

        }catch( java.lang.Throwable ivjExc )
        {
            handleException(ivjExc);
        }
    }
    return rawStateLabel;
}

private javax.swing.JLabel getRawState0() {
    if (rawState0 == null) {
        try {
            rawState0 = new javax.swing.JLabel();
            rawState0.setName("RawState0");
            rawState0.setText("0");
            rawState0.setHorizontalAlignment(SwingConstants.LEFT);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return rawState0;
}

private javax.swing.JLabel getRawState1() {
    if (rawState1 == null) {
        try {
            rawState1 = new javax.swing.JLabel();
            rawState1.setName("RawState1");
            rawState1.setText("1");
            rawState1.setHorizontalAlignment(SwingConstants.LEFT);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return rawState1;
}

private javax.swing.JLabel getRawState2() {
    if (rawState2 == null) {
        try {
            rawState2 = new javax.swing.JLabel();
            rawState2.setName("RawState2");
            rawState2.setText("2");
            rawState2.setHorizontalAlignment(SwingConstants.LEFT);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return rawState2;
}

private javax.swing.JLabel getRawState3() {
    if (rawState3 == null) {
        try {
            rawState3 = new javax.swing.JLabel();
            rawState3.setName("RawState3");
            rawState3.setText("3");
            rawState3.setHorizontalAlignment(SwingConstants.LEFT);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return rawState3;
}

private javax.swing.JLabel getRawState4() 
{
    if (rawState4 == null) {
        try {
            rawState4 = new javax.swing.JLabel();
            rawState4.setName("RawState4");
            rawState4.setText("4");
            rawState4.setHorizontalAlignment(SwingConstants.LEFT);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return rawState4;
}

private javax.swing.JLabel getRawState5() {
    if (rawState5 == null) {
        try {
            rawState5 = new javax.swing.JLabel();
            rawState5.setName("RawState5");
            rawState5.setText("5");
            rawState5.setHorizontalAlignment(SwingConstants.LEFT);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return rawState5;
}

private javax.swing.JLabel getRawState6() {
    if (rawState6 == null) {
        try {
            rawState6 = new javax.swing.JLabel();
            rawState6.setName("RawState6");
            rawState6.setText("6");
            rawState6.setHorizontalAlignment(SwingConstants.LEFT);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return rawState6;
}

private javax.swing.JLabel getRawState7() {
    if (rawState7 == null) {
        try {
            rawState7 = new javax.swing.JLabel();
            rawState7.setName("RawState7");
            rawState7.setText("7");
            rawState7.setHorizontalAlignment(SwingConstants.LEFT);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return rawState7;
}

private javax.swing.JLabel getRawState8() {
    if (rawState8 == null) {
        try {
            rawState8 = new javax.swing.JLabel();
            rawState8.setName("RawState8");
            rawState8.setText("8");
            rawState8.setHorizontalAlignment(SwingConstants.LEFT);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return rawState8;
}

private javax.swing.JLabel getRawState9() {
    if (rawState9 == null) {
        try {
            rawState9 = new javax.swing.JLabel();
            rawState9.setName("RawState9");
            rawState9.setText("9");
            rawState9.setHorizontalAlignment(SwingConstants.LEFT);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return rawState9;
}

private javax.swing.JLabel getRawState10() {
    if (rawState10 == null) {
        try {
            rawState10 = new javax.swing.JLabel();
            rawState10.setName("RawState10");
            rawState10.setText("10");
            rawState10.setHorizontalAlignment(SwingConstants.LEFT);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return rawState10;
}

public JButton getOkButton()
{
    if (okButton == null) {
        try {
            okButton = new JButton();
            okButton.setName("OKButton");
            okButton.setText("OK");
            okButton.setHorizontalTextPosition(SwingConstants.CENTER);
            okButton.setVerticalTextPosition(SwingConstants.CENTER);
            okButton.setPreferredSize(new Dimension(60, 30));
            okButton.setMinimumSize(new Dimension(60, 30));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return okButton;
}

public JCheckBox getBlinkCheckBox0() {
    if( blinkCheckBox0 == null ){
        blinkCheckBox0 = new JCheckBox("Blink");
        blinkCheckBox0.setMinimumSize(new Dimension ( 100, 24));
        blinkCheckBox0.setPreferredSize(new Dimension( 100, 24));
    }
    return blinkCheckBox0;
}

public JCheckBox getBlinkCheckBox1() {
    if( blinkCheckBox1 == null ){
        blinkCheckBox1 = new JCheckBox("Blink");
        blinkCheckBox1.setMinimumSize(new Dimension ( 100, 24));
        blinkCheckBox1.setPreferredSize(new Dimension( 100, 24));
    }
    return blinkCheckBox1;
}

public JCheckBox getBlinkCheckBox2() {
    if( blinkCheckBox2 == null ){
        blinkCheckBox2 = new JCheckBox("Blink");
        blinkCheckBox2.setMinimumSize(new Dimension ( 100, 24));
        blinkCheckBox2.setPreferredSize(new Dimension( 100, 24));
    }
    return blinkCheckBox2;
}

public JCheckBox getBlinkCheckBox3() {
    if( blinkCheckBox3 == null ){
        blinkCheckBox3 = new JCheckBox("Blink");
        blinkCheckBox3.setMinimumSize(new Dimension ( 100, 24));
        blinkCheckBox3.setPreferredSize(new Dimension( 100, 24));
    }
    return blinkCheckBox3;
}

public JCheckBox getBlinkCheckBox4() {
    if( blinkCheckBox4 == null ){
        blinkCheckBox4 = new JCheckBox("Blink");
        blinkCheckBox4.setMinimumSize(new Dimension ( 100, 24));
        blinkCheckBox4.setPreferredSize(new Dimension( 100, 24));
    }
    return blinkCheckBox4;
}

public JCheckBox getBlinkCheckBox5() {
    if( blinkCheckBox5 == null ){
        blinkCheckBox5 = new JCheckBox("Blink");
        blinkCheckBox5.setMinimumSize(new Dimension ( 100, 24));
        blinkCheckBox5.setPreferredSize(new Dimension( 100, 24));
    }
    return blinkCheckBox5;
}

public JCheckBox getBlinkCheckBox6() {
    if( blinkCheckBox6 == null ){
        blinkCheckBox6 = new JCheckBox("Blink");
        blinkCheckBox6.setMinimumSize(new Dimension ( 100, 24));
        blinkCheckBox6.setPreferredSize(new Dimension( 100, 24));
    }
    return blinkCheckBox6;
}

public JCheckBox getBlinkCheckBox7() {
    if( blinkCheckBox7 == null ){
        blinkCheckBox7 = new JCheckBox("Blink");
        blinkCheckBox7.setMinimumSize(new Dimension ( 100, 24));
        blinkCheckBox7.setPreferredSize(new Dimension( 100, 24));
    }
    return blinkCheckBox7;
}

public JCheckBox getBlinkCheckBox8() {
    if( blinkCheckBox8 == null ){
        blinkCheckBox8 = new JCheckBox("Blink");
        blinkCheckBox8.setMinimumSize(new Dimension ( 100, 24));
        blinkCheckBox8.setPreferredSize(new Dimension( 100, 24));
    }
    return blinkCheckBox8;
}

public JCheckBox getBlinkCheckBox9() {
    if( blinkCheckBox9 == null ){
        blinkCheckBox9 = new JCheckBox("Blink");
        blinkCheckBox9.setMinimumSize(new Dimension ( 100, 24));
        blinkCheckBox9.setPreferredSize(new Dimension( 100, 24));
    }
    return blinkCheckBox9;
}

public JCheckBox getBlinkCheckBox10() {
    if( blinkCheckBox10 == null ){
        blinkCheckBox10 = new JCheckBox("Blink");
        blinkCheckBox10.setMinimumSize(new Dimension ( 100, 24));
        blinkCheckBox10.setPreferredSize(new Dimension( 100, 24));
    }
    return blinkCheckBox10;
}

private JButton getResetButton()
{
    if (resetButton == null) {
        try {
            resetButton = new JButton();
            resetButton.setName("resetButton");
            resetButton.setText("Reset");
            resetButton.setPreferredSize(new Dimension( 80, 30));
            resetButton.setMinimumSize(new Dimension( 80, 30));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return resetButton;
}

public void actionPerformed(ActionEvent e) {
    Object source = e.getSource();
    if (source == getResetButton()) {
        map.clear();
        LitePoint p = getPointSelectionPanel().getSelectedPoint();
        resetPreviewPanelValues(p);

    }else if( source == getOkButton()) {
        // save comboBox index values to custom map
        for(int i = 0; i < 11; i++) {
            if(checkBoxes[i].isSelected()) {
                map.put(i, new Integer(1));
            }
        }
    }
}
}