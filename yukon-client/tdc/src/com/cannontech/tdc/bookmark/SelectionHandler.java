package com.cannontech.tdc.bookmark;

/**
 * Insert the type's description here.
 * Creation date: (4/11/00 1:42:27 PM)
 * @author: 
 * @Version: <version>
 */
public class SelectionHandler implements java.awt.event.ActionListener 
{
	BookMarkSelectionListener listener = null;	
/**
 * SelectionHandler constructor comment.
 */
public SelectionHandler() {
	super();
}
/**
 * SelectionHandler constructor comment.
 */
public SelectionHandler( BookMarkSelectionListener bookMarkListener )
{	
	super();

	listener = bookMarkListener;
}
/**
 * actionPerformed method comment.
 */
public void actionPerformed(java.awt.event.ActionEvent e) 
{
	if( e.getSource() instanceof javax.swing.JRadioButtonMenuItem )
	{
		javax.swing.JRadioButtonMenuItem radioMenuItem = ((javax.swing.JRadioButtonMenuItem)e.getSource());

		// system bookmark views level
		listener.fireBookMarkSelected( radioMenuItem );

		//callerPanel.getJComboCurrentDisplay().setSelectedItem(
				//com.cannontech.tdc.utils.TDCDefines.SYSTEM_VIEWS_STRING );			
	}	
	else if( e.getSource() instanceof javax.swing.JMenuItem )
	{
		javax.swing.JMenuItem menuItem = ((javax.swing.JMenuItem)e.getSource());

		// user bookmark views level
		//java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(menuItem.getText(), ":");

		listener.fireBookMarkSelected( menuItem );
		//callerPanel.getJComboCurrentDisplay().setSelectedItem(
				//com.cannontech.tdc.utils.TDCDefines.SYSTEM_VIEWS_STRING );
	}
}
}
