package com.cannontech.common.gui.tree;
/**
 * Insert the type's description here.
 * Creation date: (11/21/00 4:08:38 PM)
 * @author: 
 */
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TextFieldDocument;

public class TreeFindPanel extends DataInputPanel implements KeyListener
{
	private javax.swing.JLabel ivjJLabelText = null;
	private javax.swing.JTextField ivjJTextFieldText = null;

	/**
	 * QuickContactPanel constructor comment.
	 */
	public TreeFindPanel() {
		super();
		initialize();
	}
	
	
	 public void keyTyped(KeyEvent e)
	 {

	 	if( e.getKeyChar() == KeyEvent.VK_ENTER )
 		{
			//forward an OK event on
 			fireInputDataPanelEvent(
 				new PropertyPanelEvent(this, PropertyPanelEvent.OK_SELECTION) );
 		}

		if( e.getKeyChar() == KeyEvent.VK_ESCAPE )
		{
			//forward an CANCEL event on
			fireInputDataPanelEvent(
				new PropertyPanelEvent(this, PropertyPanelEvent.CANCEL_SELECTION) );
		}

	 }

	 public void keyPressed(KeyEvent e) {}
	 public void keyReleased(KeyEvent e) {}

	
	/**
	 * Return the NameLabel property value.
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelFirstName() 
	{
		if (ivjJLabelText == null) {
			try {
				ivjJLabelText = new javax.swing.JLabel();
				ivjJLabelText.setName("JLabelText");
				ivjJLabelText.setFont(new java.awt.Font("dialog", 0, 14));
				ivjJLabelText.setText("Text:");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJLabelText;
	}
	
	/**
	 * Return the JTextField1 property value.
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextFieldText() {
		if (ivjJTextFieldText == null) {
			try {
				ivjJTextFieldText = new javax.swing.JTextField();
				ivjJTextFieldText.setName("JTextFieldText");
				// user code begin {1}
				
				ivjJTextFieldText.setSize(100, 100);
				ivjJTextFieldText.setPreferredSize( new Dimension(190, 20) );
				ivjJTextFieldText.setDocument(
								new com.cannontech.common.gui.util.TextFieldDocument(
										com.cannontech.common.gui.util.TextFieldDocument.STRING_LENGTH_100));
				
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJTextFieldText;
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Object
	 * @param o java.lang.Object
	 */
	public Object getValue(Object val)
	{
		return getJTextFieldText().getText();
	}
	
	
	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {
	
		/* Uncomment the following lines to print uncaught exceptions to stdout */
		com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
	
	
	/**
	 * Initialize the class.
	 */
	private void initialize() 
	{
		setName("DestinationLocationInfoPanel");
		setLayout(new FlowLayout());
		//setSize(300, 50);


		add( getJLabelFirstName(), FlowLayout.LEFT);
		add( getJTextFieldText(), FlowLayout.CENTER );	
		
		
		//add listeners
		getJTextFieldText().addKeyListener( this );
	}
	
	/**
	 * This method was created in VisualAge.
	 * @param o java.lang.Object
	 */
	public void setValue(Object val) 
	{
		getJTextFieldText().requestFocus();

		if( val == null )
			return;

		getJTextFieldText().setText( val.toString() );
	}
}