package com.cannontech.cbc.gui;

/**
 * Insert the type's description here.
 * Creation date: (3/1/2002 4:09:13 PM)
 * @author: 
 */
public class CompositeJSplitPane extends javax.swing.JSplitPane 
{
	private javax.swing.JSplitPane jSplitPaneInner = null;

	private javax.swing.JComponent topComp = null;
	private javax.swing.JComponent middleComp = null;
	private javax.swing.JComponent bottomComp = null;
/**
 * CompositeJSplitPane constructor comment.
 */
private CompositeJSplitPane() {
	super();

	initialize();

}
/**
 * CompositeJSplitPane constructor comment.
 */
public CompositeJSplitPane(javax.swing.JComponent topComp_,
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
 * Creation date: (8/23/00 9:29:43 AM)
 * @return javax.swing.JSplitPane
 */
public javax.swing.JSplitPane getJSplitPaneInner() 
{
	if( jSplitPaneInner == null )
	{
		jSplitPaneInner = new javax.swing.JSplitPane(javax.swing.JSplitPane.VERTICAL_SPLIT);
		jSplitPaneInner.setName("JSplitPaneOuter");
		jSplitPaneInner.setBounds(23, 236, 582, 307);
		jSplitPaneInner.setDividerSize(8);
		jSplitPaneInner.setDividerLocation(0.0);
		jSplitPaneInner.setOneTouchExpandable(true);

		com.cannontech.common.gui.util.SplitPaneDividerListener list = 
				new com.cannontech.common.gui.util.SplitPaneDividerListener( jSplitPaneInner );

		jSplitPaneInner.addComponentListener( list );
		jSplitPaneInner.addPropertyChangeListener( list );
		jSplitPaneInner.addMouseMotionListener( list );
	
		//add( getJSplitPaneInner(), "Center" );
	}
	
	return jSplitPaneInner;
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/00 3:07:26 PM)
 */
public void initDividerPosition() 
{
	//just have the split pane start open now
	setDividerLocation( 100 );
	getJSplitPaneInner().setDividerLocation( 100 );

/*** Crazy Code starts here *****************************************************
	// The following code is ugly, so is the bug its working around!!!!
	//  Java 1.3 has some more functions for the JSplitPane, available so change 
	//  this when we get 1.3.
	Thread t = new Thread( new Runnable()
	{
		public void run()
		{
			while( getDividerLocation() < (getMaximumDividerLocation() - 10) )
			{
				setDividerLocation( getMaximumDividerLocation() );	
				try
				{
					Thread.sleep(100);
				}
				catch(InterruptedException e)
				{
					com.cannontech.clientutils.CTILogger.info("(1)The thread that sets the divider in the CompositeJSplitPane was interrupted.");
				}

				repaint();
			}
		}

	} );
	
	t.setDaemon( true );
	t.start();

/*	Thread t1 = new Thread( new Runnable()
	{
		public void run()
		{
			while( getJSplitPaneInner().getDividerLocation() < (getJSplitPaneInner().getMaximumDividerLocation() - 10) )
			{
				getJSplitPaneInner().setDividerLocation( getJSplitPaneInner().getMaximumDividerLocation() );	
				try
				{
					Thread.sleep(100);
				}
				catch(InterruptedException e)
				{
					com.cannontech.clientutils.CTILogger.info("(2)The thread that sets the divider in the CompositeJSplitPane was interrupted.");
				}

				getJSplitPaneInner().repaint();
			}
		}

	} );
	
	t1.setDaemon( true );
	t1.start();
*/
/*** Crazy Code ends here *****************************************************/

}
/**
 * This method was created in VisualAge.
 */
private void initialize()
{
	setOrientation( javax.swing.JSplitPane.VERTICAL_SPLIT );
	setName("JSplitPaneOuter");
	setBounds(23, 236, 582, 307);
	setDividerSize(8);
	setDividerLocation(0.0);
	setOneTouchExpandable(true);

	com.cannontech.common.gui.util.SplitPaneDividerListener list = 
				new com.cannontech.common.gui.util.SplitPaneDividerListener( this );

	addComponentListener( list );
	addPropertyChangeListener( list );
	addMouseMotionListener( list );

	//add our comps to this split pane
	add( topComp, "top" );

	add( getJSplitPaneInner(), "bottom" );
	getJSplitPaneInner().add( middleComp, "top" );
	getJSplitPaneInner().add( bottomComp, "bottom" );
}
/**
 * Insert the method's description here.
 * Creation date: (3/1/2002 4:17:39 PM)
 * @param newBottomComp javax.swing.JComponent
 */
private void setBottomComp(javax.swing.JComponent newBottomComp) {
	bottomComp = newBottomComp;
}
/**
 * Insert the method's description here.
 * Creation date: (3/1/2002 4:17:39 PM)
 * @param newMiddleComp javax.swing.JComponent
 */
private void setMiddleComp(javax.swing.JComponent newMiddleComp) {
	middleComp = newMiddleComp;
}
/**
 * Insert the method's description here.
 * Creation date: (3/1/2002 4:17:39 PM)
 * @param newTopComp javax.swing.JComponent
 */
private void setTopComp(javax.swing.JComponent newTopComp) {
	topComp = newTopComp;
}
}
