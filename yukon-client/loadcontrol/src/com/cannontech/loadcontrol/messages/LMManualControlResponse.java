package com.cannontech.loadcontrol.messages;

import java.util.Set;

/**
 * LMManualControlResponse is sent in response to a LMManualControlRequest
 * It contains information about the constraints that were violated, if any.
 */

public class LMManualControlResponse extends LMMessage 
{
	// Set<String> - A String for each constraint violated
	private Set _constraintViolations;

/**
 * ScheduleCommand constructor comment.
 */
public LMManualControlResponse() {
	super();
}

	/**
	 * @return
	 */
	public Set getConstraintViolations() {
		return _constraintViolations;
	}

	/**
	 * @param set
	 */
	public void setConstraintViolations(Set set) {
		_constraintViolations = set;
	}

}
