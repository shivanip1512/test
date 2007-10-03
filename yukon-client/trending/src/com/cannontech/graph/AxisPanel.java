package com.cannontech.graph;

/**
 * Insert the type's description here.
 * Creation date: (1/4/2002 3:55:34 PM)
 * @author: 
 */

import java.awt.event.ItemEvent;

public class AxisPanel extends javax.swing.JPanel implements java.awt.event.ItemListener {
	private javax.swing.JPanel ivjLeftAxisPanel = null;
	private javax.swing.JPanel ivjRightAxisPanel = null;
	private javax.swing.JLabel ivjLeftMaxLabel = null;
	private javax.swing.JLabel ivjLeftMinLabel = null;
	private javax.swing.JLabel ivjRightMaxLabel = null;
	private javax.swing.JLabel ivjRightMinLabel = null;
	private javax.swing.JTextField ivjLeftMaxTextField = null;
	private javax.swing.JTextField ivjLeftMinTextField = null;
	private javax.swing.JTextField ivjRightMaxTextField = null;
	private javax.swing.JTextField ivjRightMinTextField = null;
	private javax.swing.JCheckBox ivjLeftAutoScalingCheckBox = null;
	private javax.swing.JCheckBox ivjRightAutoScalingCheckBox = null;
/**
 * AxisPanel constructor comment.
 */
public AxisPanel() {
	super();
	initialize();
}

/**
 * Return the JCheckBox1 property value.
 * @return javax.swing.JCheckBox
 */
public javax.swing.JCheckBox getLeftAutoScalingCheckBox() {
	if (ivjLeftAutoScalingCheckBox == null) {
		try {
			ivjLeftAutoScalingCheckBox = new javax.swing.JCheckBox();
			ivjLeftAutoScalingCheckBox.setName("LeftAutoScalingCheckBox");
			ivjLeftAutoScalingCheckBox.setSelected(true);
			ivjLeftAutoScalingCheckBox.setText("Auto Scaling");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLeftAutoScalingCheckBox;
}
/**
 * Return the LeftAxisPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getLeftAxisPanel() {
	if (ivjLeftAxisPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitle("Left Axis");
			ivjLeftAxisPanel = new javax.swing.JPanel();
			ivjLeftAxisPanel.setName("LeftAxisPanel");
			ivjLeftAxisPanel.setBorder(ivjLocalBorder);
			ivjLeftAxisPanel.setLayout(new java.awt.GridBagLayout());
			ivjLeftAxisPanel.setAlignmentY(java.awt.Component.CENTER_ALIGNMENT);
			ivjLeftAxisPanel.setComponentOrientation(java.awt.ComponentOrientation.UNKNOWN);

			java.awt.GridBagConstraints constraintsLeftAutoScalingCheckBox = new java.awt.GridBagConstraints();
			constraintsLeftAutoScalingCheckBox.gridx = 0; constraintsLeftAutoScalingCheckBox.gridy = 0;
			constraintsLeftAutoScalingCheckBox.gridwidth = 2;
			constraintsLeftAutoScalingCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsLeftAutoScalingCheckBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getLeftAxisPanel().add(getLeftAutoScalingCheckBox(), constraintsLeftAutoScalingCheckBox);

			java.awt.GridBagConstraints constraintsLeftMinLabel = new java.awt.GridBagConstraints();
			constraintsLeftMinLabel.gridx = 0; constraintsLeftMinLabel.gridy = 1;
			constraintsLeftMinLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getLeftAxisPanel().add(getLeftMinLabel(), constraintsLeftMinLabel);

			java.awt.GridBagConstraints constraintsLeftMaxLabel = new java.awt.GridBagConstraints();
			constraintsLeftMaxLabel.gridx = 0; constraintsLeftMaxLabel.gridy = 2;
			constraintsLeftMaxLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getLeftAxisPanel().add(getLeftMaxLabel(), constraintsLeftMaxLabel);

			java.awt.GridBagConstraints constraintsLeftMinTextField = new java.awt.GridBagConstraints();
			constraintsLeftMinTextField.gridx = 1; constraintsLeftMinTextField.gridy = 1;
			constraintsLeftMinTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsLeftMinTextField.weightx = 1.0;
			constraintsLeftMinTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getLeftAxisPanel().add(getLeftMinTextField(), constraintsLeftMinTextField);

			java.awt.GridBagConstraints constraintsLeftMaxTextField = new java.awt.GridBagConstraints();
			constraintsLeftMaxTextField.gridx = 1; constraintsLeftMaxTextField.gridy = 2;
			constraintsLeftMaxTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsLeftMaxTextField.weightx = 1.0;
			constraintsLeftMaxTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getLeftAxisPanel().add(getLeftMaxTextField(), constraintsLeftMaxTextField);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLeftAxisPanel;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLeftMaxLabel() {
	if (ivjLeftMaxLabel == null) {
		try {
			ivjLeftMaxLabel = new javax.swing.JLabel();
			ivjLeftMaxLabel.setName("LeftMaxLabel");
			ivjLeftMaxLabel.setText("Max:");
			ivjLeftMaxLabel.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLeftMaxLabel;
}
/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
public javax.swing.JTextField getLeftMaxTextField() {
	if (ivjLeftMaxTextField == null) {
		try {
			ivjLeftMaxTextField = new javax.swing.JTextField();
			ivjLeftMaxTextField.setName("LeftMaxTextField");
			ivjLeftMaxTextField.setPreferredSize(new java.awt.Dimension(44, 20));
            		ivjLeftMaxTextField.setMinimumSize(new java.awt.Dimension(44, 20));
			ivjLeftMaxTextField.setText("100.0");
			ivjLeftMaxTextField.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLeftMaxTextField;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLeftMinLabel() {
	if (ivjLeftMinLabel == null) {
		try {
			ivjLeftMinLabel = new javax.swing.JLabel();
			ivjLeftMinLabel.setName("LeftMinLabel");
			ivjLeftMinLabel.setText("Min:");
			ivjLeftMinLabel.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLeftMinLabel;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JTextField getLeftMinTextField() {
	if (ivjLeftMinTextField == null) {
		try {
			ivjLeftMinTextField = new javax.swing.JTextField();
			ivjLeftMinTextField.setName("LeftMinTextField");
			ivjLeftMinTextField.setPreferredSize(new java.awt.Dimension(44, 20));
            		ivjLeftMinTextField.setMinimumSize(new java.awt.Dimension(44, 20));
			ivjLeftMinTextField.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjLeftMinTextField.setText("0.0");
			ivjLeftMinTextField.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLeftMinTextField;
}
/**
 * Return the JCheckBox2 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBox getRightAutoScalingCheckBox() {
	if (ivjRightAutoScalingCheckBox == null) {
		try {
			ivjRightAutoScalingCheckBox = new javax.swing.JCheckBox();
			ivjRightAutoScalingCheckBox.setName("RightAutoScalingCheckBox");
			ivjRightAutoScalingCheckBox.setSelected(true);
			ivjRightAutoScalingCheckBox.setText("Auto Scaling");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRightAutoScalingCheckBox;
}
/**
 * Return the RightAxisPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getRightAxisPanel() {
	if (ivjRightAxisPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitle("Right Axis");
			ivjRightAxisPanel = new javax.swing.JPanel();
			ivjRightAxisPanel.setName("RightAxisPanel");
			ivjRightAxisPanel.setAlignmentX(java.awt.Component.RIGHT_ALIGNMENT);
			ivjRightAxisPanel.setLayout(new java.awt.GridBagLayout());
			ivjRightAxisPanel.setAlignmentY(java.awt.Component.CENTER_ALIGNMENT);
			ivjRightAxisPanel.setBorder(ivjLocalBorder1);

			java.awt.GridBagConstraints constraintsRightAutoScalingCheckBox = new java.awt.GridBagConstraints();
			constraintsRightAutoScalingCheckBox.gridx = 0; constraintsRightAutoScalingCheckBox.gridy = 0;
			constraintsRightAutoScalingCheckBox.gridwidth = 0;
			constraintsRightAutoScalingCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRightAutoScalingCheckBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getRightAxisPanel().add(getRightAutoScalingCheckBox(), constraintsRightAutoScalingCheckBox);

			java.awt.GridBagConstraints constraintsRightMinLabel = new java.awt.GridBagConstraints();
			constraintsRightMinLabel.gridx = 0; constraintsRightMinLabel.gridy = 1;
			constraintsRightMinLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getRightAxisPanel().add(getRightMinLabel(), constraintsRightMinLabel);

			java.awt.GridBagConstraints constraintsRightMaxLabel = new java.awt.GridBagConstraints();
			constraintsRightMaxLabel.gridx = 0; constraintsRightMaxLabel.gridy = 2;
			constraintsRightMaxLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getRightAxisPanel().add(getRightMaxLabel(), constraintsRightMaxLabel);

			java.awt.GridBagConstraints constraintsRightMinTextField = new java.awt.GridBagConstraints();
			constraintsRightMinTextField.gridx = 1; constraintsRightMinTextField.gridy = 1;
			constraintsRightMinTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRightMinTextField.weightx = 1.0;
			constraintsRightMinTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getRightAxisPanel().add(getRightMinTextField(), constraintsRightMinTextField);

			java.awt.GridBagConstraints constraintsRightMaxTextField = new java.awt.GridBagConstraints();
			constraintsRightMaxTextField.gridx = 1; constraintsRightMaxTextField.gridy = 2;
			constraintsRightMaxTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRightMaxTextField.weightx = 1.0;
			constraintsRightMaxTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getRightAxisPanel().add(getRightMaxTextField(), constraintsRightMaxTextField);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRightAxisPanel;
}
/**
 * Return the JLabel4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRightMaxLabel() {
	if (ivjRightMaxLabel == null) {
		try {
			ivjRightMaxLabel = new javax.swing.JLabel();
			ivjRightMaxLabel.setName("RightMaxLabel");
			ivjRightMaxLabel.setText("Max:");
			ivjRightMaxLabel.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRightMaxLabel;
}
/**
 * Return the JTextField4 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JTextField getRightMaxTextField() {
	if (ivjRightMaxTextField == null) {
		try {
			ivjRightMaxTextField = new javax.swing.JTextField();
			ivjRightMaxTextField.setName("RightMaxTextField");
			ivjRightMaxTextField.setPreferredSize(new java.awt.Dimension(44, 20));
            		ivjRightMaxTextField.setMinimumSize(new java.awt.Dimension(44, 20));
			ivjRightMaxTextField.setText("100.0");
			ivjRightMaxTextField.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRightMaxTextField;
}
/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRightMinLabel() {
	if (ivjRightMinLabel == null) {
		try {
			ivjRightMinLabel = new javax.swing.JLabel();
			ivjRightMinLabel.setName("RightMinLabel");
			ivjRightMinLabel.setText("Min:");
			ivjRightMinLabel.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRightMinLabel;
}
/**
 * Return the JTextField3 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JTextField getRightMinTextField() {
	if (ivjRightMinTextField == null) {
		try {
			ivjRightMinTextField = new javax.swing.JTextField();
			ivjRightMinTextField.setName("RightMinTextField");
			ivjRightMinTextField.setPreferredSize(new java.awt.Dimension(44, 20));
            		ivjRightMinTextField.setMinimumSize(new java.awt.Dimension(44, 20));
			ivjRightMinTextField.setText("0.0");
			ivjRightMinTextField.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRightMinTextField;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
private void initConnections() throws java.lang.Exception
{
	getLeftAutoScalingCheckBox().addItemListener(this);
	getRightAutoScalingCheckBox().addItemListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("AxisPanel");
		setLayout(new java.awt.GridLayout());
		setSize(387, 152);
		add(getLeftAxisPanel(), getLeftAxisPanel().getName());
		add(getRightAxisPanel(), getRightAxisPanel().getName());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	try
	{
		initConnections();
	}
	catch (Exception e)
	{
		com.cannontech.clientutils.CTILogger.info(" Error initializing Event listeners");
		e.printStackTrace();
	}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (1/7/2002 11:05:58 AM)
 * @param event java.awt.event.ItemEvent
 */
public void itemStateChanged(ItemEvent event)
{
	if (event.getSource() == getLeftAutoScalingCheckBox() )
	{
		boolean enable = !getLeftAutoScalingCheckBox().isSelected();
		getLeftMinLabel().setEnabled(enable);
		getLeftMinTextField().setEnabled(enable);
		getLeftMaxLabel().setEnabled(enable);
		getLeftMaxTextField().setEnabled(enable);

	}
	
	else if (event.getSource() == getRightAutoScalingCheckBox())
	{
		boolean enable = !getRightAutoScalingCheckBox().isSelected();
		getRightMinLabel().setEnabled(enable);
		getRightMinTextField().setEnabled(enable);
		getRightMaxLabel().setEnabled(enable);
		getRightMaxTextField().setEnabled(enable);
	}
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		AxisPanel aAxisPanel;
		aAxisPanel = new AxisPanel();
		frame.setContentPane(aAxisPanel);
		frame.setSize(aAxisPanel.getSize());
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
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}
