package com.cannontech.loadcontrol.messages;

import java.util.List;

/**
 * LMManualControlResponse is sent in response to a LMManualControlRequest
 * It contains information about the constraints that were violated, if any.
 */

public class LMManualControlResponse extends LMMessage 
{
	// List<String> - A String for each constraint violated
	private List _constraintViolations;

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

}
