package com.cannontech.esub.editor.element;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.editor.PropertyPanel;
import com.cannontech.common.gui.util.ColorLabel;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.esub.element.RectangleElement;

public class RectangleElementEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements ActionListener, ChangeListener {
    
    private RectangleElement rectangleElement;
    private ColorLabel borderColorLabel = null;
    private ColorLabel fillColorLabel = null;
    private JLabel thicknessLabel = null;
    private JLabel opacityLabel = null;
    private JButton rectangleBorderColorButton = null;
    private JButton rectangleFillColorButton = null;
    private JCheckBox rectangleFillCheckBox = null;
    private JSlider thicknessSlider = null;
    private JSlider opacitySlider = null;
    private JColorChooser borderColorChooser;
    private JColorChooser fillColorChooser;
    private PropertyPanel parent;
    private java.awt.Color initialBorderColor;
    private java.awt.Color initialFillColor;
    private float initialThickness;
    private float initialOpacity;

    /**
     * Constructer for RectangleElementEditorPanel
     */
    public RectangleElementEditorPanel( PropertyPanel p) {
        super();
        parent = p;
        initialize();
    }
    
    /**
     * Initializes this LIneElementEditorPanel.
     */
    private void initialize() {
        try {
            setName("RectangleElementEditorPanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(500, 650);
            
            java.awt.GridBagConstraints borderColorLabelConstraint = new java.awt.GridBagConstraints();
            borderColorLabelConstraint.gridx = 0; borderColorLabelConstraint.gridy = 0;
            borderColorLabelConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            borderColorLabelConstraint.insets = new java.awt.Insets(5,5,5,5);
            add(getBorderColorLabel(), borderColorLabelConstraint);
            
            java.awt.GridBagConstraints fillColorLabelConstraint = new java.awt.GridBagConstraints();
            fillColorLabelConstraint.gridx = 0; fillColorLabelConstraint.gridy = 1;
            fillColorLabelConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            fillColorLabelConstraint.insets = new java.awt.Insets(5,5,5,5);
            add(getFillColorLabel(), fillColorLabelConstraint);
            
            java.awt.GridBagConstraints thicknessLabelConstraint = new java.awt.GridBagConstraints();
            thicknessLabelConstraint.gridx = 0; thicknessLabelConstraint.gridy = 2;
            thicknessLabelConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            thicknessLabelConstraint.insets = new java.awt.Insets(5,5,5,5);
            add(getThicknessLabel(), thicknessLabelConstraint);
            
            java.awt.GridBagConstraints opacityLabelConstraint = new java.awt.GridBagConstraints();
            opacityLabelConstraint.gridx = 0; opacityLabelConstraint.gridy = 3;
            opacityLabelConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            opacityLabelConstraint.insets = new java.awt.Insets(5,5,5,5);
            add(getOpacityLabel(), opacityLabelConstraint);
            
            java.awt.GridBagConstraints borderColorButtonConstraint = new java.awt.GridBagConstraints();
            borderColorButtonConstraint.gridx = 1; borderColorButtonConstraint.gridy = 0;
            borderColorButtonConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            borderColorButtonConstraint.insets = new java.awt.Insets(5,5,5,5);
            add(getBorderColorButton(), borderColorButtonConstraint);
            
            java.awt.GridBagConstraints fillColorButtonConstraint = new java.awt.GridBagConstraints();
            fillColorButtonConstraint.gridx = 1; fillColorButtonConstraint.gridy = 1;
            fillColorButtonConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            fillColorButtonConstraint.insets = new java.awt.Insets(5,5,5,5);
            add(getFillColorButton(), fillColorButtonConstraint);
            
            java.awt.GridBagConstraints fillCheckBoxConstraint = new java.awt.GridBagConstraints();
            fillCheckBoxConstraint.gridx = 2; fillCheckBoxConstraint.gridy = 1;
            fillCheckBoxConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            fillCheckBoxConstraint.insets = new java.awt.Insets(5,5,5,5);
            add(getFillCheckBox(), fillCheckBoxConstraint);
            
            java.awt.GridBagConstraints thicnessSliderConstraint = new java.awt.GridBagConstraints();
            thicnessSliderConstraint.gridx = 1; thicnessSliderConstraint.gridy = 2;
            thicnessSliderConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            thicnessSliderConstraint.insets = new java.awt.Insets(5,5,5,5);
            add(getThicknessSlider(), thicnessSliderConstraint);
            
            java.awt.GridBagConstraints opacitySliderConstraint = new java.awt.GridBagConstraints();
            opacitySliderConstraint.gridx = 1; opacitySliderConstraint.gridy = 3;
            opacitySliderConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            opacitySliderConstraint.insets = new java.awt.Insets(5,5,5,5);
            add(getOpacitySlider(), opacitySliderConstraint);
            
            initConnections();
            JFrame parent = (JFrame) SwingUtil.getParentFrame(this);
            if(parent != null) {
                parent.setTitle("Rectangle Element Editor");
            }
            borderColorChooser = new JColorChooser();
            fillColorChooser = new JColorChooser();
            
        } catch (java.lang.Throwable ex) {
            handleException(ex);
        }
    }
    
    /**
     * Initializes connections such as listeners
     * @exception java.lang.Exception The exception description.
     */
    private void initConnections() throws java.lang.Exception {
        getBorderColorButton().addActionListener(this);
        getFillColorButton().addActionListener(this);
        getFillCheckBox().addActionListener(this);
        getThicknessSlider().addChangeListener(this);
        getOpacitySlider().addChangeListener(this);
        parent.getPropertyButtonPanel().getCancelJButton().addActionListener(this);
    }
    
    /**
     * Called whenever initialize() throws an exception without catching it.
     * @param exception java.lang.Throwable
     */
    private void handleException(java.lang.Throwable e) {
        /* Uncomment the following rectangle to print uncaught exceptions to CTILogger */
        CTILogger.error("--------- UNCAUGHT EXCEPTION ---------", e);
        
    }
    
    public JButton getBorderColorButton() {
        if( rectangleBorderColorButton == null ){
            rectangleBorderColorButton = new JButton();
            rectangleBorderColorButton = new JButton("Edit Border Color");
        }
        return rectangleBorderColorButton;
    }
    
    public ColorLabel getBorderColorLabel() {
        if( borderColorLabel == null ){
            borderColorLabel = new ColorLabel();
            borderColorLabel.setSize(new Dimension(40,25));
            borderColorLabel.setPreferredSize(new Dimension(40,25));
        }
        return borderColorLabel;
    }
    
    public JButton getFillColorButton() {
        if( rectangleFillColorButton == null ){
            rectangleFillColorButton = new JButton();
            rectangleFillColorButton = new JButton("Edit Fill Color");
        }
        return rectangleFillColorButton;
    }
    
    public JCheckBox getFillCheckBox() {
        if( rectangleFillCheckBox == null ){
            rectangleFillCheckBox = new JCheckBox("Fill");
            rectangleFillCheckBox.setSelected(false);
        }
        return rectangleFillCheckBox;
    }
    
    public ColorLabel getFillColorLabel() {
        if( fillColorLabel == null ){
            fillColorLabel = new ColorLabel();
            fillColorLabel.setSize(new Dimension(40,25));
            fillColorLabel.setPreferredSize(new Dimension(40,25));
        }
        return fillColorLabel;
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
    
    public JLabel getThicknessLabel() {
        if( thicknessLabel == null ){
            thicknessLabel = new JLabel("Border Thickness:");
        }
        return thicknessLabel;
    }
    
    public void selectionPerformed() {
        getValue(rectangleElement);
    }
    
    @Override
    public Object getValue(Object o) {
        RectangleElement rectangle = (RectangleElement) o;
        rectangle.setIsNew(false);
        if(getFillCheckBox().isSelected()) {
            rectangle.setPaint(fillColorChooser.getColor());
        } else {
            rectangle.setPaint(null);
        }
        rectangle.setLineColor(borderColorChooser.getColor());
        rectangle.setLineThickness(new Float(getThicknessSlider().getValue()).floatValue());
        rectangle.setTransparency(new Float(getOpacitySlider().getValue()).floatValue()*.01f);
        return rectangle;
    }

    @Override
    public void setValue(Object o) {
        rectangleElement = (RectangleElement) o;
        
        //save off initial values in case of a cancel
        initialBorderColor = rectangleElement.getLineColor();
        initialFillColor = (Color)rectangleElement.getPaint();
        initialThickness = rectangleElement.getLineThickness();
        initialOpacity = rectangleElement.getTransparency();
       
        if(!rectangleElement.isNew()){
            borderColorChooser.setColor(rectangleElement.getLineColor());
            Color fillColor = (Color)rectangleElement.getPaint();
            if(fillColor == null) {
                fillColorChooser.setColor(Color.WHITE);
                getFillCheckBox().setSelected(false);
                getFillColorButton().setEnabled(false);
                getFillColorLabel().setEnabled(false);
            } else {
                fillColorChooser.setColor(fillColor);
                getFillCheckBox().setSelected(true);
                getFillColorButton().setEnabled(true);
                getFillColorLabel().setEnabled(true);
            }
            getThicknessSlider().setValue(new Float(rectangleElement.getLineThickness()).intValue());
            float f = rectangleElement.getTransparency() *100f;
            getOpacitySlider().setValue(new Float(f).intValue());
        }else {
            borderColorChooser.setColor(Color.WHITE);
            fillColorChooser.setColor(Color.WHITE);
            getFillCheckBox().setSelected(false);
            getFillColorButton().setEnabled(false);
            getFillColorLabel().setEnabled(false);
            rectangleElement.setLineColor(borderColorChooser.getColor());
            rectangleElement.setLineThickness(new Float(getThicknessSlider().getValue()).floatValue());
            rectangleElement.setTransparency(new Float(getOpacitySlider().getValue()).floatValue() * .01f);
        }
        getBorderColorButton().setBackground(borderColorChooser.getColor());
        getFillColorButton().setBackground(fillColorChooser.getColor());
        getBorderColorLabel().setColor(borderColorChooser.getColor());
        getFillColorLabel().setColor(fillColorChooser.getColor());
    }
    
    public void actionPerformed(ActionEvent e) {
        
        Object source = e.getSource();
        if (source == getBorderColorButton()) {
            javax.swing.JDialog d = javax.swing.JColorChooser.createDialog(this, "Select a color", true, borderColorChooser, 
                new java.awt.event.ActionListener() { //ok listener
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        getBorderColorButton().setBackground(borderColorChooser.getColor());
                        getBorderColorLabel().setColor(borderColorChooser.getColor());
                        getBorderColorLabel().revalidate();
                        getBorderColorLabel().repaint();
                        rectangleElement.setLineColor(borderColorChooser.getColor());
                    }
                },
                new java.awt.event.ActionListener() { //cancel listener
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                    }
                }
            );

            d.setVisible(true);
            d.dispose();
        } else if (source == getFillColorButton()) {
            javax.swing.JDialog d = javax.swing.JColorChooser.createDialog(this, "Select a color", true, fillColorChooser, 
                new java.awt.event.ActionListener() { //ok listener
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        getFillColorButton().setBackground(fillColorChooser.getColor());
                        getFillColorLabel().setColor(fillColorChooser.getColor());
                        getFillColorLabel().revalidate();
                        getFillColorLabel().repaint();
                        rectangleElement.setPaint(fillColorChooser.getColor());
                    }
                },
                new java.awt.event.ActionListener() { //cancel listener
                    public void actionPerformed(java.awt.event.ActionEvent e) {}
                }
            );
            d.setVisible(true);
            d.dispose();
        } else if( source ==  parent.getPropertyButtonPanel().getCancelJButton()) {
            cancelPerformed();
        } else if( source ==  getFillCheckBox()) {
            if(getFillCheckBox().isSelected()) {
                getFillColorLabel().setEnabled(true);
                getFillColorButton().setEnabled(true);
            }else {
                getFillColorLabel().setEnabled(false);
                getFillColorButton().setEnabled(false);
            }
        }
    }
    
    private void cancelPerformed() {
        rectangleElement.setPaint(initialFillColor);
        rectangleElement.setLineColor(initialBorderColor);
        rectangleElement.setLineThickness(initialThickness);
        rectangleElement.setTransparency(initialOpacity);
    }

    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();
        if(source == getOpacitySlider()) {
            rectangleElement.setTransparency(new Float(getOpacitySlider().getValue()).floatValue() * .01f);
        }else if(source == getThicknessSlider()) {
            rectangleElement.setLineThickness(new Float(getThicknessSlider().getValue()).floatValue());
        }
    }
    
}