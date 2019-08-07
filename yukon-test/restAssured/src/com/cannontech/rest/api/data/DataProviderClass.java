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

        return new Object[][] { { "", "Group Name is required.", 422 },
            { "Test\\Nest", "Cannot be blank or include any of the following characters: / \\ , ' \" |", 422 },
            { "Test,Nest", "Cannot be blank or include any of the following characters: / \\ , ' \" |", 422 },
            { "TestNestMoreThanSixtyCharacter_TestNestMoreThanSixtyCharacters", "Exceeds maximum length of 60.", 422 },
            { context.getAttribute("Itron_GrpName"), "Group Name must be unique.", 422 } };
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
            // {(float) , "kW Capacity is required.", 422 },
            { (float) -222.0, "Must be between 0 and 99,999.999.", 422 },
            { (float) 100000.0, "Must be between 0 and 99,999.999.", 422 } };
    }

    /**
     * DataProvider provides data to test method in the form of object array
     * Data provided -
     * col1 : VirtualRelayIdData
     * col2 : Expected field errors code in response
     * col3 : Expected response code
     */
    @DataProvider(name = "VirtualRelayIdData")
    public Object[][] getVirtualRelayIdData() {

        return new Object[][] { 
        //{ "", "Virtual RelayId  is required.", 422 },
        { (int) -2, "Must be between 1 and 8.", 422 }, 
        { (int) 11, "Must be between 1 and 8.", 422 } };
    }

    /**
     * DataProvider provides data to test method in the form of object array
     * Data provided -
     * col1 : goldAddress
     * col2 : silverAddress
     * col3 : Expected field errors code in response
     * col4 : Expected response code
     */
    @DataProvider(name = "EmetconAddressData")
    public Object[][] getEmetconAddressData() {

        return new Object[][] { { "", "22", "Gold Address is required.", 422 },
            { "3", "", "Silver Address is required.", 422 }, { "5", "22", "Must be between 0 and 4.", 422 },
            { "0", "0", "Gold Address must be between 1 and 4 when Gold is selected for Address to Use.", 422 },
            { "-1", "22", "Must be between 0 and 4.", 422 }, { "3", "61", "Must be between 0 and 60.", 422 },
            { "3", "-1", "Must be between 0 and 60.", 422 } };
    }
}
