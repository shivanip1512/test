package com.cannontech.clientutils.commander;

/**
 * Insert the type's description here.
 * Creation date: (10/11/2001 1:59:07 PM)
 * @author: 
 */
public class CommandLogPanel extends javax.swing.JPanel
{
	private javax.swing.JLabel commandLogPanelLabel = null;
	private javax.swing.JScrollPane commandLogPanelScrollPane = null;
	private javax.swing.JList commandLogPanelScrollPaneList = null;
/**
 * CommandLogPanel constructor comment.
 */
public CommandLogPanel()
{
	super();
	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (10/15/2001 11:03:05 AM)
 * @param stringObject java.lang.Object
 */
public void addLogElement(Object stringObject)
{
	((javax.swing.DefaultListModel)getCommandLogPanelScrollPaneList().getModel()).addElement( stringObject );

	int position = getCommandLogPanelScrollPaneList().getModel().getSize();
	java.awt.Rectangle cellBounds = getCommandLogPanelScrollPaneList().getCellBounds(position-1,position-1);
		
	if( cellBounds != null )
	{
		// 2* so that you get bottom of cell
		cellBounds.translate(0,2*cellBounds.height);
		getCommandLogPanelScrollPaneList().scrollRectToVisible( cellBounds );
	}

}
/**
 * Return the MessagePanelLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getCommandLogPanelLabel()
{
	if (commandLogPanelLabel == null)
	{
		try
		{
			commandLogPanelLabel = new javax.swing.JLabel();
			commandLogPanelLabel.setName("CommandLogPanelLabel");
			commandLogPanelLabel.setText("Command Log");
		}
		catch (java.lang.Throwable t)
		{
			t.printStackTrace();
		}
	}
	return commandLogPanelLabel;
}
/**
 * Return the MessageScrollPane property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getCommandLogPanelScrollPane()
{
	if (commandLogPanelScrollPane == null)
	{
		try
		{
			commandLogPanelScrollPane = new javax.swing.JScrollPane();
			commandLogPanelScrollPane.setName("CommandLogPanelScrollPane");
			getCommandLogPanelScrollPane().setViewportView(getCommandLogPanelScrollPaneList());
		}
		catch (java.lang.Throwable t)
		{
			t.printStackTrace();
		}
	}
	return commandLogPanelScrollPane;
}
/**
 * Return the MessageScrollPaneList property value.
 * @return javax.swing.JList
 */
public javax.swing.JList getCommandLogPanelScrollPaneList()
{
	if (commandLogPanelScrollPaneList == null)
	{
		try
		{
			commandLogPanelScrollPaneList = new javax.swing.JList(new javax.swing.DefaultListModel());
			commandLogPanelScrollPaneList.setName("CommandLogPanelScrollPaneList");
			commandLogPanelScrollPaneList.setBounds(0, 0, 160, 120);
		}
		catch (java.lang.Throwable t)
		{
			t.printStackTrace();
		}
	}
	return commandLogPanelScrollPaneList;
}
/**
 * Return the MessagePanel property value.
 * @return javax.swing.JPanel
 */
private void initialize()
{
	try 
	{
		setName("CommandLogPanel");
		setPreferredSize(new java.awt.Dimension(575, 90));
		setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		setLayout(new java.awt.BorderLayout());
		setAlignmentY(java.awt.Component.CENTER_ALIGNMENT);
		add(getCommandLogPanelLabel(), "North");
		add(getCommandLogPanelScrollPane(), "Center");
	}
	catch (java.lang.Throwable t)
	{
		t.printStackTrace();
	}
}
}
