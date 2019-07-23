package com.cannontech.rest.api.data;

import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

public class DataProviderClass {

	
	
	/**
	 * DataProvider provides data to test method in the form of object array
	 * Data provided - 
	 * col1 : Group Name
	 * col2 : Expected field errors code in response
	 * col3 : Expected response code
	 */
	
	@DataProvider(name = "GroupNameData")
	public Object[][] getGroupNameData(ITestContext context) {
		
		return new Object[][] {
            { "", "Group Name is required.", 422 },
            { "Test\\Nest", "Cannot be blank or include any of the following characters: / \\ , ' \" |", 422 },
            { "Test,Nest", "Cannot be blank or include any of the following characters: / \\ , ' \" |", 422 },
            { "TestNestMoreThanSixtyCharacter_TestNestMoreThanSixtyCharacters", "Exceeds maximum length of 60.", 422 },
            {context.getAttribute("Nest_GrpName"), "Group Name must be unique.", 422}
        };
	}
	
	
	/**
	 * DataProvider provides data to test method in the form of object array
	 * Data provided - 
	 * col1 : KwCapacity
	 * col2 : Expected field errors code in response
	 * col3 : Expected response code
	 */
	
	@DataProvider(name = "KwCapacityData")
	public Object[][] getKwCapacityData() {
		
		return new Object[][] {
            //{(float) , "kW Capacity is required.", 422 },
            { (float) -222.0 , "Must be between 0 and 99,999.999.", 422 },
            { (float) 100000.0, "Must be between 0 and 99,999.999.", 422 }
        };
	}
}
