package com.cannontech.esub.editor.element;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
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
import com.cannontech.esub.editor.Util;
import com.cannontech.esub.element.LineElement;
import com.cannontech.spring.YukonSpringHook;

public class LineColorPointPanel extends DataInputPanel implements ActionListener, TreeSelectionListener {
    private javax.swing.JPanel pointPanel = null;
    private PointSelectionPanel pointSelectionPanel = null;
    private javax.swing.JPanel buttonGroup = null;
    private LineElement lineElement;
    private Map<Integer, Color> map = new HashMap<Integer, Color>();
    private JButton okButton;
    
    private JLabel rawStateLabel;
    private JLabel colorLabel;
    private JPanel previewPanel;
    private JButton editButton0;
    private JButton editButton1;
    private JButton editButton2;
    private JButton editButton3;
    private JButton editButton4;
    private JButton editButton5;
    private JButton editButton6;
    private JButton editButton7;
    private JButton editButton8;
    private JButton editButton9;
    private JButton editButton10;
    private JColorChooser colorChooser;
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
    private JLabel colorLabel0;
    private JLabel colorLabel1;
    private JLabel colorLabel2;
    private JLabel colorLabel3;
    private JLabel colorLabel4;
    private JLabel colorLabel5;
    private JLabel colorLabel6;
    private JLabel colorLabel7;
    private JLabel colorLabel8;
    private JLabel colorLabel9;
    private JLabel colorLabel10;
    private JLabel[] colorLabels = new JLabel[12];
    private JButton[] buttons = new JButton[12];
    private JLabel[] stateLabels = new JLabel[12];
    private HashMap<Integer, Color> oldColorMap;
    
/**
 * StateImageEditorPanel constructor comment.
 */
public LineColorPointPanel() {
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
            getPointPanel().add(getPointSelectionPanel(), constraintsPointSelectionPanel);

        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return pointPanel;
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

            getRawStateLabel().setBounds(new Rectangle( 10, 50, 60, 20));
            previewPanel.add(getRawStateLabel());
            
            getColorLabel().setBounds(new Rectangle( 75, 50, 60, 20));
            getColorLabel().setHorizontalAlignment(SwingConstants.CENTER);
            previewPanel.add(getColorLabel());
            
            getColorLabel0().setBounds(new Rectangle( 105, 75, 40, 20));
            previewPanel.add(getColorLabel0());
            
            getColorLabel1().setBounds(new Rectangle( 105, 110, 40, 20));
            previewPanel.add(getColorLabel1());
            
            getColorLabel2().setBounds(new Rectangle( 105, 145, 40, 20));
            previewPanel.add(getColorLabel2());
            
            getColorLabel3().setBounds(new Rectangle( 105, 180, 40, 20));
            previewPanel.add(getColorLabel3());
            
            getColorLabel4().setBounds(new Rectangle( 105, 215, 40, 20));
            previewPanel.add(getColorLabel4());
            
            getColorLabel5().setBounds(new Rectangle( 105, 250, 40, 20));
            previewPanel.add(getColorLabel5());
            
            getColorLabel6().setBounds(new Rectangle( 105, 285, 40, 20));
            previewPanel.add(getColorLabel6());
            
            getColorLabel7().setBounds(new Rectangle( 105, 320, 40, 20));
            previewPanel.add(getColorLabel7());
            
            getColorLabel8().setBounds(new Rectangle( 105, 355, 40, 20));
            previewPanel.add(getColorLabel8());
            
            getColorLabel9().setBounds(new Rectangle( 105, 390, 40, 20));
            previewPanel.add(getColorLabel9());
            
            getColorLabel10().setBounds(new Rectangle( 105, 425, 40, 20));
            previewPanel.add(getColorLabel10());
            
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
            
            getEditButton0().setBounds(new Rectangle( 150, 75, 50, 25));
            previewPanel.add(getEditButton0());
            
            getEditButton1().setBounds(new Rectangle( 150, 110, 50, 25));
            previewPanel.add(getEditButton1());
            
            getEditButton2().setBounds(new Rectangle( 150, 145, 50, 25));
            previewPanel.add(getEditButton2());
            
            getEditButton3().setBounds(new Rectangle( 150, 180, 50, 25));
            previewPanel.add(getEditButton3());
            
            getEditButton4().setBounds(new Rectangle( 150, 215, 50, 25));
            previewPanel.add(getEditButton4());
            
            getEditButton5().setBounds(new Rectangle( 150, 250, 50, 25));
            previewPanel.add(getEditButton5());
            
            getEditButton6().setBounds(new Rectangle( 150, 285, 50, 25));
            previewPanel.add(getEditButton6());
            
            getEditButton7().setBounds(new Rectangle( 150, 320, 50, 25));
            previewPanel.add(getEditButton7());
            
            getEditButton8().setBounds(new Rectangle( 150, 355, 50, 25));
            previewPanel.add(getEditButton8());
            
            getEditButton9().setBounds(new Rectangle( 150, 390, 50, 25));
            previewPanel.add(getEditButton9());
            
            getEditButton10().setBounds(new Rectangle( 150, 425, 50, 25));
            previewPanel.add(getEditButton10());
            
            getResetButton().setBounds(new Rectangle( 60, 460, 80, 25));
            previewPanel.add(getResetButton());
                    
        } catch (java.lang.Throwable ivjExc) 
        {
            handleException(ivjExc);
        }
    }
    return previewPanel;
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

public Map<Integer, Color> getCustomColorMap() {
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
    lineElement.setColorPointID(getPointSelectionPanel().getSelectedPoint().getLiteID());
    lineElement.setCustomColorMap(map);
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
        setName("ColorPointPanel");
        setLayout(new java.awt.GridBagLayout());

        java.awt.GridBagConstraints constraintsPointPanel = new java.awt.GridBagConstraints();
        constraintsPointPanel.gridx = 0; constraintsPointPanel.gridy = 0;
        constraintsPointPanel.gridwidth = 1;
        constraintsPointPanel.fill = java.awt.GridBagConstraints.BOTH;
        constraintsPointPanel.anchor = java.awt.GridBagConstraints.NORTH;
        constraintsPointPanel.insets = new java.awt.Insets(4, 4, 0, 4);
        constraintsPointPanel.weightx = .5;
        add(getPointPanel(), constraintsPointPanel);

        java.awt.GridBagConstraints constraintsPreviewPanel = new java.awt.GridBagConstraints();
        constraintsPreviewPanel.gridx = 1; constraintsPreviewPanel.gridy = 0;
        constraintsPreviewPanel.gridwidth = 1;
        constraintsPreviewPanel.fill = java.awt.GridBagConstraints.BOTH;
        constraintsPreviewPanel.weightx = .5;
        constraintsPreviewPanel.insets = new java.awt.Insets(4, 4, 0, 4);
        add(getPreviewPanel(), constraintsPreviewPanel);
        
        java.awt.GridBagConstraints constraintsButtonGroup = new java.awt.GridBagConstraints();
        constraintsButtonGroup.gridx = 1; constraintsButtonGroup.gridy = 1;
        constraintsButtonGroup.gridwidth = 2;
        constraintsButtonGroup.anchor = java.awt.GridBagConstraints.EAST;
        constraintsButtonGroup.insets = new java.awt.Insets(0, 4, 4, 50);
        add(getButtonGroup(), constraintsButtonGroup);
        
        colorLabels[0] = getColorLabel0(); 
        colorLabels[1] = getColorLabel1(); 
        colorLabels[2] = getColorLabel2(); 
        colorLabels[3] = getColorLabel3(); 
        colorLabels[4] = getColorLabel4(); 
        colorLabels[5] = getColorLabel5(); 
        colorLabels[6] = getColorLabel6(); 
        colorLabels[7] = getColorLabel7(); 
        colorLabels[8] = getColorLabel8(); 
        colorLabels[9] = getColorLabel9(); 
        colorLabels[10] = getColorLabel10();

        buttons[0] = getEditButton0(); 
        buttons[1] = getEditButton1();
        buttons[2] = getEditButton2();
        buttons[3] = getEditButton3();
        buttons[4] = getEditButton4();
        buttons[5] = getEditButton5();
        buttons[6] = getEditButton6();
        buttons[7] = getEditButton7();
        buttons[8] = getEditButton8();
        buttons[9] = getEditButton9();
        buttons[10] = getEditButton10();
        
        getEditButton0().addActionListener(this);
        getEditButton1().addActionListener(this);
        getEditButton2().addActionListener(this);
        getEditButton3().addActionListener(this);
        getEditButton4().addActionListener(this);
        getEditButton5().addActionListener(this);
        getEditButton6().addActionListener(this);
        getEditButton7().addActionListener(this);
        getEditButton8().addActionListener(this);
        getEditButton9().addActionListener(this);
        getEditButton10().addActionListener(this);

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
         
        colorChooser = Util.getJColorChooser();
        
        oldColorMap = new HashMap<Integer, Color>(11);
        oldColorMap.put(0, java.awt.Color.green);
        oldColorMap.put(1, java.awt.Color.red);
        oldColorMap.put(2, java.awt.Color.white);
        oldColorMap.put(3, java.awt.Color.yellow);
        oldColorMap.put(4, java.awt.Color.blue);
        oldColorMap.put(5, java.awt.Color.cyan);
        oldColorMap.put(6, java.awt.Color.black);
        oldColorMap.put(7, java.awt.Color.orange);
        oldColorMap.put(8, java.awt.Color.magenta);
        oldColorMap.put(9, java.awt.Color.gray);
        oldColorMap.put(10, java.awt.Color.pink);
         
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
    if(!lineElement.isNew()) {
        map = new HashMap<Integer, Color>( lineElement.getCustomColorMap());
        getPointSelectionPanel().refresh();
    
        // Set selected point 
        int lpid = lineElement.getColorPointID();
        
        LitePoint litePoint = null;
        try {
            litePoint = YukonSpringHook.getBean(PointDao.class).getLitePoint(lpid);
        }catch(NotFoundException nfe) {
            CTILogger.error("The color point (pointId:"+ lineElement.getColorPointID() + ") for this line might have been deleted!", nfe);
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
        setPreviewPanelColors(p);
    }
    fireInputUpdate();
}

public void resetPreviewPanelColors(LitePoint p)
{
    List<JLabel> colors = new ArrayList<JLabel>(12);
    if(p != null)
    {
        LiteStateGroup lsg = YukonSpringHook.getBean(StateDao.class).getLiteStateGroup(p.getStateGroupID());
        List<LiteState> states = lsg.getStatesList();
        
        for(int i = 0; i < states.size(); i++) 
        {
            int color = states.get(i).getFgColor();
            LiteState state = states.get(i);
            int rawStateNumber = state.getStateRawState();
           
            colorLabels[rawStateNumber].setForeground(oldColorMap.get(color));
            colorLabels[rawStateNumber].setBackground(oldColorMap.get(color));
            colors.add(colorLabels[rawStateNumber]);
            
            stateLabels[i].setText(state.getStateText());
            
            buttons[i].setVisible(true);
            colorLabels[i].setVisible(true);
            stateLabels[i].setVisible(true);
        }
        
        for(int i = 0; i < 11; i++)
        {
            
           if(!colors.contains(colorLabels[i]))
           {
               buttons[i].setVisible(false);
               colorLabels[i].setVisible(false);
               stateLabels[i].setVisible(false);
           }
        }
    }else
    {
        for(int i = 0; i < 11; i++)
        {
            
            buttons[i].setVisible(false);
            colorLabels[i].setForeground(new Color(0));
            colorLabels[i].setVisible(false);
            stateLabels[i].setVisible(false);
        }
    }
}

public void setPreviewPanelColors(LitePoint p) 
{
    List<JLabel> colors = new ArrayList<JLabel>(12);
    LiteStateGroup lsg = YukonSpringHook.getBean(StateDao.class).getLiteStateGroup(p.getStateGroupID());
    List<LiteState> states = lsg.getStatesList();
    
    for(int i = 0; i < states.size(); i++) 
    {
        
        int color = states.get(i).getFgColor();
        LiteState state = states.get(i);
        int rawStateNumber = state.getStateRawState();
        
        if(!(map.get(rawStateNumber) == null))
        {
            
            colorLabels[rawStateNumber].setForeground(map.get(rawStateNumber));
            colorLabels[rawStateNumber].setBackground(map.get(rawStateNumber));
        }else {
       
            colorLabels[rawStateNumber].setForeground(oldColorMap.get(color));
            colorLabels[rawStateNumber].setBackground(oldColorMap.get(color));
        }
        colors.add(colorLabels[rawStateNumber]);
        
        stateLabels[i].setText(state.getStateText());
        
        buttons[rawStateNumber].setVisible(true);
        colorLabels[rawStateNumber].setVisible(true);
        stateLabels[rawStateNumber].setVisible(true);

    }
    
    for(int i = 0; i < 11; i++)
    {
       if(!colors.contains(colorLabels[i]))
       {
           buttons[i].setVisible(false);
           colorLabels[i].setVisible(false);
           stateLabels[i].setVisible(false);
       }
    }
}

private JLabel getColorLabel()
{
    if (colorLabel == null)
    {
        try
        {
            colorLabel = new JLabel();
            colorLabel.setName("ImageLabel");
            colorLabel.setText("Color");
        }catch( java.lang.Throwable ivjExc )
        {
            handleException(ivjExc);
        }
    }
    return colorLabel;
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

private JButton getEditButton0()
{
    if (editButton0 == null) {
        try {
            editButton0 = new JButton();
            editButton0.setName("EditButton0");
            editButton0.setText("Edit");
            editButton0.setHorizontalTextPosition(SwingConstants.CENTER);
            editButton0.setVerticalTextPosition(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return editButton0;
}

private JButton getEditButton1()
{
    if (editButton1 == null) {
        try {
            editButton1 = new JButton();
            editButton1.setName("EditButton1");
            editButton1.setText("Edit");
            editButton1.setHorizontalTextPosition(SwingConstants.CENTER);
            editButton1.setVerticalTextPosition(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return editButton1;
}

private JButton getEditButton2()
{
    if (editButton2 == null) {
        try {
            editButton2 = new JButton();
            editButton2.setName("EditButton2");
            editButton2.setText("Edit");
            editButton2.setHorizontalTextPosition(SwingConstants.CENTER);
            editButton2.setVerticalTextPosition(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return editButton2;
}

private JButton getEditButton3()
{
    if (editButton3 == null) {
        try {
            editButton3 = new JButton();
            editButton3.setName("EditButton3");
            editButton3.setText("Edit");
            editButton3.setHorizontalTextPosition(SwingConstants.CENTER);
            editButton3.setVerticalTextPosition(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return editButton3;
}

private JButton getEditButton4()
{
    if (editButton4 == null) {
        try {
            editButton4 = new JButton();
            editButton4.setName("EditButton0");
            editButton4.setText("Edit");
            editButton4.setHorizontalTextPosition(SwingConstants.CENTER);
            editButton4.setVerticalTextPosition(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return editButton4;
}

private JButton getEditButton5()
{
    if (editButton5 == null) {
        try {
            editButton5 = new JButton();
            editButton5.setName("EditButton5");
            editButton5.setText("Edit");
            editButton5.setHorizontalTextPosition(SwingConstants.CENTER);
            editButton5.setVerticalTextPosition(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return editButton5;
}

private JButton getEditButton6()
{
    if (editButton6 == null) {
        try {
            editButton6 = new JButton();
            editButton6.setName("EditButton6");
            editButton6.setText("Edit");
            editButton6.setHorizontalTextPosition(SwingConstants.CENTER);
            editButton6.setVerticalTextPosition(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return editButton6;
}

private JButton getEditButton7()
{
    if (editButton7 == null) {
        try {
            editButton7 = new JButton();
            editButton7.setName("EditButton7");
            editButton7.setText("Edit");
            editButton7.setHorizontalTextPosition(SwingConstants.CENTER);
            editButton7.setVerticalTextPosition(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return editButton7;
}

private JButton getEditButton8()
{
    if (editButton8 == null) {
        try {
            editButton8 = new JButton();
            editButton8.setName("EditButton8");
            editButton8.setText("Edit");
            editButton8.setHorizontalTextPosition(SwingConstants.CENTER);
            editButton8.setVerticalTextPosition(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return editButton8;
}

private JButton getEditButton9(){
    if (editButton9 == null) {
        try {
            editButton9 = new JButton();
            editButton9.setName("EditButton9");
            editButton9.setText("Edit");
            editButton9.setHorizontalTextPosition(SwingConstants.CENTER);
            editButton9.setVerticalTextPosition(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return editButton9;
}

private JButton getEditButton10(){
    if (editButton10 == null) {
        try {
            editButton10 = new JButton();
            editButton10.setName("EditButton10");
            editButton10.setText("Edit");
            editButton10.setHorizontalTextPosition(SwingConstants.CENTER);
            editButton10.setVerticalTextPosition(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return editButton10;
}

private JLabel getColorLabel0()
{
    if (colorLabel0== null) {
        try {
            colorLabel0 = new JLabel();
            colorLabel0.setName("ImageLabel0");
            colorLabel0.setHorizontalAlignment(SwingConstants.CENTER);
            colorLabel0.setMinimumSize(new Dimension(10,10));
            colorLabel0.setBorder(new EtchedBorder());
            colorLabel0.setText("____");
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return colorLabel0;
}

private JLabel getColorLabel1()
{
    if (colorLabel1== null) {
        try {
            colorLabel1 = new JLabel();
            colorLabel1.setName("ImageLabel1");
            colorLabel1.setHorizontalAlignment(SwingConstants.CENTER);
            colorLabel1.setBorder(new EtchedBorder());
            colorLabel1.setText("____");
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return colorLabel1;
}

private JLabel getColorLabel2()
{
    if (colorLabel2== null) {
        try {
            colorLabel2 = new JLabel();
            colorLabel2.setName("ImageLabel2");
            colorLabel2.setBorder(new EtchedBorder());
            colorLabel2.setText("____");
            colorLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return colorLabel2;
}

private JLabel getColorLabel3()
{
    if (colorLabel3== null) {
        try {
            colorLabel3 = new JLabel();
            colorLabel3.setName("ImageLabel3");
            colorLabel3.setBorder(new EtchedBorder());
            colorLabel3.setText("____");
            colorLabel3.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return colorLabel3;
}

private JLabel getColorLabel4()
{
    if (colorLabel4== null) {
        try {
            colorLabel4 = new JLabel();
            colorLabel4.setName("ImageLabel4");
            colorLabel4.setBorder(new EtchedBorder());
            colorLabel4.setText("____");
            colorLabel4.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return colorLabel4;
}

private JLabel getColorLabel5()
{
    if (colorLabel5== null) {
        try {
            colorLabel5 = new JLabel();
            colorLabel5.setName("ImageLabel5");
            colorLabel5.setBorder(new EtchedBorder());
            colorLabel5.setText("____");
            colorLabel5.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return colorLabel5;
}

private JLabel getColorLabel6()
{
    if (colorLabel6== null) {
        try {
            colorLabel6 = new JLabel();
            colorLabel6.setName("ImageLabel6");
            colorLabel6.setBorder(new EtchedBorder());
            colorLabel6.setText("____");
            colorLabel6.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return colorLabel6;
}

private JLabel getColorLabel7()
{
    if (colorLabel7== null) {
        try {
            colorLabel7 = new JLabel();
            colorLabel7.setName("ImageLabel7");
            colorLabel7.setBorder(new EtchedBorder());
            colorLabel7.setText("____");
            colorLabel7.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return colorLabel7;
}

private JLabel getColorLabel8()
{
    if (colorLabel8== null) {
        try {
            colorLabel8 = new JLabel();
            colorLabel8.setName("ImageLabel8");
            colorLabel8.setBorder(new EtchedBorder());
            colorLabel8.setText("____");
            colorLabel8.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return colorLabel8;
}

private JLabel getColorLabel9()
{
    if (colorLabel9== null) {
        try {
            colorLabel9 = new JLabel();
            colorLabel9.setName("ImageLabel9");
            colorLabel9.setBorder(new EtchedBorder());
            colorLabel9.setText("____");
            colorLabel9.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return colorLabel9;
}

private JLabel getColorLabel10()
{
    if (colorLabel10== null) {
        try {
            colorLabel10 = new JLabel();
            colorLabel10.setName("ImageLabel10");
            colorLabel10.setBorder(new EtchedBorder());
            colorLabel10.setText("____");
            colorLabel10.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return colorLabel10;
}

private JButton getResetButton()
{
    if (resetButton == null) {
        try {
            resetButton = new JButton();
            resetButton.setName("resetButton");
            resetButton.setText("Reset");
            resetButton.setPreferredSize(new Dimension(80, 30));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return resetButton;
}

public JColorChooser getColorChooser() {
    return colorChooser;
}

public JLabel[] getColorLabels() {
    return colorLabels;
}

public void actionPerformed(ActionEvent e) 
{
    Object source = e.getSource();
    if(source == getResetButton())
    {
        map.clear();
        LitePoint p = getPointSelectionPanel().getSelectedPoint();
        resetPreviewPanelColors(p);
    }else if (source == getEditButton0()) {
        javax.swing.JDialog d = javax.swing.JColorChooser.createDialog(this, "Select a color", true, colorChooser, 
           new java.awt.event.ActionListener() { //ok listener
            public void actionPerformed(java.awt.event.ActionEvent e) {
                   
                   getColorLabel0().setForeground(colorChooser.getColor());
                   getColorLabel0().setBackground(colorChooser.getColor());
                   map.put(0, colorChooser.getColor());
               }
           },
           new java.awt.event.ActionListener() { //cancel listener
               public void actionPerformed(java.awt.event.ActionEvent e) {
               }
           }
       );

       d.setVisible(true);
       d.dispose();
    }else if (source == getEditButton1()) {
        javax.swing.JDialog d = javax.swing.JColorChooser.createDialog(this, "Select a color", true, colorChooser, 
            new java.awt.event.ActionListener() { //ok listener
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    getColorLabel1().setForeground(colorChooser.getColor());
                    getColorLabel1().setBackground(colorChooser.getColor());
                    map.put(1, colorChooser.getColor());
                }
            },
            new java.awt.event.ActionListener() { //cancel listener
                public void actionPerformed(java.awt.event.ActionEvent e) {
                }
            }
        );
        d.setVisible(true);
        d.dispose();
    }else if (source == getEditButton2()) {
        javax.swing.JDialog d = javax.swing.JColorChooser.createDialog(this, "Select a color", true, colorChooser, 
           new java.awt.event.ActionListener() { //ok listener
            public void actionPerformed(java.awt.event.ActionEvent e) {
                   getColorLabel2().setForeground(colorChooser.getColor());
                   getColorLabel2().setBackground(colorChooser.getColor());
                   map.put(2, colorChooser.getColor());
               }
           },
           new java.awt.event.ActionListener() { //cancel listener
               public void actionPerformed(java.awt.event.ActionEvent e) {
               }
           }
       );
       d.setVisible(true);
       d.dispose();
   }else if (source == getEditButton3()) {
       javax.swing.JDialog d = javax.swing.JColorChooser.createDialog(this, "Select a color", true, colorChooser, 
          new java.awt.event.ActionListener() { //ok listener
            public void actionPerformed(java.awt.event.ActionEvent e) {
                  getColorLabel3().setForeground(colorChooser.getColor());
                  getColorLabel3().setBackground(colorChooser.getColor());
                  map.put(3, colorChooser.getColor());
              }
          },
          new java.awt.event.ActionListener() { //cancel listener
              public void actionPerformed(java.awt.event.ActionEvent e) {
              }
          }
      );

      d.setVisible(true);
      d.dispose();
   }else if (source == getEditButton4()) {
       javax.swing.JDialog d = javax.swing.JColorChooser.createDialog(this, "Select a color", true, colorChooser, 
          new java.awt.event.ActionListener() { //ok listener
              public void actionPerformed(java.awt.event.ActionEvent e) {
                  getColorLabel4().setForeground(colorChooser.getColor());
                  getColorLabel4().setBackground(colorChooser.getColor());
                  map.put(4, colorChooser.getColor());
              }
          },
          new java.awt.event.ActionListener() { //cancel listener
              public void actionPerformed(java.awt.event.ActionEvent e) {
              }
          }
      );

      d.setVisible(true);
      d.dispose();
   }else if (source == getEditButton5()) {
       javax.swing.JDialog d = javax.swing.JColorChooser.createDialog(this, "Select a color", true, colorChooser, 
          new java.awt.event.ActionListener() { //ok listener
              public void actionPerformed(java.awt.event.ActionEvent e) {
                  getColorLabel5().setForeground(colorChooser.getColor());
                  getColorLabel5().setBackground(colorChooser.getColor());
                  map.put(5, colorChooser.getColor());
              }
          },
          new java.awt.event.ActionListener() { //cancel listener
              public void actionPerformed(java.awt.event.ActionEvent e) {
              }
          }
      );

      d.setVisible(true);
      d.dispose();
   }else if (source == getEditButton6()) {
       javax.swing.JDialog d = javax.swing.JColorChooser.createDialog(this, "Select a color", true, colorChooser, 
          new java.awt.event.ActionListener() { //ok listener
              public void actionPerformed(java.awt.event.ActionEvent e) {
                  getColorLabel6().setForeground(colorChooser.getColor());
                  getColorLabel6().setBackground(colorChooser.getColor());
                  map.put(6, colorChooser.getColor());
              }
          },
          new java.awt.event.ActionListener() { //cancel listener
              public void actionPerformed(java.awt.event.ActionEvent e) {
              }
          }
      );

      d.setVisible(true);
      d.dispose();
   }else if (source == getEditButton7()) {
       javax.swing.JDialog d = javax.swing.JColorChooser.createDialog(this, "Select a color", true, colorChooser, 
          new java.awt.event.ActionListener() { //ok listener
              public void actionPerformed(java.awt.event.ActionEvent e) {
                  getColorLabel7().setForeground(colorChooser.getColor());
                  getColorLabel7().setBackground(colorChooser.getColor());
                  map.put(7, colorChooser.getColor());
              }
          },
          new java.awt.event.ActionListener() { //cancel listener
              public void actionPerformed(java.awt.event.ActionEvent e) {
              }
          }
      );

      d.setVisible(true);
      d.dispose();
   }else if (source == getEditButton8()) {
       javax.swing.JDialog d = javax.swing.JColorChooser.createDialog(this, "Select a color", true, colorChooser, 
          new java.awt.event.ActionListener() { //ok listener
              public void actionPerformed(java.awt.event.ActionEvent e) {
                  getColorLabel8().setForeground(colorChooser.getColor());
                  getColorLabel8().setBackground(colorChooser.getColor());
                  map.put(8, colorChooser.getColor());
              }
          },
          new java.awt.event.ActionListener() { //cancel listener
              public void actionPerformed(java.awt.event.ActionEvent e) {
              }
          }
      );

      d.setVisible(true);
      d.dispose();
   }else if (source == getEditButton9()) {
       javax.swing.JDialog d = javax.swing.JColorChooser.createDialog(this, "Select a color", true, colorChooser, 
          new java.awt.event.ActionListener() { //ok listener
              public void actionPerformed(java.awt.event.ActionEvent e) {
                  getColorLabel9().setForeground(colorChooser.getColor());
                  getColorLabel9().setBackground(colorChooser.getColor());
                  map.put(9, colorChooser.getColor());
              }
          },
          new java.awt.event.ActionListener() { //cancel listener
              public void actionPerformed(java.awt.event.ActionEvent e) {
              }
          }
      );

      d.setVisible(true);
      d.dispose();
   }else if (source == getEditButton10()) {
       javax.swing.JDialog d = javax.swing.JColorChooser.createDialog(this, "Select a color", true, colorChooser, 
          new java.awt.event.ActionListener() { //ok listener
              public void actionPerformed(java.awt.event.ActionEvent e) {
                  getColorLabel10().setForeground(colorChooser.getColor());
                  getColorLabel10().setBackground(colorChooser.getColor());
                  map.put(10, colorChooser.getColor());
              }
          },
          new java.awt.event.ActionListener() { //cancel listener
              public void actionPerformed(java.awt.event.ActionEvent e) {
              }
          }
      );

      d.setVisible(true);
      d.dispose();
   }
}
}