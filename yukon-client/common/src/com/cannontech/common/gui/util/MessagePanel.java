package com.cannontech.common.gui.util;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.common.util.MessageEvent;

import javax.swing.*;
import java.awt.*;

public class MessagePanel extends JPanel implements com.cannontech.common.util.MessageEventListener {

	class MessageCellRenderer implements javax.swing.ListCellRenderer{
		public Component getListCellRendererComponent(JList list,
														Object value,
														int index,
														boolean isSelected,
														boolean cellHasFocus)
		{
			JLabel tempLabel = new JLabel(value.toString());
			if ( ((MessageEvent) value).getMessageType() == MessageEvent.ERROR_MESSAGE )
				tempLabel.setForeground(java.awt.Color.red);
			else if ( ((MessageEvent) value).getMessageType() == MessageEvent.INFORMATION_MESSAGE )
				tempLabel.setForeground(java.awt.Color.black);
			return tempLabel;
		}
	}
	private javax.swing.JScrollPane displayListScrollPane;
	private javax.swing.JList displayList;
	private javax.swing.JLabel systemStatusLabel;
	private int maxMessages = 1000;
	private boolean showTitle = true;
/**
 * SystemStatusPanel constructor comment.
 */
public MessagePanel() {
	super();
	initialize();
}
/**
 * SystemStatusPanel constructor comment.
 */
public MessagePanel(boolean title) {
	super();
	this.showTitle = title;
	
	initialize();
}
/**
 * This method was created in VisualAge.
 */
public void clear() {

	((javax.swing.DefaultListModel) displayList.getModel()).removeAllElements();
}
/**
 * This method was created in VisualAge.
 */
private void initialize() {
	
	systemStatusLabel = new javax.swing.JLabel(" Message Log");
	systemStatusLabel.setFont(new java.awt.Font("dialog", 0, 14));
	systemStatusLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		
	displayList = new javax.swing.JList(new javax.swing.DefaultListModel());
	displayList.setFont(new java.awt.Font("dialog", 0, 14));
	
	displayListScrollPane = new javax.swing.JScrollPane(displayList);
	displayListScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	displayListScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

	setPreferredSize( new java.awt.Dimension( 575, 90) );
	setLayout( new java.awt.BorderLayout() );

	if( this.showTitle )
	{	
		add( systemStatusLabel, "North" );
	}
	
	add( displayListScrollPane, "Center" );

	displayList.setCellRenderer(new MessageCellRenderer());
}
/**
 * systemEvent method comment.
 */
public void messageEvent(MessageEvent eventObject) {

	((javax.swing.DefaultListModel) displayList.getModel()).addElement(eventObject);
	
/*
	if (eventObject.getMessageType()==1)
		displayList.setForeground(java.awt.Color.red);
	else
		displayList.setForeground(java.awt.Color.black);
	displayList.ensureIndexIsVisible( displayList.getModel().getSize()  );
*/
	//Bug ensureIndexIsVisible doesn't always scroll the the bottom
	//partial workaround to bug #4145919

	int position = displayList.getModel().getSize();
	java.awt.Rectangle cellBounds= displayList.getCellBounds(position-1,position-1);
	
	if( cellBounds != null )
	{
		// 2* so that you get bottom of cell
		cellBounds.translate(0,2*cellBounds.height);
		displayList.scrollRectToVisible( cellBounds );
	}
	
}
/**
 * This method was created in VisualAge.
 * @param max int
 */
public void setMaxMessages(int max) {
	this.maxMessages = max;
}
/**
 * This method was created in VisualAge.
 * @param val boolean
 */
public void showTitle(boolean val) {

	if( val == this.showTitle )
		return;
		
	if( val )
	{
		this.remove( systemStatusLabel );
	}
	else
	{		
		this.add( systemStatusLabel, "North" );		
	}

	revalidate();
}
}
