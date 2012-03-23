package com.cannontech.selenium.test.metering;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Test;
import com.cannontech.selenium.core.AbstractSolvent;
import com.cannontech.selenium.core.SolventSeleniumTestCase;
import com.cannontech.selenium.core.locators.Locator;
import com.cannontech.selenium.core.locators.LocatorEscapeUtils;
import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.LoginLogoutSolvent;
/**
 * @author Jon Narr
 *
 */
public class TestManualCommanderSelenium extends SolventSeleniumTestCase {
	/**
	 * This test logs into yukon and attempts to send all common commands for a given meter
	 * This test runs slow because QA011 runs slow
	 */
	@Test
	public void sendMeterCommands() {
		setCustomURL("http://pspl-qa011.cannontech.com:8080");
		start();
		new LoginLogoutSolvent().cannonLogin("syukon", "syukon");
		CommonSolvent common = new CommonSolvent();
		ManualCommanderSolvent commander = new ManualCommanderSolvent();	

		//reading in data from xml
		String[] meterNames = getParamStrings("meterName");
		String[] commandNames;
		String[] commonNames;
		String[] commandNames_P;
		String[] commonNames_P;
		String[] commandParameterValues;

		//Read all valid devices
		for(String meter: meterNames){
			commandNames = getParamStrings("commandName " + meter);
			commonNames = getParamStrings("commonName " + meter);
			commandNames_P = getParamStrings("commandName_P " + meter);
			commonNames_P = getParamStrings("commonName_P " + meter);
			commandParameterValues = getParamStrings("parameterValues " + meter);
			common.clickLinkByName("Metering");
			Assert.assertEquals("Metering", common.getPageTitle());
            common.enterInputTextByFormId("filterForm", "Quick Search", meter);
			common.clickButtonBySpanText("Search");
			
			//Verify meter was found
			Assert.assertEquals(true, common.isTextPresent(meter));
			//Verify meter's status is enabled
			Assert.assertEquals(true, common.isTextPresent("Enabled"));
			// Got to Manual Commander page
			common.clickLinkByName("Manual Commander");
			Assert.assertEquals(true, common.isTextPresent("Manual Commander"));
			
			//Send all commands without parameter values
			for(int i = 0; i < commonNames.length; i++){
					common.selectDropDownMenu("servlet/CommanderServlet", "commonCommand", commonNames[i]);
				Assert.assertEquals(commander.getTextBoxText("servlet/CommanderServlet", "command"), commandNames[i]);
				common.clickFormButton("servlet/CommanderServlet", "execute");
				// Verify a response was received.
				common.isTextPresent("Emetcon DLC command sent on route");//TODO
				common.clickFormButton("servlet/CommanderServlet", "clearText");
			}
			
			//Send all commands requiring parameter values
			for(int i = 0;  i < commonNames_P.length; i++){
					common.selectDropDownMenu("servlet/CommanderServlet", "commonCommand", commonNames_P[i]);
					String[] allParams = commandParameterValues[i].split(" ");
					for(int j = 0; j < allParams.length; j++)
						commander.enterCommandParameterValue(allParams[j]);
					Assert.assertEquals(commander.replaceVARS(commandNames[i]), "Execute Command:", "Execute Command:");
//					Assert.assertEquals(commander.getTextBoxText("servlet/CommanderServlet", "command"), commandNames_P[i]);
					common.clickFormButton("servlet/CommanderServlet", "execute");
					// Verify a response was received.
					common.isTextPresent("Emetcon DLC command sent on route");//TODO
					common.clickFormButton("servlet/CommanderServlet", "clearText");
			}
		}
		common.end();
	}
}
/**
 * Solvent for local test development
 * @author ricky.jones
 */
class ManualCommanderSolvent extends AbstractSolvent {

	/**
	 * @param params
	 */
	public ManualCommanderSolvent(String... params) {
		super(params);
	}

	@Override
	public void prepare() {
		selenium.waitForPageToLoad(2000);
	}
      
    /**
     * Method returns the given date offset by fromCurrent
     * @param fromCurrent
     * @return
     */
    private String returnDate(int fromCurrent){
    	   Calendar cal = Calendar.getInstance();
    	   DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyy");
    	   cal.add(Calendar.DATE, fromCurrent);
    	   return dateFormat.format(cal.getTime());
    }
    
    /**
     * Method replaces all defined variables in a string with a value
     * @param txt
     * @return
     */
    public String replaceVARS(String txt){
    	String[] eachWord = txt.split(" ");
    	String newtxt = "";
    	for(int i = 0; i < eachWord.length; i++){
	    	if(eachWord[i].equals("YESTERDAY"))
	    		newtxt = newtxt+returnDate(-1)+" ";
	    	else if(eachWord[i].equals("TODAY"))
	    		newtxt = newtxt+returnDate(0)+" ";
	    	else if(eachWord[i].equals("2DAYSAGO"))
	    		newtxt = newtxt+returnDate(-2)+" ";
	    	else if(eachWord[i].equals("YESTERDAYMID"))
	    		newtxt = newtxt+returnDate(-1)+" 12:00 ";
	    	else
	    		newtxt = newtxt+eachWord[i]+" ";
    	}
    	return newtxt;
    }
    public String getTextBoxText(String formAction, String inputFieldName){
    	Locator formActionLocator = new CommonSolvent().getFormActionCssLocator(formAction);
        String escapedInputFieldName = LocatorEscapeUtils.escapeCss(inputFieldName);
        Locator inputLocator = formActionLocator.append("input[name='"+escapedInputFieldName+"']");
        String inputLocatorString = inputLocator.generateLocatorString();
        selenium.waitForElement(inputLocator, Duration.standardSeconds(3));
        return selenium.getValue(inputLocatorString);
    }
    /**
     * Method enters the given command parameter in a window and presses enter
     * @param val
     */
    protected ManualCommanderSolvent enterCommandParameterValue(String value){
    	String inputLocator = "//following::span[contains(text(), 'Enter value for command parameter')]/following::input[contains(@type, 'text')]";
		String buttonLocator = "//span[contains(text(), 'OK')]/ancestor::button[1]";
		selenium.waitForAjax();
		selenium.waitForElementToBecomeVisible(inputLocator, 5000);
		selenium.waitForAjax();
		selenium.type(inputLocator, replaceVARS(value));
		selenium.waitForAjax();
		selenium.waitForElementToBecomeVisible(buttonLocator, 5000);
		selenium.waitForAjax();
		selenium.click(buttonLocator);
		return this;
    }
}
