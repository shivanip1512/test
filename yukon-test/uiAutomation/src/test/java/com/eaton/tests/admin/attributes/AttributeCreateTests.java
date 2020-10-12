package com.eaton.tests.admin.attributes;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.admin.attributes.AttributeService;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.admin.attributes.AttributesListPage;

public class AttributeCreateTests extends SeleniumTestSetup {

    private AttributesListPage page;
    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        navigate(Urls.Admin.ATTRIBUTES_LIST);
        page = new AttributesListPage(driverExt);
    }
    
    @AfterMethod(alwaysRun=true)
    public void afterMethod() {
        if (getRefreshPage()) {
            refreshPage(page);    
        }
        setRefreshPage(false);
    }

    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.ADMIN})
    public void attributeCreate_AttributeName_PlaceholderCorrect() {
        String actualPlaceholderName = page.getAttributeName().getPlaceHolder();
        
        assertThat(actualPlaceholderName).isEqualTo("Attribute Name");
    } 
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.ADMIN})
    public void attributeCreate_AttributeName_MaxLength60Chars() {
        String maxLength = page.getAttributeName().getMaxLength();
        
        assertThat(maxLength).isEqualTo("60");
    } 
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.ADMIN})
    public void attributeCreate_AttributeName_InvalidCharsValidation() {
        page.getAttributeName().setInputValue("Create Attr / \\ , ' \" |");
        page.getCreateBtn().click();

        String actualErrorMsg = page.getAttributeName().getValidationError();

        assertThat(actualErrorMsg).isEqualTo("Name must not contain any of the following characters: / \\ , ' \" |.");
    } 
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TRENDS })
    public void attributeCreate_AttributeName_RequiredValidation() {
        page.getAttributeName().clearInputValue();
        page.getCreateBtn().click();

        String actualErrorMsg = page.getAttributeName().getValidationError();

        assertThat(actualErrorMsg).isEqualTo("Attribute Name is required.");
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.TRENDS })
    public void attributeCreate_AttributeName_AlreadyExistsValidation() {
        Pair<JSONObject, JSONObject> pair = AttributeService.createAttribute(Optional.empty());        

        JSONObject response = pair.getValue1();
        String name = response.getString("name");
        
        final String EXPECTED_MSG = name + " could not be saved. Error: Unable to create Custom Attribute. An attribute with this name may already exist.";
        
        page.getAttributeName().setInputValue(name);
        page.getCreateBtn().click();

        String actualErrorMsg = page.getUserMessage();

        assertThat(actualErrorMsg).isEqualTo(EXPECTED_MSG);
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.ADMIN})
    public void attributeCreate_AllFields_Success() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Attr " + timeStamp;
        
        page.getAttributeName().setInputValue(name);
        page.getCreateBtn().click();

        String actualMsg = page.getUserMessage();
        
        assertThat(actualMsg).isEqualTo(name + " saved successfully.");
    } 
}
