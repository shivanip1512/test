package com.cannontech.rest.api.trend;

import org.testng.ITestContext;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.utilities.Log;

import io.restassured.response.ExtractableResponse;

public class TrendApiTest {

    @Test
    public void trend_Delete(ITestContext context) {

        SoftAssert softAssert = new SoftAssert();
        Log.startTestCase("trend_Delete");
        // TODO
        /**
         * I have hard coded trendId as create API is not available.
         * Once YUK-22204 is done we will be use the created trend for deletion.If YUK-22204 is not done in this sprint i will call
         * create API to create a trend and will delete the same.
         **/
        context.setAttribute("trendId", 25);
        ExtractableResponse<?> deleteTrendResponse = ApiCallHelper.delete("deleteTrend",
                context.getAttribute("trendId").toString());
        softAssert.assertTrue(deleteTrendResponse.statusCode() == 200, "Status code should be 200");
        // TODO
        /**
         * We need to make sure the above trend is deleted .For this we need to make a get call and verify that above trend is
         * deleted or not once get API is available.
         **/
        Log.endTestCase("trend_Delete");
    }
}