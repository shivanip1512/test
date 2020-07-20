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
import com.cannontech.rest.api.point.request.MockCalcCompType;
import com.cannontech.rest.api.point.request.MockCalcOperation;
import com.cannontech.rest.api.point.request.MockCalcStatusPointModel;
import com.cannontech.rest.api.point.request.MockCalcUpdateType;
import com.cannontech.rest.api.point.request.MockCalculationComponent;
import com.cannontech.rest.api.utilities.Log;
import com.cannontech.rest.api.utilities.ValidationHelper;

import io.restassured.response.ExtractableResponse;

public class CalcStatusPointTest extends StatusPointApiTest {

    @BeforeClass
    public void setUp() {
        pointType = MockPointType.CalcStatus;
        mockPointBase = (MockCalcStatusPointModel) PointHelper.buildPoint(pointType);
    }

    @Test(dependsOnMethods = { "point_01_Create" })
    public void calcStatusPoint_01_Get(ITestContext context) {

        Log.info("Point Id of CALC_STATUS_POINT point created is : "
                + context.getAttribute(PointHelper.CONTEXT_POINT_ID).toString());

        ExtractableResponse<?> getResponse = ApiCallHelper.get("pointBaseUrl",
                context.getAttribute(PointHelper.CONTEXT_POINT_ID).toString());

        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        MockCalcStatusPointModel calcStatusPointModel = getResponse.as(MockCalcStatusPointModel.class);

        assertTrue("paoId Should be : " + mockPointBase.getPaoId(), mockPointBase.getPaoId().equals(calcStatusPointModel.getPaoId()));
        assertTrue("Name Should be : " + mockPointBase.getPointName(),
                   mockPointBase.getPointName().equals(calcStatusPointModel.getPointName()));
        assertTrue("Point Type Should be : " + mockPointBase.getPointType(),
                   mockPointBase.getPointType().equals(calcStatusPointModel.getPointType()));
        assertTrue("Point Offset Should be : " + mockPointBase.getPointOffset(),
                   mockPointBase.getPointOffset().equals(calcStatusPointModel.getPointOffset()));

        assertTrue("Archive Type Should be : " + calcStatusPointModel.getArchiveType(),
                calcStatusPointModel.getArchiveType().equals(calcStatusPointModel.getArchiveType()));
        assertTrue("Timing Group Should be : " + calcStatusPointModel.getTimingGroup(),
                calcStatusPointModel.getTimingGroup().equals(calcStatusPointModel.getTimingGroup()));
        assertTrue("Update Style Should be : " + calcStatusPointModel.getStaleData().getUpdateStyle(),
                calcStatusPointModel.getStaleData().getUpdateStyle().equals(calcStatusPointModel.getStaleData().getUpdateStyle()));
        assertTrue("State Group Id Should be : " + calcStatusPointModel.getStateGroupId(),
                calcStatusPointModel.getStateGroupId().equals(calcStatusPointModel.getStateGroupId()));
        assertTrue("Update Type Should be : " + calcStatusPointModel.getCalculationBase().getUpdateType(),
                calcStatusPointModel.getCalculationBase().getUpdateType().equals(calcStatusPointModel.getCalculationBase().getUpdateType()));
        assertTrue("Periodic Rate Should be : " + calcStatusPointModel.getCalculationBase().getPeriodicRate(),
                calcStatusPointModel.getCalculationBase().getPeriodicRate().equals(calcStatusPointModel.getCalculationBase().getPeriodicRate()));
    }
    
    /**
     * Test case to validate Calc Status Point cannot be created with invalid periodic rate value if update Type is On Timer or On Timer Update
     * and validates valid error message in response
     */
    @Test
    public void calcStatusPoint_02_InvalidPeriodicRateValue() {
        MockCalcStatusPointModel mockCalcStatusPointModel = (MockCalcStatusPointModel) PointHelper.buildPoint(pointType);

        mockCalcStatusPointModel.getCalculationBase().setUpdateType(MockCalcUpdateType.ON_TIMER);
        mockCalcStatusPointModel.getCalculationBase().setPeriodicRate(14);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockCalcStatusPointModel);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "calculationBase.periodicRate",
                        "Invalid " +mockCalcStatusPointModel.getCalculationBase().getPeriodicRate()+ " value."),
                "Expected code in response is not correct");
    }
    
    
    /**
     * Test case to validate Calc Status Point cannot be created with invalid Operation if component type is Operation or constant.
     * and validates valid error message in response
     */
    @Test
    public void calcStatusPoint_03_InvalidOperation() {
        MockCalcStatusPointModel mockCalcStatusPointModel = (MockCalcStatusPointModel) PointHelper.buildPoint(pointType);

        List<MockCalculationComponent> mockCalcComponents=mockCalcStatusPointModel.getCalcComponents();
        MockCalculationComponent mockCalcComponent=mockCalcComponents.get(0);
        mockCalcComponent.setComponentType(MockCalcCompType.OPERATION);
        mockCalcComponent.setOperation(MockCalcOperation.DEMAND_AVG15_FUNCTION);
       
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockCalcStatusPointModel);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "calcComponents[0].operation",
                        "Invalid Operation when Component Type is Operation or Constant."), "Expected code in response is not correct");
    }

    
    /**
     * Test case to validate Calc Status Point cannot be created with invalid function if component type is Function.
     * and validates valid error message in response
     */
    @Test
    public void calcStatusPoint_04_InvalidOpeartionFunction() {
        MockCalcStatusPointModel mockCalcStatusPointModel = (MockCalcStatusPointModel) PointHelper.buildPoint(pointType);

        List<MockCalculationComponent> mockCalcComponents=mockCalcStatusPointModel.getCalcComponents();
        MockCalculationComponent mockCalcComponent=mockCalcComponents.get(0);
        mockCalcComponent.setComponentType(MockCalcCompType.FUNCTION);
        mockCalcComponent.setOperation(MockCalcOperation.ADDITION_OPERATION);
       
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockCalcStatusPointModel);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "calcComponents[0].operation",
                        "Invalid Operation(Function) value when Component Type is Function."), "Expected code in response is not correct");
    }
    
    /**
     * Test case to validate Calc Status Point cannot be created with invalid baseline id when component type is function and function name is Baseline.
     * and validates valid error message in response
     */
    @Test
    public void calcAnalogPoint_05_InvalidBaselineId() {
        MockCalcStatusPointModel mockCalcStatusPointModel = (MockCalcStatusPointModel) PointHelper.buildPoint(pointType);

        List<MockCalculationComponent> mockCalcComponents=mockCalcStatusPointModel.getCalcComponents();
        MockCalculationComponent mockCalcComponent=mockCalcComponents.get(0);
        mockCalcComponent.setComponentType(MockCalcCompType.FUNCTION);
        mockCalcComponent.setOperation(MockCalcOperation.BASELINE_FUNCTION);
        mockCalcStatusPointModel.setBaselineId(45222);
       
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockCalcStatusPointModel);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "baselineId",
                      "baselineId does not exist."), "Expected code in response is not correct");
    }

    /**
     * Test case to validate Calc Status Point cannot be created with invalid point id i.e. Point id is not of calc Status type.
     * and validates valid error message in response
     */
    @Test
    public void calcStatusPoint_06_InvalidPointId() {
        MockCalcStatusPointModel mockCalcStatusPointModel = (MockCalcStatusPointModel) PointHelper.buildPoint(pointType);

        List<MockCalculationComponent> mockCalcComponents=mockCalcStatusPointModel.getCalcComponents();
        MockCalculationComponent mockCalcComponent=mockCalcComponents.get(0);
        mockCalcComponent.setOperation(MockCalcOperation.ADDITION_FUNCTION);
        mockCalcComponent.setOperand(12444.0);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("createPoint", mockCalcStatusPointModel);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "calcComponents[0].operand",
                        "Point Id should be of type " +mockCalcStatusPointModel.getPointType()+ "."), "Expected code in response is not correct");
    }

}
