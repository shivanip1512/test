package com.cannontech.integration.crs;

import java.util.List;

import com.cannontech.stars.database.db.integration.CRSToSAM_PTJ;

public class ImportTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<CRSToSAM_PTJ> entries = CRSToSAM_PTJ.getAllCurrentPTJEntries();
		YukonCRSIntegrator integrator = new YukonCRSIntegrator();
		integrator.runCRSToSAM_PTJ(entries);
		System.out.println("Done");
	}
}
