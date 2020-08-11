package com.eaton.tests.tools.trends;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.tools.webtrends.TrendCreateBuilder;
import com.eaton.builders.tools.webtrends.TrendPointBuilder;
import com.eaton.elements.WebTableRow;
import com.eaton.elements.WebTableRow.Icon;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.tools.trends.TrendEditPage;

import io.restassured.response.ExtractableResponse;

public class TrendEditTests extends SeleniumTestSetup {

    private TrendEditPage editPage;
    private DriverExtensions driverExt;
    private Integer trendId;
    String trendName;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        Pair<JSONObject, ExtractableResponse<?>> pair = new TrendCreateBuilder.Builder(Optional.empty())
                .create();

        ExtractableResponse<?> response = pair.getValue1();

        trendId = response.path("trendId");
        trendName = response.path("name").toString();
        
        navigate(Urls.Tools.TREND + trendId + Urls.EDIT);
        editPage = new TrendEditPage(driverExt, Urls.Tools.TREND, trendId);
    }    

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS })
    public void trendEdit_pageTitleCorrect() {
        navigate(Urls.Tools.TREND + trendId + Urls.EDIT);
        
        final String EXPECTED_TITLE = "Edit Trend: " + trendName;

        String actualPageTitle = editPage.getPageTitle();

        assertThat(EXPECTED_TITLE).isEqualTo(actualPageTitle);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.COMM_CHANNELS, TestConstants.Assets.ASSETS })
    public void trendEdit_RemovePoint_Successfully() {                
        Pair<JSONObject, ExtractableResponse<?>> pair = new TrendCreateBuilder.Builder(Optional.empty())
                .withPoints(new JSONObject[] {new TrendPointBuilder.Builder()
                        .withpointId(4999)
                        .withLabel(Optional.empty())
                        .withColor(Optional.empty())
                        .withStyle(Optional.empty())
                        .withType(Optional.empty())
                        .withAxis(Optional.empty())
                        .withMultiplier(Optional.empty())
                        .withDate(Optional.empty())
                        .build()})
                .create();

        ExtractableResponse<?> response = pair.getValue1();
        
        Integer editTrendId = response.path("trendId");
        
        navigate(Urls.Tools.TREND + editTrendId + Urls.EDIT);

        editPage.getSetupTab().click();
        WebTableRow row = editPage.getPointSetupTable().getDataRowByIndex(0);
        row.clickIcon(Icon.REMOVE);
        
        editPage.getSave().click();
    }
}
