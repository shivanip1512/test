package com.cannontech.esub.editor.element;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
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
import com.cannontech.esub.element.LineElement;
import com.cannontech.spring.YukonSpringHook;

public class LineArrowPointPanel extends DataInputPanel implements ActionListener, TreeSelectionListener {
    private javax.swing.JPanel pointPanel = null;
    private javax.swing.JPanel buttonGroup = null;
    private PointSelectionPanel pointSelectionPanel = null;
    private LineElement lineElement;
    private Map<Integer, Integer> map = new HashMap<Integer, Integer>();
    private JButton okButton;
    private JLabel rawStateLabel;
    private JLabel arrowLabel;
    private JPanel previewPanel;
    private JComboBox arrowComboBox0;
    private JComboBox arrowComboBox1;
    private JComboBox arrowComboBox2;
    private JComboBox arrowComboBox3;
    private JComboBox arrowComboBox4;
    private JComboBox arrowComboBox5;
    private JComboBox arrowComboBox6;
    private JComboBox arrowComboBox7;
    private JComboBox arrowComboBox8;
    private JComboBox arrowComboBox9;
    private JComboBox arrowComboBox10;
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
    private JComboBox[] comboBoxes = new JComboBox[12];
    private JLabel[] stateLabels = new JLabel[12];

/**
 * ArrowPointPanel constructor comment.
 */
public LineArrowPointPanel() {
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
            
            getRawStateLabel().setBounds(new Rectangle( 10, 40, 60, 20));
            previewPanel.add(getRawStateLabel());
            
            getArrowLabel().setBounds(new Rectangle( 75, 40, 60, 20));
            getArrowLabel().setHorizontalAlignment(SwingConstants.CENTER);
            previewPanel.add(getArrowLabel());
            
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
            
            getArrowComboBox0().setBounds(new Rectangle( 105, 75, 80, 27));
            previewPanel.add(getArrowComboBox0());
            
            getArrowComboBox1().setBounds(new Rectangle( 105, 110, 80, 27));
            previewPanel.add(getArrowComboBox1());
            
            getArrowComboBox2().setBounds(new Rectangle( 105, 145, 80, 27));
            previewPanel.add(getArrowComboBox2());
            
            getArrowComboBox3().setBounds(new Rectangle( 105, 180, 80, 27));
            previewPanel.add(getArrowComboBox3());
            
            getArrowComboBox4().setBounds(new Rectangle( 105, 215, 80, 27));
            previewPanel.add(getArrowComboBox4());
            
            getArrowComboBox5().setBounds(new Rectangle( 105, 250, 80, 27));
            previewPanel.add(getArrowComboBox5());
            
            getArrowComboBox6().setBounds(new Rectangle( 105, 285, 80, 27));
            previewPanel.add(getArrowComboBox6());
            
            getArrowComboBox7().setBounds(new Rectangle( 105, 320, 80, 27));
            previewPanel.add(getArrowComboBox7());
            
            getArrowComboBox8().setBounds(new Rectangle( 105, 355, 80, 27));
            previewPanel.add(getArrowComboBox8());
            
            getArrowComboBox9().setBounds(new Rectangle( 105, 390, 80, 27));
            previewPanel.add(getArrowComboBox9());
            
            getArrowComboBox10().setBounds(new Rectangle( 105, 425, 80, 27));
            previewPanel.add(getArrowComboBox10());
                    
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

public Map<Integer, Integer> getCustomArrowMap() {
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
    lineElement.setArrowPointID(getPointSelectionPanel().getSelectedPoint().getLiteID());
    lineElement.setCustomArrowMap(map);
    return lineElement;
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

        comboBoxes[0] = getArrowComboBox0(); 
        comboBoxes[1] = getArrowComboBox1();
        comboBoxes[2] = getArrowComboBox2();
        comboBoxes[3] = getArrowComboBox3();
        comboBoxes[4] = getArrowComboBox4();
        comboBoxes[5] = getArrowComboBox5();
        comboBoxes[6] = getArrowComboBox6();
        comboBoxes[7] = getArrowComboBox7();
        comboBoxes[8] = getArrowComboBox8();
        comboBoxes[9] = getArrowComboBox9();
        comboBoxes[10] = getArrowComboBox10();
        
        getArrowComboBox0().addActionListener(this);
        getArrowComboBox1().addActionListener(this);
        getArrowComboBox2().addActionListener(this);
        getArrowComboBox3().addActionListener(this);
        getArrowComboBox4().addActionListener(this);
        getArrowComboBox5().addActionListener(this);
        getArrowComboBox6().addActionListener(this);
        getArrowComboBox7().addActionListener(this);
        getArrowComboBox8().addActionListener(this);
        getArrowComboBox9().addActionListener(this);
        getArrowComboBox10().addActionListener(this);

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
public void setValue(Object o) 
{
    lineElement = (LineElement) o;
    map = new HashMap<Integer, Integer>( lineElement.getCustomArrowMap());

    getPointSelectionPanel().refresh();

    // Set selected point 
    int lpid = lineElement.getArrowPointID();
    LitePoint litePoint = null;
    try {
        litePoint = YukonSpringHook.getBean(PointDao.class).getLitePoint(lpid);
    }catch(NotFoundException nfe) {
        CTILogger.error("The arrow point (pointId:"+ lpid + ") for this line might have been deleted!", nfe);
    }
    if(litePoint != null) {
        getPointSelectionPanel().selectPoint(litePoint);
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

public void resetPreviewPanelValues(LitePoint p)
{
    List<JComboBox> values = new ArrayList<JComboBox>(12);
    if(p != null)
    {
        LiteStateGroup lsg = YukonSpringHook.getBean(StateDao.class).getLiteStateGroup(p.getStateGroupID());
        List<LiteState> states = lsg.getStatesList();
        
        for(int i = 0; i < states.size(); i++) 
        {
            LiteState state = states.get(i);
            int rawStateNumber = state.getStateRawState();
           
            comboBoxes[rawStateNumber].setSelectedIndex(0);
            values.add(comboBoxes[rawStateNumber]);
            
            stateLabels[i].setText(state.getStateText());
            
            comboBoxes[i].setVisible(true);
            stateLabels[i].setVisible(true);
        }
        
        for(int i = 0; i < 11; i++)
        {
            
           if(!values.contains(comboBoxes[i]))
           {
               comboBoxes[i].setVisible(false);
               stateLabels[i].setVisible(false);
           }
        }
    }else
    {
        for(int i = 0; i < 11; i++)
        {
            
            comboBoxes[i].setVisible(false);
            stateLabels[i].setVisible(false);
        }
    }
}

public void setPreviewPanelValues(LitePoint p) 
{
    List<JComboBox> values = new ArrayList<JComboBox>(12);
    LiteStateGroup lsg = YukonSpringHook.getBean(StateDao.class).getLiteStateGroup(p.getStateGroupID());
    List<LiteState> states = lsg.getStatesList();
    
    for(int i = 0; i < states.size(); i++) 
    {
        
        LiteState state = states.get(i);
        int rawStateNumber = state.getStateRawState();
        
        if(!(map.get(rawStateNumber) == null))
        {
            int arrowNumber = new Integer( map.get(rawStateNumber)).intValue();
            if(arrowNumber == 5 || arrowNumber == 6 || arrowNumber == 7) {
                arrowNumber = arrowNumber - 4;
            }else if(arrowNumber == 12) {
                arrowNumber = 4;
            }
            comboBoxes[rawStateNumber].setSelectedIndex(arrowNumber);
            
        }else {
       
            comboBoxes[rawStateNumber].setSelectedIndex(0);
        
        }
        values.add(comboBoxes[rawStateNumber]);
        
        stateLabels[i].setText(state.getStateText());
        comboBoxes[rawStateNumber].setVisible(true);
        stateLabels[rawStateNumber].setVisible(true);
    }
    
    for(int i = 0; i < 11; i++)
    {
       if(!values.contains(comboBoxes[i]))
       {
           comboBoxes[i].setVisible(false);
           stateLabels[i].setVisible(false);
       }
    }
}

private JLabel getArrowLabel()
{
    if (arrowLabel == null)
    {
        try
        {
            arrowLabel = new JLabel();
            arrowLabel.setName("ArrowLabel");
            arrowLabel.setText("Arrow:");
        }catch( java.lang.Throwable ivjExc )
        {
            handleException(ivjExc);
        }
    }
    return arrowLabel;
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

public JComboBox getArrowComboBox0() {
    if( arrowComboBox0 == null ){
        arrowComboBox0 = new JComboBox(new String[] {
                "None",
                "Left",
                "Right",
                "Both",
                "Middle"
        });
        arrowComboBox0.setMinimumSize(new Dimension ( 120, 50));
        arrowComboBox0.setPreferredSize(new Dimension( 120, 50));
    }
    return arrowComboBox0;
}

public JComboBox getArrowComboBox1() {
    if( arrowComboBox1 == null ){
        arrowComboBox1 = new JComboBox(new String[] {
                "None",
                "Left",
                "Right",
                "Both",
                "Middle"
        });
        arrowComboBox1.setMinimumSize(new Dimension ( 120, 50));
        arrowComboBox1.setPreferredSize(new Dimension( 120, 50));
    }
    return arrowComboBox1;
}

public JComboBox getArrowComboBox2() {
    if( arrowComboBox2 == null ){
        arrowComboBox2 = new JComboBox(new String[] {
                "None",
                "Left",
                "Right",
                "Both",
                "Middle"
        });
        arrowComboBox2.setMinimumSize(new Dimension ( 120, 50));
        arrowComboBox2.setPreferredSize(new Dimension( 120, 50));
    }
    return arrowComboBox2;
}

public JComboBox getArrowComboBox3() {
    if( arrowComboBox3 == null ){
        arrowComboBox3 = new JComboBox(new String[] {
                "None",
                "Left",
                "Right",
                "Both",
                "Middle"
        });
        arrowComboBox3.setMinimumSize(new Dimension ( 120, 50));
        arrowComboBox3.setPreferredSize(new Dimension( 120, 50));
    }
    return arrowComboBox3;
}

public JComboBox getArrowComboBox4() {
    if( arrowComboBox4 == null ){
        arrowComboBox4 = new JComboBox(new String[] {
                "None",
                "Left",
                "Right",
                "Both",
                "Middle"
        });
        arrowComboBox4.setMinimumSize(new Dimension ( 120, 50));
        arrowComboBox4.setPreferredSize(new Dimension( 120, 50));
    }
    return arrowComboBox4;
}

public JComboBox getArrowComboBox5() {
    if( arrowComboBox5 == null ){
        arrowComboBox5 = new JComboBox(new String[] {
                "None",
                "Left",
                "Right",
                "Both",
                "Middle"
        });
        arrowComboBox5.setMinimumSize(new Dimension ( 120, 50));
        arrowComboBox5.setPreferredSize(new Dimension( 120, 50));
    }
    return arrowComboBox5;
}

public JComboBox getArrowComboBox6() {
    if( arrowComboBox6 == null ){
        arrowComboBox6 = new JComboBox(new String[] {
                "None",
                "Left",
                "Right",
                "Both",
                "Middle"
        });
        arrowComboBox6.setMinimumSize(new Dimension ( 120, 50));
        arrowComboBox6.setPreferredSize(new Dimension( 120, 50));
    }
    return arrowComboBox6;
}

public JComboBox getArrowComboBox7() {
    if( arrowComboBox7 == null ){
        arrowComboBox7 = new JComboBox(new String[] {
                "None",
                "Left",
                "Right",
                "Both",
                "Middle"
        });
        arrowComboBox7.setMinimumSize(new Dimension ( 120, 50));
        arrowComboBox7.setPreferredSize(new Dimension( 120, 50));
    }
    return arrowComboBox7;
}

public JComboBox getArrowComboBox8() {
    if( arrowComboBox8 == null ){
        arrowComboBox8 = new JComboBox(new String[] {
                "None",
                "Left",
                "Right",
                "Both",
                "Middle"
        });
        arrowComboBox8.setMinimumSize(new Dimension ( 120, 50));
        arrowComboBox8.setPreferredSize(new Dimension( 120, 50));
    }
    return arrowComboBox8;
}

public JComboBox getArrowComboBox9() {
    if( arrowComboBox9 == null ){
        arrowComboBox9 = new JComboBox(new String[] {
                "None",
                "Left",
                "Right",
                "Both",
                "Middle"
        });
        arrowComboBox9.setMinimumSize(new Dimension ( 120, 50));
        arrowComboBox9.setPreferredSize(new Dimension( 120, 50));
    }
    return arrowComboBox9;
}

public JComboBox getArrowComboBox10() {
    if( arrowComboBox10 == null ){
        arrowComboBox10 = new JComboBox(new String[] {
                "None",
                "Left",
                "Right",
                "Both",
                "Middle"
        });
        arrowComboBox10.setMinimumSize(new Dimension ( 120, 50));
        arrowComboBox10.setPreferredSize(new Dimension( 120, 50));
    }
    return arrowComboBox10;
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
            if(comboBoxes[i].getSelectedIndex() !=0) {
                
                int arrowNumber = comboBoxes[i].getSelectedIndex();
                if(arrowNumber == 1 || arrowNumber == 2 || arrowNumber == 3) {
                    arrowNumber = arrowNumber +4;
                }else if(arrowNumber == 4) {
                    arrowNumber = 12;
                }
                
                map.put(i, new Integer(arrowNumber));
            }
        }
    }
}
}