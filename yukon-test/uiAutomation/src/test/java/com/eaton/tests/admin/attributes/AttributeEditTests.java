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
import com.eaton.elements.TextEditElement;
import com.eaton.elements.editwebtable.EditWebTableRow;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.admin.attributes.AttributesListPage;

public class AttributeEditTests extends SeleniumTestSetup {

    private AttributesListPage page;
    private DriverExtensions driverExt;
    private String name;
    private Integer id;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);

        Pair<JSONObject, JSONObject> pair = AttributeService.createAttribute(Optional.empty());

        JSONObject response = pair.getValue1();
        name = response.getString("name");
        id = response.getInt("customAttributeId");

        navigate(Urls.Admin.ATTRIBUTES_LIST);
        page = new AttributesListPage(driverExt);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        if (getRefreshPage()) {
            refreshPage(page);
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void attributeEdit_AttributeName_MaxLength60Chars() {
        TextEditElement el = page.editAttributeDefByName(name);

        String maxLength = el.getMaxLength();
        assertThat(maxLength).isEqualTo("60");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void attributeEdit_AttributeName_AlreadyExistsValidation() {
        setRefreshPage(true);
        Pair<JSONObject, JSONObject> pair = AttributeService.createAttribute(Optional.empty());

        JSONObject response = pair.getValue1();
        String updateName = response.getString("name");
        refreshPage(page);

        page.editAttributeDefNameAndClickSave(name, updateName);
        
        assertThat(page.getUserMessage()).isEqualTo(updateName + " could not be saved. Error: Unable to update Custom Attribute. An attribute with this name may already exist.");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void attributeEdit_AttributeName_RequiredValidation() {
        setRefreshPage(true);
        Pair<JSONObject, JSONObject> pair = AttributeService.createAttribute(Optional.empty());

        JSONObject response = pair.getValue1();
        String attrName = response.getString("name");
        Integer attrId = response.getInt("customAttributeId");
        refreshPage(page);

        page.editAttributeDefNameAndClickSave(attrName, "");
        
        TextEditElement el = page.waitForSave(attrId.toString());

        String actualErrorMsg = el.getValidationError();

        assertThat(actualErrorMsg).isEqualTo("Attribute Name is required.");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void attributeEdit_AttributeName_InvalidCharsValidation() {
        setRefreshPage(true);

        page.editAttributeDefNameAndClickSave(name, "Edit. /|");
        
        TextEditElement el = page.waitForSave(id.toString());

        String actualErrorMsg = el.getValidationError();

        assertThat(actualErrorMsg).isEqualTo("Name must not contain any of the following characters: / \\ , ' \" |.");
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void attributeEdit_AllFields_Success() {
        setRefreshPage(true);
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        Pair<JSONObject, JSONObject> pair = AttributeService.createAttribute(Optional.empty());

        JSONObject response = pair.getValue1();
        String attrName = response.getString("name");
        refreshPage(page);

        String updatedName = "AT Attr Edit " + timeStamp;
        page.editAttributeDefNameClickSaveAndWait(attrName, updatedName);
        
        assertThat(page.getUserMessage()).isEqualTo(updatedName + " saved successfully.");
    }
    
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.ATTRIBUTES, TestConstants.Features.ADMIN })
    public void attributeEdit_Cancel_Success() {
        setRefreshPage(true);

        page.editAttributeDefNameAndClickCancelAndWait(name, "Atr No Edit");    
        
        EditWebTableRow row = page.getAttrDefTable().getDataRowByName(name);
        
        assertThat(row).isNotNull();
    }
}
