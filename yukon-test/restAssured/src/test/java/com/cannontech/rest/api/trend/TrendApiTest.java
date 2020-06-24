package com.cannontech.rest.api.trend;

import static org.junit.Assert.assertTrue;

import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.trend.helper.TrendHelper;
import com.cannontech.rest.api.trend.request.MockColor;
import com.cannontech.rest.api.trend.request.MockTrendAxis;
import com.cannontech.rest.api.trend.request.MockTrendModel;
import com.cannontech.rest.api.trend.request.MockTrendSeries;
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
    public void trend_01_Update(ITestContext context) {
        Log.startTestCase("trend_01_Update");
        trendModel.setName("TEST_TREND_UPDATE");
        MockTrendSeries series = trendModel.getTrendSeries().get(0);
        series.setPointId(-100);
        series.setColor(MockColor.BLUE);
        series.setAxis(MockTrendAxis.RIGHT);

        ExtractableResponse<?> updateResponse = ApiCallHelper.put("updateTrend", trendModel,
                context.getAttribute(TrendHelper.CONTEXT_TREND_ID).toString());
        String trendId = updateResponse.path(TrendHelper.CONTEXT_TREND_ID).toString();

        assertTrue("Status code should be 200", updateResponse.statusCode() == 200);
        assertTrue("Trend Id should not be Null", trendId != null);

        MockTrendModel modelResponse = updateResponse.as(MockTrendModel.class);
        MockTrendSeries seriesResponse = modelResponse.getTrendSeries().get(0);

        assertTrue("Name Should be : " + modelResponse.getName(), trendModel.getName().equals(modelResponse.getName()));
        assertTrue("Point ID Should be : " + seriesResponse.getPointId(), series.getPointId() == seriesResponse.getPointId());
        assertTrue("Color Should be : " + seriesResponse.getColor(), series.getColor() == seriesResponse.getColor());
        assertTrue("Axis Should be : " + seriesResponse.getAxis(), series.getAxis() == seriesResponse.getAxis());

        Log.endTestCase("trend_01_Update");
    }

    @Test(dependsOnMethods = "trend_01_Update")
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