package com.cannontech.tdc.spawn;

/**
 * Insert the type's description here.
 * Creation date: (10/10/00 3:55:14 PM)
 * @author: 
 */
import java.util.Vector;

import com.cannontech.tdc.TDCMainFrame;

public final class TDCOverSeeer implements TDCMainFrameSpawnListener
{
	private static Vector tdcMainFrames = null;

	//protected transient TDCMainFrameSpawnListener listenerEventMulticaster = null;
/**
 * TDCOverSeeer constructor comment.
 */
public TDCOverSeeer() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/00 3:56:16 PM)
 * @return com.cannontech.tdc.TDCMainFrame
 */
public TDCMainFrame createTDCMainFrame() 
{
	return createTDCMainFrame( null );
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/00 3:56:16 PM)
 * @return com.cannontech.tdc.TDCMainFrame
 */
public TDCMainFrame createTDCMainFrame( String[] parameters ) 
{
	TDCMainFrame newTDCMainFrame = null;
	
	if( parameters == null )
		newTDCMainFrame = new TDCMainFrame();
	else
		newTDCMainFrame = new TDCMainFrame( parameters );
		
	// add a listener to the new tdc frame
	newTDCMainFrame.addTDCMainFrameSpawnListener( this );
	
	getTdcMainFrames().addElement( newTDCMainFrame );


	// give the newest added TDCMainFrame a name
	newTDCMainFrame.setName("TDCMainFrame#" + getTdcMainFrames().size() );
	
	return newTDCMainFrame;
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/00 3:56:16 PM)
 * @return com.cannontech.tdc.TDCMainFrame
 */
private boolean deleteTDCMainFrame( TDCMainFrame tdcMainFrame )
{
	boolean wasRemoved = false;

	// remove the listener to the soon to be deleted tdc frame
	if( getTdcMainFrames().contains( tdcMainFrame ) )
	{
		wasRemoved = getTdcMainFrames().removeElement( tdcMainFrame );
	}
	

	return wasRemoved;
}
/**
 * Insert the method's description here.
 * Creation date: (10/12/00 5:58:09 PM)
 */
private void doOtherTDCMainFrame_actionPerformed( SpawnTDCMainFrameEvent e ) 
{
	for( int i = 0; i < getTdcMainFrames().size(); i++ )
	{
		// is the source also the listener, we do not need to repeat events
		if( e.getSource() != ((TDCMainFrameSpawnListener)getTdcMainFrames().elementAt(i)) )			
			((TDCMainFrameSpawnListener)getTdcMainFrames().elementAt(i)).otherTDCMainFrameActionPerformed( e );		
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (10/13/00 2:31:53 PM)
 */
private void exitSystem() 
{
	int numberOfFrames = getTdcMainFrames().size();
	
	for( int i = 0; i < numberOfFrames; i++ )
	{
		TDCMainFrame frame = ((TDCMainFrame)getTdcMainFrames().elementAt(getTdcMainFrames().size() - 1));
		
		// destroy frame
		otherTDCMainFrameActionPerformed( new SpawnTDCMainFrameEvent( frame, SpawnTDCMainFrameEvent.DISPOSE_TDCMAINFRAME) );

		// we will System.exit() at the end of this loop!!!
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/00 3:57:08 PM)
 * @return com.cannontech.tdc.TDCMainFrame[]
 */
private Vector getTdcMainFrames() 
{
	if( tdcMainFrames == null )
		tdcMainFrames = new Vector(1);
		
	return tdcMainFrames;
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/00 4:11:03 PM)
 * @param e com.cannontech.tdc.spawn.TDCMainFrameEvent
 */
public void otherTDCMainFrameActionPerformed(SpawnTDCMainFrameEvent e)
{
	if( !(e.getSource() instanceof TDCMainFrame) )
		throw new Error("In otherTDCMainFrameActionPerformed(SpawnTDCMainFrameEvent e), e was an event NOT caused by a TDCMainFrame");
		
	if( e.getId() == SpawnTDCMainFrameEvent.CREATE_TDCMAINFRAME )
	{
		// create a new spawn of TDC
		TDCMainFrame tempTDCMainFrame = createTDCMainFrame();
		tempTDCMainFrame.visibilityInitialization();
	}
	else if( e.getId() == SpawnTDCMainFrameEvent.EXIT_TDC )
	{
		exitSystem();
	}
	else if( e.getId() == SpawnTDCMainFrameEvent.DISPOSE_TDCMAINFRAME )			 
	{
		// destroy the TDCMainFrame that originated this event
		// if its the only existing TDCMainFrame, destroy the whole application
		if( getTdcMainFrames().size() == 1 )
		{
			((TDCMainFrame)e.getSource()).destroySpawn();
			((TDCMainFrame)e.getSource()).destroySingularities();
			System.exit(0);
		}
		else
		{
			// remove the deleted TDCMainFrame from the list of TDCMainFrames
			deleteTDCMainFrame( (TDCMainFrame)e.getSource() );

			// destroy the connections now
			((TDCMainFrame)e.getSource()).destroySpawn();
			((TDCMainFrame)e.getSource()).dispose();
		}
	}		
	else
		doOtherTDCMainFrame_actionPerformed( e );
}
}
