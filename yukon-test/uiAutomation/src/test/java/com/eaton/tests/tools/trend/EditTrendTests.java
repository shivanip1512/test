package com.eaton.tests.tools.trend;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.json.simple.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eaton.elements.modals.SelectPointModal;
import com.eaton.elements.modals.trend.AddMarkerModal;
import com.eaton.elements.modals.trend.EditPointModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.trend.TrendEditPage;
import com.eaton.rest.api.assets.AssetsCreateRequestAPI;
import com.eaton.rest.api.drsetup.JsonFileHelper;
import com.eaton.rest.api.trend.TrendRequest;

import io.restassured.response.ExtractableResponse;

public class EditTrendTests extends SeleniumTestSetup{
    
    private DriverExtensions driverExt;
    private TrendEditPage trendEditPage;
    private String trendId;
    private String trendName;
    private JSONObject jo;
    private String timeStamp;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        trendName = "AT Trend " + timeStamp;

        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.trend\\createTrend.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        jo = (JSONObject) body;
        jo.put("name", trendName);
        ExtractableResponse<?> createResponse = TrendRequest.createTrend(body);
        trendId = createResponse.path("trendId").toString();
    }
    
    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        navigate(Urls.Tools.TREND + trendId + Urls.EDIT);
        trendEditPage = new TrendEditPage(driverExt);
    }

    @Test
    public void editTrend_PageTitleCorrect() {
        final String EXPECTED_TITLE = "Edit Trend: " +trendName;

        String actualPageTitle = trendEditPage.getPageTitle();
        System.out.println(actualPageTitle);

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
    
    @Test
    public void editTrend_AllFieldSuccess() {
        trendName = "AT EditTrend " + timeStamp;
        trendEditPage.getName().setInputValue(trendName);
        trendEditPage.getSave().click();
        
        assertThat(trendEditPage.getUserMessage()).isEqualTo(trendName + " saved successfully.");
    }
    
    @Test
    public void editTrend_WithMultipleMarkerSetupSuccess() {
        trendEditPage.getTabElement().clickTabAndWait("Additional Options");
        trendEditPage.getAddMarker().click();
        AddMarkerModal addMarkerModal1 = new AddMarkerModal(driverExt, Optional.of("Add Marker"), Optional.empty());
        addMarkerModal1.getLabel().setInputValue("Marker1");
        addMarkerModal1.clickOkAndWait();
        //trendEditPage = new TrendEditPage(driverExt);
        trendEditPage.getAddMarker().click();
        AddMarkerModal addMarkerModal2 = new AddMarkerModal(driverExt, Optional.of("Add Marker"), Optional.of("20"));
        addMarkerModal2.getLabel().setInputValue("Marker2");
        addMarkerModal2.clickOkAndWait();
        
        trendEditPage.getTabElement().clickTabAndWait("Setup");
        trendEditPage.getSave().click();

        assertThat(trendEditPage.getUserMessage()).isEqualTo(trendName + " saved successfully.");
        
    }
}
