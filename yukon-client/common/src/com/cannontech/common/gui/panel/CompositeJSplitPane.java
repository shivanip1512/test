package com.cannontech.common.gui.panel;


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
		jSplitPaneInner.setDividerSize(8);
	}
	
	return jSplitPaneInner;
}

private void initialize()
{
	setOrientation( javax.swing.JSplitPane.VERTICAL_SPLIT );
	setName("JSplitPaneOuter");
	setBounds(23, 236, 582, 307);
	setDividerSize(8);
	
	//add our comps to this split pane
	add( topComp, "top" );

	getJSplitPaneInner().add( middleComp, "top" );
	getJSplitPaneInner().add( bottomComp, "bottom" );
	add( getJSplitPaneInner(), "bottom" );	
	
	//layout the child compoonents to their intended sizes
	resetToPreferredSizes();
	getJSplitPaneInner().resetToPreferredSizes();
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
