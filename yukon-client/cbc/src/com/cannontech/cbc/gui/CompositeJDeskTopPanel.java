package com.cannontech.cbc.gui;

/**
 * Insert the type's description here.
 * Creation date: (3/4/2002 9:39:04 AM)
 * @author: 
 */
public class CompositeJDeskTopPanel extends javax.swing.JDesktopPane 
{
	private javax.swing.JComponent topComp = null;
	private javax.swing.JComponent middleComp = null;
	private javax.swing.JComponent bottomComp = null;


	private javax.swing.JInternalFrame internalFrameSubBus = null;
	private javax.swing.JInternalFrame internalFrameFeeder = null;
	private javax.swing.JInternalFrame internalFrameCapBank = null;
/**
 * CompositeJDeskTopPanel constructor comment.
 */
private CompositeJDeskTopPanel() {
	super();
}
/**
 * CompositeJDeskTopPanel constructor comment.
 */
public CompositeJDeskTopPanel(javax.swing.JComponent topComp_,
			javax.swing.JComponent middleComp_,
			javax.swing.JComponent bottomComp_ ) 
{
	super();

	setTopComp( topComp_ );
	setMiddleComp( middleComp_ );
	setBottomComp( bottomComp_ );

	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (3/4/2002 10:07:04 AM)
 * @return javax.swing.JInternalFrame
 */
private javax.swing.JInternalFrame getInternalFrameCapBank() 
{
	if( internalFrameCapBank == null )
	{
		internalFrameCapBank = new javax.swing.JInternalFrame(
							"Capacitor Banks", true, false, true, true );
			
		internalFrameCapBank.setName("JInternalFrameCapBank");
		internalFrameCapBank.setBounds(6, 235, 800, 110);
		internalFrameCapBank.setFrameIcon(
			new javax.swing.ImageIcon(com.cannontech.tdc.utils.TDCDefines.ICON_TDC) );

		javax.swing.JPanel contentPane = new javax.swing.JPanel();
		contentPane.setLayout(new java.awt.BorderLayout());
		contentPane.add( bottomComp, "Center" );
		internalFrameCapBank.setContentPane( contentPane );
		
	}

	return internalFrameCapBank;
}
/**
 * Insert the method's description here.
 * Creation date: (3/4/2002 10:07:04 AM)
 * @return javax.swing.JInternalFrame
 */
private javax.swing.JInternalFrame getInternalFrameFeeder() 
{
	if( internalFrameFeeder == null )
	{
		internalFrameFeeder = new javax.swing.JInternalFrame(
							"Feeders", true, false, true, true );

		internalFrameFeeder.setName("JInternalFrameFeeder");
		internalFrameFeeder.setBounds(6, 120, 800, 110);
		internalFrameFeeder.setFrameIcon(
			new javax.swing.ImageIcon(com.cannontech.tdc.utils.TDCDefines.ICON_TDC) );

		javax.swing.JPanel contentPane = new javax.swing.JPanel();
		contentPane.setLayout(new java.awt.BorderLayout());
		contentPane.add( middleComp, "Center" );
		internalFrameFeeder.setContentPane( contentPane );
	}

	return internalFrameFeeder;
}
/**
 * Insert the method's description here.
 * Creation date: (3/4/2002 10:07:04 AM)
 * @return javax.swing.JInternalFrame
 */
private javax.swing.JInternalFrame getInternalFrameSubBus() 
{
	if( internalFrameSubBus == null )
	{
		internalFrameSubBus = new javax.swing.JInternalFrame(
						"Substation Buses", true, false, true, true );

		internalFrameSubBus.setName("JInternalFrameSubBus");
		internalFrameSubBus.setBounds(6, 5, 800, 110);		
		internalFrameSubBus.setFrameIcon(
			new javax.swing.ImageIcon(com.cannontech.tdc.utils.TDCDefines.ICON_TDC) );
		
		javax.swing.JPanel contentPane = new javax.swing.JPanel();
		contentPane.setLayout(new java.awt.BorderLayout());
		contentPane.add( topComp, "Center" );		
		internalFrameSubBus.setContentPane( contentPane );
	}

	return internalFrameSubBus;
}
/**
 * This method was created in VisualAge.
 */
private void initialize()
{
	setName("JDeskTopPaneComposite");
	setBounds(23, 236, 582, 307);
	
	//add our comps to this split pane
	add( getInternalFrameSubBus(), getInternalFrameSubBus().getName() );
	add( getInternalFrameFeeder(), getInternalFrameFeeder().getName() );
	add( getInternalFrameCapBank(), getInternalFrameCapBank().getName() );


	getInternalFrameSubBus().setVisible(true);
	getInternalFrameFeeder().setVisible(true);
	getInternalFrameCapBank().setVisible(true);
}
/**
 * Insert the method's description here.
 * Creation date: (3/4/2002 9:41:10 AM)
 * @param newBottomComp javax.swing.JComponent
 */
private void setBottomComp(javax.swing.JComponent newBottomComp) {
	bottomComp = newBottomComp;
}
/**
 * Insert the method's description here.
 * Creation date: (3/4/2002 9:41:10 AM)
 * @param newMiddleComp javax.swing.JComponent
 */
private void setMiddleComp(javax.swing.JComponent newMiddleComp) {
	middleComp = newMiddleComp;
}
/**
 * Insert the method's description here.
 * Creation date: (3/4/2002 9:41:10 AM)
 * @param newTopComp javax.swing.JComponent
 */
private void setTopComp(javax.swing.JComponent newTopComp) {
	topComp = newTopComp;
}
}
