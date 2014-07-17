/*
 * Created on May 20, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.fdemulator.common;
/**
 * 
 * 
 * This class implements the Observable class for the GUI
 * 
 * This class provides the messaging glue between the FDTestPanel on the
 * GUI and any class that would interface with the panel.  When the user makes a
 * selection, the FDTestPanel set the action and notifys the observers.
 * 
 * @author Ben Wallace
 * 
 */

import java.util.Observable;

public class FDTestPanelNotifier extends Observable
{
	/** <br> This action indicates that the user wants the stats cleared */
	public static final int ACTION_CLEAR_STATS = 1;

	/** <br> This action indicates that the user wants to stop the emulator test */
	public static final int ACTION_STOP_TEST = 2;

	public static final int ACTION_CONNECTION_LOST = 3;

	// Private class variables
	private FDTestPanel fdTestPanel = null;
	private int action = 0;

	/** 
	  * Constructor that takes an activity panel reference
	  * 
	  */
	public FDTestPanelNotifier(FDTestPanel fdPanel)
	{
		fdTestPanel = fdPanel;
	}

	/** 
	  * method used to set the action that has occured
	  * 
	  * @param int newAction from the static list of actions
	  * 
	  */
	public void setActionCode(int newAction)
	{
		action = newAction;
		setChanged();
		notifyObservers();
	}

	/** 
	  * wrapper method to access the company name stored in
	  * the activity panel
	  * 
	  * @return String company name
	  */

	/**
	 * wrapper method to access the FDTestPanel
	 * 
	 * @return ActivityPanel
	 */
	public FDTestPanel getFDTestPanel()
	{
		return fdTestPanel;
	}

	/**
	 * Returns the action event.
	 * 
	 * @return int action from static 
	 */
	public int getAction()
	{
		return action;
	}

}