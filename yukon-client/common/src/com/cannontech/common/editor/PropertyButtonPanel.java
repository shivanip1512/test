package com.cannontech.common.editor;

/**
 * This type was created in VisualAge.
 */
public class PropertyButtonPanel extends javax.swing.JPanel {
	private javax.swing.JPanel ivjButtonPanel = null;
	private javax.swing.JButton ivjApplyJButton = null;
	private javax.swing.JButton ivjCancelJButton = null;
	private javax.swing.JButton ivjOkJButton = null;
	private java.awt.BorderLayout ivjPropertyButtonPanelBorderLayout = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PropertyButtonPanel() {
	super();
	initialize();
}
/**
 * Return the ApplyButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JButton getApplyJButton() {
	if (ivjApplyJButton == null) {
		try {
			ivjApplyJButton = new javax.swing.JButton();
			ivjApplyJButton.setName("ApplyJButton");
			ivjApplyJButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjApplyJButton.setText("Apply");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjApplyJButton;
}

/**
 * Return the ButtonPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getButtonPanel() {
	if (ivjButtonPanel == null) {
		try {
			ivjButtonPanel = new javax.swing.JPanel();
			ivjButtonPanel.setName("ButtonPanel");
			ivjButtonPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsOkJButton = new java.awt.GridBagConstraints();
			constraintsOkJButton.gridx = 0; constraintsOkJButton.gridy = 0;
			getButtonPanel().add(getOkJButton(), constraintsOkJButton);

			java.awt.GridBagConstraints constraintsCancelJButton = new java.awt.GridBagConstraints();
			constraintsCancelJButton.gridx = 1; constraintsCancelJButton.gridy = 0;
			constraintsCancelJButton.insets = new java.awt.Insets(0, 10, 0, 10);
			getButtonPanel().add(getCancelJButton(), constraintsCancelJButton);

			java.awt.GridBagConstraints constraintsApplyJButton = new java.awt.GridBagConstraints();
			constraintsApplyJButton.gridx = 2; constraintsApplyJButton.gridy = 0;
			constraintsApplyJButton.insets = new java.awt.Insets(0, 0, 0, 10);
			getButtonPanel().add(getApplyJButton(), constraintsApplyJButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonPanel;
}
/**
 * Return the CancelButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JButton getCancelJButton() {
	if (ivjCancelJButton == null) {
		try {
			ivjCancelJButton = new javax.swing.JButton();
			ivjCancelJButton.setName("CancelJButton");
			ivjCancelJButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjCancelJButton.setText("Cancel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCancelJButton;
}
/**
 * Return the OkButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JButton getOkJButton() {
	if (ivjOkJButton == null) {
		try {
			ivjOkJButton = new javax.swing.JButton();
			ivjOkJButton.setName("OkJButton");
			ivjOkJButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjOkJButton.setText("OK");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOkJButton;
}
/**
 * Return the PropertyButtonPanelBorderLayout property value.
 * @return java.awt.BorderLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.BorderLayout getPropertyButtonPanelBorderLayout() {
	java.awt.BorderLayout ivjPropertyButtonPanelBorderLayout = null;
	try {
		/* Create part */
		ivjPropertyButtonPanelBorderLayout = new java.awt.BorderLayout();
		ivjPropertyButtonPanelBorderLayout.setVgap(0);
		ivjPropertyButtonPanelBorderLayout.setHgap(0);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjPropertyButtonPanelBorderLayout;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PropertyButtonPanel");
		setPreferredSize(new java.awt.Dimension(233, 55));
		setLayout(getPropertyButtonPanelBorderLayout());
		setSize(413, 65);
		setMinimumSize(new java.awt.Dimension(233, 55));
		add(getButtonPanel(), "East");
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame;
		try {
			Class aFrameClass = Class.forName("com.ibm.uvm.abt.edit.TestFrame");
			frame = (java.awt.Frame)aFrameClass.newInstance();
		} catch (java.lang.Throwable ivjExc) {
			frame = new java.awt.Frame();
		}
		PropertyButtonPanel aPropertyButtonPanel;
		aPropertyButtonPanel = new PropertyButtonPanel();
		frame.add("Center", aPropertyButtonPanel);
		frame.setSize(aPropertyButtonPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
}
