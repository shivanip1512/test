package com.cannontech.esub.editor.element;

import javax.swing.JColorChooser;
import java.awt.Font;
import java.awt.Color;
import java.awt.GraphicsEnvironment;
import javax.swing.event.TreeSelectionEvent;

import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.esub.element.AlarmTextElement;


/**
 * Insert the type's description here.
 * Creation date: (5/8/2003 5:22:35 PM)
 * @author: 
 */
public class AlarmTextElementEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements javax.swing.event.TreeSelectionListener {
	private javax.swing.JLabel ivjAlarmColorLabel = null;
	private javax.swing.JComboBox ivjAlarmFontComboBox = null;
	private javax.swing.JLabel ivjAlarmFontLabel = null;
	private javax.swing.JLabel ivjAlarmTextLabel = null;
	private javax.swing.JTextField ivjAlarmTextTextField = null;
	private javax.swing.JButton ivjDefaultColorButton = null;
	private javax.swing.JLabel ivjDefaultColorLabel = null;
	private javax.swing.JComboBox ivjDefaultFontComboBox = null;
	private javax.swing.JLabel ivjDefaultFontLabel = null;
	private javax.swing.JLabel ivjDefaultTextLabel = null;
	private javax.swing.JTextField ivjDefaultTextTextField = null;
	private LinkToPanel ivjLinkToPanel = null;
	private PointSelectionPanel ivjPointSelectionPanel = null;
	private javax.swing.JPanel ivjTextPanel = null;
	private JColorChooser colorChooser;
	private static final int[] availableFontSizes = {
		6,8,9,10,11,12,14,18,24,36,48,60,72,84,96
	};
	private javax.swing.JButton ivjAlarmColorButton = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JComboBox ivjAlarmSizeComboBox = null;
	private javax.swing.JLabel ivjAlarmSizeLabel = null;
	private javax.swing.JComboBox ivjDefaultSizeComboBox = null;
	private javax.swing.JLabel ivjDefaultSizeLabel = null;
	
	private AlarmTextElement alarmTextElement;
	

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == AlarmTextElementEditorPanel.this.getDefaultColorButton()) 
				connEtoC1(e);
			if (e.getSource() == AlarmTextElementEditorPanel.this.getAlarmColorButton()) 
				connEtoC2();
		};
	};
/**
 * AlarmTextElementEditorPanel constructor comment.
 */
public AlarmTextElementEditorPanel() {
	super();
	initialize();
}
/**
 * Comment
 */
public void alarmColorButton_ActionEvents() {
		javax.swing.JDialog d = javax.swing.JColorChooser.createDialog(this, "Select a color", true, colorChooser, 
		new java.awt.event.ActionListener() { //ok listener
			public void actionPerformed(java.awt.event.ActionEvent e) {
				getAlarmColorButton().setBackground(colorChooser.getColor());				
			}
		},
		new java.awt.event.ActionListener() { //cancel listener
			public void actionPerformed(java.awt.event.ActionEvent e) {
			}
		}
			);

	d.show();
	d.dispose();
}
/**
 * Comment
 */
public void alarmColorButton_ActionEvents1() {
		javax.swing.JDialog d = javax.swing.JColorChooser.createDialog(this, "Select a color", true, colorChooser, 
		new java.awt.event.ActionListener() { //ok listener
			public void actionPerformed(java.awt.event.ActionEvent e) {
				getAlarmColorButton().setBackground(colorChooser.getColor());				
			}
		},
		new java.awt.event.ActionListener() { //cancel listener
			public void actionPerformed(java.awt.event.ActionEvent e) {
			}
		}
			);

	d.show();
	d.dispose();
}
/**
 * connEtoC1:  (DefaultColorButton.action.actionPerformed(java.awt.event.ActionEvent) --> AlarmTextElementEditorPanel.defaultColorButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.defaultColorButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (AlarmColorButton.action. --> AlarmTextElementEditorPanel.alarmColorButton_ActionEvents1()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2() {
	try {
		// user code begin {1}
		// user code end
		this.alarmColorButton_ActionEvents1();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
public void defaultColorButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	javax.swing.JDialog d = javax.swing.JColorChooser.createDialog(this, "Select a color", true, colorChooser, 
		new java.awt.event.ActionListener() { //ok listener
			public void actionPerformed(java.awt.event.ActionEvent e) {
				getDefaultColorButton().setBackground(colorChooser.getColor());				
			}
		},
		new java.awt.event.ActionListener() { //cancel listener
			public void actionPerformed(java.awt.event.ActionEvent e) {
			}
		}
			);

	d.show();
	d.dispose();
}
/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getAlarmColorButton() {
	if (ivjAlarmColorButton == null) {
		try {
			ivjAlarmColorButton = new javax.swing.JButton();
			ivjAlarmColorButton.setName("AlarmColorButton");
			ivjAlarmColorButton.setPreferredSize(new java.awt.Dimension(65, 22));
			ivjAlarmColorButton.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAlarmColorButton;
}
/**
 * Return the AlarmColorLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getAlarmColorLabel() {
	if (ivjAlarmColorLabel == null) {
		try {
			ivjAlarmColorLabel = new javax.swing.JLabel();
			ivjAlarmColorLabel.setName("AlarmColorLabel");
			ivjAlarmColorLabel.setText("Color:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAlarmColorLabel;
}
/**
 * Return the AlarmFontComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getAlarmFontComboBox() {
	if (ivjAlarmFontComboBox == null) {
		try {
			ivjAlarmFontComboBox = new javax.swing.JComboBox();
			ivjAlarmFontComboBox.setName("AlarmFontComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAlarmFontComboBox;
}
/**
 * Return the AlarmFontLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getAlarmFontLabel() {
	if (ivjAlarmFontLabel == null) {
		try {
			ivjAlarmFontLabel = new javax.swing.JLabel();
			ivjAlarmFontLabel.setName("AlarmFontLabel");
			ivjAlarmFontLabel.setText("Font:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAlarmFontLabel;
}
/**
 * Return the AlarmSizeComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getAlarmSizeComboBox() {
	if (ivjAlarmSizeComboBox == null) {
		try {
			ivjAlarmSizeComboBox = new javax.swing.JComboBox();
			ivjAlarmSizeComboBox.setName("AlarmSizeComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAlarmSizeComboBox;
}
/**
 * Return the AlarmSizeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getAlarmSizeLabel() {
	if (ivjAlarmSizeLabel == null) {
		try {
			ivjAlarmSizeLabel = new javax.swing.JLabel();
			ivjAlarmSizeLabel.setName("AlarmSizeLabel");
			ivjAlarmSizeLabel.setText("Size:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAlarmSizeLabel;
}
/**
 * Return the AlarmTextLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getAlarmTextLabel() {
	if (ivjAlarmTextLabel == null) {
		try {
			ivjAlarmTextLabel = new javax.swing.JLabel();
			ivjAlarmTextLabel.setName("AlarmTextLabel");
			ivjAlarmTextLabel.setText("Alarm Text:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAlarmTextLabel;
}
/**
 * Return the AlarmTextTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getAlarmTextTextField() {
	if (ivjAlarmTextTextField == null) {
		try {
			ivjAlarmTextTextField = new javax.swing.JTextField();
			ivjAlarmTextTextField.setName("AlarmTextTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAlarmTextTextField;
}
/**
 * Return the DefaultColorButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getDefaultColorButton() {
	if (ivjDefaultColorButton == null) {
		try {
			ivjDefaultColorButton = new javax.swing.JButton();
			ivjDefaultColorButton.setName("DefaultColorButton");
			ivjDefaultColorButton.setPreferredSize(new java.awt.Dimension(65, 22));
			ivjDefaultColorButton.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultColorButton;
}
/**
 * Return the DefaultColorLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDefaultColorLabel() {
	if (ivjDefaultColorLabel == null) {
		try {
			ivjDefaultColorLabel = new javax.swing.JLabel();
			ivjDefaultColorLabel.setName("DefaultColorLabel");
			ivjDefaultColorLabel.setText("Color:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultColorLabel;
}
/**
 * Return the DefaultFontComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getDefaultFontComboBox() {
	if (ivjDefaultFontComboBox == null) {
		try {
			ivjDefaultFontComboBox = new javax.swing.JComboBox();
			ivjDefaultFontComboBox.setName("DefaultFontComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultFontComboBox;
}
/**
 * Return the DefaultFontLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDefaultFontLabel() {
	if (ivjDefaultFontLabel == null) {
		try {
			ivjDefaultFontLabel = new javax.swing.JLabel();
			ivjDefaultFontLabel.setName("DefaultFontLabel");
			ivjDefaultFontLabel.setText("Font:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultFontLabel;
}
/**
 * Return the DefaultSizeComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getDefaultSizeComboBox() {
	if (ivjDefaultSizeComboBox == null) {
		try {
			ivjDefaultSizeComboBox = new javax.swing.JComboBox();
			ivjDefaultSizeComboBox.setName("DefaultSizeComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultSizeComboBox;
}
/**
 * Return the DefaultSizeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDefaultSizeLabel() {
	if (ivjDefaultSizeLabel == null) {
		try {
			ivjDefaultSizeLabel = new javax.swing.JLabel();
			ivjDefaultSizeLabel.setName("DefaultSizeLabel");
			ivjDefaultSizeLabel.setText("Size:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultSizeLabel;
}
/**
 * Return the DefaultTextLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDefaultTextLabel() {
	if (ivjDefaultTextLabel == null) {
		try {
			ivjDefaultTextLabel = new javax.swing.JLabel();
			ivjDefaultTextLabel.setName("DefaultTextLabel");
			ivjDefaultTextLabel.setText("Default Text:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultTextLabel;
}
/**
 * Return the DefaultTextTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getDefaultTextTextField() {
	if (ivjDefaultTextTextField == null) {
		try {
			ivjDefaultTextTextField = new javax.swing.JTextField();
			ivjDefaultTextTextField.setName("DefaultTextTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultTextTextField;
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
/**
 * Return the PointSelectionPanel property value.
 * @return com.cannontech.esub.editor.element.PointSelectionPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private PointSelectionPanel getPointSelectionPanel() {
	if (ivjPointSelectionPanel == null) {
		try {
			ivjPointSelectionPanel = new com.cannontech.esub.editor.element.PointSelectionPanel();
			ivjPointSelectionPanel.setName("PointSelectionPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPointSelectionPanel;
}
/**
 * Return the TextPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getTextPanel() {
	if (ivjTextPanel == null) {
		try {
			ivjTextPanel = new javax.swing.JPanel();
			ivjTextPanel.setName("TextPanel");
			ivjTextPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsDefaultTextTextField = new java.awt.GridBagConstraints();
			constraintsDefaultTextTextField.gridx = 1; constraintsDefaultTextTextField.gridy = 0;
			constraintsDefaultTextTextField.gridwidth = 3;
			constraintsDefaultTextTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsDefaultTextTextField.weightx = 1.0;
			constraintsDefaultTextTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getTextPanel().add(getDefaultTextTextField(), constraintsDefaultTextTextField);

			java.awt.GridBagConstraints constraintsDefaultTextLabel = new java.awt.GridBagConstraints();
			constraintsDefaultTextLabel.gridx = 0; constraintsDefaultTextLabel.gridy = 0;
			constraintsDefaultTextLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDefaultTextLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getTextPanel().add(getDefaultTextLabel(), constraintsDefaultTextLabel);

			java.awt.GridBagConstraints constraintsDefaultFontLabel = new java.awt.GridBagConstraints();
			constraintsDefaultFontLabel.gridx = 0; constraintsDefaultFontLabel.gridy = 1;
			constraintsDefaultFontLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDefaultFontLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getTextPanel().add(getDefaultFontLabel(), constraintsDefaultFontLabel);

			java.awt.GridBagConstraints constraintsDefaultFontComboBox = new java.awt.GridBagConstraints();
			constraintsDefaultFontComboBox.gridx = 1; constraintsDefaultFontComboBox.gridy = 1;
			constraintsDefaultFontComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsDefaultFontComboBox.weightx = 1.0;
			constraintsDefaultFontComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getTextPanel().add(getDefaultFontComboBox(), constraintsDefaultFontComboBox);

			java.awt.GridBagConstraints constraintsDefaultColorLabel = new java.awt.GridBagConstraints();
			constraintsDefaultColorLabel.gridx = 2; constraintsDefaultColorLabel.gridy = 2;
			constraintsDefaultColorLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getTextPanel().add(getDefaultColorLabel(), constraintsDefaultColorLabel);

			java.awt.GridBagConstraints constraintsDefaultColorButton = new java.awt.GridBagConstraints();
			constraintsDefaultColorButton.gridx = 3; constraintsDefaultColorButton.gridy = 2;
			constraintsDefaultColorButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getTextPanel().add(getDefaultColorButton(), constraintsDefaultColorButton);

			java.awt.GridBagConstraints constraintsAlarmTextLabel = new java.awt.GridBagConstraints();
			constraintsAlarmTextLabel.gridx = 0; constraintsAlarmTextLabel.gridy = 3;
			constraintsAlarmTextLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsAlarmTextLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getTextPanel().add(getAlarmTextLabel(), constraintsAlarmTextLabel);

			java.awt.GridBagConstraints constraintsAlarmTextTextField = new java.awt.GridBagConstraints();
			constraintsAlarmTextTextField.gridx = 1; constraintsAlarmTextTextField.gridy = 3;
			constraintsAlarmTextTextField.gridwidth = 3;
			constraintsAlarmTextTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsAlarmTextTextField.weightx = 1.0;
			constraintsAlarmTextTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getTextPanel().add(getAlarmTextTextField(), constraintsAlarmTextTextField);

			java.awt.GridBagConstraints constraintsAlarmFontLabel = new java.awt.GridBagConstraints();
			constraintsAlarmFontLabel.gridx = 0; constraintsAlarmFontLabel.gridy = 4;
			constraintsAlarmFontLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsAlarmFontLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getTextPanel().add(getAlarmFontLabel(), constraintsAlarmFontLabel);

			java.awt.GridBagConstraints constraintsAlarmFontComboBox = new java.awt.GridBagConstraints();
			constraintsAlarmFontComboBox.gridx = 1; constraintsAlarmFontComboBox.gridy = 4;
			constraintsAlarmFontComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsAlarmFontComboBox.weightx = 1.0;
			constraintsAlarmFontComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getTextPanel().add(getAlarmFontComboBox(), constraintsAlarmFontComboBox);

			java.awt.GridBagConstraints constraintsAlarmColorLabel = new java.awt.GridBagConstraints();
			constraintsAlarmColorLabel.gridx = 2; constraintsAlarmColorLabel.gridy = 5;
			constraintsAlarmColorLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getTextPanel().add(getAlarmColorLabel(), constraintsAlarmColorLabel);

			java.awt.GridBagConstraints constraintsAlarmColorButton = new java.awt.GridBagConstraints();
			constraintsAlarmColorButton.gridx = 3; constraintsAlarmColorButton.gridy = 5;
			constraintsAlarmColorButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getTextPanel().add(getAlarmColorButton(), constraintsAlarmColorButton);

			java.awt.GridBagConstraints constraintsDefaultSizeLabel = new java.awt.GridBagConstraints();
			constraintsDefaultSizeLabel.gridx = 0; constraintsDefaultSizeLabel.gridy = 2;
			constraintsDefaultSizeLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDefaultSizeLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getTextPanel().add(getDefaultSizeLabel(), constraintsDefaultSizeLabel);

			java.awt.GridBagConstraints constraintsDefaultSizeComboBox = new java.awt.GridBagConstraints();
			constraintsDefaultSizeComboBox.gridx = 1; constraintsDefaultSizeComboBox.gridy = 2;
			constraintsDefaultSizeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsDefaultSizeComboBox.weightx = 1.0;
			constraintsDefaultSizeComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getTextPanel().add(getDefaultSizeComboBox(), constraintsDefaultSizeComboBox);

			java.awt.GridBagConstraints constraintsAlarmSizeLabel = new java.awt.GridBagConstraints();
			constraintsAlarmSizeLabel.gridx = 0; constraintsAlarmSizeLabel.gridy = 5;
			constraintsAlarmSizeLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsAlarmSizeLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getTextPanel().add(getAlarmSizeLabel(), constraintsAlarmSizeLabel);

			java.awt.GridBagConstraints constraintsAlarmSizeComboBox = new java.awt.GridBagConstraints();
			constraintsAlarmSizeComboBox.gridx = 1; constraintsAlarmSizeComboBox.gridy = 5;
			constraintsAlarmSizeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsAlarmSizeComboBox.weightx = 1.0;
			constraintsAlarmSizeComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getTextPanel().add(getAlarmSizeComboBox(), constraintsAlarmSizeComboBox);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTextPanel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) {
	alarmTextElement.setLinkTo( getLinkToPanel().getLinkTo());
	
	alarmTextElement.setDefaultText( getDefaultTextTextField().getText());
	alarmTextElement.setDefaultTextFont( new Font(
											getDefaultFontComboBox().getSelectedItem().toString(),
											alarmTextElement.getDefaultTextFont().getStyle(),
											Integer.parseInt(getDefaultSizeComboBox().getSelectedItem().toString())));
	alarmTextElement.setDefaultTextColor(getDefaultColorButton().getBackground());											
										
	alarmTextElement.setAlarmText( getAlarmTextTextField().getText());
	alarmTextElement.setAlarmTextFont( new Font(
											getAlarmFontComboBox().getSelectedItem().toString(),
											alarmTextElement.getAlarmTextFont().getStyle(),
											Integer.parseInt(getAlarmSizeComboBox().getSelectedItem().toString())));
	alarmTextElement.setAlarmTextColor(getAlarmColorButton().getBackground());
	
	LitePoint[] selectedPoints = getPointSelectionPanel().getSelectedPoints();
	alarmTextElement.setPoints(selectedPoints);
	return alarmTextElement;											
	
	
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getDefaultColorButton().addActionListener(ivjEventHandler);
	getAlarmColorButton().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("AlarmTextElementEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(464, 657);

		java.awt.GridBagConstraints constraintsLinkToPanel = new java.awt.GridBagConstraints();
		constraintsLinkToPanel.gridx = 0; constraintsLinkToPanel.gridy = 0;
		constraintsLinkToPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsLinkToPanel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getLinkToPanel(), constraintsLinkToPanel);

		java.awt.GridBagConstraints constraintsTextPanel = new java.awt.GridBagConstraints();
		constraintsTextPanel.gridx = 0; constraintsTextPanel.gridy = 1;
		constraintsTextPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsTextPanel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getTextPanel(), constraintsTextPanel);

		java.awt.GridBagConstraints constraintsPointSelectionPanel = new java.awt.GridBagConstraints();
		constraintsPointSelectionPanel.gridx = 0; constraintsPointSelectionPanel.gridy = 2;
		constraintsPointSelectionPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsPointSelectionPanel.weightx = 1.0;
		constraintsPointSelectionPanel.weighty = 1.0;
		constraintsPointSelectionPanel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getPointSelectionPanel(), constraintsPointSelectionPanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	getPointSelectionPanel().getIvjDevicePointTree().addTreeSelectionListener(this);
	Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
	for( int i = 0; i < fonts.length; i++ ) {
		getDefaultFontComboBox().addItem(fonts[i].getFontName());
	}

	for( int i = 0; i < availableFontSizes.length; i++ ) {
		getDefaultSizeComboBox().addItem( new Integer(availableFontSizes[i] ));
	}

	for( int i = 0; i < fonts.length; i++ ) {
		getAlarmFontComboBox().addItem(fonts[i].getFontName());
	}

	for( int i = 0; i < availableFontSizes.length; i++ ) {
		getAlarmSizeComboBox().addItem( new Integer(availableFontSizes[i] ));
	}
	
	colorChooser = com.cannontech.esub.util.Util.getJColorChooser();
	// user code end
}
/**
 * Comment
 */
public void jButton1_ActionEvents() {
		javax.swing.JDialog d = javax.swing.JColorChooser.createDialog(this, "Select a color", true, colorChooser, 
		new java.awt.event.ActionListener() { //ok listener
			public void actionPerformed(java.awt.event.ActionEvent e) {
				getAlarmColorButton().setBackground(colorChooser.getColor());				
			}
		},
		new java.awt.event.ActionListener() { //cancel listener
			public void actionPerformed(java.awt.event.ActionEvent e) {
			}
		}
			);

	d.show();
	d.dispose();
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		AlarmTextElementEditorPanel aAlarmIndicatorEditorPanel;
		aAlarmIndicatorEditorPanel = new AlarmTextElementEditorPanel();
		frame.setContentPane(aAlarmIndicatorEditorPanel);
		frame.setSize(aAlarmIndicatorEditorPanel.getSize());
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
 * @param o java.lang.Object
 */
public void setValue(Object o) {
	AlarmTextElement elem = (AlarmTextElement) o;
	
	getLinkToPanel().setLinkTo(elem.getLinkTo());
		
	getDefaultTextTextField().setText(elem.getDefaultText());
	
	for( int i = 0; i < getDefaultFontComboBox().getItemCount(); i++ ) {
		if( getDefaultFontComboBox().getItemAt(i).toString().equalsIgnoreCase(elem.getDefaultTextFont().getFontName()) ) {
			getDefaultFontComboBox().setSelectedIndex(i);
		}
	}

	for( int i = 0; i < getDefaultSizeComboBox().getItemCount(); i++ ) {
		if( ((Integer) getDefaultSizeComboBox().getItemAt(i)).intValue() == elem.getDefaultTextFont().getSize() ) {
			getDefaultSizeComboBox().setSelectedIndex(i);
		}
	}
	
	getAlarmTextTextField().setText(elem.getAlarmText());
		
	for( int i = 0; i < getAlarmFontComboBox().getItemCount(); i++ ) {
		if( getAlarmFontComboBox().getItemAt(i).toString().equalsIgnoreCase(elem.getAlarmTextFont().getFontName()) ) {
			getAlarmFontComboBox().setSelectedIndex(i);
		}
	}

	for( int i = 0; i < getAlarmSizeComboBox().getItemCount(); i++ ) {
		if( ((Integer) getAlarmSizeComboBox().getItemAt(i)).intValue() == elem.getAlarmTextFont().getSize() ) {
			getAlarmSizeComboBox().setSelectedIndex(i);
		}
	}
	
	Color textColor = (java.awt.Color) elem.getDefaultTextColor();
	getDefaultColorButton().setBackground(textColor);
	colorChooser.setColor(textColor);
	
	textColor = elem.getAlarmTextColor();
	getAlarmColorButton().setBackground(textColor);
	colorChooser.setColor(textColor);
	
	getPointSelectionPanel().selectPoints(elem.getPoints());
	
	alarmTextElement = elem;
}

	/** 
	  * Called whenever the value of the selection changes.
	  * @param e the event that characterizes the change.
	  */
public void valueChanged(javax.swing.event.TreeSelectionEvent e) {}
}
