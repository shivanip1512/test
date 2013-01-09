package com.cannontech.esub.editor.element;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.esub.PointAttributes;
import com.cannontech.esub.editor.Util;
import com.cannontech.esub.element.DynamicText;

/**
 * Creation date: (12/18/2001 2:05:01 PM)
 * @author: 
 */
public class DynamicTextEditorPanel extends DataInputPanel implements TreeSelectionListener, ActionListener {
	
	private static final String ATTRIBUTE_CURRENT_VALUE = "Current Value";
	private static final String ATTRIBUTE_CURRENT_VALUE_AND_UNIT_OF_MEASURE = "Current Value / Unit of Measure";
	private static final String ATTRIBUTE_POINT_NAME = "Point Name";
	private static final String ATTRIBUTE_DEVICE_NAME = "Device Name";
	private static final String ATTRIBUTE_LAST_UPDATE = "Last Update";
	private static final String ATTRIBUTE_LOW_LIMIT = "Low Limit";
	private static final String ATTRIBUTE_HIGH_LIMIT = "High Limit";
	private static final String ATTRIBUTE_LIMIT_DURATION = "Limit Duration";
	private static final String ATTRIBUTE_MULTIPLIER = "Multiplier";
	private static final String ATTRIBUTE_DATA_OFFSET = "Data Offset";
	private static final String ATTRIBUTE_ALARM_TEXT = "Alarm Text";
	private static final String ATTRIBUTE_CURRENT_STATE = "Current State";	
			
	private DynamicText dynamicText;
	private JColorChooser colorChooser;
	private PointSelectionPropertyPanel pointSelectionPropertyPanel = null;
    private PointSelectionPropertyPanel enableControlPointPanel = null;
    private BlinkPointPanel blinkPointPanel = null;
	private JButton ivjColorButton = null;
	private JLabel ivjColorLabel = null;
	private JComboBox ivjFontComboBox = null;
	private JLabel ivjFontLabel = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private LinkToPanel ivjLinkToPanel = null;
	private JComboBox ivjFontSizeComboBox = null;
	private JLabel ivjFontSizeLabel = null;
	private static final int[] availableFontSizes = {
		6,8,9,10,11,12,14,18,24,36,48,60,72,84,96
	};
	private JComboBox ivjDisplayAttributesComboBox = null;
	private JLabel ivjDisplayAttributesLabel = null;
    private JCheckBox controlCheckBox;
    private JCheckBox colorPointCheckBox;
    private JCheckBox blinkCheckBox;
    private JCheckBox blinkPointCheckBox;
    private JButton colorPointButton;
    private JButton textPointButton;
    private JButton enableControlPointButton;
    private JButton blinkPointButton;
    private JDialog pointPanelDialog;
    private TextColorPointPanel textColorPointPanel = null;
    private TextPointPanel textPointPanel = null;
    private JLabel colorPointLabel;
    private JLabel currentStatePointLabel;
    private JLabel enableControlPointLabel;
    private JLabel enableControlLabel;
    private JLabel blinkLabel;
    private JLabel blinkPointLabel;

/**
 * DynamicTextInputPanel constructor comment.
 */
public DynamicTextEditorPanel() {
	super();
	initialize();
}

private javax.swing.JButton getBlinkPointButton() {
    if (blinkPointButton == null) {
        try {
            blinkPointButton = new javax.swing.JButton();
            blinkPointButton.setName("BlinkPointButton");
            blinkPointButton.setText("Point");
            blinkPointButton.setEnabled(false);
            blinkPointButton.setMaximumSize(new java.awt.Dimension(65, 22));
            blinkPointButton.setPreferredSize(new java.awt.Dimension(65, 22));
            blinkPointButton.setMinimumSize(new java.awt.Dimension(65, 22));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return blinkPointButton;
}

/**
 * Return the ColorButton property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getColorButton() {
	if (ivjColorButton == null) {
		try {
			ivjColorButton = new javax.swing.JButton();
			ivjColorButton.setName("ColorButton");
			ivjColorButton.setBorder(new javax.swing.border.LineBorder(java.awt.Color.black));
			ivjColorButton.setText("Color Picker");
			ivjColorButton.setMaximumSize(new java.awt.Dimension(65, 22));
			ivjColorButton.setPreferredSize(new java.awt.Dimension(65, 22));
			ivjColorButton.setMinimumSize(new java.awt.Dimension(65, 22));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjColorButton;
}

private javax.swing.JCheckBox getColorPointCheckBox() {
    if (colorPointCheckBox == null) {
        try {
            colorPointCheckBox = new javax.swing.JCheckBox();
            colorPointCheckBox.setName("ColorPointCheckBox");
            colorPointCheckBox.setText("Point Driven:");
            colorPointCheckBox.setMaximumSize(new java.awt.Dimension(90, 22));
            colorPointCheckBox.setPreferredSize(new java.awt.Dimension(90, 22));
            colorPointCheckBox.setMinimumSize(new java.awt.Dimension(90, 22));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return colorPointCheckBox;
}

private javax.swing.JCheckBox getBlinkPointCheckBox() {
    if (blinkPointCheckBox == null) {
        try {
            blinkPointCheckBox = new javax.swing.JCheckBox();
            blinkPointCheckBox.setName("BlinkPointCheckBox");
            blinkPointCheckBox.setText("Point Driven:");
            blinkPointCheckBox.setMaximumSize(new java.awt.Dimension(90, 22));
            blinkPointCheckBox.setPreferredSize(new java.awt.Dimension(90, 22));
            blinkPointCheckBox.setMinimumSize(new java.awt.Dimension(90, 22));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return blinkPointCheckBox;
}

private javax.swing.JCheckBox getBlinkCheckBox() {
    if (blinkCheckBox == null) {
        try {
            blinkCheckBox = new javax.swing.JCheckBox();
            blinkCheckBox.setName("BlinkCheckBox");
            blinkCheckBox.setText("Blink");
            blinkCheckBox.setMaximumSize(new java.awt.Dimension(65, 22));
            blinkCheckBox.setPreferredSize(new java.awt.Dimension(65, 22));
            blinkCheckBox.setMinimumSize(new java.awt.Dimension(65, 22));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return blinkCheckBox;
}

/**
 * Return the ColorButton property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getColorPointButton() {
    if (colorPointButton == null) {
        try {
            colorPointButton = new javax.swing.JButton();
            colorPointButton.setName("ColorPointButton");
            colorPointButton.setText("Point");
            colorPointButton.setEnabled(false);
            colorPointButton.setMaximumSize(new java.awt.Dimension(65, 22));
            colorPointButton.setPreferredSize(new java.awt.Dimension(65, 22));
            colorPointButton.setMinimumSize(new java.awt.Dimension(65, 22));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return colorPointButton;
}

/**
 * Return the ColorButton property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getEnableControlPointButton() {
    if (enableControlPointButton == null) {
        try {
            enableControlPointButton = new javax.swing.JButton();
            enableControlPointButton.setName("EnableControlPointButton");
            enableControlPointButton.setText("Point");
            enableControlPointButton.setEnabled(false);
            enableControlPointButton.setMaximumSize(new java.awt.Dimension(65, 22));
            enableControlPointButton.setPreferredSize(new java.awt.Dimension(65, 22));
            enableControlPointButton.setMinimumSize(new java.awt.Dimension(65, 22));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return enableControlPointButton;
}

/**
 * Return the ColorButton property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getTextPointButton() {
    if (textPointButton == null) {
        try {
            textPointButton = new javax.swing.JButton();
            textPointButton.setName("ColorPointButton");
            textPointButton.setText("Select a Point");
            textPointButton.setForeground(java.awt.Color.RED);
            textPointButton.setMaximumSize(new java.awt.Dimension(130, 22));
            textPointButton.setPreferredSize(new java.awt.Dimension(130, 22));
            textPointButton.setMinimumSize(new java.awt.Dimension(130, 22));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return textPointButton;
}

/**
 * Return the ColorLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getColorLabel() {
	if (ivjColorLabel == null) {
		try {
			ivjColorLabel = new javax.swing.JLabel();
			ivjColorLabel.setName("ColorLabel");
			ivjColorLabel.setText("Color:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjColorLabel;
}
/**
 * Return the DisplayAttributesComboBox property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getDisplayAttributesComboBox() {
	if (ivjDisplayAttributesComboBox == null) {
		try {
			ivjDisplayAttributesComboBox = new javax.swing.JComboBox();
			ivjDisplayAttributesComboBox.setName("DisplayAttributesComboBox");
			ivjDisplayAttributesComboBox.setToolTipText("The attributes of the selected point that will be displayed");
			ivjDisplayAttributesComboBox.addItem(ATTRIBUTE_CURRENT_VALUE);
			ivjDisplayAttributesComboBox.addItem(ATTRIBUTE_CURRENT_VALUE_AND_UNIT_OF_MEASURE);
			ivjDisplayAttributesComboBox.addItem(ATTRIBUTE_CURRENT_STATE);
			ivjDisplayAttributesComboBox.addItem(ATTRIBUTE_POINT_NAME);
			ivjDisplayAttributesComboBox.addItem(ATTRIBUTE_DEVICE_NAME);
			ivjDisplayAttributesComboBox.addItem(ATTRIBUTE_LAST_UPDATE);
			ivjDisplayAttributesComboBox.addItem(ATTRIBUTE_LOW_LIMIT);
			ivjDisplayAttributesComboBox.addItem(ATTRIBUTE_HIGH_LIMIT);
			ivjDisplayAttributesComboBox.addItem(ATTRIBUTE_LIMIT_DURATION);
			ivjDisplayAttributesComboBox.addItem(ATTRIBUTE_MULTIPLIER);
			ivjDisplayAttributesComboBox.addItem(ATTRIBUTE_DATA_OFFSET);
			ivjDisplayAttributesComboBox.addItem(ATTRIBUTE_ALARM_TEXT);
            ivjDisplayAttributesComboBox.setPreferredSize(new Dimension(170, 22));
            ivjDisplayAttributesComboBox.setMinimumSize(new Dimension(170, 22));
            ivjDisplayAttributesComboBox.setMaximumSize(new Dimension(170, 22));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDisplayAttributesComboBox;
}

/**
 * Return the DisplayAttributesLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDisplayAttributesLabel() {
	if (ivjDisplayAttributesLabel == null) {
		try {
			ivjDisplayAttributesLabel = new javax.swing.JLabel();
			ivjDisplayAttributesLabel.setName("DisplayAttributesLabel");
			ivjDisplayAttributesLabel.setToolTipText("The attributes of the selected point that will be displayed");
			ivjDisplayAttributesLabel.setText("Attributes:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDisplayAttributesLabel;
}
/**
 * Return the FontComboBox property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getFontComboBox() {
	if (ivjFontComboBox == null) {
		try {
			ivjFontComboBox = new javax.swing.JComboBox();
			ivjFontComboBox.setName("FontComboBox");
            ivjFontComboBox.setPreferredSize(new Dimension( 170, 22));
            ivjFontComboBox.setMinimumSize(new Dimension( 170, 22));
            ivjFontComboBox.setMaximumSize(new Dimension( 170, 22));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFontComboBox;
}
/**
 * Return the FontLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getFontLabel() {
	if (ivjFontLabel == null) {
		try {
			ivjFontLabel = new javax.swing.JLabel();
			ivjFontLabel.setName("FontLabel");
			ivjFontLabel.setText("Font:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFontLabel;
}

private javax.swing.JLabel getColorPointLabel() {
    if (colorPointLabel == null) {
        try {
            colorPointLabel = new javax.swing.JLabel();
            colorPointLabel.setName("ColorPointLabel");
            colorPointLabel.setText("None Chosen");
            colorPointLabel.setForeground(java.awt.Color.RED);
            colorPointLabel.setEnabled(false);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return colorPointLabel;
}

private javax.swing.JLabel getBlinkPointLabel() {
    if (blinkPointLabel == null) {
        try {
            blinkPointLabel = new javax.swing.JLabel();
            blinkPointLabel.setName("BlinkPointLabel");
            blinkPointLabel.setText("None Chosen");
            blinkPointLabel.setForeground(java.awt.Color.RED);
            blinkPointLabel.setEnabled(false);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return blinkPointLabel;
}

private javax.swing.JLabel getEnableControlPointLabel() {
    if (enableControlPointLabel == null) {
        try {
            enableControlPointLabel = new javax.swing.JLabel();
            enableControlPointLabel.setName("EnableControlPointLabel");
            enableControlPointLabel.setText("None Chosen");
            enableControlPointLabel.setForeground(java.awt.Color.RED);
            enableControlPointLabel.setEnabled(false);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return enableControlPointLabel;
}

private javax.swing.JLabel getEnableControlLabel() {
    if (enableControlLabel == null) {
        try {
            enableControlLabel = new javax.swing.JLabel();
            enableControlLabel.setName("EnableControlLabel");
            enableControlLabel.setText("Control:");
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return enableControlLabel;
}

private javax.swing.JLabel getBlinkLabel() {
    if (blinkLabel == null) {
        try {
            blinkLabel = new javax.swing.JLabel();
            blinkLabel.setName("BlinkLabel");
            blinkLabel.setText("Blink:");
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return blinkLabel;
}

/**
 * Return the FontSizeComboBox property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getFontSizeComboBox() {
	if (ivjFontSizeComboBox == null) {
		try {
			ivjFontSizeComboBox = new javax.swing.JComboBox();
			ivjFontSizeComboBox.setName("FontSizeComboBox");
            ivjFontSizeComboBox.setPreferredSize(new Dimension( 60, 25));
            ivjFontSizeComboBox.setMinimumSize(new Dimension( 60, 25));
            ivjFontSizeComboBox.setMaximumSize(new Dimension( 60, 25));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjFontSizeComboBox;
}
/**
 * Return the FontSizeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getFontSizeLabel() {
	if (ivjFontSizeLabel == null) {
		try {
			ivjFontSizeLabel = new javax.swing.JLabel();
			ivjFontSizeLabel.setName("FontSizeLabel");
			ivjFontSizeLabel.setText("Size:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFontSizeLabel;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setPreferredSize(new java.awt.Dimension(405, 220));
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsFontLabel = new java.awt.GridBagConstraints();
			constraintsFontLabel.gridx = 0; constraintsFontLabel.gridy = 0;
			constraintsFontLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsFontLabel.insets = new java.awt.Insets(0, 0, 4, 0);
			getJPanel1().add(getFontLabel(), constraintsFontLabel);

			java.awt.GridBagConstraints constraintsFontComboBox = new java.awt.GridBagConstraints();
			constraintsFontComboBox.gridx = 1; constraintsFontComboBox.gridy = 0;
			constraintsFontComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsFontComboBox.weightx = 1.0;
            constraintsFontComboBox.gridwidth = 2;
			constraintsFontComboBox.insets = new java.awt.Insets(0, 0, 4, 0);
			getJPanel1().add(getFontComboBox(), constraintsFontComboBox);
            
            java.awt.GridBagConstraints constraintsFontSizeLabel = new java.awt.GridBagConstraints();
            constraintsFontSizeLabel.gridx = 3; constraintsFontSizeLabel.gridy = 0;
            constraintsFontSizeLabel.anchor = java.awt.GridBagConstraints.EAST;
            constraintsFontSizeLabel.insets = new java.awt.Insets(0, 0, 4, 0);
            getJPanel1().add(getFontSizeLabel(), constraintsFontSizeLabel);

            java.awt.GridBagConstraints constraintsFontSizeComboBox = new java.awt.GridBagConstraints();
            constraintsFontSizeComboBox.gridx = 4; constraintsFontSizeComboBox.gridy = 0;
            constraintsFontSizeComboBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsFontSizeComboBox.weightx = 1.0;
            constraintsFontSizeComboBox.gridwidth = 2;
            constraintsFontSizeComboBox.insets = new java.awt.Insets(0, 4, 4, 0);
            getJPanel1().add(getFontSizeComboBox(), constraintsFontSizeComboBox);

			java.awt.GridBagConstraints constraintsColorLabel = new java.awt.GridBagConstraints();
			constraintsColorLabel.gridx = 0; constraintsColorLabel.gridy = 1;
			constraintsColorLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsColorLabel.insets = new java.awt.Insets(0, 0, 4, 0);
			getJPanel1().add(getColorLabel(), constraintsColorLabel);

			java.awt.GridBagConstraints constraintsColorButton = new java.awt.GridBagConstraints();
			constraintsColorButton.gridx = 1; constraintsColorButton.gridy = 1;
			constraintsColorButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsColorButton.insets = new java.awt.Insets(0, 0, 4, 0);
			getJPanel1().add(getColorButton(), constraintsColorButton);
            
            java.awt.GridBagConstraints constraintsColorPointCheckBox = new java.awt.GridBagConstraints();
            constraintsColorPointCheckBox.gridx = 2; constraintsColorPointCheckBox.gridy = 1;
            constraintsColorPointCheckBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsColorPointCheckBox.insets = new java.awt.Insets(0, 0, 4, 0);
            getJPanel1().add(getColorPointCheckBox(), constraintsColorPointCheckBox);
            
            java.awt.GridBagConstraints constraintsColorPointLabel = new java.awt.GridBagConstraints();
            constraintsColorPointLabel.gridx = 3; constraintsColorPointLabel.gridy = 1;
            constraintsColorPointLabel.gridwidth = 2;
            constraintsColorPointLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsColorPointLabel.insets = new java.awt.Insets(0, 0, 4, 0);
            getJPanel1().add(getColorPointLabel(), constraintsColorPointLabel);
            
            java.awt.GridBagConstraints constraintsColorPointButton = new java.awt.GridBagConstraints();
            constraintsColorPointButton.gridx = 5; constraintsColorPointButton.gridy = 1;
            constraintsColorPointButton.anchor = java.awt.GridBagConstraints.EAST;
            constraintsColorPointButton.gridwidth = 1;
            constraintsColorPointButton.insets = new java.awt.Insets(0, 4, 4, 0);
            getJPanel1().add(getColorPointButton(), constraintsColorPointButton);

			java.awt.GridBagConstraints constraintsDisplayAttributesLabel = new java.awt.GridBagConstraints();
			constraintsDisplayAttributesLabel.gridx = 0; constraintsDisplayAttributesLabel.gridy = 4;
            constraintsDisplayAttributesLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDisplayAttributesLabel.insets = new java.awt.Insets(0, 0, 4, 0);
			getJPanel1().add(getDisplayAttributesLabel(), constraintsDisplayAttributesLabel);

			java.awt.GridBagConstraints constraintsDisplayAttributesComboBox = new java.awt.GridBagConstraints();
			constraintsDisplayAttributesComboBox.gridx = 1; constraintsDisplayAttributesComboBox.gridy = 4;
			constraintsDisplayAttributesComboBox.gridwidth = 2;
            constraintsDisplayAttributesComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDisplayAttributesComboBox.weightx = 1.0;
			constraintsDisplayAttributesComboBox.insets = new java.awt.Insets(0, 0, 4, 0);
			getJPanel1().add(getDisplayAttributesComboBox(), constraintsDisplayAttributesComboBox);
            
            java.awt.GridBagConstraints constraintsCurrentStatePointButton = new java.awt.GridBagConstraints();
            constraintsCurrentStatePointButton.gridx = 3; constraintsCurrentStatePointButton.gridy = 4;
            constraintsCurrentStatePointButton.gridwidth = 3;
            constraintsCurrentStatePointButton.anchor = java.awt.GridBagConstraints.EAST;
            constraintsCurrentStatePointButton.insets = new java.awt.Insets(0, 4, 4, 0);
            getJPanel1().add(getTextPointButton(), constraintsCurrentStatePointButton);
            
            java.awt.GridBagConstraints constraintsBlinklLabel = new java.awt.GridBagConstraints();
            constraintsBlinklLabel.gridx = 0; constraintsBlinklLabel.gridy = 2;
            constraintsBlinklLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsBlinklLabel.insets = new java.awt.Insets(0, 0, 4, 0);
            getJPanel1().add(getBlinkLabel(), constraintsBlinklLabel);
            
            java.awt.GridBagConstraints constraintsBlinkCheckBox = new java.awt.GridBagConstraints();
            constraintsBlinkCheckBox.gridx = 1; constraintsBlinkCheckBox.gridy = 2;
            constraintsBlinkCheckBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsBlinkCheckBox.insets = new java.awt.Insets(0, 0, 4, 0);
            getJPanel1().add(getBlinkCheckBox(), constraintsBlinkCheckBox);
            
            java.awt.GridBagConstraints constraintsBlinkPointCheckBox = new java.awt.GridBagConstraints();
            constraintsBlinkPointCheckBox.gridx = 2; constraintsBlinkPointCheckBox.gridy = 2;
            constraintsBlinkPointCheckBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsBlinkPointCheckBox.insets = new java.awt.Insets(0, 0, 4, 0);
            getJPanel1().add(getBlinkPointCheckBox(), constraintsBlinkPointCheckBox);
            
            java.awt.GridBagConstraints constraintsBlinkPointlLabel = new java.awt.GridBagConstraints();
            constraintsBlinkPointlLabel.gridx = 3; constraintsBlinkPointlLabel.gridy = 2;
            constraintsBlinkPointlLabel.gridwidth = 2;
            constraintsBlinkPointlLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsBlinkPointlLabel.insets = new java.awt.Insets(0, 0, 4, 0);
            getJPanel1().add(getBlinkPointLabel(), constraintsBlinkPointlLabel);
            
            java.awt.GridBagConstraints constraintsBlinkPointButton = new java.awt.GridBagConstraints();
            constraintsBlinkPointButton.gridx = 5; constraintsBlinkPointButton.gridy = 2;
            constraintsBlinkPointButton.anchor = java.awt.GridBagConstraints.EAST;
            constraintsBlinkPointButton.insets = new java.awt.Insets(0, 4, 4, 0);
            getJPanel1().add(getBlinkPointButton(), constraintsBlinkPointButton);
            
            java.awt.GridBagConstraints constraintsEnableControlLabel = new java.awt.GridBagConstraints();
            constraintsEnableControlLabel.gridx = 0; constraintsEnableControlLabel.gridy = 3;
            constraintsEnableControlLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsEnableControlLabel.insets = new java.awt.Insets(0, 0, 4, 0);
            getJPanel1().add(getEnableControlLabel(), constraintsEnableControlLabel);
            
            java.awt.GridBagConstraints constraintsControlCheckBox = new java.awt.GridBagConstraints();
            constraintsControlCheckBox.gridx = 2; constraintsControlCheckBox.gridy = 3;
            constraintsControlCheckBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsControlCheckBox.insets = new java.awt.Insets(0, 0, 4, 0);
            getJPanel1().add(getControlCheckBox(), constraintsControlCheckBox);
            
            java.awt.GridBagConstraints constraintsEnableControPointlLabel = new java.awt.GridBagConstraints();
            constraintsEnableControPointlLabel.gridx = 3; constraintsEnableControPointlLabel.gridy = 3;
            constraintsEnableControPointlLabel.gridwidth = 2;
            constraintsEnableControPointlLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsEnableControPointlLabel.insets = new java.awt.Insets(0, 0, 4, 0);
            getJPanel1().add(getEnableControlPointLabel(), constraintsEnableControPointlLabel);
            
            java.awt.GridBagConstraints constraintsEnableControlButton = new java.awt.GridBagConstraints();
            constraintsEnableControlButton.gridx = 5; constraintsEnableControlButton.gridy = 3;
            constraintsEnableControlButton.anchor = java.awt.GridBagConstraints.EAST;
            constraintsEnableControlButton.insets = new java.awt.Insets(0, 4, 4, 0);
            getJPanel1().add(getEnableControlPointButton(), constraintsEnableControlButton);
            
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}
/**
 * Return the LinkToPanel property value.
 * @return com.cannontech.esub.editor.element.LinkToPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private LinkToPanel getLinkToPanel() {
	if (ivjLinkToPanel == null) {
		try {
			ivjLinkToPanel = new com.cannontech.esub.editor.element.LinkToPanel();
			ivjLinkToPanel.setName("LinkToPanel");
			ivjLinkToPanel.setPreferredSize(new java.awt.Dimension(405, 33));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLinkToPanel;
}

private JCheckBox getControlCheckBox() {
    if (controlCheckBox == null) {
        try {
            controlCheckBox = new JCheckBox("Enable Control:");
            controlCheckBox.setName("ControlCheckBox");
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return controlCheckBox;
}

/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) {
	
	dynamicText.setFont( new Font( getFontComboBox().getSelectedItem().toString(), Font.PLAIN, ((Integer) getFontSizeComboBox().getSelectedItem()).intValue() ));
	dynamicText.setPaint(colorChooser.getColor());
	
	String link = getLinkToPanel().getLinkTo();
	if(link.length() > 0 ) {
		dynamicText.setLinkTo(link);
	} else {
		dynamicText.setLinkTo(null);
	}
	
	int att = PointAttributes.VALUE;
	String attStr = getDisplayAttributesComboBox().getSelectedItem().toString();
	if( attStr.equals(ATTRIBUTE_CURRENT_VALUE) ) {
		att = PointAttributes.VALUE;
	}
	else
	if( attStr.equals(ATTRIBUTE_CURRENT_VALUE_AND_UNIT_OF_MEASURE) ) {
		att = (PointAttributes.VALUE | PointAttributes.UOFM);
	}
	else
	if( attStr.equals(ATTRIBUTE_DEVICE_NAME) ) {
		att = PointAttributes.PAO;
	}
	else
	if( attStr.equals(ATTRIBUTE_LAST_UPDATE) ) {
		att = PointAttributes.LAST_UPDATE;
	}
	else
	if( attStr.equals(ATTRIBUTE_POINT_NAME) ) {
		att = PointAttributes.NAME;
	}
	else
	if( attStr.equals(ATTRIBUTE_LOW_LIMIT) ) {
		att = PointAttributes.LOW_LIMIT;			
	}
	else
	if( attStr.equals(ATTRIBUTE_HIGH_LIMIT) ) {
		att = PointAttributes.HIGH_LIMIT;
	}
	else
	if( attStr.equals(ATTRIBUTE_LIMIT_DURATION) ) {
		att = PointAttributes.LIMIT_DURATION;
	}
	else
	if( attStr.equals(ATTRIBUTE_MULTIPLIER) ) {
		att = PointAttributes.MULTIPLIER;
	}
	else
	if( attStr.equals(ATTRIBUTE_DATA_OFFSET) ) {
		att = PointAttributes.DATA_OFFSET;
	}		
	else
	if( attStr.equals(ATTRIBUTE_ALARM_TEXT) ) {
		att = PointAttributes.ALARM_TEXT;
	}
	else
	if( attStr.equals(ATTRIBUTE_CURRENT_STATE)) {
		att = PointAttributes.STATE_TEXT;
	}

	dynamicText.setDisplayAttribs(att);
	dynamicText.setControlEnabled(getControlCheckBox().isSelected());
    if(getControlCheckBox().isSelected()) {
        if(getEnableControlPointPanel().getPointSelectionPanel().getSelectedPoint() == null){
            CTILogger.error("No point selected");
            JOptionPane.showMessageDialog(this, "Please select a point for control or uncheck the checkbox.", "Settings not done yet.", JOptionPane.WARNING_MESSAGE);
        }
        dynamicText.setControlPointId(getEnableControlPointPanel().getPointSelectionPanel().getSelectedPoint().getLiteID());
    }else {
        dynamicText.setControlPointId(-1);
    }
    if(getColorPointCheckBox().isSelected()) {
        if(getTextColorPointPanel().getPointSelectionPanel().getSelectedPoint() == null){
            CTILogger.error("No point selected");
            JOptionPane.showMessageDialog(this, "Please select a point for color or uncheck the checkbox.", "Settings not done yet.", JOptionPane.WARNING_MESSAGE);
        }
        dynamicText.setColorPointID(getTextColorPointPanel().getPointSelectionPanel().getSelectedPoint().getLiteID());
    }else {
        dynamicText.setColorPointID(-1);
    }
    
    if(attStr.equals(ATTRIBUTE_CURRENT_STATE)) {
        dynamicText.setPoint(getTextPointPanel().getPointSelectionPanel().getSelectedPoint());
        dynamicText.setCurrentStateID(getTextPointPanel().getPointSelectionPanel().getSelectedPoint().getLiteID());
    }else {
        dynamicText.setPoint( getPointSelectionPropertyPanel().getPointSelectionPanel().getSelectedPoint());
        dynamicText.setCurrentStateID(-1);
    }
    
    if( getBlinkCheckBox().isSelected()) {
        dynamicText.setTextBlink(1);
    }else {
        dynamicText.setTextBlink(0);
    }
    if(getBlinkPointCheckBox().isSelected()) {
        if(getBlinkPointPanel().getPointSelectionPanel().getSelectedPoint() == null){
            CTILogger.error("No opacity point selected");
            JOptionPane.showMessageDialog(this, "Please select a point for blink or uncheck the point driven checkbox.", "Settings not done yet.", JOptionPane.WARNING_MESSAGE);
        }
        dynamicText.setBlinkPointID(getBlinkPointPanel().getPointSelectionPanel().getSelectedPoint().getLiteID());
    }else {
        dynamicText.setBlinkPointID(-1);
    }
    
	return dynamicText;
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
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
private void initConnections() throws java.lang.Exception {
    getControlCheckBox().addActionListener(this);
    getBlinkPointCheckBox().addActionListener(this);
    getColorButton().addActionListener(this);
    getBlinkPointButton().addActionListener(this);
    getColorPointButton().addActionListener(this);
    getColorPointCheckBox().addActionListener(this);
    getTextPointButton().addActionListener(this);
    getDisplayAttributesComboBox().addActionListener(this);
    getTextColorPointPanel().getOkButton().addActionListener(this);
    getTextPointPanel().getOkButton().addActionListener(this);
    getEnableControlPointButton().addActionListener(this);
    getPointSelectionPropertyPanel().getOkButton().addActionListener(this);
    getPointSelectionPropertyPanel().getCancelButton().addActionListener(this);
    getEnableControlPointPanel().getCancelButton().addActionListener(this);
    getEnableControlPointPanel().getOkButton().addActionListener(this);
    getTextPointPanel().getPointSelectionPanel().getIvjDevicePointTree().addTreeSelectionListener(this);
    getPointSelectionPropertyPanel().getPointSelectionPanel().getIvjDevicePointTree().addTreeSelectionListener(this);
    getBlinkPointPanel().getOkButton().addActionListener(this);
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("DynamicTextEditorPanel");
		setPreferredSize(new java.awt.Dimension(405, 520));
		setLayout(new java.awt.GridBagLayout());
		setSize(405, 520);
		setMinimumSize(new java.awt.Dimension(405, 520));
        this.setBorder(new TitleBorder("Dynamic Text Editor"));
        
		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 1;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(0, 4, 8, 4);
		add(getJPanel1(), constraintsJPanel1);

		java.awt.GridBagConstraints constraintsLinkToPanel = new java.awt.GridBagConstraints();
		constraintsLinkToPanel.gridx = 0; constraintsLinkToPanel.gridy = 0;
		constraintsLinkToPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsLinkToPanel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getLinkToPanel(), constraintsLinkToPanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
	for( int i = 0; i < fonts.length; i++ ) {
		getFontComboBox().addItem(fonts[i].getFontName());
	}

	for( int i = 0; i < availableFontSizes.length; i++ ) {
		getFontSizeComboBox().addItem( new Integer(availableFontSizes[i] ));
	}
	colorChooser = Util.getJColorChooser();
}
/**
 * Creation date: (12/18/2001 3:46:27 PM)
 * @return boolean
 */
public boolean isInputValid() {
	return ((getPointSelectionPropertyPanel().getPointSelectionPanel().getSelectedPoint() != null) ||
            (getTextPointPanel().getPointSelectionPanel().getSelectedPoint() != null));
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DynamicTextEditorPanel aDynamicTextEditorPanel;
		aDynamicTextEditorPanel = new DynamicTextEditorPanel();
		frame.setContentPane(aDynamicTextEditorPanel);
		frame.setSize(aDynamicTextEditorPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) {
	dynamicText = (DynamicText) o;

	String attStr = ATTRIBUTE_CURRENT_VALUE;
	int att = dynamicText.getDisplayAttribs();	
	if( (att & PointAttributes.VALUE) != 0 ) {
		if( (att & PointAttributes.UOFM) != 0 ) {
			attStr = ATTRIBUTE_CURRENT_VALUE_AND_UNIT_OF_MEASURE;
		}
		else {
			attStr = ATTRIBUTE_CURRENT_VALUE;
		}		
	}
	else
	if( (att & PointAttributes.NAME) != 0 ) {
		attStr = ATTRIBUTE_POINT_NAME;
	}
	else 
	if( (att & PointAttributes.PAO) != 0 ) {
		attStr = ATTRIBUTE_DEVICE_NAME;
	}
	else
	if( (att & PointAttributes.LAST_UPDATE) != 0 ) {
		attStr = ATTRIBUTE_LAST_UPDATE;	
	}
	else
	if( (att & PointAttributes.LOW_LIMIT) != 0 ) {
		attStr = ATTRIBUTE_LOW_LIMIT;
	}
	else
	if( (att & PointAttributes.HIGH_LIMIT) != 0 ) {
		attStr = ATTRIBUTE_HIGH_LIMIT;
	}
	else
	if( (att & PointAttributes.LIMIT_DURATION) != 0 ) {
		attStr = ATTRIBUTE_LIMIT_DURATION;
	}
	else
	if( (att & PointAttributes.MULTIPLIER) != 0 ) {
		attStr = ATTRIBUTE_MULTIPLIER;
	}
	else
	if( (att & PointAttributes.DATA_OFFSET) != 0 ) {
		attStr = ATTRIBUTE_DATA_OFFSET;
	}
	else
	if( (att & PointAttributes.ALARM_TEXT) != 0 ) {
		attStr = ATTRIBUTE_ALARM_TEXT;
	}
	else
	if( (att & PointAttributes.STATE_TEXT) != 0 ) {
		attStr = ATTRIBUTE_CURRENT_STATE;
	}
		
	for( int i = 0; i < getDisplayAttributesComboBox().getItemCount(); i++ ) {
		if( getDisplayAttributesComboBox().getItemAt(i).equals(attStr) ) {
			getDisplayAttributesComboBox().setSelectedIndex(i);
		}
	}
    
    getControlCheckBox().setSelected(dynamicText.getControlEnabled());
    if( dynamicText.getControlEnabled() ) {
        getEnableControlPointLabel().setEnabled(true);
        getEnableControlPointButton().setEnabled(true);
        int controlPointId = dynamicText.getControlPointId();
        LitePoint controlPoint = DaoFactory.getPointDao().getLitePoint(controlPointId);
        getEnableControlPointPanel().getPointSelectionPanel().selectPoint(controlPoint);
        getEnableControlPointLabel().setText(controlPoint.getPointName());
        getEnableControlPointLabel().setForeground(Color.black);

    }else {
        getEnableControlPointLabel().setEnabled(false);
        getEnableControlPointButton().setEnabled(false);
    }
    
    getPointSelectionPropertyPanel().getPointSelectionPanel().refresh();
    getTextPointPanel().getPointSelectionPanel().refresh();
    if( dynamicText.getPointId() != DynamicText.INVALID_POINT ) {
        LitePoint point = dynamicText.getPoint();
        getPointSelectionPropertyPanel().getPointSelectionPanel().selectPoint(point);
        getTextPointPanel().getPointSelectionPanel().selectPoint(point);
        getTextPointButton().setText(point.getPointName());
        getTextPointButton().setForeground(Color.BLACK);
    }
        
    getLinkToPanel().setLinkTo(dynamicText.getLinkTo());

    for( int i = 0; i < getFontComboBox().getItemCount(); i++ ) {
        if( getFontComboBox().getItemAt(i).toString().equalsIgnoreCase(dynamicText.getFont().getFontName()) ) {
            getFontComboBox().setSelectedIndex(i);
        }
    }

    for( int i = 0; i < getFontSizeComboBox().getItemCount(); i++ ) {
        if( ((Integer) getFontSizeComboBox().getItemAt(i)).intValue() == dynamicText.getFont().getSize() ) {
            getFontSizeComboBox().setSelectedIndex(i);
        }
    }
	
	Color textColor = (java.awt.Color) dynamicText.getPaint();
	getColorButton().setBackground(textColor);
	colorChooser.setColor(textColor);
    
    if(dynamicText.getColorPointID() < 0) {
        getColorPointCheckBox().setSelected(false);
        getColorPointButton().setEnabled(false);
    }else {
        getColorPointCheckBox().setSelected(true);
        getColorPointButton().setEnabled(true);
        getColorPointLabel().setEnabled(true);
        getColorButton().setEnabled(false);
        getColorButton().setBackground(java.awt.Color.LIGHT_GRAY);
        LitePoint litePoint = null;
        try {
            litePoint = DaoFactory.getPointDao().getLitePoint(dynamicText.getColorPointID());
        }catch(NotFoundException nfe) {
            CTILogger.error("The color point (pointId:"+ dynamicText.getColorPointID() + ") for this DynamicText might have been deleted!", nfe);
        }
        if(litePoint != null) {
            getTextColorPointPanel().getPointSelectionPanel().selectPoint(litePoint);
            getColorPointLabel().setText(litePoint.getPointName());
        }
        
        getColorPointLabel().setForeground(Color.black);
    }
    
    if(dynamicText.getTextBlink() > 0) {
        getBlinkCheckBox().setSelected(true);
    }else {
        getBlinkCheckBox().setSelected(false);
    }
    
    if(dynamicText.getBlinkPointID() < 0) {
        getBlinkPointCheckBox().setSelected(false);
        getBlinkPointButton().setEnabled(false);
    }else {
        getBlinkPointCheckBox().setSelected(true);
        getBlinkPointButton().setEnabled(true);
        getBlinkPointLabel().setEnabled(true);
        getBlinkCheckBox().setEnabled(false);
        
        LitePoint litePoint = null;
        try {
            litePoint = DaoFactory.getPointDao().getLitePoint(dynamicText.getBlinkPointID());
        }catch(NotFoundException nfe) {
            CTILogger.error("The blink point (pointId:"+ dynamicText.getBlinkPointID() + ") for this DynamicText might have been deleted!", nfe);
        }
        if(litePoint != null) {
            getBlinkPointPanel().getPointSelectionPanel().selectPoint(litePoint);
            getBlinkPointLabel().setText(litePoint.getPointName());
        }
        
        getBlinkPointLabel().setForeground(Color.black);
    }
}

public TextColorPointPanel getTextColorPointPanel() {
    if (textColorPointPanel == null) {
        textColorPointPanel = new TextColorPointPanel();
    }
    return textColorPointPanel;
}

public TextPointPanel getTextPointPanel() {
    if (textPointPanel == null) {
        textPointPanel = new TextPointPanel();
    }
    return textPointPanel;
}

/**
 * Creation date: (12/18/2001 4:16:51 PM)
 * @param evt javax.swing.event.TreeSelectionEvent
 */
public void valueChanged(TreeSelectionEvent evt) {
	fireInputUpdate();
}
public void actionPerformed(ActionEvent e) {
    if (e.getSource() == getColorButton()) {
        JDialog d = JColorChooser.createDialog(this, "Select a color", true, colorChooser, 
            new java.awt.event.ActionListener() { //ok listener
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    getColorButton().setBackground(colorChooser.getColor());                
                }
            },
            new java.awt.event.ActionListener() { //cancel listener
                public void actionPerformed(java.awt.event.ActionEvent e) {
                }
            }
         );

        d.show();
        d.dispose();
    }else if (e.getSource() == getColorPointCheckBox()) {
        if(getColorPointCheckBox().isSelected()) {
                getColorPointButton().setEnabled(true);
                getColorPointLabel().setEnabled(true);
                getColorButton().setEnabled(false);
                getColorButton().setBackground(java.awt.Color.LIGHT_GRAY);
                getColorLabel().setBackground(java.awt.Color.LIGHT_GRAY);
        }else {
            getColorPointButton().setEnabled(false);
            getColorPointLabel().setEnabled(false);
            getColorButton().setEnabled(true);
            getColorLabel().setBackground(colorChooser.getColor());
            getColorButton().setBackground(colorChooser.getColor());
        }
    }else if (e.getSource() == getControlCheckBox()) {
        if(getControlCheckBox().isSelected()) {
            getEnableControlPointLabel().setEnabled(true);
            getEnableControlPointButton().setEnabled(true);
        }else {
            getEnableControlPointButton().setEnabled(false);
            getEnableControlPointLabel().setEnabled(false);
        }
    }else if (e.getSource() == getBlinkPointCheckBox()) {
        if(getBlinkPointCheckBox().isSelected()) {
            getBlinkCheckBox().setEnabled(false);
            getBlinkPointButton().setEnabled(true);
            getBlinkPointLabel().setEnabled(true);
        }else {
            getBlinkPointButton().setEnabled(false);
            getBlinkPointLabel().setEnabled(false);
            getBlinkCheckBox().setEnabled(true);
        }
    }else if(e.getSource() == getColorPointButton()) {
        pointPanelDialog = new JDialog(SwingUtil.getParentFrame(this), true);
        pointPanelDialog.setContentPane(getTextColorPointPanel());
        pointPanelDialog.setSize(new java.awt.Dimension(540, 630));
        getTextColorPointPanel().setValue(dynamicText);
        pointPanelDialog.setVisible(true);
    }else if(e.getSource() == getEnableControlPointButton()) {
        pointPanelDialog = new JDialog(SwingUtil.getParentFrame(this), true);
        pointPanelDialog.setContentPane(getEnableControlPointPanel());
        pointPanelDialog.setSize(new java.awt.Dimension(300, 500));
        pointPanelDialog.setVisible(true);
    }else if(e.getSource() == getBlinkPointButton()) {
        pointPanelDialog = new JDialog(SwingUtil.getParentFrame(this), true);
        pointPanelDialog.setContentPane(getBlinkPointPanel());
        pointPanelDialog.setSize(new java.awt.Dimension(540, 630));
        getBlinkPointPanel().setValue(dynamicText);
        pointPanelDialog.setVisible(true);
    }else if(e.getSource() == getTextPointButton()) {
        if(getDisplayAttributesComboBox().getSelectedItem().toString().equals(ATTRIBUTE_CURRENT_STATE)){
            pointPanelDialog = new JDialog(SwingUtil.getParentFrame(this), true);
            pointPanelDialog.setContentPane(getTextPointPanel());
            pointPanelDialog.setSize(new java.awt.Dimension(540, 630));
            getTextPointPanel().setValue(dynamicText);
            pointPanelDialog.setVisible(true);
        } else {
            pointPanelDialog = new JDialog(SwingUtil.getParentFrame(this), true);
            pointPanelDialog.setContentPane(getPointSelectionPropertyPanel());
            pointPanelDialog.setSize(new java.awt.Dimension(300, 500));
            pointPanelDialog.setVisible(true);
        }
    }else if( e.getSource() == getTextColorPointPanel().getOkButton()) {
        getTextColorPointPanel().getValue(dynamicText);
        getColorPointLabel().setText(getTextColorPointPanel().getPointSelectionPanel().getSelectedPoint().getPointName());
        getColorPointLabel().setForeground(java.awt.Color.BLACK);
        pointPanelDialog.setVisible(false);
    }else if( e.getSource() == getTextPointPanel().getOkButton()) {
        getTextPointPanel().getValue(dynamicText);
        getTextPointButton().setText(getTextPointPanel().getPointSelectionPanel().getSelectedPoint().getPointName());
        getTextPointButton().setForeground(java.awt.Color.BLACK);
        getPointSelectionPropertyPanel().getPointSelectionPanel().selectPoint(getTextPointPanel().getPointSelectionPanel().getSelectedPoint());
        pointPanelDialog.setVisible(false);
    }else if( e.getSource() == getPointSelectionPropertyPanel().getOkButton()) {
        getTextPointButton().setText(getPointSelectionPropertyPanel().getPointSelectionPanel().getSelectedPoint().getPointName());
        getTextPointButton().setForeground(java.awt.Color.BLACK);
        getTextPointPanel().getPointSelectionPanel().selectPoint(getPointSelectionPropertyPanel().getPointSelectionPanel().getSelectedPoint());
        pointPanelDialog.setVisible(false);
    }
    else if( e.getSource() == getPointSelectionPropertyPanel().getCancelButton()) {
        pointPanelDialog.setVisible(false);
    }
    else if( e.getSource() == getEnableControlPointPanel().getCancelButton()) {
        pointPanelDialog.setVisible(false);
    }
    else if( e.getSource() == getEnableControlPointPanel().getOkButton()) {
        getEnableControlPointLabel().setText( getEnableControlPointPanel().getPointSelectionPanel().getSelectedPoint().getPointName());
        getEnableControlPointLabel().setForeground(java.awt.Color.BLACK);
        pointPanelDialog.setVisible(false);
    }else if( e.getSource() == getBlinkPointPanel().getOkButton()) {
        getBlinkPointPanel().getValue(dynamicText);
        getBlinkPointLabel().setText( getBlinkPointPanel().getPointSelectionPanel().getSelectedPoint().getPointName());
        getBlinkPointLabel().setForeground(java.awt.Color.BLACK);
        pointPanelDialog.setVisible(false);
    }
}

private PointSelectionPropertyPanel getPointSelectionPropertyPanel() {
    if(pointSelectionPropertyPanel == null) {
        pointSelectionPropertyPanel = new PointSelectionPropertyPanel();
        
    }
    return pointSelectionPropertyPanel;
}

private PointSelectionPropertyPanel getEnableControlPointPanel() {
    if(enableControlPointPanel == null) {
        enableControlPointPanel = new PointSelectionPropertyPanel();
        
    }
    return enableControlPointPanel;
}

public BlinkPointPanel getBlinkPointPanel() {
    if (blinkPointPanel == null) {
        blinkPointPanel = new BlinkPointPanel();
    }
    return blinkPointPanel;
}
}
