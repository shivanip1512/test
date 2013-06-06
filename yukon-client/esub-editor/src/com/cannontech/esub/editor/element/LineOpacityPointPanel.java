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

public class LineOpacityPointPanel extends DataInputPanel implements ActionListener, TreeSelectionListener {
    private javax.swing.JPanel pointPanel = null;
    private javax.swing.JPanel buttonGroup = null;
    private PointSelectionPanel pointSelectionPanel = null;
    private LineElement lineElement;
    private Map<Integer, Float> map = new HashMap<Integer, Float>();
    private JButton okButton;
    private JLabel rawStateLabel;
    private JLabel opacityLabel;
    private JPanel previewPanel;
    private JSlider opacitySlider0;
    private JSlider opacitySlider1;
    private JSlider opacitySlider2;
    private JSlider opacitySlider3;
    private JSlider opacitySlider4;
    private JSlider opacitySlider5;
    private JSlider opacitySlider6;
    private JSlider opacitySlider7;
    private JSlider opacitySlider8;
    private JSlider opacitySlider9;
    private JSlider opacitySlider10;
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
public LineOpacityPointPanel() {
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
            
            getOpacityLabel().setBounds(new Rectangle( 75, 40, 60, 20));
            getOpacityLabel().setHorizontalAlignment(SwingConstants.CENTER);
            previewPanel.add(getOpacityLabel());
            
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
            
            getOpacitySlider0().setBounds(new Rectangle( 105, 65, 120, 50));
            previewPanel.add(getOpacitySlider0());
            
            getOpacitySlider1().setBounds(new Rectangle( 105, 110, 120, 50));
            previewPanel.add(getOpacitySlider1());
            
            getOpacitySlider2().setBounds(new Rectangle( 105, 155, 120, 50));
            previewPanel.add(getOpacitySlider2());
            
            getOpacitySlider3().setBounds(new Rectangle( 105, 200, 120, 50));
            previewPanel.add(getOpacitySlider3());
            
            getOpacitySlider4().setBounds(new Rectangle( 105, 245, 120, 50));
            previewPanel.add(getOpacitySlider4());
            
            getOpacitySlider5().setBounds(new Rectangle( 105, 290, 120, 50));
            previewPanel.add(getOpacitySlider5());
            
            getOpacitySlider6().setBounds(new Rectangle( 105, 335, 120, 50));
            previewPanel.add(getOpacitySlider6());
            
            getOpacitySlider7().setBounds(new Rectangle( 105, 380, 120, 50));
            previewPanel.add(getOpacitySlider7());
            
            getOpacitySlider8().setBounds(new Rectangle( 105, 425, 120, 50));
            previewPanel.add(getOpacitySlider8());
            
            getOpacitySlider9().setBounds(new Rectangle( 105, 470, 120, 50));
            previewPanel.add(getOpacitySlider9());
            
            getOpacitySlider10().setBounds(new Rectangle( 105, 515, 120, 50));
            previewPanel.add(getOpacitySlider10());
                    
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

public Map<Integer, Float> getCustomOpacityMap() {
    return map;
}

/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) {
    
    
//  save slider values to custom map
    for(int i = 0; i < 11; i++) {
        if(sliders[i].getValue() !=100) {
            float f = sliders[i].getValue() * .01f;
            map.put(i, new Float(f));
        }
    }
    if ( getPointSelectionPanel().getSelectedPoint() == null) {
        JOptionPane.showMessageDialog(this, "Please select a point from the device tree.", "Settings not done yet.", JOptionPane.WARNING_MESSAGE);
    }
    lineElement.setOpacityPointID(getPointSelectionPanel().getSelectedPoint().getLiteID());
    lineElement.setCustomOpacityMap(map);
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
        setName("OpacityPointPanel");
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

        sliders[0] = getOpacitySlider0(); 
        sliders[1] = getOpacitySlider1();
        sliders[2] = getOpacitySlider2();
        sliders[3] = getOpacitySlider3();
        sliders[4] = getOpacitySlider4();
        sliders[5] = getOpacitySlider5();
        sliders[6] = getOpacitySlider6();
        sliders[7] = getOpacitySlider7();
        sliders[8] = getOpacitySlider8();
        sliders[9] = getOpacitySlider9();
        sliders[10] = getOpacitySlider10();

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
    map = new HashMap<Integer, Float>( lineElement.getCustomOpacityMap());

    getPointSelectionPanel().refresh();

    // Set selected point 
    int lpid = lineElement.getOpacityPointID();
    LitePoint litePoint = null;
    try {
        litePoint = YukonSpringHook.getBean(PointDao.class).getLitePoint(lpid);
    }catch(NotFoundException nfe) {
        CTILogger.error("The opacity point (pointId:"+ lpid + ") for this line might have been deleted!", nfe);
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
           
            sliders[rawStateNumber].setValue(100);
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
            float f = new Float(map.get(rawStateNumber)) * 100;
            sliders[rawStateNumber].setValue(new Float(f).intValue());
            
        }else {
       
            sliders[rawStateNumber].setValue(100);
        
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

private JLabel getOpacityLabel()
{
    if (opacityLabel == null)
    {
        try
        {
            opacityLabel = new JLabel();
            opacityLabel.setName("OpacityLabel");
            opacityLabel.setText("Opacity:");
        }catch( java.lang.Throwable ivjExc )
        {
            handleException(ivjExc);
        }
    }
    return opacityLabel;
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

public JSlider getOpacitySlider0() {
    if( opacitySlider0 == null ){
        opacitySlider0 = new JSlider(0,100,100);
        // Turn on labels at major tick marks.
        opacitySlider0.setMajorTickSpacing(20);
        opacitySlider0.setMinorTickSpacing(5);
        opacitySlider0.setPaintTicks(true);
        opacitySlider0.setPaintLabels(true);
        opacitySlider0.setMinimumSize(new Dimension ( 120, 50));
        opacitySlider0.setPreferredSize(new Dimension( 120, 50));
    }
    return opacitySlider0;
}

public JSlider getOpacitySlider1() {
    if( opacitySlider1 == null ){
        opacitySlider1 = new JSlider(0,100,100);
        // Turn on labels at major tick marks.
        opacitySlider1.setMajorTickSpacing(20);
        opacitySlider1.setMinorTickSpacing(5);
        opacitySlider1.setPaintTicks(true);
        opacitySlider1.setPaintLabels(true);
        opacitySlider1.setMinimumSize(new Dimension ( 120, 50));
        opacitySlider1.setPreferredSize(new Dimension( 120, 50));
    }
    return opacitySlider1;
}

public JSlider getOpacitySlider2() {
    if( opacitySlider2 == null ){
        opacitySlider2 = new JSlider(0,100,100);
        // Turn on labels at major tick marks.
        opacitySlider2.setMajorTickSpacing(20);
        opacitySlider2.setMinorTickSpacing(5);
        opacitySlider2.setPaintTicks(true);
        opacitySlider2.setPaintLabels(true);
        opacitySlider2.setMinimumSize(new Dimension ( 120, 50));
        opacitySlider2.setPreferredSize(new Dimension( 120, 50));
    }
    return opacitySlider2;
}

public JSlider getOpacitySlider3() {
    if( opacitySlider3 == null ){
        opacitySlider3 = new JSlider(0,100,100);
        // Turn on labels at major tick marks.
        opacitySlider3.setMajorTickSpacing(20);
        opacitySlider3.setMinorTickSpacing(5);
        opacitySlider3.setPaintTicks(true);
        opacitySlider3.setPaintLabels(true);
        opacitySlider3.setMinimumSize(new Dimension ( 120, 50));
        opacitySlider3.setPreferredSize(new Dimension( 120, 50));
    }
    return opacitySlider3;
}

public JSlider getOpacitySlider4() {
    if( opacitySlider4 == null ){
        opacitySlider4 = new JSlider(0,100,100);
        // Turn on labels at major tick marks.
        opacitySlider4.setMajorTickSpacing(20);
        opacitySlider4.setMinorTickSpacing(5);
        opacitySlider4.setPaintTicks(true);
        opacitySlider4.setPaintLabels(true);
        opacitySlider4.setMinimumSize(new Dimension ( 120, 50));
        opacitySlider4.setPreferredSize(new Dimension( 120, 50));
    }
    return opacitySlider4;
}

public JSlider getOpacitySlider5() {
    if( opacitySlider5 == null ){
        opacitySlider5 = new JSlider(0,100,100);
        // Turn on labels at major tick marks.
        opacitySlider5.setMajorTickSpacing(20);
        opacitySlider5.setMinorTickSpacing(5);
        opacitySlider5.setPaintTicks(true);
        opacitySlider5.setPaintLabels(true);
        opacitySlider5.setMinimumSize(new Dimension ( 120, 50));
        opacitySlider5.setPreferredSize(new Dimension( 120, 50));
    }
    return opacitySlider5;
}

public JSlider getOpacitySlider6() {
    if( opacitySlider6 == null ){
        opacitySlider6 = new JSlider(0,100,100);
        // Turn on labels at major tick marks.
        opacitySlider6.setMajorTickSpacing(20);
        opacitySlider6.setMinorTickSpacing(5);
        opacitySlider6.setPaintTicks(true);
        opacitySlider6.setPaintLabels(true);
        opacitySlider6.setMinimumSize(new Dimension ( 120, 50));
        opacitySlider6.setPreferredSize(new Dimension( 120, 50));
    }
    return opacitySlider6;
}

public JSlider getOpacitySlider7() {
    if( opacitySlider7 == null ){
        opacitySlider7 = new JSlider(0,100,100);
        // Turn on labels at major tick marks.
        opacitySlider7.setMajorTickSpacing(20);
        opacitySlider7.setMinorTickSpacing(5);
        opacitySlider7.setPaintTicks(true);
        opacitySlider7.setPaintLabels(true);
        opacitySlider7.setMinimumSize(new Dimension ( 120, 50));
        opacitySlider7.setPreferredSize(new Dimension( 120, 50));
    }
    return opacitySlider7;
}

public JSlider getOpacitySlider8() {
    if( opacitySlider8 == null ){
        opacitySlider8 = new JSlider(0,100,100);
        // Turn on labels at major tick marks.
        opacitySlider8.setMajorTickSpacing(20);
        opacitySlider8.setMinorTickSpacing(5);
        opacitySlider8.setPaintTicks(true);
        opacitySlider8.setPaintLabels(true);
        opacitySlider8.setMinimumSize(new Dimension ( 120, 50));
        opacitySlider8.setPreferredSize(new Dimension( 120, 50));
    }
    return opacitySlider8;
}

public JSlider getOpacitySlider9() {
    if( opacitySlider9 == null ){
        opacitySlider9 = new JSlider(0,100,100);
        // Turn on labels at major tick marks.
        opacitySlider9.setMajorTickSpacing(20);
        opacitySlider9.setMinorTickSpacing(5);
        opacitySlider9.setPaintTicks(true);
        opacitySlider9.setPaintLabels(true);
        opacitySlider9.setMinimumSize(new Dimension ( 120, 50));
        opacitySlider9.setPreferredSize(new Dimension( 120, 50));
    }
    return opacitySlider9;
}

public JSlider getOpacitySlider10() {
    if( opacitySlider10 == null ){
        opacitySlider10 = new JSlider(0,100,100);
        // Turn on labels at major tick marks.
        opacitySlider10.setMajorTickSpacing(20);
        opacitySlider10.setMinorTickSpacing(5);
        opacitySlider10.setPaintTicks(true);
        opacitySlider10.setPaintLabels(true);
        opacitySlider10.setMinimumSize(new Dimension ( 120, 50));
        opacitySlider10.setPreferredSize(new Dimension( 120, 50));
    }
    return opacitySlider10;
}

private JButton getResetButton()
{
    if (resetButton == null) {
        try {
            resetButton = new JButton();
            resetButton.setName("resetButton");
            resetButton.setText("Reset");
            resetButton.setPreferredSize(new Dimension(60, 30));

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
}