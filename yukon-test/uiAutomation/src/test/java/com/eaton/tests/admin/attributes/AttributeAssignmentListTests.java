package com.eaton.tests.admin.attributes;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.admin.attributes.AttributeService;
import com.eaton.elements.WebTableColumnHeader.SortDirection;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.admin.attributes.AttributesListPage;

public class AttributeAssignmentListTests extends SeleniumTestSetup {

    private AttributesListPage page;
    private DriverExtensions driverExt;
    private List<String> attrNames;
    private List<String> deviceTypes;
    private List<String> pointTypes;
    private List<String> pointOffsets;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String[] attributeName = { "attribute" + timeStamp, "a@ttribute" + timeStamp, "123attr" + timeStamp,
                "VirtAttr" + timeStamp, "ATTRIBUTE1" + timeStamp };

        for (String name : attributeName) {
            AttributeService.createAttributeWithAssignment(Optional.of(name));
        }

        navigate(Urls.Admin.ATTRIBUTES_LIST);

        page = new AttributesListPage(driverExt);
        attrNames = page.getAttrAsgmtTable().getAllRowsTextForColumnByIndex(1);
        deviceTypes = page.getAttrAsgmtTable().getAllRowsTextForColumnByIndex(2);
        pointTypes = page.getAttrAsgmtTable().getAllRowsTextForColumnByIndex(3);
        pointOffsets = page.getAttrAsgmtTable().getAllRowsTextForColumnByIndex(4);
    }

    @AfterMethod
    public void afterMethod() {
        if(getRefreshPage()) {
            refreshPage(page);    
        }
        setRefreshPage(false);
    }        
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.ADMIN, TestConstants.Features.ATTRIBUTES })
    public void attributeList_AttrAsgmtSortNamesAsc_Correct() {
        setRefreshPage(true);
        Collections.sort(attrNames, String.CASE_INSENSITIVE_ORDER);
        
        page.getAttrAsgmtTable().sortTableHeaderByIndex(0, SortDirection.ASCENDING);
        
        List<String> namesList = page.getAttrAsgmtTable().getAllRowsTextForColumnByIndex(1);
        assertThat(attrNames).isEqualTo(namesList);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.ADMIN, TestConstants.Features.ATTRIBUTES })
    public void attributeList_AttrAsgmtSortNamesDesc_Correct() {
        setRefreshPage(true);
        Collections.sort(attrNames, String.CASE_INSENSITIVE_ORDER);
        Collections.reverse(attrNames);

        page.getAttrAsgmtTable().sortTableHeaderByIndex(0, SortDirection.DESCENDING);
        
        List<String> namesList = page.getAttrAsgmtTable().getAllRowsTextForColumnByIndex(1);
        assertThat(attrNames).isEqualTo(namesList);
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.ADMIN, TestConstants.Features.ATTRIBUTES })
    public void attributeList_AttrAsgmtSortDeviceTypesAsc_Correct() {
        setRefreshPage(true);
        Collections.sort(deviceTypes, String.CASE_INSENSITIVE_ORDER);

        page.getAttrAsgmtTable().sortTableHeaderByIndex(1, SortDirection.ASCENDING);

        List<String> typesList = page.getAttrAsgmtTable().getAllRowsTextForColumnByIndex(2);
        assertThat(deviceTypes).isEqualTo(typesList);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.ADMIN, TestConstants.Features.ATTRIBUTES })
    public void attributeList_AttrAsgmtSortDeviceTypesDesc_Correct() {
        setRefreshPage(true);
        Collections.sort(deviceTypes, String.CASE_INSENSITIVE_ORDER);
        Collections.reverse(deviceTypes);

        page.getAttrAsgmtTable().sortTableHeaderByIndex(1, SortDirection.DESCENDING);

        List<String> typesList = page.getAttrAsgmtTable().getAllRowsTextForColumnByIndex(2);

        assertThat(deviceTypes).isEqualTo(typesList);
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.ADMIN, TestConstants.Features.ATTRIBUTES })
    public void attributeList_AttrAsgmtSortPointTypesAsc_Correct() {
        setRefreshPage(true);
        Collections.sort(pointTypes, String.CASE_INSENSITIVE_ORDER);

        page.getAttrAsgmtTable().sortTableHeaderByIndex(2, SortDirection.ASCENDING);

        List<String> pointTypeList = page.getAttrAsgmtTable().getAllRowsTextForColumnByIndex(3);
        assertThat(pointTypes).isEqualTo(pointTypeList);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.ADMIN, TestConstants.Features.ATTRIBUTES })
    public void attributeList_AttrAsgmtSortPointTypesDesc_Correct() {
        setRefreshPage(true);
        Collections.sort(pointTypes, String.CASE_INSENSITIVE_ORDER);
        Collections.reverse(pointTypes);

        page.getAttrAsgmtTable().sortTableHeaderByIndex(2, SortDirection.DESCENDING);

        List<String> pointTypeList = page.getAttrAsgmtTable().getAllRowsTextForColumnByIndex(3);

        assertThat(pointTypes).isEqualTo(pointTypeList);
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.ADMIN, TestConstants.Features.ATTRIBUTES })
    public void attributeList_AttrAsgmtSortPointOffsetAsc_Correct() {
        setRefreshPage(true);
        Collections.sort(pointOffsets, String.CASE_INSENSITIVE_ORDER);

        page.getAttrAsgmtTable().sortTableHeaderByIndex(3, SortDirection.ASCENDING);

        List<String> pointOffsetList = page.getAttrAsgmtTable().getAllRowsTextForColumnByIndex(4);
        assertThat(pointOffsets).isEqualTo(pointOffsetList);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.ADMIN, TestConstants.Features.ATTRIBUTES })
    public void attributeList_AttrAsgmtSortPointOffsetDesc_Correct() {
        setRefreshPage(true);
        Collections.sort(pointOffsets, String.CASE_INSENSITIVE_ORDER);
        Collections.reverse(pointOffsets);

        page.getAttrAsgmtTable().sortTableHeaderByIndex(3, SortDirection.DESCENDING);

        List<String> pointOffsetList = page.getAttrAsgmtTable().getAllRowsTextForColumnByIndex(4);

        assertThat(pointOffsets).isEqualTo(pointOffsetList);
    }
}
