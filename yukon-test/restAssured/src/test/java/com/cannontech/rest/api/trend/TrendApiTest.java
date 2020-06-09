package com.cannontech.rest.api.trend;

import org.testng.ITestContext;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.cannontech.rest.api.common.ApiCallHelper;

import io.restassured.response.ExtractableResponse;

public class TrendApiTest {

    @Test
    public void trend_Delete(ITestContext context) {

        SoftAssert softAssert = new SoftAssert();
        // trend create
        context.setAttribute("trendId", 25);
        ExtractableResponse<?> deleteTrendResponse = ApiCallHelper.delete("deleteTrend",
                context.getAttribute("trendId").toString());
        softAssert.assertTrue(deleteTrendResponse.statusCode() == 200,
                "Status code should be 200, delete Trend failed.");
    }
}
