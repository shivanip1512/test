package com.cannontech.common.gui.util;

/**
 * This type was created in VisualAge.
 */
public class SystemStatusPanel extends javax.swing.JPanel implements com.cannontech.common.util.SystemEventListener {

	private javax.swing.JScrollPane displayListScrollPane;
	private javax.swing.JList displayList;
	private javax.swing.JLabel systemStatusLabel;
/**
 * SystemStatusPanel constructor comment.
 */
public SystemStatusPanel() {
	super();
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

	systemStatusLabel = new javax.swing.JLabel("System Status1");
	systemStatusLabel.setFont(new java.awt.Font("dialog", 0, 14));
	systemStatusLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
	
	displayList = new javax.swing.JList(new javax.swing.DefaultListModel());
	displayList.setFont(new java.awt.Font("dialog", 0, 14));
	
	displayListScrollPane = new javax.swing.JScrollPane(displayList);
	displayListScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	displayListScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

	setPreferredSize( new java.awt.Dimension( 575, 125) );
	setLayout( new java.awt.BorderLayout() );
	
	add( systemStatusLabel, "North" );
	add( displayListScrollPane, "Center" );

}
/**
 * systemEvent method comment.
 */
public void systemEvent(com.cannontech.common.util.SystemEvent eventObject) {

	((javax.swing.DefaultListModel) displayList.getModel()).addElement(eventObject);

	displayList.ensureIndexIsVisible( displayList.getModel().getSize()  );

	//Bug ensureIndexIsVisible doesn't always scroll the the bottom
	/* partial workaround to bug #4145919
							 Rectangle cellBounds = aJList.getCellBounds(position,position);
							 if (cellBounds == null) {
							 cellBounds = aJList.getCellBounds(position-1,position-1);
							 if (cellBounds != null) {
							 // 2* so that you get bottom of cell
							 cellBounds.translate(0,2*cellBounds.height);
							 aJList.scrollRectToVisible( cellBounds );
							 }
							 } else {
							 aJList.ensureIndexIsVisible(position);
							 }

	 */
}
}
