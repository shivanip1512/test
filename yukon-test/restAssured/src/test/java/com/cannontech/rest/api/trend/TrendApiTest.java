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
    public void trend_01_Get(ITestContext context) {
        Log.startTestCase("trend_01_Get");
        String trendId = context.getAttribute(TrendHelper.CONTEXT_TREND_ID).toString();
        MockTrendSeries series = trendModel.getTrendSeries().get(0);

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getTrend", trendId);

        MockTrendModel modelResponse = getResponse.as(MockTrendModel.class);
        MockTrendSeries seriesResponse = modelResponse.getTrendSeries().get(0);

        assertTrue("Status code should be 200", getResponse.statusCode() == 200);
        assertTrue("Trend Id should be :" + trendId, modelResponse.getTrendId() == Integer.valueOf(trendId));
        assertTrue("Name Should be : " + modelResponse.getName(), trendModel.getName().equals(modelResponse.getName()));
        assertTrue("Point ID Should be : " + seriesResponse.getPointId(), series.getPointId() == seriesResponse.getPointId());
        assertTrue("Color Should be : " + seriesResponse.getColor(), series.getColor() == seriesResponse.getColor());
        assertTrue("Axis Should be : " + seriesResponse.getAxis(), series.getAxis() == seriesResponse.getAxis());
        assertTrue("Multiplier Should be : " + seriesResponse.getMultiplier(), Double.compare(series.getMultiplier(), seriesResponse.getMultiplier()) == 0);
        assertTrue("Style Should be : " + seriesResponse.getStyle(), series.getStyle() == seriesResponse.getStyle());
        assertTrue("Date Should be : " + seriesResponse.getDate(), series.getDate().compareTo(seriesResponse.getDate()) == 0);
        Log.endTestCase("trend_01_Get");
    }

    @Test(dependsOnMethods = "trend_01_Get")
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
        String trendId = context.getAttribute(TrendHelper.CONTEXT_TREND_ID).toString();
        ExtractableResponse<?> deleteTrendResponse = ApiCallHelper.delete("deleteTrend", trendId);
        softAssert.assertTrue(deleteTrendResponse.statusCode() == 200, "Status code should be 200");
        // Get request to validate trend is deleted
        ExtractableResponse<?> getResponse = ApiCallHelper.get("getTrend", trendId);
        softAssert.assertTrue(getResponse.statusCode() == 400, "Status code should be 400");
        Log.endTestCase("trend_Delete");
    }
}