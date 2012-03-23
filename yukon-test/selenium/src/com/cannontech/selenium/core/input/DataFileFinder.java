/**
 * 
 */
package com.cannontech.selenium.core.input;

import java.io.InputStream;

import org.apache.log4j.Logger;

import com.cannontech.selenium.core.SolventSeleniumTestCase;

/**
 * This class fille find if there is an input file for the test case.
 * @author anuradha.uduwage
 *
 */
public class DataFileFinder {

	private static final Logger log = Logger.getLogger(DataFileFinder.class.getName());
	
	/**
	 * 
	 * @param testcase
	 * @return
	 */
	public static InputStream getDataInputFileAsStream(SolventSeleniumTestCase testcase) {
		return testcase.getClass().getClassLoader().getResourceAsStream(getDataFileNameForTest(testcase));
	}

	/**
	 * Determines the appropriate name and the location of the input file for the given testcase.
	 * @param testcase 
	 * @return return the file name
	 */
	private static String getDataFileNameForTest(SolventSeleniumTestCase testcase) {
		String packageName = "";
		if(testcase.getClass().getPackage() != null) {
			packageName = testcase.getClass().getPackage().getName() + ".";
		}
		String resourceName = (packageName + testcase.getClass().getSimpleName()).replace('.', '/');
		resourceName += ".xml";
		log.info("Searching for data input file " + resourceName);
		return resourceName;
	}
}
