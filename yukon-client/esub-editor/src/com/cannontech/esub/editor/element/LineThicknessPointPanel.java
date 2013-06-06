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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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

public class LineThicknessPointPanel extends DataInputPanel implements ActionListener, ChangeListener, TreeSelectionListener {
    private javax.swing.JPanel pointPanel = null;
    private javax.swing.JPanel buttonGroup = null;
    private PointSelectionPanel ivjPointSelectionPanel = null;
    private LineElement lineElement;
    private Map<Integer, Float> map = new HashMap<Integer, Float>();
    private JButton okButton;
    private JLabel rawStateLabel;
    private JLabel thicknessLabel;
    private JPanel previewPanel;
    private JSlider thicknessSlider0;
    private JSlider thicknessSlider1;
    private JSlider thicknessSlider2;
    private JSlider thicknessSlider3;
    private JSlider thicknessSlider4;
    private JSlider thicknessSlider5;
    private JSlider thicknessSlider6;
    private JSlider thicknessSlider7;
    private JSlider thicknessSlider8;
    private JSlider thicknessSlider9;
    private JSlider thicknessSlider10;
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
    private JSlider[] sliders = new JSlider[12];
    private JLabel[] stateLabels = new JLabel[12];
    
/**
 * StateImageEditorPanel constructor comment.
 */
public LineThicknessPointPanel() {
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
            
            previewPanel.setPreferredSize(new Dimension( 180, 580 ));
            previewPanel.setSize(new Dimension( 180, 580 ));
            previewPanel.setMinimumSize(new Dimension( 180, 580 ));
            previewPanel.setBorder(new TitleBorder("Preview"));
            previewPanel.setAlignmentX(javax.swing.SwingConstants.TOP);
            
            getRawStateLabel().setBounds(new Rectangle( 10, 40, 60, 20));
            previewPanel.add(getRawStateLabel());
            
            getThicknessLabel().setBounds(new Rectangle( 75, 40, 60, 20));
            getThicknessLabel().setHorizontalAlignment(SwingConstants.CENTER);
            previewPanel.add(getThicknessLabel());
            
            getRawState0().setBounds(new Rectangle( 10, 65, 90, 30));
            previewPanel.add(getRawState0());
            
            getRawState1().setBounds(new Rectangle( 10, 110, 90, 30));
            previewPanel.add(getRawState1());
            
            getRawState2().setBounds(new Rectangle( 10, 155, 90, 30));
            previewPanel.add(getRawState2());
            
            getRawState3().setBounds(new Rectangle( 10, 200, 90, 30));
            previewPanel.add(getRawState3());
            
            getRawState4().setBounds(new Rectangle( 10, 245, 90, 30));
            previewPanel.add(getRawState4());
            
            getRawState5().setBounds(new Rectangle( 10, 290, 85, 30));
            previewPanel.add(getRawState5());
            
            getRawState6().setBounds(new Rectangle( 10, 335, 90, 30));
            previewPanel.add(getRawState6());
            
            getRawState7().setBounds(new Rectangle( 10, 380, 90, 30));
            previewPanel.add(getRawState7());
            
            getRawState8().setBounds(new Rectangle( 10, 425, 90, 30));
            previewPanel.add(getRawState8());
            
            getRawState9().setBounds(new Rectangle( 10, 470, 90, 30));
            previewPanel.add(getRawState9());
            
            getRawState10().setBounds(new Rectangle( 10, 515, 90, 30));
            previewPanel.add(getRawState10());
            
            getThicknessSlider0().setBounds(new Rectangle( 105, 65, 120, 50));
            previewPanel.add(getThicknessSlider0());
            
            getThicknessSlider1().setBounds(new Rectangle( 105, 110, 120, 50));
            previewPanel.add(getThicknessSlider1());
            
            getThicknessSlider2().setBounds(new Rectangle( 105, 155, 120, 50));
            previewPanel.add(getThicknessSlider2());
            
            getThicknessSlider3().setBounds(new Rectangle( 105, 200, 120, 50));
            previewPanel.add(getThicknessSlider3());
            
            getThicknessSlider4().setBounds(new Rectangle( 105, 245, 120, 50));
            previewPanel.add(getThicknessSlider4());
            
            getThicknessSlider5().setBounds(new Rectangle( 105, 290, 120, 50));
            previewPanel.add(getThicknessSlider5());
            
            getThicknessSlider6().setBounds(new Rectangle( 105, 335, 120, 50));
            previewPanel.add(getThicknessSlider6());
            
            getThicknessSlider7().setBounds(new Rectangle( 105, 380, 120, 50));
            previewPanel.add(getThicknessSlider7());
            
            getThicknessSlider8().setBounds(new Rectangle( 105, 425, 120, 50));
            previewPanel.add(getThicknessSlider8());
            
            getThicknessSlider9().setBounds(new Rectangle( 105, 470, 120, 50));
            previewPanel.add(getThicknessSlider9());
            
            getThicknessSlider10().setBounds(new Rectangle( 105, 515, 120, 50));
            previewPanel.add(getThicknessSlider10());
                    
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
    if (ivjPointSelectionPanel == null) {
        try {
            ivjPointSelectionPanel = new PointSelectionPanel();
            ivjPointSelectionPanel.setName("PointSelectionPanel");
            ivjPointSelectionPanel.setPreferredSize(new Dimension(150,400));
            ivjPointSelectionPanel.setMinimumSize(new Dimension(150,400));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjPointSelectionPanel;
}

public Map<Integer, Float> getCustomThicknessMap() {
    return map;
}

/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) {
    // save slider values to custom map
    for(int i = 0; i < 11; i++) {
        if(sliders[i].getValue() !=1) {
            map.put(i, new Float(sliders[i].getValue()));
        }
    }
    if ( getPointSelectionPanel().getSelectedPoint() == null) {
        JOptionPane.showMessageDialog(this, "Please select a point from the device tree.", "Settings not done yet.", JOptionPane.WARNING_MESSAGE);
    }
    lineElement.setThicknessPointID(getPointSelectionPanel().getSelectedPoint().getLiteID());
    lineElement.setCustomThicknessMap(map);
    return lineElement;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable e) {

    /* Uncomment the following lines to print uncaught exceptions to stdout */
     CTILogger.error("--------- UNCAUGHT EXCEPTION ---------", e);
     
}
/**
 * Initialize the class.
 */
private void initialize() {
    try {
        setName("ThicknessPointPanel");
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

        sliders[0] = getThicknessSlider0(); 
        sliders[1] = getThicknessSlider1();
        sliders[2] = getThicknessSlider2();
        sliders[3] = getThicknessSlider3();
        sliders[4] = getThicknessSlider4();
        sliders[5] = getThicknessSlider5();
        sliders[6] = getThicknessSlider6();
        sliders[7] = getThicknessSlider7();
        sliders[8] = getThicknessSlider8();
        sliders[9] = getThicknessSlider9();
        sliders[10] = getThicknessSlider10();
        
        getThicknessSlider0().addChangeListener(this);
        getThicknessSlider1().addChangeListener(this);
        getThicknessSlider2().addChangeListener(this);
        getThicknessSlider3().addChangeListener(this);
        getThicknessSlider4().addChangeListener(this);
        getThicknessSlider5().addChangeListener(this);
        getThicknessSlider6().addChangeListener(this);
        getThicknessSlider7().addChangeListener(this);
        getThicknessSlider8().addChangeListener(this);
        getThicknessSlider9().addChangeListener(this);
        getThicknessSlider10().addChangeListener(this);

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
    map = new HashMap<Integer, Float>( lineElement.getCustomThicknessMap());

    getPointSelectionPanel().refresh();

    // Set selected point 
    int lpid = lineElement.getThicknessPointID();
    LitePoint litePoint = null;
    try {
        litePoint = YukonSpringHook.getBean(PointDao.class).getLitePoint(lpid);
    }catch(NotFoundException nfe) {
        CTILogger.error("The thickness point (pointId:"+ lineElement.getThicknessPointID() + ") for this line might have been deleted!", nfe);
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
    List<JSlider> values = new ArrayList<JSlider>(12);
    if(p != null)
    {
        LiteStateGroup lsg = YukonSpringHook.getBean(StateDao.class).getLiteStateGroup(p.getStateGroupID());
        List<LiteState> states = lsg.getStatesList();
        
        for(int i = 0; i < states.size(); i++) 
        {
            LiteState state = states.get(i);
            int rawStateNumber = state.getStateRawState();
           
            sliders[rawStateNumber].setValue(1);
            values.add(sliders[rawStateNumber]);
            
            stateLabels[i].setText(state.getStateText());
            
            sliders[i].setVisible(true);
            stateLabels[i].setVisible(true);
        }
        
        for(int i = 0; i < 11; i++)
        {
            
           if(!values.contains(sliders[i]))
           {
               sliders[i].setVisible(false);
               stateLabels[i].setVisible(false);
           }
        }
    }else
    {
        for(int i = 0; i < 11; i++)
        {
            
            sliders[i].setVisible(false);
            stateLabels[i].setVisible(false);
        }
    }
}

public void setPreviewPanelValues(LitePoint p) 
{
    List<JSlider> values = new ArrayList<JSlider>(12);
    LiteStateGroup lsg = YukonSpringHook.getBean(StateDao.class).getLiteStateGroup(p.getStateGroupID());
    List<LiteState> states = lsg.getStatesList();
    
    for(int i = 0; i < states.size(); i++) 
    {
        
        LiteState state = states.get(i);
        int rawStateNumber = state.getStateRawState();
        
        if(!(map.get(rawStateNumber) == null))
        {
            
            sliders[rawStateNumber].setValue(new Float(map.get(rawStateNumber)).intValue());
            
        }else {
       
            sliders[rawStateNumber].setValue(1);
        
        }
        values.add(sliders[rawStateNumber]);
        
        stateLabels[i].setText(state.getStateText());
        sliders[rawStateNumber].setVisible(true);
        stateLabels[rawStateNumber].setVisible(true);
    }
    
    for(int i = 0; i < 11; i++)
    {
       if(!values.contains(sliders[i]))
       {
           sliders[i].setVisible(false);
           stateLabels[i].setVisible(false);
       }
    }
}

private JLabel getThicknessLabel()
{
    if (thicknessLabel == null)
    {
        try
        {
            thicknessLabel = new JLabel();
            thicknessLabel.setName("ThicknessLabel");
            thicknessLabel.setText("Thickness");
        }catch( java.lang.Throwable ivjExc )
        {
            handleException(ivjExc);
        }
    }
    return thicknessLabel;
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
            okButton.setPreferredSize(new Dimension(60, 25));
            okButton.setMinimumSize(new Dimension(60, 25));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return okButton;
}

public JSlider getThicknessSlider0() {
    if( thicknessSlider0 == null ){
        thicknessSlider0 = new JSlider(0,10,1);
        // Turn on labels at major tick marks.
        thicknessSlider0.setSnapToTicks(true);
        thicknessSlider0.setMajorTickSpacing(1);
        thicknessSlider0.setPaintTicks(true);
        thicknessSlider0.setPaintLabels(true);
        thicknessSlider0.setMinimumSize(new Dimension ( 120, 50));
        thicknessSlider0.setPreferredSize(new Dimension( 120, 50));
    }
    return thicknessSlider0;
}

public JSlider getThicknessSlider1() {
    if( thicknessSlider1 == null ){
        thicknessSlider1 = new JSlider(0,10,1);
        // Turn on labels at major tick marks.
        thicknessSlider1.setSnapToTicks(true);
        thicknessSlider1.setMajorTickSpacing(1);
        thicknessSlider1.setPaintTicks(true);
        thicknessSlider1.setPaintLabels(true);
        thicknessSlider1.setMinimumSize(new Dimension ( 120, 50));
        thicknessSlider1.setPreferredSize(new Dimension( 120, 50));
    }
    return thicknessSlider1;
}

public JSlider getThicknessSlider2() {
    if( thicknessSlider2 == null ){
        thicknessSlider2 = new JSlider(0,10,1);
        // Turn on labels at major tick marks.
        thicknessSlider2.setSnapToTicks(true);
        thicknessSlider2.setMajorTickSpacing(1);
        thicknessSlider2.setPaintTicks(true);
        thicknessSlider2.setPaintLabels(true);
        thicknessSlider2.setMinimumSize(new Dimension ( 120, 50));
        thicknessSlider2.setPreferredSize(new Dimension( 120, 50));
    }
    return thicknessSlider2;
}

public JSlider getThicknessSlider3() {
    if( thicknessSlider3 == null ){
        thicknessSlider3 = new JSlider(0,10,1);
        // Turn on labels at major tick marks.
        thicknessSlider3.setSnapToTicks(true);
        thicknessSlider3.setMajorTickSpacing(1);
        thicknessSlider3.setPaintTicks(true);
        thicknessSlider3.setPaintLabels(true);
        thicknessSlider3.setMinimumSize(new Dimension ( 120, 50));
        thicknessSlider3.setPreferredSize(new Dimension( 120, 50));
    }
    return thicknessSlider3;
}

public JSlider getThicknessSlider4() {
    if( thicknessSlider4 == null ){
        thicknessSlider4 = new JSlider(0,10,1);
        // Turn on labels at major tick marks.
        thicknessSlider4.setSnapToTicks(true);
        thicknessSlider4.setMajorTickSpacing(1);
        thicknessSlider4.setPaintTicks(true);
        thicknessSlider4.setPaintLabels(true);
        thicknessSlider4.setMinimumSize(new Dimension ( 120, 50));
        thicknessSlider4.setPreferredSize(new Dimension( 120, 50));
    }
    return thicknessSlider4;
}

public JSlider getThicknessSlider5() {
    if( thicknessSlider5 == null ){
        thicknessSlider5 = new JSlider(0,10,1);
        // Turn on labels at major tick marks.
        thicknessSlider5.setSnapToTicks(true);
        thicknessSlider5.setMajorTickSpacing(1);
        thicknessSlider5.setPaintTicks(true);
        thicknessSlider5.setPaintLabels(true);
        thicknessSlider5.setMinimumSize(new Dimension ( 120, 50));
        thicknessSlider5.setPreferredSize(new Dimension( 120, 50));
    }
    return thicknessSlider5;
}

public JSlider getThicknessSlider6() {
    if( thicknessSlider6 == null ){
        thicknessSlider6 = new JSlider(0,10,1);
        // Turn on labels at major tick marks.
        thicknessSlider6.setSnapToTicks(true);
        thicknessSlider6.setMajorTickSpacing(1);
        thicknessSlider6.setPaintTicks(true);
        thicknessSlider6.setPaintLabels(true);
        thicknessSlider6.setMinimumSize(new Dimension ( 120, 50));
        thicknessSlider6.setPreferredSize(new Dimension( 120, 50));
    }
    return thicknessSlider6;
}

public JSlider getThicknessSlider7() {
    if( thicknessSlider7 == null ){
        thicknessSlider7 = new JSlider(0,10,1);
        // Turn on labels at major tick marks.
        thicknessSlider7.setSnapToTicks(true);
        thicknessSlider7.setMajorTickSpacing(1);
        thicknessSlider7.setPaintTicks(true);
        thicknessSlider7.setPaintLabels(true);
        thicknessSlider7.setMinimumSize(new Dimension ( 120, 50));
        thicknessSlider7.setPreferredSize(new Dimension( 120, 50));
    }
    return thicknessSlider7;
}

public JSlider getThicknessSlider8() {
    if( thicknessSlider8 == null ){
        thicknessSlider8 = new JSlider(0,10,1);
        // Turn on labels at major tick marks.
        thicknessSlider8.setSnapToTicks(true);
        thicknessSlider8.setMajorTickSpacing(1);
        thicknessSlider8.setPaintTicks(true);
        thicknessSlider8.setPaintLabels(true);
        thicknessSlider8.setMinimumSize(new Dimension ( 120, 50));
        thicknessSlider8.setPreferredSize(new Dimension( 120, 50));
    }
    return thicknessSlider8;
}

public JSlider getThicknessSlider9() {
    if( thicknessSlider9 == null ){
        thicknessSlider9 = new JSlider(0,10,1);
        // Turn on labels at major tick marks.
        thicknessSlider9.setSnapToTicks(true);
        thicknessSlider9.setMajorTickSpacing(1);
        thicknessSlider9.setPaintTicks(true);
        thicknessSlider9.setPaintLabels(true);
        thicknessSlider9.setMinimumSize(new Dimension ( 120, 50));
        thicknessSlider9.setPreferredSize(new Dimension( 120, 50));
    }
    return thicknessSlider9;
}

public JSlider getThicknessSlider10() {
    if( thicknessSlider10 == null ){
        thicknessSlider10 = new JSlider(0,10,1);
        // Turn on labels at major tick marks.
        thicknessSlider10.setSnapToTicks(true);
        thicknessSlider10.setMajorTickSpacing(1);
        thicknessSlider10.setPaintTicks(true);
        thicknessSlider10.setPaintLabels(true);
        thicknessSlider10.setMinimumSize(new Dimension ( 120, 50));
        thicknessSlider10.setPreferredSize(new Dimension( 120, 50));
    }
    return thicknessSlider10;
}


private JButton getResetButton()
{
    if (resetButton == null) {
        try {
            resetButton = new JButton();
            resetButton.setName("resetButton");
            resetButton.setText("Reset");
            resetButton.setPreferredSize(new Dimension(80, 25));
            resetButton.setMinimumSize(new Dimension(80, 25));
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
       
    }
}

public void stateChanged(ChangeEvent e) {}
}