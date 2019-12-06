package com.cannontech.rest.api.dr.helper;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.ArrayList;
import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import com.cannontech.rest.api.UIComponent.request.MockPickerIdSearchCriteria;
import com.cannontech.rest.api.UIComponent.request.MockPickerSearchCriteria;

public class PointPickerHelper {

    public static MockPickerSearchCriteria buildSearchCriteria() {

        MockPickerSearchCriteria mockPickerSearchCriteria = MockPickerSearchCriteria.builder()
                .type("devicePointPicker")
                .queryString("")
                .startCount(0)
                .count(6)
                .extraArgs("")
                .build();
        return mockPickerSearchCriteria;
    }

    public static MockPickerIdSearchCriteria buildIdSearchCriteria() {
        List<Integer> initialId = new ArrayList<>();
        initialId.add(-1);
        MockPickerIdSearchCriteria mockPickerIdSearchCriteria = MockPickerIdSearchCriteria.builder()
                .type("devicePointPicker")
                .initialIds(initialId)
                .extraArgs("")
                .build();
        return mockPickerIdSearchCriteria;

    }

    public static FieldDescriptor[] pickerResponseDataFields() {
        return new FieldDescriptor[] {
                fieldWithPath("hitCount").type(JsonFieldType.NUMBER).description("Hit Count: Total count of results available."),
                fieldWithPath("startIndex").type(JsonFieldType.NUMBER).description("Start Index 0 based"),
                fieldWithPath("endIndex").type(JsonFieldType.NUMBER).description("End Index"),

                fieldWithPath("resultList[].deviceName").type(JsonFieldType.STRING).description("Device Name"),
                fieldWithPath("resultList[].pointName").type(JsonFieldType.STRING).description("Point Name"),
                fieldWithPath("resultList[].pointType").type(JsonFieldType.STRING).description("Point Type"),
                fieldWithPath("resultList[].deviceId").type(JsonFieldType.NUMBER).description("Device Id"),
                fieldWithPath("resultList[].category").type(JsonFieldType.STRING).description("Category"),
                fieldWithPath("resultList[].pointId").type(JsonFieldType.NUMBER).description("Point Id"),

                fieldWithPath("previousStartIndex").type(JsonFieldType.NUMBER).description("Previous Start Index"),
                fieldWithPath("count").type(JsonFieldType.NUMBER).description("Count : Number of results per page"),
                fieldWithPath("lastStartIndex").type(JsonFieldType.NUMBER)
                        .description("Last start index : Index of last page of results"),
                fieldWithPath("numberOfPages").type(JsonFieldType.NUMBER).description("total number of result pages"),
                fieldWithPath("currentPage").type(JsonFieldType.NUMBER).description("Current Page"),
                fieldWithPath("previousNeeded").type(JsonFieldType.BOOLEAN).description("Previous Needed : boolean value"),
                fieldWithPath("nextNeeded").type(JsonFieldType.BOOLEAN).description("Next Needed : boolean value"),
                fieldWithPath("resultCount").type(JsonFieldType.NUMBER).description("Result Count"),
        };
    }

    public static FieldDescriptor[] pickerOnLoadResponseFields() {
        return new FieldDescriptor[] {
                fieldWithPath("POINT_PICKER.dialogTitle.codes").type(JsonFieldType.VARIES)
                        .description("Dialog Title Codes : i18 keys for dialog title"),
                fieldWithPath("POINT_PICKER.dialogTitle.arguments").type(JsonFieldType.VARIES)
                        .description("Extra arguments for picker filtering"),
                fieldWithPath("POINT_PICKER.dialogTitle.defaultMessage").type(JsonFieldType.VARIES)
                        .description("Default Message"),
                fieldWithPath("POINT_PICKER.dialogTitle.code").type(JsonFieldType.STRING)
                        .description("Dialog Title Code : i18 key for dialog title "),
                fieldWithPath("POINT_PICKER.criteria.criteria.minimumNumberShouldMatch").type(JsonFieldType.NUMBER)
                        .description("Minimum Number Should Match"),
                fieldWithPath("POINT_PICKER.criteria.criteria.coordDisabled").type(JsonFieldType.BOOLEAN)
                        .description("Disable Coordinates"),
                fieldWithPath("POINT_PICKER.idFieldName").type(JsonFieldType.STRING).description("id Field Name"),

        };
    }
}
