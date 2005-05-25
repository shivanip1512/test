/*
 * Created on May 25, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.capcontrol;

import java.util.Observable;

/**
 * @author ASolberg
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

public class ActionNotifier extends Observable
{
	public static final int ACTION_VALIDATION_SUCCESSFUL = 1;

	public static final int ACTION_VALIDATION_FAILURE = 2;
	
	public static final int ACTION_DBWRITE_SUCCESSFUL = 3;
	
	public static final int ACTION_DBWRITE_FAILURE = 4;


	// Private class variables
	private InputFrame inputFrame = null;
	private int action = 0;

	/** 
	  * Constructor that takes an activity panel reference
	  * 
	  */
	public ActionNotifier(InputFrame frame)
	{
		inputFrame = frame;
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
	public InputFrame getInputFrame()
	{
		return inputFrame;
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
