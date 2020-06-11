package com.cannontech.rest.api.trend;

import static org.junit.Assert.assertTrue;

import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.trend.helper.TrendHelper;
import com.cannontech.rest.api.trend.request.MockTrendModel;

import io.restassured.response.ExtractableResponse;

public class TrendApiTest {
    MockTrendModel trendModel = null;

    @BeforeClass
    public void setUp() {
        trendModel = TrendHelper.buildTrend();
    }
    @Test
    public void trend_01_Create(ITestContext context) {
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createTrend", trendModel);
        String trendId = createResponse.asString();
        context.setAttribute(TrendHelper.CONTEXT_TREND_ID, trendId);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("Trend Id should not be Null", trendId != null);
    }
}
