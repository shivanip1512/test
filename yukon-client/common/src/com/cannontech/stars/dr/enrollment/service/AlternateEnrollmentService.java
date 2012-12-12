package com.cannontech.stars.dr.enrollment.service;

import java.util.Map;
import java.util.Set;

import com.cannontech.common.util.Pair;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.user.YukonUserContext;

public interface AlternateEnrollmentService {

	/**
	 *  Builds up two maps, first is a map of available alternate enrollments per device on the account,
	 *  next is a map of active alternate enrollments per device on the account.
	 */
	void buildEnrollmentMaps(int accountId,
			Map<HardwareSummary, Pair<Set<Program>, Set<Program>>> available,
			Map<HardwareSummary, Pair<Set<Program>, Set<Program>>> active);
	
	/**
	 * Enrolls the devices of @param alternate into their alternate programs, also reverts alternate
	 * enrollments for the devices in @param normal.  
	 */
	void doEnrollment(Integer[] alternate, Integer[] normal, int accountId, YukonUserContext context);

}
