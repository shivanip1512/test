package com.cannontech.web.api.point;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.util.TimeIntervals;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LiteBaseline;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;
import com.cannontech.web.tools.points.model.CalcCompType;
import com.cannontech.web.tools.points.model.CalcOperation;
import com.cannontech.web.tools.points.model.CalcUpdateType;
import com.cannontech.web.tools.points.model.CalculationBase;
import com.cannontech.web.tools.points.model.CalculationComponent;
import com.cannontech.yukon.IDatabaseCache;

public class CalcPointHelper {

    @Autowired private static IDatabaseCache cache;
    @Autowired private static PointDao pointDao;
    protected static final String baseKey = "yukon.web.api.error";

    @Autowired
    public CalcPointHelper(IDatabaseCache cache, PointDao pointDao) {
        CalcPointHelper.cache = cache;
        CalcPointHelper.pointDao = pointDao;
    }

    public static void ValidateCalcComponent(List<CalculationComponent> calcComponents, PointType pointType, Errors errors) {

        if (CollectionUtils.isNotEmpty(calcComponents)) {
            for (int i = 0; i < calcComponents.size(); i++) {
                errors.pushNestedPath("calcComponents[" + i + "]");
                CalculationComponent calcComponent = calcComponents.get(i);
                if (calcComponent != null) {
                    YukonValidationUtils.checkIfFieldRequired("componentType", errors, calcComponent.getComponentType(), "componentType");
                    YukonValidationUtils.checkIfFieldRequired("operation", errors, calcComponent.getOperation(), "operation");
                    if (!errors.hasFieldErrors("operation")) {
                        if (calcComponent.getComponentType() == CalcCompType.OPERATION || calcComponent.getComponentType() == CalcCompType.CONSTANT) {
                            List<CalcOperation> calcComponentTypes = List.of(CalcOperation.CALC_OPERATIONS);
                            if (!calcComponentTypes.contains(calcComponent.getOperation())) {
                                errors.rejectValue("operation", baseKey + ".invalidOperation");
                            }
                        }
                        if (calcComponent.getComponentType() == CalcCompType.FUNCTION) {
                            List<CalcOperation> calcFunctions = List.of(CalcOperation.CALC_FUNCTIONS);
                            if (!calcFunctions.contains(calcComponent.getOperation())) {
                                errors.rejectValue("operation", baseKey + ".invalidFunction");
                            }
                        }
                    }
                    YukonValidationUtils.checkIfFieldRequired("operand", errors, calcComponent.getOperand(), "operand");
                    if (!errors.hasFieldErrors("operand")) {
                        if (calcComponent.getComponentType() == CalcCompType.OPERATION || calcComponent.getComponentType() == CalcCompType.FUNCTION) {
                            PointBase pointBase = pointDao.get(calcComponent.getOperand().intValue());
                            if (!pointBase.getPoint().getPointType().equals(pointType.toString())) {
                                errors.rejectValue("operand", baseKey + ".invalidPointId", new Object[] { pointType }, "");
                            }
                        }
                    }
                }
                errors.popNestedPath();
            }
        }
    }

    public static void validateCalcBaseline(List<CalculationComponent> calcComponents, Integer baselineId, Errors errors) {
        boolean isBaselineCheck = true;
        for (int i = 0; i < calcComponents.size(); i++) {
            CalculationComponent calcComponent = calcComponents.get(i);
            if (calcComponent.getComponentType() == null || calcComponent.getOperation() == null) {
                isBaselineCheck = false;
            }
        }
        if (isBaselineCheck) {
            boolean isBaselineAssigned = calcComponents.stream()
                                                       .anyMatch(component -> component.getComponentType() == CalcCompType.FUNCTION
                                                       && component.getOperation().getCalcOperation().equals(CalcComponentTypes.BASELINE_FUNCTION));
            if (isBaselineAssigned) {
                YukonValidationUtils.checkIfFieldRequired("baselineId", errors, baselineId, "baseLineId");
                if (!errors.hasFieldErrors("baselineId")) {
                    List<Integer> baseLineIds = cache.getAllBaselines().stream()
                                                                       .map(LiteBaseline::getBaselineID)
                                                                       .collect(Collectors.toList());
                    if (!baseLineIds.contains(baselineId)) {
                        errors.rejectValue("baselineId", baseKey + ".doesNotExist", new Object[] { "BaseLineId" }, "");
                    }
                }
            }
        }
    }

}
