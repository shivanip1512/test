package com.cannontech.esub.editor.element;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.core.dao.StateDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.esub.element.DynamicText;
import com.cannontech.spring.YukonSpringHook;

public class TextPointPanel extends DataInputPanel implements ActionListener, TreeSelectionListener {
    private javax.swing.JPanel pointPanel = null;
    private javax.swing.JPanel buttonGroup = null;
    private PointSelectionPanel pointSelectionPanel = null;
    private DynamicText dynamicText;
    private HashMap<Integer, String> map = new HashMap<Integer, String>();
    private JButton okButton;
    private JLabel previewLabel;
    private JLabel rawStateLabel;
    private JLabel textLabel;
    private JPanel previewPanel;
    private JTextField textField0;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JTextField textField5;
    private JTextField textField6;
    private JTextField textField7;
    private JTextField textField8;
    private JTextField textField9;
    private JTextField textField10;
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
    private JTextField[] textFields = new JTextField[12];
    private JLabel[] stateLabels = new JLabel[12];
    
/**
 * ArrowPointPanel constructor comment.
 */
public TextPointPanel() {
    super();
    initialize();
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getPointPanel() {
    if (pointPanel == null) {
        try {
            pointPanel = new javax.swing.JPanel();
            pointPanel.setName("PointPanel");
            pointPanel.setLayout(new java.awt.GridBagLayout());
            pointPanel.setBorder(new TitleBorder("Point Selection:"));

            java.awt.GridBagConstraints constraintsPointSelectionPanel = new java.awt.GridBagConstraints();
            constraintsPointSelectionPanel.gridx = 0; constraintsPointSelectionPanel.gridy = 0;
            constraintsPointSelectionPanel.fill = java.awt.GridBagConstraints.BOTH;
            constraintsPointSelectionPanel.weightx = 1.0;
            constraintsPointSelectionPanel.weighty = 1.0;
            constraintsPointSelectionPanel.gridwidth = 1;
            constraintsPointSelectionPanel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsPointSelectionPanel.insets = new java.awt.Insets(4, 4, 4, 4);
            pointPanel.add(getPointSelectionPanel(), constraintsPointSelectionPanel);

        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return pointPanel;
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
            previewPanel.setLayout(null);
            
            previewPanel.setPreferredSize(new Dimension( 180, 520 ));
            previewPanel.setSize(new Dimension( 180, 520 ));
            previewPanel.setMinimumSize(new Dimension( 180, 520 ));
            previewPanel.setBorder(new TitleBorder("Preview"));
            previewPanel.setAlignmentX(javax.swing.SwingConstants.TOP);
            
            getPreviewLabel().setBounds(new Rectangle(20,20,100,20));
            previewPanel.add(getPreviewLabel());
            
            getRawStateLabel().setBounds(new Rectangle( 10, 40, 60, 20));
            previewPanel.add(getRawStateLabel());
            
            getTextLabel().setBounds(new Rectangle( 75, 40, 60, 20));
            getTextLabel().setHorizontalAlignment(SwingConstants.CENTER);
            previewPanel.add(getTextLabel());
            
            getRawState0().setBounds(new Rectangle( 10, 75, 90, 30));
            previewPanel.add(getRawState0());
            
            getRawState1().setBounds(new Rectangle( 10, 110, 90, 30));
            previewPanel.add(getRawState1());
            
            getRawState2().setBounds(new Rectangle( 10, 145, 90, 30));
            previewPanel.add(getRawState2());
            
            getRawState3().setBounds(new Rectangle( 10, 180, 90, 30));
            previewPanel.add(getRawState3());
            
            getRawState4().setBounds(new Rectangle( 10, 215, 90, 30));
            previewPanel.add(getRawState4());
            
            getRawState5().setBounds(new Rectangle( 10, 250, 85, 30));
            previewPanel.add(getRawState5());
            
            getRawState6().setBounds(new Rectangle( 10, 285, 90, 30));
            previewPanel.add(getRawState6());
            
            getRawState7().setBounds(new Rectangle( 10, 320, 90, 30));
            previewPanel.add(getRawState7());
            
            getRawState8().setBounds(new Rectangle( 10, 355, 90, 30));
            previewPanel.add(getRawState8());
            
            getRawState9().setBounds(new Rectangle( 10, 390, 90, 30));
            previewPanel.add(getRawState9());
            
            getRawState10().setBounds(new Rectangle( 10, 425, 90, 30));
            previewPanel.add(getRawState10());
            
            getTextField0().setBounds(new Rectangle( 105, 75, 120, 27));
            previewPanel.add(getTextField0());
            
            getTextField1().setBounds(new Rectangle( 105, 110, 120, 27));
            previewPanel.add(getTextField1());
            
            getTextField2().setBounds(new Rectangle( 105, 145, 120, 27));
            previewPanel.add(getTextField2());
            
            getTextField3().setBounds(new Rectangle( 105, 180, 120, 27));
            previewPanel.add(getTextField3());
            
            getTextField4().setBounds(new Rectangle( 105, 215, 120, 27));
            previewPanel.add(getTextField4());
            
            getTextField5().setBounds(new Rectangle( 105, 250, 120, 27));
            previewPanel.add(getTextField5());
            
            getTextField6().setBounds(new Rectangle( 105, 285, 120, 27));
            previewPanel.add(getTextField6());
            
            getTextField7().setBounds(new Rectangle( 105, 320, 120, 27));
            previewPanel.add(getTextField7());
            
            getTextField8().setBounds(new Rectangle( 105, 355, 120, 27));
            previewPanel.add(getTextField8());
            
            getTextField9().setBounds(new Rectangle( 105, 390, 120, 27));
            previewPanel.add(getTextField9());
            
            getTextField10().setBounds(new Rectangle( 105, 425, 120, 27));
            previewPanel.add(getTextField10());
                    
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

/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) {
    if ( getPointSelectionPanel().getSelectedPoint() == null) {
        JOptionPane.showMessageDialog(this, "Please select a point from the device tree.", "Settings not done yet.", JOptionPane.WARNING_MESSAGE);
    }
    dynamicText.setPoint(getPointSelectionPanel().getSelectedPoint());
    dynamicText.setCustomTextMap(map);
    return dynamicText;
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
        setName("ArrowPointPanel");
        setLayout(new java.awt.GridBagLayout());

        java.awt.GridBagConstraints constraintsPointPanel = new java.awt.GridBagConstraints();
        constraintsPointPanel.gridx = 0; constraintsPointPanel.gridy = 0;
        constraintsPointPanel.gridwidth = 1;
        constraintsPointPanel.fill = java.awt.GridBagConstraints.BOTH;
        constraintsPointPanel.anchor = java.awt.GridBagConstraints.NORTH;
        constraintsPointPanel.insets = new java.awt.Insets(4, 4, 4, 4);
        constraintsPointPanel.weightx = .5;
        add(getPointPanel(), constraintsPointPanel);

        java.awt.GridBagConstraints constraintsPreviewPanel = new java.awt.GridBagConstraints();
        constraintsPreviewPanel.gridx = 1; constraintsPreviewPanel.gridy = 0;
        constraintsPreviewPanel.gridwidth = 1;
        constraintsPreviewPanel.fill = java.awt.GridBagConstraints.BOTH;
        constraintsPreviewPanel.weightx = .5;
        constraintsPreviewPanel.insets = new java.awt.Insets(4, 4, 4, 4);
        add(getPreviewPanel(), constraintsPreviewPanel);
        
        java.awt.GridBagConstraints constraintsButtonGroup = new java.awt.GridBagConstraints();
        constraintsButtonGroup.gridx = 1; constraintsButtonGroup.gridy = 1;
        constraintsButtonGroup.gridwidth = 2;
        constraintsButtonGroup.anchor = java.awt.GridBagConstraints.EAST;
        constraintsButtonGroup.insets = new java.awt.Insets(4, 4, 4, 50);
        add(getButtonGroup(), constraintsButtonGroup);

        textFields[0] = getTextField0(); 
        textFields[1] = getTextField1();
        textFields[2] = getTextField2();
        textFields[3] = getTextField3();
        textFields[4] = getTextField4();
        textFields[5] = getTextField5();
        textFields[6] = getTextField6();
        textFields[7] = getTextField7();
        textFields[8] = getTextField8();
        textFields[9] = getTextField9();
        textFields[10] = getTextField10();
        
        getTextField0().addActionListener(this);
        getTextField1().addActionListener(this);
        getTextField2().addActionListener(this);
        getTextField3().addActionListener(this);
        getTextField4().addActionListener(this);
        getTextField5().addActionListener(this);
        getTextField6().addActionListener(this);
        getTextField7().addActionListener(this);
        getTextField8().addActionListener(this);
        getTextField9().addActionListener(this);
        getTextField10().addActionListener(this);

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
    dynamicText = (DynamicText) o;
    map = new HashMap( dynamicText.getCustomTextMap());

    getPointSelectionPanel().refresh();

    // Set selected point 
    LitePoint lp = dynamicText.getPoint();
    if( lp != null )
    {  // this is usually not null since the default pointid is 7 ?
        getPointSelectionPanel().selectPoint(lp);
    }
    
//    boolean control = stateImage.getControlEnabled();
//    getControlCheckBox().setSelected(control);
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
           
            textFields[rawStateNumber].setText(state.getStateText());
            values.add(textFields[rawStateNumber]);
            
            stateLabels[i].setText(state.getStateText());
            
            textFields[i].setVisible(true);
            stateLabels[i].setVisible(true);
        }
        
        for(int i = 0; i < 11; i++)
        {
            
           if(!values.contains(textFields[i]))
           {
               textFields[i].setVisible(false);
               stateLabels[i].setVisible(false);
           }
        }
    }else
    {
        for(int i = 0; i < 11; i++)
        {
            
            textFields[i].setVisible(false);
            stateLabels[i].setVisible(false);
        }
    }
}

public void setPreviewPanelValues(LitePoint p) 
{
    List<JTextField> values = new ArrayList<JTextField>(12);
    LiteStateGroup lsg = YukonSpringHook.getBean(StateDao.class).getLiteStateGroup(p.getStateGroupID());
    List<LiteState> states = lsg.getStatesList();
    
    for(int i = 0; i < states.size(); i++) 
    {
        
        LiteState state = states.get(i);
        int rawStateNumber = state.getStateRawState();
        
        if(!(map.get(rawStateNumber) == null))
        {
            textFields[rawStateNumber].setText(map.get(rawStateNumber));
            
        }else {
       
            textFields[rawStateNumber].setText(state.getStateText());
        
        }
        values.add(textFields[rawStateNumber]);
        
        stateLabels[i].setText(state.getStateText());
        textFields[rawStateNumber].setVisible(true);
        stateLabels[rawStateNumber].setVisible(true);
    }
    
    for(int i = 0; i < 11; i++)
    {
       if(!values.contains(textFields[i]))
       {
           textFields[i].setVisible(false);
           stateLabels[i].setVisible(false);
       }
    }
}

private JLabel getPreviewLabel()
{
    if (previewLabel == null)
    {
        try
        {
            previewLabel = new JLabel();
            previewLabel.setName("PreviewLabel");
            previewLabel.setText("State Text:");
            previewLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
            
        }catch( java.lang.Throwable ivjExc )
        {
            handleException(ivjExc);
        }
    }
    return previewLabel;
}

private JLabel getTextLabel()
{
    if (textLabel == null)
    {
        try
        {
            textLabel = new JLabel();
            textLabel.setName("TextLabel");
            textLabel.setText("Text:");
        }catch( java.lang.Throwable ivjExc )
        {
            handleException(ivjExc);
        }
    }
    return textLabel;
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
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return okButton;
}

public JTextField getTextField0() {
    if( textField0 == null ){
        textField0 = new JTextField();
        textField0.setMinimumSize(new Dimension ( 120, 50));
        textField0.setPreferredSize(new Dimension( 120, 50));
    }
    return textField0;
}

public JTextField getTextField1() {
    if( textField1 == null ){
        textField1 = new JTextField();
        textField1.setMinimumSize(new Dimension ( 120, 50));
        textField1.setPreferredSize(new Dimension( 120, 50));
    }
    return textField1;
}

public JTextField getTextField2() {
    if( textField2 == null ){
        textField2 = new JTextField();
        textField2.setMinimumSize(new Dimension ( 120, 50));
        textField2.setPreferredSize(new Dimension( 120, 50));
    }
    return textField2;
}

public JTextField getTextField3() {
    if( textField3 == null ){
        textField3 = new JTextField();
        textField3.setMinimumSize(new Dimension ( 120, 50));
        textField3.setPreferredSize(new Dimension( 120, 50));
    }
    return textField3;
}

public JTextField getTextField4() {
    if( textField4 == null ){
        textField4 = new JTextField();
        textField4.setMinimumSize(new Dimension ( 120, 50));
        textField4.setPreferredSize(new Dimension( 120, 50));
    }
    return textField4;
}

public JTextField getTextField5() {
    if( textField5 == null ){
        textField5 = new JTextField();
        textField5.setMinimumSize(new Dimension ( 120, 50));
        textField5.setPreferredSize(new Dimension( 120, 50));
    }
    return textField5;
}

public JTextField getTextField6() {
    if( textField6 == null ){
        textField6 = new JTextField();
        textField6.setMinimumSize(new Dimension ( 120, 50));
        textField6.setPreferredSize(new Dimension( 120, 50));
    }
    return textField6;
}

public JTextField getTextField7() {
    if( textField7 == null ){
        textField7 = new JTextField();
        textField7.setMinimumSize(new Dimension ( 120, 50));
        textField7.setPreferredSize(new Dimension( 120, 50));
    }
    return textField7;
}

public JTextField getTextField8() {
    if( textField8 == null ){
        textField8 = new JTextField();
        textField8.setMinimumSize(new Dimension ( 120, 50));
        textField8.setPreferredSize(new Dimension( 120, 50));
    }
    return textField8;
}

public JTextField getTextField9() {
    if( textField9 == null ){
        textField9 = new JTextField();
        textField9.setMinimumSize(new Dimension ( 120, 50));
        textField9.setPreferredSize(new Dimension( 120, 50));
    }
    return textField9;
}

public JTextField getTextField10() {
    if( textField10 == null ){
        textField10 = new JTextField();
        textField10.setMinimumSize(new Dimension ( 120, 50));
        textField10.setPreferredSize(new Dimension( 120, 50));
    }
    return textField10;
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

@SuppressWarnings("unchecked")
public void actionPerformed(ActionEvent e) {
    Object source = e.getSource();
    if (source == getResetButton()) {
        map.clear();
        LitePoint p = getPointSelectionPanel().getSelectedPoint();
        resetPreviewPanelValues(p);

    }else if( source == getOkButton()) {
        // save comboBox index values to custom map
        for(int i = 0; i < 11; i++) {
            map.put(i, textFields[i].getText());
        }
    }
}
}