package com.cannontech.rest.api.documentation.trend;

import org.testng.annotations.Test;

public class TrendApiDoc extends TrendApiDocBase {

    private String trendId = null;

    @Test
    public void Test_Trend_01_Create() {
        trendId = createDoc();
    }

    @Override
    protected String getTrendId() {
        return trendId;
    }
}
