package com.comopt.windows;

/**
 * Insert the type's description here.
 * Creation date: (10/9/2002 11:53:25 PM)
 * @author: 
 */
public class ServicePopUpMenu extends javax.swing.JPopupMenu implements java.awt.event.ActionListener
{
	private NTService ntService = null;
	private javax.swing.JMenuItem jMenuItemStart = null;
	private javax.swing.JMenuItem jMenuItemStop = null;
	private javax.swing.JMenuItem jMenuItemPause = null;
	private javax.swing.JMenuItem jMenuItemRefresh = null;


/**
 * ServicePopUpMenu constructor comment.
 */
public ServicePopUpMenu() {
	super();

	initialize();
}

/**
 */
public void execute_Start(java.awt.event.ActionEvent e) 
{
   
   if( ntService.getCurrentState() == IServiceConstants.SERVICE_STOPPED )
   {
      firePropertyChange( "PopUpChange", IServiceConstants.STATE_RUNNING, ntService.getServiceName() );
   }

}

/**
 */
public void execute_Stop(java.awt.event.ActionEvent e) 
{

   if( ntService.getCurrentState() == IServiceConstants.SERVICE_RUNNING
       || ntService.getCurrentState() == IServiceConstants.SERVICE_START_PENDING
       || ntService.getCurrentState() == IServiceConstants.SERVICE_STOP_PENDING )
   {
      firePropertyChange( "PopUpChange", IServiceConstants.STATE_STOPPED, ntService.getServiceName() );
   }

}

/**
 */
public void execute_Pause(java.awt.event.ActionEvent e) 
{
}

/**
 */
public void execute_Refresh(java.awt.event.ActionEvent e) 
{
   firePropertyChange( "PopUpChange", "refresh", ntService.getServiceName() );   
}

/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent e) 
{
	if( e.getSource() == getJMenuItemStart() )
		execute_Start(e);

   if( e.getSource() == getJMenuItemStop() )
      execute_Stop(e);

   if( e.getSource() == getJMenuItemPause() )
      execute_Pause(e);

   if( e.getSource() == getJMenuItemRefresh() )
      execute_Refresh(e);

}

/**
 * Insert the method's description here.
 * Creation date: (10/9/2002 11:59:44 PM)
 * @return javax.swing.JMenuItem
 */
public javax.swing.JMenuItem getJMenuItemPause() 
{
	if( jMenuItemPause == null )
	{
		jMenuItemPause = new javax.swing.JMenuItem();
		jMenuItemPause.setName("JMenuItemEnablePause");
		jMenuItemPause.setMnemonic('p');
		jMenuItemPause.setText("Pause");

	}

	return jMenuItemPause;
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/2002 11:59:44 PM)
 * @return javax.swing.JMenuItem
 */
public javax.swing.JMenuItem getJMenuItemRefresh() 
{
	if( jMenuItemRefresh == null )
	{
		jMenuItemRefresh = new javax.swing.JMenuItem();
		jMenuItemRefresh.setName("JMenuItemEnableRefresh");
		jMenuItemRefresh.setMnemonic('r');
		jMenuItemRefresh.setText("Refresh");

	}

	return jMenuItemRefresh;
}

/**
 * Insert the method's description here.
 * Creation date: (10/9/2002 11:59:44 PM)
 * @return javax.swing.JMenuItem
 */
public javax.swing.JMenuItem getJMenuItemStart() 
{
	if( jMenuItemStart == null )
	{
		jMenuItemStart = new javax.swing.JMenuItem();
		jMenuItemStart.setName("JMenuItemEnableStart");
		jMenuItemStart.setMnemonic('s');
		jMenuItemStart.setText("Start");

	}

	return jMenuItemStart;
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/2002 11:59:44 PM)
 * @return javax.swing.JMenuItem
 */
public javax.swing.JMenuItem getJMenuItemStop() 
{
	if( jMenuItemStop == null )
	{
		jMenuItemStop = new javax.swing.JMenuItem();
		jMenuItemStop.setName("JMenuItemEnableStop");
		jMenuItemStop.setMnemonic('t');
		jMenuItemStop.setText("Stop");

	}

	return jMenuItemStop;
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/2002 11:59:44 PM)
 * @return com.comopt.windows.NTService[]
 */
public NTService getNtService() {
	return ntService;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) 
{
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------" );
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
private void initConnections() throws java.lang.Exception 
{
	getJMenuItemStart().addActionListener(this);
	getJMenuItemStop().addActionListener(this);

	getJMenuItemRefresh().addActionListener(this);
	getJMenuItemPause().addActionListener(this);

}
/**
 * Insert the method's description here.
 * Creation date: (10/9/2002 11:57:41 PM)
 */
private void initialize() 
{
	try 
	{
		setName("ServicesPopUp");
		add(getJMenuItemStart(), getJMenuItemStart().getName());
		add(getJMenuItemStop(), getJMenuItemStop().getName());
		//add(getJMenuItemPause(), getJMenuItemPause().getName());

      add( new javax.swing.JSeparator(), "seperator" );

      add(getJMenuItemRefresh(), getJMenuItemRefresh().getName());

		initConnections();
	}
	catch (java.lang.Throwable ivjExc) 
	{
		handleException(ivjExc);
	}
	
}

private void initMenuItems( boolean isEnabled )
{
   if( ntService == null )
      return;

   //set our ablement buttons accordingly
   getJMenuItemStop().setEnabled( isEnabled 
            && ntService.getCurrentState() == IServiceConstants.SERVICE_RUNNING );

   getJMenuItemStart().setEnabled( isEnabled
            && ntService.getCurrentState() == IServiceConstants.SERVICE_STOPPED );

   getJMenuItemPause().setEnabled( isEnabled
            && ntService.getCurrentState() == IServiceConstants.SERVICE_RUNNING );

   //getJMenuItemRefresh().setEnabled( isEnabled );
   
}

/**
 * Insert the method's description here.
 * Creation date: (10/9/2002 11:59:44 PM)
 * @param newNtServices NTService[]
 */
public void setNtServices( NTService newNtService, boolean isEnabled ) 
{
	ntService = newNtService;
   initMenuItems( isEnabled );
}
}
