package com.cannontech.yc.gui;

/**
 * Insert the type's description here.
 * Creation date: (10/5/2001 3:29:19 PM)
 * @author: 
 */
public class CommandPanel extends javax.swing.JPanel
{
	private javax.swing.JComboBox availableCommandsComboBox = null;
	private javax.swing.JLabel availableCommandsLabel = null;

	private javax.swing.JComboBox executeCommandComboBox = null;
	private javax.swing.JTextField executeCommandComboBoxTextField = null;
	private javax.swing.JLabel executeCommandLabel = null;

	private javax.swing.JButton executeButton = null;
	private javax.swing.JButton stopButton = null;

	private javax.swing.JSeparator separator = null;
/**
 * CommandPanel constructor comment.
 */
public CommandPanel()
{
	super();
	initialize();
}
/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
public javax.swing.JComboBox getAvailableCommandsComboBox()
{
	if (availableCommandsComboBox == null)
	{
		try
		{
			availableCommandsComboBox = new javax.swing.JComboBox();
			availableCommandsComboBox.setName("AvailableCommandsComboBox");
			availableCommandsComboBox.setToolTipText("Select a common command from the drop down list.");
			//availableCommandsComboBox.setPreferredSize(new java.awt.Dimension(130, 24));
			//availableCommandsComboBox.setMinimumSize(new java.awt.Dimension(126, 24));
			//availableCommandsComboBox.setMaximumSize(new java.awt.Dimension(32767, 24));
			//availableCommandsComboBox.addItemListener(this);
		}
		catch (java.lang.Throwable t)
		{
			t.printStackTrace();
		}
	}
	return availableCommandsComboBox;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getAvailableCommandsLabel()
{
	if (availableCommandsLabel == null)
	{
		try
		{
			availableCommandsLabel = new javax.swing.JLabel("Common Commands:");
			availableCommandsLabel.setName("AvailableCommandsLabel");
			availableCommandsLabel.setFont(new java.awt.Font("dialog", 0, 14));
		}
		catch (java.lang.Throwable t)
		{
			t.printStackTrace();
		}
	}
	return availableCommandsLabel;
}
/**
 * Return the JButton3 property value.
 * @return javax.swing.JButton
 */
public javax.swing.JButton getExecuteButton()
{
	if (executeButton == null)
	{
		try
		{
			executeButton = new javax.swing.JButton("Execute");
			executeButton.setName("ExecuteButton");
			executeButton.setToolTipText("Execute the current command.");
			executeButton.setMaximumSize(new java.awt.Dimension(77, 24));
			executeButton.setPreferredSize(new java.awt.Dimension(77, 24));
			executeButton.setFont(new java.awt.Font("dialog", 0, 12));
			executeButton.setMinimumSize(new java.awt.Dimension(77, 24));
			//executeButton.addActionListener(this);
		}
		catch (java.lang.Throwable t)
		{
			t.printStackTrace();
		}
	}
	return executeButton;
}
/**
 * Return the CommandComboBox property value.
 * @return javax.swing.JComboBox
 */
public javax.swing.JComboBox getExecuteCommandComboBox()
{
	if (executeCommandComboBox == null)
	{
		try
		{
			executeCommandComboBox = new javax.swing.JComboBox();
			executeCommandComboBox.setName("ExecuteCommandComboBox");
			executeCommandComboBox.setToolTipText("Current command to execute.");
			//executeCommandComboBox.setMaximumSize(new java.awt.Dimension(32767, 24));
			//executeCommandComboBox.setPreferredSize(new java.awt.Dimension(122, 24));
			executeCommandComboBox.setEditable(true);
			//executeCommandComboBox.setMinimumSize(new java.awt.Dimension(118, 24));
			//executeCommandComboBox.addItemListener(this);
		}
		catch (java.lang.Throwable t)
		{
			t.printStackTrace();
		}
	}
	return executeCommandComboBox;
}
/**
 * This method was created in VisualAge.
 * @return javax.swing.JTextField
 */
public javax.swing.JTextField getExecuteCommandComboBoxTextField()
{
	if (executeCommandComboBoxTextField == null )
	{
		try
		{
			executeCommandComboBoxTextField = (javax.swing.JTextField) getExecuteCommandComboBox().getEditor().getEditorComponent();
			//executeCommandComboBoxTextField.addKeyListener(this);
			//executeCommandComboBoxTextField.addActionListener(this);
		}
		catch (java.lang.Throwable t)
		{
			t.printStackTrace();
		}
	}
	return executeCommandComboBoxTextField;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getExecuteCommandLabel()
{
	if (executeCommandLabel == null)
	{
		try
		{
			executeCommandLabel = new javax.swing.JLabel("Execute Command:");
			executeCommandLabel.setName("ExecuteCommandLabel");
			executeCommandLabel.setFont(new java.awt.Font("dialog", 0, 14));
		} 
		catch (java.lang.Throwable t)
		{
			t.printStackTrace();
		}
	}
	return executeCommandLabel;
}
/**
 * Return the JSeparator1 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getSeparatorRightPane()
{
	if (separator == null)
	{
		try
		{
			separator = new javax.swing.JSeparator();
			separator.setName("SeparatorRightPane");
			separator.setPreferredSize(new java.awt.Dimension(0, 3));
			separator.setBounds(20, 767, 604, 3);
			separator.setMinimumSize(new java.awt.Dimension(1, 3));
		}
		catch (java.lang.Throwable t)
		{
			t.printStackTrace();
		}
	}
	return separator;
}
/**
 * Return the StopButton property value.
 * @return javax.swing.JButton
 */
public javax.swing.JButton getStopButton()
{
	if (stopButton == null)
	{
		try
		{
			stopButton = new javax.swing.JButton("Stop");
			stopButton.setName("StopButton");
			stopButton.setToolTipText("Stop queued command execution on port control service.");
			stopButton.setMaximumSize(new java.awt.Dimension(77, 24));
			stopButton.setPreferredSize(new java.awt.Dimension(77, 24));
			stopButton.setFont(new java.awt.Font("dialog", 0, 12));
			stopButton.setMinimumSize(new java.awt.Dimension(77, 24));
			//stopButton.addActionListener(this);
		}
		catch (java.lang.Throwable t)
		{
			t.printStackTrace();
		}
	}
	return stopButton;
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/2001 3:31:04 PM)
 */
private void initialize()
{
	try
	{
		this.setName("CommandPanel");
		this.setLayout(new java.awt.GridBagLayout());

		java.awt.GridBagConstraints constraintsExecuteCommandLabel = new java.awt.GridBagConstraints();
		constraintsExecuteCommandLabel.gridx = 0; constraintsExecuteCommandLabel.gridy = 1;
		constraintsExecuteCommandLabel.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsExecuteCommandLabel.insets = new java.awt.Insets(0, 12, 5, 0);
		this.add(getExecuteCommandLabel(), constraintsExecuteCommandLabel);

		java.awt.GridBagConstraints constraintsExecuteButton = new java.awt.GridBagConstraints();
		constraintsExecuteButton.gridx = 2; constraintsExecuteButton.gridy = 1;
		constraintsExecuteButton.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsExecuteButton.insets = new java.awt.Insets(0, 0, 0, 5);
		this.add(getExecuteButton(), constraintsExecuteButton);

		java.awt.GridBagConstraints constraintsStopButton = new java.awt.GridBagConstraints();
		constraintsStopButton.gridx = 3; constraintsStopButton.gridy = 1;
		constraintsStopButton.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsStopButton.insets = new java.awt.Insets(0, 0, 0, 5);
		this.add(getStopButton(), constraintsStopButton);

		java.awt.GridBagConstraints constraintsAvailableCommandsLabel = new java.awt.GridBagConstraints();
		constraintsAvailableCommandsLabel.gridx = 0; constraintsAvailableCommandsLabel.gridy = 0;
		constraintsAvailableCommandsLabel.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsAvailableCommandsLabel.insets = new java.awt.Insets(5, 12, 5, 0);
		this.add(getAvailableCommandsLabel(), constraintsAvailableCommandsLabel);

		java.awt.GridBagConstraints constraintsExecuteCommandComboBox = new java.awt.GridBagConstraints();
		constraintsExecuteCommandComboBox.gridx = 1; constraintsExecuteCommandComboBox.gridy = 1;
		constraintsExecuteCommandComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsExecuteCommandComboBox.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsExecuteCommandComboBox.weightx = 1.0;
		constraintsExecuteCommandComboBox.insets = new java.awt.Insets(0, 5, 5, 5);
		this.add(getExecuteCommandComboBox(), constraintsExecuteCommandComboBox);

		java.awt.GridBagConstraints constraintsAvailableCommandsComboBox = new java.awt.GridBagConstraints();
		constraintsAvailableCommandsComboBox.gridx = 1; constraintsAvailableCommandsComboBox.gridy = 0;
		constraintsAvailableCommandsComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsAvailableCommandsComboBox.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsAvailableCommandsComboBox.weightx = 1.0;
		constraintsAvailableCommandsComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
		this.add(getAvailableCommandsComboBox(), constraintsAvailableCommandsComboBox);

		java.awt.GridBagConstraints constraintsSeparator = new java.awt.GridBagConstraints();
		constraintsSeparator.gridy = 2;
		constraintsSeparator.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		constraintsSeparator.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsSeparator.ipadx = 10;
		constraintsSeparator.insets = new java.awt.Insets(0, 5, 5, 5);
		add(getSeparatorRightPane(), constraintsSeparator);

	}
	catch (java.lang.Throwable t)
	{
		t.printStackTrace();
	}
}
}
