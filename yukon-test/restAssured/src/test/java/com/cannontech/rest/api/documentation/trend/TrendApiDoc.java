package com.cannontech.rest.api.documentation.trend;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.documentation.DocumentationBase;
import com.cannontech.rest.api.documentation.DocumentationFields;
import com.cannontech.rest.api.documentation.DocumentationFields.Copy;
import com.cannontech.rest.api.documentation.DocumentationFields.Create;
import com.cannontech.rest.api.documentation.DocumentationFields.Delete;
import com.cannontech.rest.api.documentation.DocumentationFields.Get;
import com.cannontech.rest.api.documentation.DocumentationFields.Update;
import com.cannontech.rest.api.trend.helper.TrendHelper;
import com.cannontech.rest.api.trend.request.MockTrendModel;

public class TrendApiDoc extends DocumentationBase {

    private String trendId = null;
    public final static String idStr = "trendId";
    public final static String idDescStr = "Trend Id";
    
    private MockTrendModel getMockObject() {
        return TrendHelper.buildTrend();
    }
    
    private static List<FieldDescriptor> getFieldDescriptors() {
        FieldDescriptor[] trendFieldDescriptor = new FieldDescriptor[] {
                fieldWithPath("name")
                    .type(JsonFieldType.STRING)
                    .description("Trend Name"),
                fieldWithPath("trendSeries[].type")
                    .type(JsonFieldType.STRING)
                    .optional()
                    .description("Graph Type. Expected: BASIC_TYPE, USAGE_TYPE, PEAK_TYPE, YESTERDAY_TYPE, MARKER_TYPE, DATE_TYPE. Default Type: BASIC_TYPE"),
                fieldWithPath("trendSeries[].pointId")
                    .type(JsonFieldType.NUMBER)
                    .description("Point ID"),
                fieldWithPath("trendSeries[].label")
                    .type(JsonFieldType.STRING)
                    .description("Device Name / Point Name. Max length 40 Character"),
                fieldWithPath("trendSeries[].color")
                    .type(JsonFieldType.STRING)
                    .optional()
                    .description("Color. Expected: BLACK, BLUE, CYAN, GREY, GREEN, MAGENTA, ORANGE, PINK, RED, YELLOW. Default Color: BLUE"),
                fieldWithPath("trendSeries[].axis")
                    .type(JsonFieldType.STRING)
                    .optional()
                    .description("Axis. Expected: LEFT, RIGHT. Default Axis: LEFT"),
                fieldWithPath("trendSeries[].multiplier")
                    .type(JsonFieldType.NUMBER)
                    .optional()
                    .description("Multiplier. Default Value: 1"),
                fieldWithPath("trendSeries[].style")
                    .type(JsonFieldType.STRING)
                    .optional()
                    .description("Render Style. Expected:LINE, BAR, STEP. Default Style: LINE"),
                fieldWithPath("trendSeries[].date")
                    .type(JsonFieldType.STRING)
                    .optional()
                    .description("Date in mm/dd/yyyy format. Applicable only when type is DATE_TYPE")
        };
        return new ArrayList<>(Arrays.asList(trendFieldDescriptor));
    }

    @Test
    public void Test_Trend_01_Create() {
        trendId = createDoc();
    }

    @Test(dependsOnMethods = "Test_Trend_01_Create")
    public void Test_Trend_01_Get() {
        getDoc();
    }

    @Test(dependsOnMethods = "Test_Trend_01_Get")
    public void Test_Trend_01_Update() {
        updateAllDoc();
    }

    @Test(dependsOnMethods = "Test_Trend_01_Update")
    public void Test_Trend_01_Delete() {
        deleteDoc();
    }

    @Override
    protected Get buildGetFields() {
        List<FieldDescriptor> responseFields = getFieldDescriptors();
        responseFields.add(0, fieldWithPath(idStr).type(JsonFieldType.NUMBER).description(idDescStr));
        String url = ApiCallHelper.getProperty("getTrend") + trendId;
        return new DocumentationFields.Get(responseFields, url);
    }

    @Override
    protected Create buildCreateFields() {
        List<FieldDescriptor> requestFields = getFieldDescriptors();
        List<FieldDescriptor> responseFields = getFieldDescriptors();
        responseFields.add(0, fieldWithPath(idStr).type(JsonFieldType.NUMBER).description(idDescStr));
        String url = ApiCallHelper.getProperty("createTrend");
        return new DocumentationFields.Create(requestFields, responseFields, idStr, idDescStr, getMockObject(), url);
    }

    @Override
    protected Update buildUpdateFields() {
        List<FieldDescriptor> requestFields = getFieldDescriptors();
        List<FieldDescriptor> responseFields = getFieldDescriptors();
        responseFields.add(0, fieldWithPath(idStr).type(JsonFieldType.NUMBER).description(idDescStr));
        String url = ApiCallHelper.getProperty("updateTrend") + trendId;
        return new DocumentationFields.Update(requestFields, responseFields, idStr, idDescStr, getMockObject(), url);
    }

    @Override
    protected Copy buildCopyFields() {
        return null;
    }

    @Override
    protected Delete buildDeleteFields() {
        String url = ApiCallHelper.getProperty("deleteTrend") + trendId;
        return new DocumentationFields.Delete(url);
    }
}

