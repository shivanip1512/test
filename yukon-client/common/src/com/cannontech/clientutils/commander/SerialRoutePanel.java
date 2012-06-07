package com.cannontech.clientutils.commander;

/**
 * Insert the type's description here.
 * Creation date: (10/5/2001 11:36:04 AM)
 * @author: 
 */
public class SerialRoutePanel extends javax.swing.JPanel
{
	private javax.swing.JComboBox routeComboBox = null;
	private javax.swing.JLabel routeLabel = null;
	private javax.swing.JTextField serialTextField = null;
	private javax.swing.JLabel serialNumberLabel = null;
	private javax.swing.JSeparator separator = null;
		
/**
 * SerialNumberRouteEditor constructor comment.
 */
public SerialRoutePanel() {
	super();
	initialize();
}
/**
 * Return the RouteComboBox property value.
 * @return javax.swing.JComboBox
 */
public javax.swing.JComboBox getRouteComboBox()
{
	if (routeComboBox == null)
	{
		try
		{
			routeComboBox = new javax.swing.JComboBox();
			routeComboBox.setName("RouteComboBox");
			routeComboBox.setToolTipText("Select a specific route.");
			routeComboBox.setMaximumRowCount(10);
			//routeComboBox.setMaximumSize(new java.awt.Dimension(32767, 24));
			//routeComboBox.setPreferredSize(new java.awt.Dimension(130, 24));
			routeComboBox.setEditable(false);
			//routeComboBox.setMinimumSize(new java.awt.Dimension(126, 24));
		}
		catch (java.lang.Throwable t)
		{
			t.printStackTrace();
		}
	}
	return routeComboBox;
}
/**
 * Return the RouteLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getRouteLabel()
{
	if (routeLabel == null)
	{
		try
		{
			routeLabel = new javax.swing.JLabel();
			routeLabel.setName("RouteLabel");
			routeLabel.setText("Route #:");
			routeLabel.setFont(new java.awt.Font("dialog", 0, 14));
		}
		catch (java.lang.Throwable t)
		{
			t.printStackTrace();
		}
	}
	return routeLabel;
}
/**
 * Return the LeftPaneSeparator property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getSeparatorLeftPane()
{
	if (separator == null)
	{
		try
		{
			separator = new javax.swing.JSeparator();
			separator.setName("SeparatorLeftPane");
			separator.setBounds(250, 930, 208, 555);
		}
		catch (java.lang.Throwable t)
		{
			t.printStackTrace();
		}
	}
	return separator;
}
/**
 * Return the SerialNumberLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getSerialNumberLabel()
{
	if (serialNumberLabel == null)
	{
		try
		{
			serialNumberLabel = new javax.swing.JLabel();
			serialNumberLabel.setName("SerialNumberLabel");
			serialNumberLabel.setText("Serial #:");
			serialNumberLabel.setFont(new java.awt.Font("dialog", 0, 14));
		}
		catch (java.lang.Throwable t)
		{
			t.printStackTrace();
		}
	}
	return serialNumberLabel;
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/2001 3:17:57 PM)
 * @return java.lang.String
 */
public String getSerialNumberText()
{
	return getSerialTextField().getText().trim();
}
/**
 * Return the SerialTextField property value.
 * @return javax.swing.JTextField
 */
public javax.swing.JTextField getSerialTextField()
{
	if (serialTextField == null)
	{
		try
		{
			serialTextField = new javax.swing.JTextField();
			serialTextField.setName("SerialTextField");
		}
		catch (java.lang.Throwable t)
		{
			t.printStackTrace();
		}
	}
	return serialTextField;
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/2001 12:31:53 PM)
 */
private void initialize()
{
	try
	{
		this.setName("SerialRoutePanel");
		this.setLayout(new java.awt.GridBagLayout());

		java.awt.GridBagConstraints constraintsSerialNumberLabel = new java.awt.GridBagConstraints();
		constraintsSerialNumberLabel.gridx = 0; constraintsSerialNumberLabel.gridy = 0;
		constraintsSerialNumberLabel.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsSerialNumberLabel.ipadx = 3;
		constraintsSerialNumberLabel.ipady = -2;
		constraintsSerialNumberLabel.insets = new java.awt.Insets(5, 5, 5, 3);
		add(getSerialNumberLabel(), constraintsSerialNumberLabel);

		java.awt.GridBagConstraints constraintsSerialTextField = new java.awt.GridBagConstraints();
		constraintsSerialTextField.gridx = 1; constraintsSerialTextField.gridy = 0;
		constraintsSerialTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsSerialTextField.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsSerialTextField.weightx = 1.0;
		constraintsSerialTextField.insets = new java.awt.Insets(5, 5, 5, 0);
		add(getSerialTextField(), constraintsSerialTextField);

		java.awt.GridBagConstraints constraintsRouteLabel = new java.awt.GridBagConstraints();
		constraintsRouteLabel.gridx = 0; constraintsRouteLabel.gridy = 1;
		constraintsRouteLabel.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsRouteLabel.ipadx = 3;
		constraintsRouteLabel.ipady = -2;
		constraintsRouteLabel.insets = new java.awt.Insets(0, 3, 5, 3);
		add(getRouteLabel(), constraintsRouteLabel);

		java.awt.GridBagConstraints constraintsRouteComboBox = new java.awt.GridBagConstraints();
		constraintsRouteComboBox.gridx = 1; constraintsRouteComboBox.gridy = 1;
		constraintsRouteComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsRouteComboBox.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsRouteComboBox.weightx = 1.0;
		constraintsRouteComboBox.ipadx = 10;
		constraintsRouteComboBox.insets = new java.awt.Insets(0, 5, 5, 0);
		add(getRouteComboBox(), constraintsRouteComboBox);

		java.awt.GridBagConstraints constraintsSeparator = new java.awt.GridBagConstraints();
		constraintsSeparator.gridy = 2;
		constraintsSeparator.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		constraintsSeparator.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsSeparator.ipadx = 10;
		constraintsSeparator.insets = new java.awt.Insets(0, 5, 5,5);
		add(getSeparatorLeftPane(), constraintsSeparator);
		
	}
	catch (java.lang.Throwable t)
	{
		t.printStackTrace();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/2001 2:58:20 PM)
 * @param value boolean
 */
public void setObjectsEnabled(boolean value)
{
	getRouteComboBox().setEnabled(value);
	getRouteLabel().setEnabled(value);
	getSerialNumberLabel().setEnabled(value);
	getSerialTextField().setEnabled(value);
}
/**
 * Insert the method's description here.
 * Creation date: (10/5/2001 3:24:05 PM)
 * @param serialNumber java.lang.String
 */
public void setSerialNumberText(String serialNumber)
{
	getSerialTextField().setText( serialNumber );
}
}
