package com.eaton.tests.admin.attributes;

import static org.assertj.core.api.Assertions.assertThat;

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

public class AttributesListTests extends SeleniumTestSetup {

    private AttributesListPage page;
    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        driverExt = getDriverExt();
        navigate(Urls.Admin.ATTRIBUTES_LIST);
        page = new AttributesListPage(driverExt);
    }
    
    @AfterMethod(alwaysRun=true)
    public void afterMethod() {
        refreshPage(page);
    }
    
    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Features.ADMIN})
    public void attributeList_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Attributes";
        
        String actualPageTitle = page.getLinkedPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }  
    
    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Features.ADMIN})
    public void attributeList_AttributeDefinitionsSection_TitleCorrect() {
        assertThat(page.getAttrDefSection()).isNotNull();
    }  
    
    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Features.ADMIN})
    public void attributeList_AttributeAssignmentsSection_TitleCorrect() {
        assertThat(page.getAttrAsgmtSection()).isNotNull();
    }  
    
    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Features.ADMIN})
    public void testing() {
//        Pair<JSONObject, JSONObject> pair = AttributeService.createAttribute(Optional.empty());
//        
//        JSONObject response = pair.getValue1();
//        
//        int attributeId = response.getInt("customAttributeId");
//        
//        Pair<JSONObject, JSONObject> asgmt = AttributeService.createAttributeAssignment(attributeId, Optional.empty(), Optional.empty());
//        
//        JSONObject created = asgmt.getValue1();
//        
//        assertThat(created).isNotNull();
    }
}
