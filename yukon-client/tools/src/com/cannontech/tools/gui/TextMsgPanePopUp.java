package com.cannontech.tools.gui;

/**
 * Insert the type's description here.
 * Creation date: (1/5/2001 4:26:25 PM)
 * @author: 
 */
import javax.swing.JTextArea;

public class TextMsgPanePopUp extends javax.swing.JPopupMenu implements java.awt.event.ActionListener
{
	private javax.swing.JMenuItem jMenuItemClear = null;
	private javax.swing.JMenuItem jMenuItemAddLine = null;

	private JTextArea txtArea = null;

	/**
	 * TextMsgPanePopUp constructor comment.
	 */
	public TextMsgPanePopUp() {
		super();
		initialize();
	}

	/**
	 * Method to handle events for the ActionListener interface.
	 * @param e java.awt.event.ActionEvent
	 */
	public void actionPerformed(java.awt.event.ActionEvent e) 
	{		
		//all actions should affect the txtArea component
		if( txtArea == null )
			return;

		if (e.getSource() == getjMenuItemClear() ) 
			txtArea.setText( "" );

		if (e.getSource() == getjMenuItemAddLine() ) 
			txtArea.append( System.getProperty("line.separator") );
	}

	/**
	 * Return the jMenuItemClear property value.
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getjMenuItemClear() 
	{
		if (jMenuItemClear == null) 
		{
			jMenuItemClear= new javax.swing.JMenuItem();
			jMenuItemClear.setName("jMenuItemClear");
			jMenuItemClear.setText("Clear");
			jMenuItemClear.setMnemonic('c');
		}

		return jMenuItemClear;
	}

	/**
	 * Return the jMenuItemClear property value.
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getjMenuItemAddLine() 
	{
		if (jMenuItemAddLine == null) 
		{
			jMenuItemAddLine = new javax.swing.JMenuItem();
			jMenuItemAddLine.setName("jMenuItemAddLine");
			jMenuItemAddLine.setText("Add Line");
			jMenuItemAddLine.setMnemonic('a');
		}

		return jMenuItemAddLine;
	}

	/**
	 * Initializes connections
	 */
	private void initConnections() 
	{
		getjMenuItemClear().addActionListener(this);
		getjMenuItemAddLine().addActionListener(this);
	}

	public void setTextArea( JTextArea txtArea_ )
	{
		txtArea = txtArea_;
	}
	
	/**
	 * Initialize the class.
	 */
	private void initialize() 
	{
		setName("FeederPopUp");
		add( getjMenuItemClear() );
		add( getjMenuItemAddLine() );

		initConnections();
	}

}
