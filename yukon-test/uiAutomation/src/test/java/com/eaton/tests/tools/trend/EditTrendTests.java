package com.eaton.tests.tools.trend;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;

import org.json.simple.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.trend.TrendEditPage;
import com.eaton.rest.api.assets.AssetsCreateRequestAPI;
import com.eaton.rest.api.dbetoweb.JsonFileHelper;

import io.restassured.response.ExtractableResponse;

public class EditTrend extends SeleniumTestSetup{
    
    private DriverExtensions driverExt;
    private TrendEditPage trendEditPage;
    private String trendId = "12";
    private String trendName;
    private JSONObject jo;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        trendName = "AT Trend " + timeStamp;

        String payloadFile = System.getProperty("user.dir")
                + "\\src\\test\\resources\\payload\\payload.commchannel\\CommChannelTCP.json";

        Object body = JsonFileHelper.parseJSONFile(payloadFile);
        jo = (JSONObject) body;
        jo.put("name", trendName);
        ExtractableResponse<?> createResponse = AssetsCreateRequestAPI.createCommChannel(body);
        trendId = createResponse.path("id").toString();
    }
    
    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        navigate(Urls.Tools.TREND + trendId+Urls.EDIT);
        trendEditPage = new TrendEditPage(driverExt);
    }

    @Test
    public void editTrend_PageTitleCorrect() {
/*        trendEditPage.getName().setInputValue("trendEdit");
        //trendEditPage.get
        trendEditPage.getTabElement().clickTab("Additional Options");*/
        //waitForUrlToLoad(Urls.Tools.TREND + trendId+Urls.EDIT, Optional.empty());
        //trendEditPage.getCancel().click();
        final String EXPECTED_TITLE = "Edit Trend: ";

        String actualPageTitle = trendEditPage.getPageTitle();
        System.out.println(actualPageTitle);

        //assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

}
