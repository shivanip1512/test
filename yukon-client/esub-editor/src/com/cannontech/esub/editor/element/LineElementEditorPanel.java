package com.cannontech.esub.editor.element;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.editor.PropertyPanel;
import com.cannontech.common.gui.util.SimpleLabel;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.esub.editor.Util;
import com.cannontech.esub.element.LineElement;

public class LineElementEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements ActionListener, ChangeListener {
    
    private LineElement lineElement;
    private SimpleLabel colorLabel = null;
    private JLabel thicknessLabel = null;
    private JLabel arrowLabel = null;
    private JLabel opacityLabel = null;
    private JLabel blinkLabel = null;
    private JButton lineColorButton = null;
    private JSlider thicknessSlider = null;
    private JComboBox arrowComboBox = null;
    private JSlider opacitySlider = null;
    private JCheckBox blinkCheckBox = null;
    private JCheckBox colorPointCheckBox = null;
    private JCheckBox thicknessPointCheckBox = null;
    private JCheckBox arrowPointCheckBox = null;
    private JCheckBox opacityPointCheckBox = null;
    private JCheckBox blinkPointCheckBox = null;
    private JButton colorButton = null;
    private JButton thicknessPointButton = null;
    private JButton arrowPointButton = null;
    private JButton opacityPointButton = null;
    private JButton blinkPointButton = null;
    private LineColorPointPanel colorPointPanel = null;
    private LineThicknessPointPanel thicknessPointPanel = null;
    private LineArrowPointPanel arrowPointPanel = null;
    private LineOpacityPointPanel opacityPointPanel = null;
    private BlinkPointPanel blinkPointPanel = null;
    private JColorChooser colorChooser;
    private JDialog pointPanelDialog;
    private PropertyPanel parent;
    private java.awt.Color initialColor;
    private int initialColorPointID;
    private float initialThickness;
    private int initialThicknessPointID;
    private int initialArrow;
    private int initialArrowPointID;
    private float initialOpacity;
    private int initialOpacityPointID;
    private int initialBlink;
    private int initialBlinkPointID;

    /**
     * Constructer for LineElementEditorPanel
     */
    public LineElementEditorPanel( PropertyPanel p) {
        super();
        parent = p;
        initialize();
    }
    
    /**
     * Initializes this LIneElementEditorPanel.
     */
    private void initialize() {
        try {
            setName("LineElementEditorPanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(500, 650);
            
            java.awt.GridBagConstraints colorLabelConstraint = new java.awt.GridBagConstraints();
            colorLabelConstraint.gridx = 0; colorLabelConstraint.gridy = 0;
            colorLabelConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            colorLabelConstraint.insets = new java.awt.Insets(5,5,5,5);
            add(getColorLabel(), colorLabelConstraint);
            
            java.awt.GridBagConstraints thicknessLabelConstraint = new java.awt.GridBagConstraints();
            thicknessLabelConstraint.gridx = 0; thicknessLabelConstraint.gridy = 1;
            thicknessLabelConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            thicknessLabelConstraint.insets = new java.awt.Insets(5,5,5,5);
            add(getThicknessLabel(), thicknessLabelConstraint);
            
            java.awt.GridBagConstraints arrowLabelConstraint = new java.awt.GridBagConstraints();
            arrowLabelConstraint.gridx = 0; arrowLabelConstraint.gridy = 2;
            arrowLabelConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            arrowLabelConstraint.insets = new java.awt.Insets(5,5,5,5);
            add(getArrowLabel(), arrowLabelConstraint);
            
            java.awt.GridBagConstraints opacityLabelConstraint = new java.awt.GridBagConstraints();
            opacityLabelConstraint.gridx = 0; opacityLabelConstraint.gridy = 3;
            opacityLabelConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            opacityLabelConstraint.insets = new java.awt.Insets(5,5,5,5);
            add(getOpacityLabel(), opacityLabelConstraint);
            
            java.awt.GridBagConstraints blinkLabelConstraint = new java.awt.GridBagConstraints();
            blinkLabelConstraint.gridx = 0; blinkLabelConstraint.gridy = 4;
            blinkLabelConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            blinkLabelConstraint.insets = new java.awt.Insets(5,5,5,5);
            add(getBlinkLabel(), blinkLabelConstraint);
            
            java.awt.GridBagConstraints colorButtonConstraint = new java.awt.GridBagConstraints();
            colorButtonConstraint.gridx = 1; colorButtonConstraint.gridy = 0;
            colorButtonConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            colorButtonConstraint.insets = new java.awt.Insets(5,5,5,5);
            add(getColorButton(), colorButtonConstraint);
            
            java.awt.GridBagConstraints thicnessSliderConstraint = new java.awt.GridBagConstraints();
            thicnessSliderConstraint.gridx = 1; thicnessSliderConstraint.gridy = 1;
            thicnessSliderConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            thicnessSliderConstraint.insets = new java.awt.Insets(5,5,5,5);
            add(getThicknessSlider(), thicnessSliderConstraint);
            
            java.awt.GridBagConstraints arrowComboBoxConstraint = new java.awt.GridBagConstraints();
            arrowComboBoxConstraint.gridx = 1; arrowComboBoxConstraint.gridy = 2;
            arrowComboBoxConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            arrowComboBoxConstraint.insets = new java.awt.Insets(5,5,5,5);
            add(getArrowComboBox(), arrowComboBoxConstraint);
            
            java.awt.GridBagConstraints opacitySliderConstraint = new java.awt.GridBagConstraints();
            opacitySliderConstraint.gridx = 1; opacitySliderConstraint.gridy = 3;
            opacitySliderConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            opacitySliderConstraint.insets = new java.awt.Insets(5,5,5,5);
            add(getOpacitySlider(), opacitySliderConstraint);
            
            java.awt.GridBagConstraints blinkCheckBoxConstraint = new java.awt.GridBagConstraints();
            blinkCheckBoxConstraint.gridx = 1; blinkCheckBoxConstraint.gridy = 4;
            blinkCheckBoxConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            blinkCheckBoxConstraint.insets = new java.awt.Insets(5,5,5,5);
            add(getBlinkCheckBox(), blinkCheckBoxConstraint);
            
            java.awt.GridBagConstraints colorCheckBoxConstraint = new java.awt.GridBagConstraints();
            colorCheckBoxConstraint.gridx = 2; colorCheckBoxConstraint.gridy = 0;
            colorCheckBoxConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            colorCheckBoxConstraint.insets = new java.awt.Insets(5,5,5,5);
            add(getColorPointCheckBox(), colorCheckBoxConstraint);
            
            java.awt.GridBagConstraints thicknessCheckBoxConstraint = new java.awt.GridBagConstraints();
            thicknessCheckBoxConstraint.gridx = 2; thicknessCheckBoxConstraint.gridy = 1;
            thicknessCheckBoxConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            thicknessCheckBoxConstraint.insets = new java.awt.Insets(5,5,5,5);
            add(getThicknessPointCheckBox(), thicknessCheckBoxConstraint);
            
            java.awt.GridBagConstraints arrowCheckBoxConstraint = new java.awt.GridBagConstraints();
            arrowCheckBoxConstraint.gridx = 2; arrowCheckBoxConstraint.gridy = 2;
            arrowCheckBoxConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            arrowCheckBoxConstraint.insets = new java.awt.Insets(5,5,5,5);
            add(getArrowPointCheckBox(), arrowCheckBoxConstraint);
            
            java.awt.GridBagConstraints opacityCheckBoxConstraint = new java.awt.GridBagConstraints();
            opacityCheckBoxConstraint.gridx = 2; opacityCheckBoxConstraint.gridy = 3;
            opacityCheckBoxConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            opacityCheckBoxConstraint.insets = new java.awt.Insets(5,5,5,5);
            add(getOpacityPointCheckBox(), opacityCheckBoxConstraint);
            
            java.awt.GridBagConstraints blinkPointCheckBoxConstraint = new java.awt.GridBagConstraints();
            blinkPointCheckBoxConstraint.gridx = 2; blinkPointCheckBoxConstraint.gridy = 4;
            blinkPointCheckBoxConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            blinkPointCheckBoxConstraint.insets = new java.awt.Insets(5,5,5,5);
            add(getBlinkPointCheckBox(), blinkPointCheckBoxConstraint);
            
            java.awt.GridBagConstraints colorPointButtonConstraint = new java.awt.GridBagConstraints();
            colorPointButtonConstraint.gridx = 3; colorPointButtonConstraint.gridy = 0;
            colorPointButtonConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            colorPointButtonConstraint.insets = new java.awt.Insets(5,5,5,5);
            add(getColorPointButton(), colorPointButtonConstraint);
            
            java.awt.GridBagConstraints thicknessButtonConstraint = new java.awt.GridBagConstraints();
            thicknessButtonConstraint.gridx = 3; thicknessButtonConstraint.gridy = 1;
            thicknessButtonConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            thicknessButtonConstraint.insets = new java.awt.Insets(5,5,5,5);
            add(getThicknessPointButton(), thicknessButtonConstraint);
            
            java.awt.GridBagConstraints arrowButtonConstraint = new java.awt.GridBagConstraints();
            arrowButtonConstraint.gridx = 3; arrowButtonConstraint.gridy = 2;
            arrowButtonConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            arrowButtonConstraint.insets = new java.awt.Insets(5,5,5,5);
            add(getArrowPointButton(), arrowButtonConstraint);
            
            java.awt.GridBagConstraints opacityButtonConstraint = new java.awt.GridBagConstraints();
            opacityButtonConstraint.gridx = 3; opacityButtonConstraint.gridy = 3;
            opacityButtonConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            opacityButtonConstraint.insets = new java.awt.Insets(5,5,5,5);
            add(getOpacityPointButton(), opacityButtonConstraint);
            
            java.awt.GridBagConstraints blinkButtonConstraint = new java.awt.GridBagConstraints();
            blinkButtonConstraint.gridx = 3; blinkButtonConstraint.gridy = 4;
            blinkButtonConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            blinkButtonConstraint.insets = new java.awt.Insets(5,5,5,5);
            add(getBlinkPointButton(), blinkButtonConstraint);
            
            initConnections();
            JFrame parent = (JFrame) SwingUtil.getParentFrame(this);
            if(parent != null) {
                parent.setTitle("Line Element Editor");
            }
            colorChooser = Util.getJColorChooser();
            
        } catch (java.lang.Throwable ex) {
            handleException(ex);
        }
    }
    
    /**
     * Initializes connections such as listeners
     * @exception java.lang.Exception The exception description.
     */
    private void initConnections() throws java.lang.Exception {
        getColorButton().addActionListener(this);
        getColorPointCheckBox().addActionListener(this);
        getColorPointButton().addActionListener(this);
        
        getThicknessSlider().addChangeListener(this);
        getThicknessPointCheckBox().addActionListener(this);
        getThicknessPointButton().addActionListener(this);
        
        getArrowPointCheckBox().addActionListener(this);
        getArrowPointButton().addActionListener(this);
        getArrowComboBox().addActionListener(this);
        
        getOpacitySlider().addChangeListener(this);
        getOpacityPointCheckBox().addActionListener(this);
        getOpacityPointButton().addActionListener(this);
        
        getBlinkCheckBox().addChangeListener(this);
        getBlinkPointCheckBox().addActionListener(this);
        getBlinkPointButton().addActionListener(this);
        
        getColorPointPanel().getOkButton().addActionListener(this);
        getThicknessPointPanel().getOkButton().addActionListener(this);
        getOpacityPointPanel().getOkButton().addActionListener(this);
        getArrowPointPanel().getOkButton().addActionListener(this);
        getBlinkPointPanel().getOkButton().addActionListener(this);
        parent.getPropertyButtonPanel().getCancelJButton().addActionListener(this);
    }
    
    /**
     * Called whenever initialize() throws an exception without catching it.
     * @param exception java.lang.Throwable
     */
    private void handleException(java.lang.Throwable e) {
        /* Uncomment the following line to print uncaught exceptions to CTILogger */
        CTILogger.error("--------- UNCAUGHT EXCEPTION ---------", e);
        
    }
    
    public JComboBox getArrowComboBox() {
        if( arrowComboBox == null ){
        	arrowComboBox = new JComboBox(new String[] {
                    "None",
                    "Left",
                    "Right",
                    "Both",
                    "Middle"
            });
        	
        }
        return arrowComboBox;
    }

    public JLabel getArrowLabel() {
        if( arrowLabel == null ){
        	arrowLabel = new JLabel("Arrows:");
        	
        }
        return arrowLabel;
    }
    
    public JButton getColorButton() {
        if( lineColorButton == null ){
        	lineColorButton = new JButton();
        	lineColorButton = new JButton("Edit Color");
        }
        return lineColorButton;
    }
    
    public SimpleLabel getColorLabel() {
        if( colorLabel == null ){
        	colorLabel = new SimpleLabel("Color:",  new java.awt.Font("dialog", 0, 12), java.awt.Color.white, java.awt.Color.black );
            colorLabel.setBorder(new EtchedBorder());
            colorLabel.setPreferredSize(new Dimension(40,27));
        	colorLabel.size.width = 40;
            colorLabel.size.height = 20;
        }
        return colorLabel;
    }
    
    public JLabel getOpacityLabel() {
        if( opacityLabel == null ){
        	opacityLabel = new JLabel("Opacity:");
        	
        }
        return opacityLabel;
    }
    
    public JSlider getOpacitySlider() {
        if( opacitySlider == null ){
        	opacitySlider = new JSlider(0,100,100);
        	// Turn on labels at major tick marks.
            opacitySlider.setMajorTickSpacing(20);
            opacitySlider.setMinorTickSpacing(5);
            opacitySlider.setPaintTicks(true);
            opacitySlider.setPaintLabels(true);
        	opacitySlider.setMinimumSize(new Dimension ( 120, 50));
            opacitySlider.setPreferredSize(new Dimension( 120, 50));
        }
        return opacitySlider;
    }

    public JSlider getThicknessSlider() {
        if( thicknessSlider == null ){
            thicknessSlider = new JSlider(0,10,1);
            // Turn on labels at major tick marks.
            thicknessSlider.setSnapToTicks(true);
            thicknessSlider.setMajorTickSpacing(1);
            thicknessSlider.setPaintTicks(true);
            thicknessSlider.setPaintLabels(true);
            thicknessSlider.setMinimumSize(new Dimension ( 120, 50));
            thicknessSlider.setPreferredSize(new Dimension( 120, 50));
        }
        return thicknessSlider;
    }
    
    public JCheckBox getBlinkCheckBox() {
        if( blinkCheckBox == null ){
            blinkCheckBox = new JCheckBox("Line will blink");
        }
        return blinkCheckBox;
    }

    public JLabel getThicknessLabel() {
        if( thicknessLabel == null ){
        	thicknessLabel = new JLabel("Thickness:");
        }
        return thicknessLabel;
    }
    
    public JLabel getBlinkLabel() {
        if( blinkLabel == null ){
            blinkLabel = new JLabel("Blink:");
        }
        return blinkLabel;
    }
    
    public JCheckBox getThicknessPointCheckBox() {
        if( thicknessPointCheckBox == null ){
            thicknessPointCheckBox = new JCheckBox("Point Driven?:");
        }
        return thicknessPointCheckBox;
    }
    
    public JCheckBox getColorPointCheckBox() {
        if( colorPointCheckBox == null ){
            colorPointCheckBox = new JCheckBox("Point Driven?:");
        }
        return colorPointCheckBox;
    }
    
    public JCheckBox getArrowPointCheckBox() {
        if( arrowPointCheckBox == null ){
            arrowPointCheckBox = new JCheckBox("Point Driven?:");
        }
        return arrowPointCheckBox;
    }
    
    public JCheckBox getOpacityPointCheckBox() {
        if( opacityPointCheckBox == null ){
            opacityPointCheckBox = new JCheckBox("Point Driven?:");
        }
        return opacityPointCheckBox;
    }
    
    public JCheckBox getBlinkPointCheckBox() {
        if( blinkPointCheckBox == null ){
            blinkPointCheckBox = new JCheckBox("Point Driven?:");
        }
        return blinkPointCheckBox;
    }
    
    public JButton getColorPointButton() {
        if( colorButton == null ){
            colorButton = new JButton("Point");
            colorButton.setEnabled(false);
        }
        return colorButton;
    }
    
    public JButton getThicknessPointButton() {
        if( thicknessPointButton == null ){
            thicknessPointButton = new JButton("Point");
            thicknessPointButton.setEnabled(false);
        }
        return thicknessPointButton;
    }
    
    public JButton getArrowPointButton() {
        if( arrowPointButton == null ){
            arrowPointButton = new JButton("Point");
            arrowPointButton.setEnabled(false);
        }
        return arrowPointButton;
    }
    
    public JButton getOpacityPointButton() {
        if( opacityPointButton == null ){
            opacityPointButton = new JButton("Point");
            opacityPointButton.setEnabled(false);
        }
        return opacityPointButton;
    }
    
    public JButton getBlinkPointButton() {
        if( blinkPointButton == null ){
            blinkPointButton = new JButton("Point");
            blinkPointButton.setEnabled(false);
        }
        return blinkPointButton;
    }
    
    public void selectionPerformed() {
        getValue(lineElement);
    }
    
    @Override
    public Object getValue(Object o) {
        LineElement line = (LineElement) o;
        line.setIsNew(false);
        line.setPaint(colorChooser.getColor());
        line.setLineColor(colorChooser.getColor());
        line.setColor(colorChooser.getColor());
        
        if(getColorPointCheckBox().isSelected()) {
            if(getColorPointPanel().getPointSelectionPanel().getSelectedPoint() == null){
                CTILogger.error("No color point selected");
                JOptionPane.showMessageDialog(this, "Please select a point for color or uncheck the point driven checkbox.", "Settings not done yet.", JOptionPane.WARNING_MESSAGE);
            }
            line.setColorPointID(getColorPointPanel().getPointSelectionPanel().getSelectedPoint().getLiteID());
            line.setCustomColorMap(getColorPointPanel().getCustomColorMap());
        }else {
            line.setColorPointID(-1);
        }
        line.setLineThickness(new Float(getThicknessSlider().getValue()).floatValue());
        if(getThicknessPointCheckBox().isSelected()) {
            if(getThicknessPointPanel().getPointSelectionPanel().getSelectedPoint() == null){
                CTILogger.error("No thickness point selected");
                JOptionPane.showMessageDialog(this, "Please select a point for thickness or uncheck the point driven checkbox.", "Settings not done yet.", JOptionPane.WARNING_MESSAGE);
            }
            line.setThicknessPointID(getThicknessPointPanel().getPointSelectionPanel().getSelectedPoint().getLiteID());
        }else {
            line.setThicknessPointID(-1);
        }
        int slider = getArrowComboBox().getSelectedIndex();
        if(slider == 1 || slider == 2 || slider == 3) {
            slider = slider +4;
        }else if(slider == 4) {
            slider = 12;
        }
        line.setLineArrow(slider);
        if(getArrowPointCheckBox().isSelected()) {
            if(getArrowPointPanel().getPointSelectionPanel().getSelectedPoint() == null){
                CTILogger.error("No arrow point selected");
                JOptionPane.showMessageDialog(this, "Please select a point for arrows or uncheck the point driven checkbox.", "Settings not done yet.", JOptionPane.WARNING_MESSAGE);
            }
            line.setArrowPointID(getArrowPointPanel().getPointSelectionPanel().getSelectedPoint().getLiteID());
        }else {
            line.setArrowPointID(-1);
        }
        line.setTransparency(new Float(getOpacitySlider().getValue()).floatValue()*.01f);
        if(getOpacityPointCheckBox().isSelected()) {
            if(getOpacityPointPanel().getPointSelectionPanel().getSelectedPoint() == null){
                CTILogger.error("No opacity point selected");
                JOptionPane.showMessageDialog(this, "Please select a point for opacity or uncheck the point driven checkbox.", "Settings not done yet.", JOptionPane.WARNING_MESSAGE);
            }
            line.setOpacityPointID(getOpacityPointPanel().getPointSelectionPanel().getSelectedPoint().getLiteID());
        }else {
            line.setOpacityPointID(-1);
        }
        if( getBlinkCheckBox().isSelected()) {
            line.setLineBlink(1);
        }else {
            line.setLineBlink(0);
        }
        if(getBlinkPointCheckBox().isSelected()) {
            if(getBlinkPointPanel().getPointSelectionPanel().getSelectedPoint() == null){
                CTILogger.error("No opacity point selected");
                JOptionPane.showMessageDialog(this, "Please select a point for blink or uncheck the point driven checkbox.", "Settings not done yet.", JOptionPane.WARNING_MESSAGE);
            }
            line.setBlinkPointID(getBlinkPointPanel().getPointSelectionPanel().getSelectedPoint().getLiteID());
        }else {
            line.setBlinkPointID(-1);
        }
        
        return line;
    }

    @Override
    public void setValue(Object o) {
        
        lineElement = (LineElement) o;
        
        //save off initial values in case of a cancel
        initialColor = lineElement.getLineColor();
        initialColorPointID = lineElement.getColorPointID();
        initialThickness = lineElement.getLineThickness();
        initialThicknessPointID = lineElement.getThicknessPointID();
        initialArrow = lineElement.getLineArrow();
        initialArrowPointID = lineElement.getArrowPointID();
        initialOpacity = lineElement.getTransparency();
        initialOpacityPointID = lineElement.getOpacityPointID();
        initialBlink = lineElement.getLineBlink();
        initialBlinkPointID = lineElement.getBlinkPointID();
        
        if(!lineElement.isNew()){
            colorChooser.setColor(lineElement.getLineColor());
            getColorButton().setBackground(colorChooser.getColor());
            getColorLabel().setBackground(colorChooser.getColor());
            getThicknessSlider().setValue(new Float(lineElement.getLineThickness()).intValue());
            int slider = lineElement.getLineArrow();
            if(slider == 5 || slider == 6 || slider == 7) {
                slider = slider - 4;
            }else if(slider == 12) {
                slider = 4;
            }
            getArrowComboBox().setSelectedIndex(slider);
            
            float f = lineElement.getTransparency() *100f;
            getOpacitySlider().setValue(new Float(f).intValue());
            
            if(lineElement.getLineBlink() == 0) {
                getBlinkCheckBox().setSelected(false);
            }else {
                getBlinkCheckBox().setSelected(true);
            }
            if(lineElement.getColorPointID() < 0) {
                getColorPointCheckBox().setSelected(false);
                getColorPointButton().setEnabled(false);
            }else {
                getColorPointCheckBox().setSelected(true);
                getColorPointButton().setEnabled(true);
                getColorButton().setEnabled(false);
                getColorButton().setBackground(java.awt.Color.LIGHT_GRAY);
                getColorLabel().setBackground(java.awt.Color.LIGHT_GRAY);
                
                LitePoint litePoint = null;
                try {
                    litePoint = DaoFactory.getPointDao().getLitePoint(lineElement.getColorPointID());
                }catch(NotFoundException nfe) {
                    CTILogger.error("The color point (pointId:"+ lineElement.getColorPointID() + ") for this line might have been deleted!", nfe);
                }
                if(litePoint != null) {
                    getColorPointPanel().getPointSelectionPanel().selectPoint(litePoint);
                }
            }
            
            if(lineElement.getThicknessPointID() < 0) {
                getThicknessPointCheckBox().setSelected(false);
                getThicknessPointButton().setEnabled(false);
            }else {
                getThicknessPointCheckBox().setSelected(true);
                getThicknessPointButton().setEnabled(true);
                getThicknessSlider().setEnabled(false);
                
                LitePoint litePoint = null;
                try {
                    litePoint = DaoFactory.getPointDao().getLitePoint(lineElement.getThicknessPointID());
                }catch(NotFoundException nfe) {
                    CTILogger.error("The thickness point (pointId:"+ lineElement.getThicknessPointID() + ") for this line might have been deleted!", nfe);
                }
                if(litePoint != null) {
                    getThicknessPointPanel().getPointSelectionPanel().selectPoint(litePoint);
                }
            }
            
            if(lineElement.getArrowPointID() < 0) {
                getArrowPointCheckBox().setSelected(false); 
                getArrowPointButton().setEnabled(false);
            }else {
                getArrowPointCheckBox().setSelected(true);
                getArrowPointButton().setEnabled(true);
                getArrowComboBox().setEnabled(false);
                
                LitePoint litePoint = null;
                try {
                    litePoint = DaoFactory.getPointDao().getLitePoint(lineElement.getArrowPointID());
                }catch(NotFoundException nfe) {
                    CTILogger.error("The arrow point (pointId:"+ lineElement.getArrowPointID() + ") for this line might have been deleted!", nfe);
                }
                if(litePoint != null) {
                    getArrowPointPanel().getPointSelectionPanel().selectPoint(litePoint);
                }
            }
            
            if(lineElement.getOpacityPointID() < 0) {
                getOpacityPointCheckBox().setSelected(false);
                getOpacityPointButton().setEnabled(false);
            }else {
                getOpacityPointCheckBox().setSelected(true);
                getOpacityPointButton().setEnabled(true);
                getOpacitySlider().setEnabled(false);
                
                LitePoint litePoint = null;
                try {
                    litePoint = DaoFactory.getPointDao().getLitePoint(lineElement.getOpacityPointID());
                }catch(NotFoundException nfe) {
                    CTILogger.error("The opacity point (pointId:"+ lineElement.getOpacityPointID() + ") for this line might have been deleted!", nfe);
                }
                if(litePoint != null) {
                    getOpacityPointPanel().getPointSelectionPanel().selectPoint(litePoint);
                }
            }
            
            if(lineElement.getBlinkPointID() < 0) {
                getBlinkPointCheckBox().setSelected(false);
                getBlinkPointButton().setEnabled(false);
            }else {
                getBlinkPointCheckBox().setSelected(true);
                getBlinkPointButton().setEnabled(true);
                getBlinkCheckBox().setEnabled(false);
                
                LitePoint litePoint = null;
                try {
                    litePoint = DaoFactory.getPointDao().getLitePoint(lineElement.getBlinkPointID());
                }catch(NotFoundException nfe) {
                    CTILogger.error("The blink point (pointId:"+ lineElement.getBlinkPointID() + ") for this line might have been deleted!", nfe);
                }
                if(litePoint != null) {
                    getBlinkPointPanel().getPointSelectionPanel().selectPoint(litePoint);
                }
            }
        }else {
            // this is a newly created line so reset all the point driven components.
            getColorPointCheckBox().setSelected(false);
            getColorLabel().setEnabled(true);
            getColorButton().setEnabled(true);
            getColorPointButton().setEnabled(false);
            getColorButton().setBackground(colorChooser.getColor());
            getColorLabel().setBackground(colorChooser.getColor());
            lineElement.setLineColor(colorChooser.getColor());
            
            getThicknessPointCheckBox().setSelected(false);
            getThicknessSlider().setEnabled(true);
            getThicknessPointButton().setEnabled(false);
            lineElement.setLineThickness(new Float(getThicknessSlider().getValue()).floatValue());
            
            getArrowPointCheckBox().setSelected(false);
            getArrowComboBox().setEnabled(true);
            getArrowPointButton().setEnabled(false);
            int arrow = getArrowComboBox().getSelectedIndex();
            if (arrow == 1 || arrow == 2 || arrow == 3) {
                arrow = arrow + 4;
            }else if (arrow ==4) {
                arrow = 12;
            }
            lineElement.setLineArrow(arrow);
            
            getOpacityPointButton().setEnabled(false);
            getOpacityPointCheckBox().setSelected(false);
            getOpacitySlider().setEnabled(true);
            lineElement.setTransparency(new Float(getOpacitySlider().getValue()).floatValue() * .01f);
            
            getBlinkCheckBox().setEnabled(true);
            getBlinkPointButton().setEnabled(false);
            getBlinkPointCheckBox().setSelected(false);
            if( getBlinkCheckBox().isSelected()) {
                lineElement.setLineBlink(1);
            }else {
                lineElement.setLineBlink(0);
            }
        }
    }
    
    public LineColorPointPanel getColorPointPanel() {
        if (colorPointPanel == null) {
            colorPointPanel = new LineColorPointPanel();
        }
        return colorPointPanel;
    }
    
    public LineThicknessPointPanel getThicknessPointPanel() {
        if (thicknessPointPanel == null) {
            thicknessPointPanel = new LineThicknessPointPanel();
        }
        return thicknessPointPanel;
    }
    
    public LineArrowPointPanel getArrowPointPanel() {
        if (arrowPointPanel == null) {
            arrowPointPanel = new LineArrowPointPanel();
        }
        return arrowPointPanel;
    }
    
    public LineOpacityPointPanel getOpacityPointPanel() {
        if (opacityPointPanel == null) {
            opacityPointPanel = new LineOpacityPointPanel();
        }
        return opacityPointPanel;
    }
    
    public BlinkPointPanel getBlinkPointPanel() {
        if (blinkPointPanel == null) {
            blinkPointPanel = new BlinkPointPanel();
        }
        return blinkPointPanel;
    }

    public void actionPerformed(ActionEvent e) {
        
        Object source = e.getSource();
        
        if (source == getColorButton()) {
            javax.swing.JDialog d = javax.swing.JColorChooser.createDialog(this, "Select a color", true, colorChooser, 
                new java.awt.event.ActionListener() { //ok listener
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        getColorButton().setBackground(colorChooser.getColor());
                        getColorLabel().setBackground(colorChooser.getColor());
                        getColorLabel().revalidate();
                        getColorLabel().repaint();
                        lineElement.setLineColor(colorChooser.getColor());
                    }
                },
                new java.awt.event.ActionListener() { //cancel listener
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                    }
                }
            );

            d.setVisible(true);
            d.dispose();
        }else if( source == getArrowComboBox()) {
            int arrow = getArrowComboBox().getSelectedIndex();
            if (arrow == 1 || arrow == 2 || arrow == 3) {
                arrow = arrow + 4;
            }else if (arrow ==4) {
                arrow = 12;
            }
            lineElement.setLineArrow(arrow);
        }else if( source == getColorPointCheckBox()) {
            if( getColorPointCheckBox().isSelected()) {
                getColorPointButton().setEnabled(true);
                getColorButton().setEnabled(false);
                getColorButton().setBackground(java.awt.Color.LIGHT_GRAY);
                getColorLabel().setBackground(java.awt.Color.LIGHT_GRAY);
                getColorLabel().repaint(getColorLabel().getVisibleRect());
            }else { 
                getColorPointButton().setEnabled(false);
                getColorButton().setEnabled(true);
                getColorButton().setBackground(colorChooser.getColor());
                getColorLabel().setBackground(colorChooser.getColor());
                getColorLabel().repaint(getColorLabel().getVisibleRect());
            }
        }else if( source == getThicknessPointCheckBox()) {
            if( getThicknessPointCheckBox().isSelected()) {
                getThicknessPointButton().setEnabled(true);
                getThicknessSlider().setEnabled(false);
            }else { 
                getThicknessPointButton().setEnabled(false);
                getThicknessSlider().setEnabled(true);
            }
        }else if( source == getArrowPointCheckBox()) {
            if( getArrowPointCheckBox().isSelected()) {
                getArrowPointButton().setEnabled(true);
                getArrowComboBox().setEnabled(false);
            }else {
                getArrowPointButton().setEnabled(false);
                getArrowComboBox().setEnabled(true);
            }
        }else if( source == getOpacityPointCheckBox()) {
            if( getOpacityPointCheckBox().isSelected()) {
                getOpacityPointButton().setEnabled(true);
                getOpacitySlider().setEnabled(false);
            }else {
                getOpacityPointButton().setEnabled(false);
                getOpacitySlider().setEnabled(true);
            }
        }else if( source == getBlinkPointCheckBox()) {
            if( getBlinkPointCheckBox().isSelected()) {
                getBlinkPointButton().setEnabled(true);
                getBlinkCheckBox().setEnabled(false);
            }else {
                getBlinkPointButton().setEnabled(false);
                getBlinkCheckBox().setEnabled(true);
            }
        }else if( source == getColorPointButton()) {
            pointPanelDialog = new JDialog(SwingUtil.getParentFrame(this), true);
            pointPanelDialog.setContentPane(getColorPointPanel());
            pointPanelDialog.setSize(new java.awt.Dimension(540, 630));
            getColorPointPanel().setValue(lineElement);
            pointPanelDialog.setVisible(true);
        }
        else if( source == getThicknessPointButton()) {
            pointPanelDialog = new JDialog(SwingUtil.getParentFrame(this), true);
            pointPanelDialog.setContentPane(getThicknessPointPanel());
            pointPanelDialog.setSize(new java.awt.Dimension(540, 650));
            getThicknessPointPanel().setValue(lineElement);
            pointPanelDialog.setVisible(true);
        }
        else if( source == getArrowPointButton()) {
            pointPanelDialog = new JDialog(SwingUtil.getParentFrame(this), true);
            pointPanelDialog.setContentPane(getArrowPointPanel());
            pointPanelDialog.setSize(new java.awt.Dimension(540, 630));
            getArrowPointPanel().setValue(lineElement);
            pointPanelDialog.setVisible(true);
        }
        else if( source == getOpacityPointButton()) {
            pointPanelDialog = new JDialog(SwingUtil.getParentFrame(this), true);
            pointPanelDialog.setContentPane(getOpacityPointPanel());
            pointPanelDialog.setSize(new java.awt.Dimension(540, 650));
            getOpacityPointPanel().setValue(lineElement);
            pointPanelDialog.setVisible(true);
        }
        else if( source == getBlinkPointButton()) {
            pointPanelDialog = new JDialog(SwingUtil.getParentFrame(this), true);
            pointPanelDialog.setContentPane(getBlinkPointPanel());
            pointPanelDialog.setSize(new java.awt.Dimension(540, 500));
            getBlinkPointPanel().setValue(lineElement);
            pointPanelDialog.setVisible(true);
        }
        else if( source == getColorPointPanel().getOkButton()) {
            getColorPointPanel().getValue(lineElement);
            pointPanelDialog.setVisible(false);
        }else if( source == getThicknessPointPanel().getOkButton()) {
            getThicknessPointPanel().getValue(lineElement);
            pointPanelDialog.setVisible(false);
        }else if( source == getOpacityPointPanel().getOkButton()) {
            getOpacityPointPanel().getValue(lineElement);
            pointPanelDialog.setVisible(false);
        }else if( source == getArrowPointPanel().getOkButton()) {
            getArrowPointPanel().getValue(lineElement);
            pointPanelDialog.setVisible(false);
        }else if( source == getBlinkPointPanel().getOkButton()) {
            getBlinkPointPanel().getValue(lineElement);
            pointPanelDialog.setVisible(false);
        }else if( source ==  parent.getPropertyButtonPanel().getCancelJButton()) {
            cancelPerformed();
        }
    }
    
    private void cancelPerformed() {
        lineElement.setPaint(initialColor);
        lineElement.setLineColor(initialColor);
        lineElement.setColor(initialColor);
        lineElement.setColorPointID(initialColorPointID);
        lineElement.setLineThickness(initialThickness);
        lineElement.setThicknessPointID(initialThicknessPointID);
        lineElement.setLineArrow(initialArrow);
        lineElement.setArrowPointID(initialArrowPointID);
        lineElement.setTransparency(initialOpacity);
        lineElement.setOpacityPointID(initialOpacityPointID);
        lineElement.setLineBlink(initialBlink);
        lineElement.setBlinkPointID(initialBlinkPointID);
    }

    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();
        if(source == getOpacitySlider()) {
            lineElement.setTransparency(new Float(getOpacitySlider().getValue()).floatValue() * .01f);
        }else if(source == getThicknessSlider()) {
            lineElement.setLineThickness(new Float(getThicknessSlider().getValue()).floatValue());
        }
    }
}