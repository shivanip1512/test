package com.eaton.tests.admin.attributes;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.admin.attributes.AttributeService;
import com.eaton.elements.TextEditElement;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.admin.attributes.AttributesListPage;

public class AttributeEditTests extends SeleniumTestSetup {

    private AttributesListPage page;
    private DriverExtensions driverExt;
    private String name;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        
        Pair<JSONObject, JSONObject> pair = AttributeService.createAttribute(Optional.empty());
        
        JSONObject response = pair.getValue1();
        name = response.getString("name");
        
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
    public void attributeEdit_AttributeName_MaxLength60Chars() {
        setRefreshPage(true);
        TextEditElement el = page.editAttributeDefByName(name);
        
        String maxLength = el.getMaxLength();
        assertThat(maxLength).isEqualTo("60");
    } 
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.ADMIN})
    public void attributeEdit_AttributeName_InvalidCharsValidation() {
        setRefreshPage(true);
        
        TextEditElement el = page.editAttributeDefByNameAndClickSave(name, "Edit Attr / \\ , ' \" |");
        
        assertThat(el.getValidationError()).isEqualTo("Name must not contain any of the following characters: / \\ , ' \" |.");
    }
}
