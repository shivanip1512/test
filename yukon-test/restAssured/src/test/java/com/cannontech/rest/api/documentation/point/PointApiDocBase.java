package com.cannontech.rest.api.documentation.point;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockPointType;
import com.cannontech.rest.api.documentation.DocumentationBase;
import com.cannontech.rest.api.documentation.DocumentationFields;
import com.cannontech.rest.api.documentation.DocumentationFields.Copy;
import com.cannontech.rest.api.documentation.DocumentationFields.Create;
import com.cannontech.rest.api.documentation.DocumentationFields.Delete;
import com.cannontech.rest.api.documentation.DocumentationFields.Get;
import com.cannontech.rest.api.documentation.DocumentationFields.Update;
import com.cannontech.rest.api.point.helper.PointHelper;
import com.cannontech.rest.api.point.request.MockPointBase;
import com.cannontech.rest.api.point.request.MockPointCopy;

public abstract class PointApiDocBase extends DocumentationBase {

    public final static String idStr = "pointId";
    public final static String idDescStr = "Point Id";

    protected static FieldDescriptor[] pointBaseFields() {
        return new FieldDescriptor[] {
                fieldWithPath("paoId").type(JsonFieldType.NUMBER).description("Pao Id"),
                fieldWithPath("pointName").type(JsonFieldType.STRING).description("Point Name"),
                fieldWithPath("pointType").type(JsonFieldType.STRING).description("Point Type"),
                fieldWithPath("pointOffset").type(JsonFieldType.NUMBER).description("Point Offset"),
                fieldWithPath("stateGroupId").type(JsonFieldType.NUMBER).description("State Group Id"),
                fieldWithPath("enable").type(JsonFieldType.BOOLEAN).description("Enable"),
                fieldWithPath("archiveType").type(JsonFieldType.STRING).description("Archive Type possible values are NONE,ON_CHANGE,ON_TIMER,ON_UPDATE,ON_TIMER_OR_UPDATE"),
                fieldWithPath("archiveInterval").type(JsonFieldType.NUMBER).description("Archive Interval"),
                fieldWithPath("timingGroup").type(JsonFieldType.STRING).description("Timing Group possible values are DEFAULT,SOE"),
                fieldWithPath("alarmsDisabled").type(JsonFieldType.BOOLEAN).description("Alarms Disabled"),
                fieldWithPath("staleData.updateStyle").type(JsonFieldType.NUMBER).description("Stale data Update Style"),
                fieldWithPath("staleData.time").type(JsonFieldType.NUMBER).description("Stale data time"),
               
                fieldWithPath("alarming.notificationGroupId").type(JsonFieldType.NUMBER).description("Alarming Notification Group Id").optional(),
                fieldWithPath("alarming.notifyOnAck").type(JsonFieldType.BOOLEAN).description("Alarming Notify on ack").optional(),
                fieldWithPath("alarming.notifyOnClear").type(JsonFieldType.BOOLEAN).description("Alarming Notify on clear").optional(),
                fieldWithPath("alarming.alarmTableList[].condition").type(JsonFieldType.STRING).description("Alarming condition").optional(),
                fieldWithPath("alarming.alarmTableList[].category").type(JsonFieldType.STRING).description("Alarming category").optional(),
                fieldWithPath("alarming.alarmTableList[].notify").type(JsonFieldType.STRING).description("Alarming notify possible values are NONE,EXCLUDE_NOTIFY,AUTO_ACK,BOTH_OPTIONS").optional(),

                fieldWithPath("fdrList[].direction").type(JsonFieldType.STRING).description("FDR direction possible values are RECEIVE,RECEIVE_FOR_CONTROL,RECEIVE_FOR_ANALOG_OUTPUT,SEND,SEND_FOR_CONTROL,LINK_STATUS").optional(),
                fieldWithPath("fdrList[].fdrInterfaceType").type(JsonFieldType.STRING).description("FDR interface Type").optional(),
                fieldWithPath("fdrList[].translation").type(JsonFieldType.STRING).description("FDR translation").optional()};
    }
    
    protected static FieldDescriptor[] buildPointUnitAndLimitDescriptor() {
        return new FieldDescriptor[] {

                fieldWithPath("limits[].limitNumber").type(JsonFieldType.NUMBER).description("Limit Number").optional(),
                fieldWithPath("limits[].highLimit").type(JsonFieldType.NUMBER).description("High Limit").optional(),
                fieldWithPath("limits[].lowLimit").type(JsonFieldType.NUMBER).description("Low Limit").optional(),
                fieldWithPath("limits[].limitDuration").type(JsonFieldType.NUMBER).description("Limit Duration").optional(),
                
                fieldWithPath("pointUnit.uomId").type(JsonFieldType.NUMBER).description("Point Unit Uom Id").optional(),
                fieldWithPath("pointUnit.decimalPlaces").type(JsonFieldType.NUMBER).description("Point Unit decimal places").optional(),
                fieldWithPath("pointUnit.highReasonabilityLimit").type(JsonFieldType.NUMBER).description("Point Unit high reasonability limit").optional(),
                fieldWithPath("pointUnit.lowReasonabilityLimit").type(JsonFieldType.NUMBER).description("Point Unit low reasonability limit").optional(),
                fieldWithPath("pointUnit.meterDials").type(JsonFieldType.NUMBER).description("Point Unit meter dials").optional(),
        };
    }

    protected static FieldDescriptor[] buildStatusDescriptor() {
        return new FieldDescriptor[] {
                fieldWithPath("initialState").type(JsonFieldType.NUMBER).description("Initial State").optional(),
                fieldWithPath("pointStatusControl.closeTime1").type(JsonFieldType.NUMBER).description("Point Status Control Close Time 1").optional(),
                fieldWithPath("pointStatusControl.closeTime2").type(JsonFieldType.NUMBER).description("Point Status Control Close Time 2").optional(),
                fieldWithPath("pointStatusControl.openCommand").type(JsonFieldType.STRING).description("Point Status Control Open command").optional(),
                fieldWithPath("pointStatusControl.closeCommand").type(JsonFieldType.STRING).description("Point Status Control Close command").optional(),
                fieldWithPath("pointStatusControl.commandTimeOut").type(JsonFieldType.NUMBER).description("Point Status Control command time out").optional(),
                fieldWithPath("pointStatusControl.controlType").type(JsonFieldType.STRING).description("Point Status Control Type possible values : NONE,NORMAL,LATCH,PSEUDO,SBOLATCH,SBOPULSE").optional(),
                fieldWithPath("pointStatusControl.controlOffset").type(JsonFieldType.NUMBER).description("Point Status Control Offset").optional(),
                fieldWithPath("pointStatusControl.controlInhibited").type(JsonFieldType.BOOLEAN).description("Point Status Control Inhibited").optional(),
        };
    }

    protected static FieldDescriptor[] CalcPointsFields() {
        return new FieldDescriptor[] {
                fieldWithPath("baselineId").type(JsonFieldType.NUMBER).description("Baseline Id").optional(),
                fieldWithPath("calcComponents[].componentType").type(JsonFieldType.STRING).description("Component types possible values are : OPERATION,CONSTANT,FUNCTION ").optional(),
                fieldWithPath("calcComponents[].operation").type(JsonFieldType.STRING)
                        .description("Operation possible values if Component type is OPERATION and CONSTANT are: ADDITION_OPERATION,SUBTRACTION_OPERATION,MULTIPLICATION_OPERATION,DIVISION_OPERATION,PUSH_OPERATION"
                                + "   Operation possible values if component type is Function are: ABS_VALUE," + 
                                "    ADDITION_FUNCTION," + 
                                "    SUBTRACTION_FUNCTION," + 
                                "    MULTIPLICATION_FUNCTION," + 
                                "    DIVISION_FUNCTION," + 
                                "    MIN_FUNCTION," + 
                                "    MAX_FUNCTION," + 
                                "    MAX_DIFFERENCE," + 
                                "    MODULO_DIVIDE," + 
                                "    GREATER_THAN_FUNCTION," + 
                                "    GREATER_THAN_EQUAL_TO_FUNCTION," + 
                                "    LESS_THAN_FUNCTION," + 
                                "    LESS_THAN_EQUAL_TO_FUNCTION," + 
                                "    AND_FUNCTION," + 
                                "    OR_FUNCTION," + 
                                "    NOT_FUNCTION," + 
                                "    XOR_FUNCTION," + 
                                "    ARCTAN_FUNCTION," + 
                                "    BASELINE_FUNCTION," + 
                                "    BASELINE_PERCENT_FUNCTION," + 
                                "    DEMAND_AVG15_FUNCTION," + 
                                "    DEMAND_AVG30_FUNCTION," + 
                                "    DEMAND_AVG60_FUNCTION," + 
                                "    KVAR_FROM_KWKQ_FUNCTION," + 
                                "    KVA_FROM_KWKVAR_FUNCTION," + 
                                "    KVA_FROM_KWKQ_FUNCTION," + 
                                "    KW_FROM_KVAKVAR_FUNCTION," + 
                                "    PFACTOR_KW_KVAR_FUNCTION," + 
                                "    PFACTOR_KW_KQ_FUNCTION," + 
                                "    PFACTOR_KW_KVA_FUNCTION," + 
                                "    SQUARED_FUNCTION," + 
                                "    SQUARE_ROOT_FUNCTION," + 
                                "    STATE_TIMER_FUNCTION," + 
                                "    TRUE_FALSE_CONDITION," + 
                                "    REGRESSION," + 
                                "    BINARY_ENCODE," + 
                                "    MID_LEVEL_LATCH," + 
                                "    FLOAT_FORM_16BIT," + 
                                "    GET_POINT_LIMIT," + 
                                "    GET_INTERVAL_MINUTES," + 
                                "    INTERVALS_TO_VALUE," + 
                                "    LINEAR_SLOPE").optional(),
                fieldWithPath("calcComponents[].operand").type(JsonFieldType.NUMBER).description("Operand").optional()};
    }

    @Override
    protected Create buildCreateFields() {
        List<FieldDescriptor> requestFields = getFieldDescriptors();
        List<FieldDescriptor> responseFields = getFieldDescriptors();
        responseFields.add(0, fieldWithPath(idStr).type(JsonFieldType.NUMBER).description(idDescStr));
        String url = ApiCallHelper.getProperty("createPoint");
        return new DocumentationFields.Create(requestFields, responseFields, idStr, idDescStr, getMockObject(), url);
    }

    @Override
    protected Update buildUpdateFields() {
        List<FieldDescriptor> requestFields = getFieldDescriptors();
        List<FieldDescriptor> responseFields = getFieldDescriptors();
        responseFields.add(0, fieldWithPath(idStr).type(JsonFieldType.NUMBER).description(idDescStr));
        String url = ApiCallHelper.getProperty("updatePoint") + getPointId();
        return new DocumentationFields.Update(requestFields, responseFields, idStr, idDescStr, getMockObject(), url);
    }

    @Override
    protected Get buildGetFields() {
        List<FieldDescriptor> responseFields = getFieldDescriptors();
        responseFields.add(0, fieldWithPath(idStr).type(JsonFieldType.NUMBER).description(idDescStr));
        String url = ApiCallHelper.getProperty("getPoint") + getPointId();
        return new DocumentationFields.Get(responseFields, url);
    }

    @Override
    protected Delete buildDeleteFields() {
        String url = ApiCallHelper.getProperty("deletePoint") + getPointId();
        return new DocumentationFields.Delete(url);
    }

    @Override
    protected Copy buildCopyFields() {
        List<FieldDescriptor> requestFields = getCopyPointFieldDescriptors();
        List<FieldDescriptor> responseFields = getFieldDescriptors();
        responseFields.add(0, fieldWithPath(idStr).type(JsonFieldType.NUMBER).description(idDescStr));
        String url = ApiCallHelper.getProperty("copyPoint") + getPointId() +"/copy";
        return new DocumentationFields.Copy(requestFields, responseFields, idStr, idDescStr, getMockCopyObject(), url);
    }

    protected static FieldDescriptor[] pointCopyFieldsDescriptor() {
        return new FieldDescriptor[] {
                fieldWithPath("pointName").type(JsonFieldType.STRING).description("Point Name"),
                fieldWithPath("paoId").type(JsonFieldType.NUMBER).description("Pao Id"),
                fieldWithPath("pointOffset").type(JsonFieldType.NUMBER).description("Point Offset")};
        }

    private MockPointBase getMockObject() {
        return PointHelper.buildPoint(getMockPointType());
    }

    private MockPointCopy getMockCopyObject() {
            Integer paoId = Integer.valueOf(ApiCallHelper.getProperty("paoId"));
            Integer pointOffset = Integer.valueOf(ApiCallHelper.getProperty("pointOffset"))+1;
            return MockPointCopy.builder().pointName(PointHelper.getCopiedPointName(getMockPointType())).paoId(Integer.valueOf(paoId)).pointOffset(pointOffset).build();
    }

    /**
     * Return the request copy fieldDescriptors
     */
    protected abstract List<FieldDescriptor> getCopyPointFieldDescriptors();

    /**
     * Return the request fieldDescriptors
     */
    protected abstract List<FieldDescriptor> getFieldDescriptors();

    protected abstract String getPointId();

    protected abstract MockPointType getMockPointType();
}
