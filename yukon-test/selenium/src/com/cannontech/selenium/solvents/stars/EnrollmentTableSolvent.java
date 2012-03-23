package com.cannontech.selenium.solvents.stars;

import org.joda.time.Duration;
import com.cannontech.selenium.core.SolventSeleniumException;
import com.cannontech.selenium.solvents.common.CommonTableSolvent;

/**
 * This class handle all the functions that are available under stars enrollment section.
 * @author anuradha.uduwage
 *
 */
public class EnrollmentTableSolvent extends CommonTableSolvent {

	/**
	 * Default constructor sets the tableClass attribute for enrollment tables.
	 * @param params
	 */
	public EnrollmentTableSolvent(String... params) {
		super("tableClass=compactResultsTable rowHighlighting");
	}
	
	/**
	 * Method return the value of the category based on given program name.
	 * @param programName name of the program.
	 * @param categoryClass the class of the category of the date desired
	 * @return group value of the group.
	 */
	public String getEnrollmentTableDataByProgramName(String programName, String categoryClass) {
		String groupLocator = getXpathRoot() + "//td[@class='programName' and normalize-space(text())='" + 
		programName + "']//following::td[@class='" + categoryClass + "'][1]";
		String group = null;

		if(programNameIsAvailable(programName)) {
			if(!selenium.isElementPresent(groupLocator, Duration.ZERO)) {
				throw new SolventSeleniumException("Unable to locate Group for program " + programName + " at " + groupLocator);
			}
			group = selenium.getText(groupLocator);
			group.trim();
		}
		return group;
	}
	
	/**
	 * Method click the edit icon based on the given program name.
	 * @param programName name of the program.
	 * @return
	 */
	public EnrollmentTableSolvent editProgramByName(String programName) {
		String editImgLocator = getXpathRoot() + "//td[@class='programName' and normalize-space(text())='" + programName + "']//following::img[1]";

		if(programNameIsAvailable(programName)) {
			selenium.waitForElement(editImgLocator);
			selenium.click(editImgLocator);
		}
		return this;
	}
	
	/**
	 * Method click the delete icon based on the given program name.
	 * @param programName name of the program.
	 * @return
	 */
	public EnrollmentTableSolvent deleteProgramByName(String programName) {
		String deleteImgLocator = getXpathRoot() + "//td[@class='programName' and normalize-space(text())='" + programName + "']//following::img[2]";

		if(programNameIsAvailable(programName)) {
			selenium.waitForElement(deleteImgLocator);
			selenium.click(deleteImgLocator);
		}
		return this;
	}
	
	/**
	 * Click ok to confirm the delete action of the program in stars enrollment page.
	 * @return
	 */
	public EnrollmentTableSolvent clickOkToDeleteProgram() {
		String okButtonLocator = "//div[@class='actionArea']/input[@value='OK']";

		selenium.waitForElement(okButtonLocator);
		selenium.click(okButtonLocator);
		return this;
	}
	
	/**
	 * Click cancel to stop deleting an enrolled program.
	 * @return
	 */
	public EnrollmentTableSolvent clickCancelToStopDelete() {
		String cancelButton = "//div[@class='actionArea']/input[@value='Cancel']";

		selenium.waitForElement(cancelButton);
		selenium.click(cancelButton);
		return this;
	}
	
	/**
	 * Since most of the methods will depend on the existance of the program this method will do a sanity check on Program and if available
	 * it will move forward.
	 * @param programName name of the program.
	 * @return
	 */
	private boolean programNameIsAvailable(String programName) {
		String programLocator = getXpathRoot() + "//td[@class='programName' and normalize-space(text())='" + programName + "']";
		boolean programAvail;
		if(selenium.isElementPresent(programLocator))
			programAvail = true;
		else {
			programAvail = false;
			throw new SolventSeleniumException("Program name with " + programName + " is not available at " + programLocator);
		}
		return programAvail;
	}
	
	/**
	 * Method clicks on Enroll link based on a Program Name
	 * @param programName is the name of program
	 * @return
	 */
	public EnrollmentTableSolvent enrollByProgramName(String programName) {
		String enrollLocator = "//*[@class='notEnrolled program']//td[contains(., '" + programName + "')]//following::a[1]";

		selenium.waitForElement(enrollLocator);
		selenium.click(enrollLocator);
		return this;
	}
	
	/**
	 * Method clicks on Unenroll link based on a Program Name
	 * @param programName is the name of the program
	 * @return
	 */
	public EnrollmentTableSolvent unenrollByProgramName(String programName) {
		String unenrollLocator =  "//*[@class='enrolled program']//td[contains(., '" + programName +"')]//following::a[1]";

		selenium.waitForElement(unenrollLocator);
		selenium.click(unenrollLocator);
		return this;
	}
	/**
	 * Method returns the previous enrollment history date from the Enrollment page of a customer
	 * @param device - the thermostat's number
	 * @return returns the enrollment history date
	 */
	public String getEnrollmentHistoryDateByDevice(String device) {
		String textFieldLocator = "//div[normalize-space(text())='Enrollment History']//following::td[normalize-space(text())='" + device + "']/preceding::td[4]";
		String textFieldValue = null;
		if(selenium.isTextPresent(device)) {
			if(!selenium.isElementPresent(textFieldLocator))
				throw new SolventSeleniumException("Unable to find the text field label " + device + " at " + textFieldLocator);
			textFieldValue = selenium.getText(textFieldLocator).split(" ")[0];
		}
		return textFieldValue;
	}
}
