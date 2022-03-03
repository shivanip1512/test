package com.eaton.tests.admin.attributes;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.admin.attributes.AttributeService;
import com.eaton.elements.WebTable.SortDirection;
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
    private List<String> offsets;
    private List<Integer> pointOffsets = new ArrayList<Integer>();
    private String attrNameNoAssgmt;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String[] attributeName = { "Attribute" + timeStamp, "Cattribute" + timeStamp, "Jattribute" + timeStamp,
                "Qattribute" + timeStamp, "Zattr" + timeStamp };

        for (String name : attributeName) {
            AttributeService.createAttributeWithAssignment(Optional.of(name));
        }
        
        Pair<JSONObject, JSONObject> pair = AttributeService.createAttribute(Optional.empty());
        JSONObject response = pair.getValue1();
        attrNameNoAssgmt = response.getString("name");

        navigate(Urls.Admin.ATTRIBUTES_LIST);

        page = new AttributesListPage(driverExt);
        attrNames = page.getAttrAsgmtTable().getDataRowsTextByCellIndex(1);
        deviceTypes = page.getAttrAsgmtTable().getDataRowsTextByCellIndex(2);
        pointTypes = page.getAttrAsgmtTable().getDataRowsTextByCellIndex(3);
        offsets = page.getAttrAsgmtTable().getDataRowsTextByCellIndex(4);
        pointOffsets.addAll(offsets.stream().map(Integer::valueOf).collect(Collectors.toList()));
    }

    @AfterMethod
    public void afterMethod() {
        if(getRefreshPage()) {
            refreshPage(page);    
        }
        setRefreshPage(false);
    }        
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.ADMIN, TestConstants.Features.ATTRIBUTES })
    public void attrAsgmtListTests_SortNamesAsc_Correct() {
        setRefreshPage(true);
        Collections.sort(attrNames, String.CASE_INSENSITIVE_ORDER);
        
        page.getAttrAsgmtTable().sortTableHeaderByIndex(0, SortDirection.ASCENDING);
        
        List<String> namesList = page.getAttrAsgmtTable().getDataRowsTextByCellIndex(1);
        assertThat(attrNames).isEqualTo(namesList);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.ADMIN, TestConstants.Features.ATTRIBUTES })
    public void attrAsgmtListTests_SortNamesDesc_Correct() {
        setRefreshPage(true);
        Collections.sort(attrNames, String.CASE_INSENSITIVE_ORDER);
        Collections.reverse(attrNames);

        page.getAttrAsgmtTable().sortTableHeaderByIndex(0, SortDirection.DESCENDING);
        
        List<String> namesList = page.getAttrAsgmtTable().getDataRowsTextByCellIndex(1);
        assertThat(attrNames).isEqualTo(namesList);
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.ADMIN, TestConstants.Features.ATTRIBUTES })
    public void attrAsgmtListTests_SortDeviceTypesAsc_Correct() {
        setRefreshPage(true);
        Collections.sort(deviceTypes, String.CASE_INSENSITIVE_ORDER);

        page.getAttrAsgmtTable().sortTableHeaderByIndex(1, SortDirection.ASCENDING);

        List<String> typesList = page.getAttrAsgmtTable().getDataRowsTextByCellIndex(2);
        assertThat(deviceTypes).isEqualTo(typesList);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.ADMIN, TestConstants.Features.ATTRIBUTES })
    public void attrAsgmtListTests_SortDeviceTypesDesc_Correct() {
        setRefreshPage(true);
        Collections.sort(deviceTypes, String.CASE_INSENSITIVE_ORDER);
        Collections.reverse(deviceTypes);

        page.getAttrAsgmtTable().sortTableHeaderByIndex(1, SortDirection.DESCENDING);

        List<String> typesList = page.getAttrAsgmtTable().getDataRowsTextByCellIndex(2);

        assertThat(deviceTypes).isEqualTo(typesList);
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.ADMIN, TestConstants.Features.ATTRIBUTES })
    public void attrAsgmtListTests_PointTypesAsc_Correct() {
        setRefreshPage(true);
        Collections.sort(pointTypes, String.CASE_INSENSITIVE_ORDER);

        page.getAttrAsgmtTable().sortTableHeaderByIndex(2, SortDirection.ASCENDING);

        List<String> pointTypeList = page.getAttrAsgmtTable().getDataRowsTextByCellIndex(3);
        assertThat(pointTypes).isEqualTo(pointTypeList);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.ADMIN, TestConstants.Features.ATTRIBUTES })
    public void attrAsgmtListTests_SortPointTypesDesc_Correct() {
        setRefreshPage(true);
        Collections.sort(pointTypes, String.CASE_INSENSITIVE_ORDER);
        Collections.reverse(pointTypes);

        page.getAttrAsgmtTable().sortTableHeaderByIndex(2, SortDirection.DESCENDING);

        List<String> pointTypeList = page.getAttrAsgmtTable().getDataRowsTextByCellIndex(3);

        assertThat(pointTypes).isEqualTo(pointTypeList);
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.ADMIN, TestConstants.Features.ATTRIBUTES })
    public void attrAsgmtListTests_SortPointOffsetAsc_Correct() {
        setRefreshPage(true);
        Collections.sort(pointOffsets);  

        page.getAttrAsgmtTable().sortTableHeaderByIndex(3, SortDirection.ASCENDING);

        List<String> pointOffsetList = page.getAttrAsgmtTable().getDataRowsTextByCellIndex(4);
        List<Integer> offsetList = new ArrayList<Integer>();
        offsetList.addAll(pointOffsetList.stream().map(Integer::valueOf).collect(Collectors.toList()));
        assertThat(pointOffsets).isEqualTo(offsetList);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.ADMIN, TestConstants.Features.ATTRIBUTES })
    public void attrAsgmtListTests_SortPointOffsetDesc_Correct() {
        setRefreshPage(true);
        Collections.sort(pointOffsets);
        Collections.reverse(pointOffsets);

        page.getAttrAsgmtTable().sortTableHeaderByIndex(3, SortDirection.DESCENDING);

        List<String> pointOffsetList = page.getAttrAsgmtTable().getDataRowsTextByCellIndex(4);
        List<Integer> offsetList = new ArrayList<Integer>();
        offsetList.addAll(pointOffsetList.stream().map(Integer::valueOf).collect(Collectors.toList()));
        assertThat(pointOffsets).isEqualTo(offsetList);
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.ADMIN, TestConstants.Features.ATTRIBUTES })
    public void attrAsgmtListTests_FilterByAttrNoAssgmt_NoResultsFound() {
        setRefreshPage(true);
        final String EXPECTED_MSG = "No results found.";

        page.getFilterByAttr().selectItemByText(attrNameNoAssgmt);
        page.getFilterBtn().click();
        page.getAttrAsgmtTable().waitForFilter();
        
        String msg = page.getAttrAsgmtTable().getTableMessage();
        assertThat(msg).isEqualTo(EXPECTED_MSG);
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void attrAsgmtListTests_FilterByDeviceType_ResultsCorrect() {
        setRefreshPage(true);
        String deviceType = deviceTypes.get(1);
        
        page.getFilterByDeviceTypes().selectItemByText(deviceType);
        page.getFilterBtn().click();
        page.getAttrAsgmtTable().waitForFilter();

        List<String> actualTypes = page.getAttrAsgmtTable().getDataRowsTextByCellIndex(2);

        assertThat(actualTypes).contains(deviceType);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.DEMAND_RESPONSE })
    public void attrAsgmtListTests_FilterByAttr_ResultsCorrect() {
        setRefreshPage(true);
        String attrName = attrNames.get(1);
        
        page.getFilterByAttr().selectItemByText(attrName);
        page.getFilterBtn().click();
        page.getAttrAsgmtTable().waitForFilter();
        
        List<String> actualNames = page.getAttrAsgmtTable().getDataRowsTextByCellIndex(1);

        assertThat(actualNames).contains(attrName);
    }
}
