package com.cannontech.integration.crs;

import java.util.ArrayList;

import com.cannontech.database.db.stars.integration.CRSToSAM_PTJ;

public class ImportTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList entries = CRSToSAM_PTJ.getAllCurrentPTJEntries();
		YukonCRSIntegrator integrator = new YukonCRSIntegrator();
		integrator.runCRSToSAM_PTJ(entries);
		System.out.println("Done");
	}
}
