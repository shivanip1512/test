package com.cannontech.loadcontrol.messages;

import java.util.List;

/**
 * LMManualControlResponse is sent in response to a LMManualControlRequest
 * It contains information about the constraints that were violated, if any.
 * 
 * In the event that a control window or maximum control time constraint
 * is violated then the server will return the compromise it had to make
 * as the bestFitAction.
 */

public class LMManualControlResponse extends LMMessage 
{
	private int _programID;
	// List<String> - A String for each constraint violated
	private List _constraintViolations;
	// If a constraint is violated and the server is forced to
	// determine a "best fit" time period for a program to 
	// run, this field will contain info about the compromise made.
	private String _bestFitAction;
	

/**
 * ScheduleCommand constructor comment.
 */
public LMManualControlResponse() {
	super();
}

	/**
	 * @return
	 */
	public List getConstraintViolations() {
		return _constraintViolations;
	}

	/**
	 * @param set
	 */
	public void setConstraintViolations(List set) {
		_constraintViolations = set;
	}

	/**
	 * @return
	 */
	public String getBestFitAction() {
		return _bestFitAction;
	}

	/**
	 * @return
	 */
	public int getProgramID() {
		return _programID;
	}

	/**
	 * @param string
	 */
	public void setBestFitAction(String string) {
		_bestFitAction = string;
	}

	/**
	 * @param i
	 */
	public void setProgramID(int i) {
		_programID = i;
	}

}
