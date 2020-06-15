package com.cannontech.rest.api.trend;

import static org.junit.Assert.assertTrue;

import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.trend.helper.TrendHelper;
import com.cannontech.rest.api.trend.request.MockTrendModel;
import com.cannontech.rest.api.utilities.Log;

import io.restassured.response.ExtractableResponse;

public class TrendApiTest {

    MockTrendModel trendModel = null;

    @BeforeClass
    public void setUp() {
        trendModel = TrendHelper.buildTrend();
    }

    @Test
    public void trend_01_Create(ITestContext context) {
        Log.startTestCase("trend_01_Create");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createTrend", trendModel);
        String trendId = createResponse.path(TrendHelper.CONTEXT_TREND_ID).toString();
        context.setAttribute(TrendHelper.CONTEXT_TREND_ID, trendId);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("Trend Id should not be Null", trendId != null);
        Log.endTestCase("trend_01_Create");
    }

    @Test(dependsOnMethods = "trend_01_Create")
    public void trend_01_Delete(ITestContext context) {
        SoftAssert softAssert = new SoftAssert();
        Log.startTestCase("trend_01_Delete");
        ExtractableResponse<?> deleteTrendResponse = ApiCallHelper.delete("deleteTrend",
                context.getAttribute(TrendHelper.CONTEXT_TREND_ID).toString());
        softAssert.assertTrue(deleteTrendResponse.statusCode() == 200, "Status code should be 200");
        // TODO
        /**
         * We need to make sure the above trend is deleted .For this we need to make a get call and verify that above trend is
         * deleted or not once get API is available.
         **/
        Log.endTestCase("trend_Delete");
    }
}