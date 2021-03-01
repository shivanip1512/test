package com.cannontech.rest.api.point;

import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockPointType;
import com.cannontech.rest.api.point.helper.PointHelper;
import com.cannontech.rest.api.point.request.MockCalcAnalogPointModel;
import com.cannontech.rest.api.point.request.MockCalcCompType;
import com.cannontech.rest.api.point.request.MockCalcOperation;
import com.cannontech.rest.api.point.request.MockCalcUpdateType;
import com.cannontech.rest.api.point.request.MockCalculationComponent;
import com.cannontech.rest.api.utilities.Log;
import com.cannontech.rest.api.utilities.ValidationHelper;

import io.restassured.response.ExtractableResponse;

public class CalcAnalogPointTest extends ScalarPointApiTest {

    @BeforeClass
    public void setUp() {
        pointType = MockPointType.CalcAnalog;
        mockPointBase = (MockCalcAnalogPointModel) PointHelper.buildPoint(pointType);
    }

    @Test(dependsOnMethods = { "point_01_Create" })
    public void calcAnalogPoint_01_Get(ITestContext context) {

        Log.info("Point Id of CALC_ANALOG_POINT point created is : "
                + context.getAttribute(PointHelper.CONTEXT_POINT_ID).toString());

        ExtractableResponse<?> getResponse = ApiCallHelper.get("pointBaseUrl",
                context.getAttribute(PointHelper.CONTEXT_POINT_ID).toString());

        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        MockCalcAnalogPointModel calcAnalogPointModel = getResponse.as(MockCalcAnalogPointModel.class);

        assertTrue("paoId Should be : " + mockPointBase.getPaoId(), mockPointBase.getPaoId().equals(calcAnalogPointModel.getPaoId()));
        assertTrue("Name Should be : " + mockPointBase.getPointName(),
                   mockPointBase.getPointName().equals(calcAnalogPointModel.getPointName()));
        assertTrue("Point Type Should be : " + mockPointBase.getPointType(),
                   mockPointBase.getPointType().equals(calcAnalogPointModel.getPointType()));
        assertTrue("Point Offset Should be : " + mockPointBase.getPointOffset(),
                   mockPointBase.getPointOffset().equals(calcAnalogPointModel.getPointOffset()));

        assertTrue("Archive Type Should be : " + calcAnalogPointModel.getArchiveType(),
                calcAnalogPointModel.getArchiveType().equals(calcAnalogPointModel.getArchiveType()));
        assertTrue("Timing Group Should be : " + calcAnalogPointModel.getTimingGroup(),
                calcAnalogPointModel.getTimingGroup().equals(calcAnalogPointModel.getTimingGroup()));
        assertTrue("Update Style Should be : " + calcAnalogPointModel.getStaleData().getUpdateStyle(),
                calcAnalogPointModel.getStaleData().getUpdateStyle().equals(calcAnalogPointModel.getStaleData().getUpdateStyle()));
        assertTrue("State Group Id Should be : " + calcAnalogPointModel.getStateGroupId(),
                calcAnalogPointModel.getStateGroupId().equals(calcAnalogPointModel.getStateGroupId()));
        assertTrue("Update Type Should be : " + calcAnalogPointModel.getCalcAnalogBase().getUpdateType(),
                calcAnalogPointModel.getCalcAnalogBase().getUpdateType().equals(calcAnalogPointModel.getCalcAnalogBase().getUpdateType()));
        assertTrue("Periodic Rate Should be : " + calcAnalogPointModel.getCalcAnalogBase().getPeriodicRate(),
                calcAnalogPointModel.getCalcAnalogBase().getPeriodicRate().equals(calcAnalogPointModel.getCalcAnalogBase().getPeriodicRate()));
        assertTrue("Calculation Quality Should be : " + calcAnalogPointModel.getCalcAnalogBase().getCalculateQuality(),
                calcAnalogPointModel.getCalcAnalogBase().getCalculateQuality().equals(calcAnalogPointModel.getCalcAnalogBase().getCalculateQuality()));

    }
    
    /**
     * Test case to validate Calc Analog Point cannot be created with invalid periodic rate value if update Type is On Timer or On Timer Update
     * and validates valid error message in response
     */
   @Test
    public void calcAnalogPoint_02_InvalidPeriodicRateValue() {
        MockCalcAnalogPointModel mockCalcAnalogPointModel = (MockCalcAnalogPointModel) PointHelper.buildPoint(pointType);

        mockCalcAnalogPointModel.getCalcAnalogBase().setUpdateType(MockCalcUpdateType.ON_TIMER);
        mockCalcAnalogPointModel.getCalcAnalogBase().setPeriodicRate(14);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockCalcAnalogPointModel);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "calcAnalogBase.periodicRate",
                        "Invalid " +mockCalcAnalogPointModel.getCalcAnalogBase().getPeriodicRate()+ " value."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate Calc Analog Point cannot be created with invalid Opearation if component type is Operation or constant.
     * and validates valid error message in response
     */
    @Test
    public void calcAnalogPoint_03_InvalidOperation() {
        MockCalcAnalogPointModel mockCalcAnalogPointModel = (MockCalcAnalogPointModel) PointHelper.buildPoint(pointType);

        List<MockCalculationComponent> mockCalcComponents=mockCalcAnalogPointModel.getCalcComponents();
        MockCalculationComponent mockCalcComponent=mockCalcComponents.get(0);
        mockCalcComponent.setComponentType(MockCalcCompType.OPERATION);
        mockCalcComponent.setOperation(MockCalcOperation.DEMAND_AVG15_FUNCTION);
       
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockCalcAnalogPointModel);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "calcComponents[0].operation",
                        "Invalid Operation when Component Type is Operation or Constant."), "Expected code in response is not correct");
    }

    /**
     * Test case to validate Calc Analog Point cannot be created with invalid function if component type is Function.
     * and validates valid error message in response
     */
    @Test
    public void calcAnalogPoint_04_InvalidOperationFunction() {
        MockCalcAnalogPointModel MockCalcAnalogPointModel = (MockCalcAnalogPointModel) PointHelper.buildPoint(pointType);

        List<MockCalculationComponent> mockCalcComponents=MockCalcAnalogPointModel.getCalcComponents();
        MockCalculationComponent mockCalcComponent=mockCalcComponents.get(0);
        mockCalcComponent.setComponentType(MockCalcCompType.FUNCTION);
        mockCalcComponent.setOperation(MockCalcOperation.ADDITION_OPERATION);
       
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", MockCalcAnalogPointModel);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "calcComponents[0].operation",
                        "Invalid Operation(Function) value when Component Type is Function."), "Expected code in response is not correct");
    }

    /**
     * Test case to validate Calc Analog Point cannot be created with invalid Opearation if component type is Operation or constant.
     * and validates valid error message in response
     */
    @Test
    public void calcAnalogPoint_05_InvalidBaselineId() {
        MockCalcAnalogPointModel mockCalcAnalogPointModel = (MockCalcAnalogPointModel) PointHelper.buildPoint(pointType);

        List<MockCalculationComponent> mockCalcComponents=mockCalcAnalogPointModel.getCalcComponents();
        MockCalculationComponent mockCalcComponent=mockCalcComponents.get(0);
        mockCalcComponent.setComponentType(MockCalcCompType.FUNCTION);
        mockCalcComponent.setOperation(MockCalcOperation.BASELINE_FUNCTION);
        mockCalcAnalogPointModel.setBaselineId(422);
       
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockCalcAnalogPointModel);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "baselineId",
                        "baselineId does not exist."), "Expected code in response is not correct");
    }

}