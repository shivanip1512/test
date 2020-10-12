package com.eaton.tests.admin.attributes;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.admin.attributes.AttributeService;
import com.eaton.elements.modals.ConfirmModal;
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
    
    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Features.ADMIN})
    public void attributeList_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Attributes";
        
        String actualPageTitle = page.getLinkedPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }  
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.ADMIN})
    public void attributeList_AttributeDefinitionsSection_TitleCorrect() {
        assertThat(page.getAttrDefSection()).isNotNull();
    }  
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.ADMIN})
    public void attributeList_AttributeAsgmtsSection_TitleCorrect() {
        assertThat(page.getAttrAsgmtSection()).isNotNull();
    }      
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.ADMIN})
    public void attributeList_AttributeAsgmtColumnHeaders_Correct() {
        SoftAssertions softly = new SoftAssertions();
        List<String> headers = page.getAttrAsgmtTable().getListTableHeaders();
        
        softly.assertThat(5).isEqualTo(headers.size());
        softly.assertThat(headers.get(0)).isEqualTo("Attribute Name");
        softly.assertThat(headers.get(1)).isEqualTo("Device Type");
        softly.assertThat(headers.get(2)).isEqualTo("Point Type");
        softly.assertThat(headers.get(3)).isEqualTo("Point Offset");
        softly.assertAll();
    }  
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.ADMIN})
    public void attributeList_AttributeDefinitionsColumnHeaders_Correct() {
        SoftAssertions softly = new SoftAssertions();
        List<String> headers = page.getAttrDefTable().getListTableHeaders();
        
        softly.assertThat(2).isEqualTo(headers.size());
        softly.assertThat(headers.get(0)).isEqualTo("Attribute Name");
        softly.assertAll();
    } 
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.TOOLS, TestConstants.Features.TRENDS })
    public void attributeList_DeleteAttribute_Success() {
        Pair<JSONObject, JSONObject> pair = AttributeService.createAttribute(Optional.empty());

        JSONObject response = pair.getValue1();
        String name = response.getString("name");

        String expectedMessage = name + " deleted successfully.";

        refreshPage(page);

        ConfirmModal modal = page.showDeleteAttrDefByName(name);
                
        modal.clickOkAndWaitForModalToClose();

        String userMsg = page.getUserMessage();

        assertThat(userMsg).isEqualTo(expectedMessage);
    }
}
