package com.cannontech.web.api.point;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LiteBaseline;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;
import com.cannontech.web.tools.points.model.CalcCompType;
import com.cannontech.web.tools.points.model.CalcOperation;
import com.cannontech.web.tools.points.model.CalculationComponent;
import com.cannontech.yukon.IDatabaseCache;

public class CalcPointValidationHelper {

    @Autowired private static IDatabaseCache cache;
    @Autowired private static PointDao pointDao;
    @Autowired private static YukonApiValidationUtils yukonApiValidationUtils;

    public CalcPointValidationHelper(IDatabaseCache cache, PointDao pointDao) {
        CalcPointValidationHelper.cache = cache;
        CalcPointValidationHelper.pointDao = pointDao;
    }

    public static void ValidateCalcComponent(List<CalculationComponent> calcComponents, Errors errors) {
        if (CollectionUtils.isNotEmpty(calcComponents)) {
            for (int i = 0; i < calcComponents.size(); i++) {
                errors.pushNestedPath("calcComponents[" + i + "]");
                CalculationComponent calcComponent = calcComponents.get(i);
                if (calcComponent != null) {
                    yukonApiValidationUtils.checkIfFieldRequired("componentType", errors, calcComponent.getComponentType(), "componentType");
                    yukonApiValidationUtils.checkIfFieldRequired("operation", errors, calcComponent.getOperation(), "operation");
                    if (!errors.hasFieldErrors("operation")) {
                        if (calcComponent.getComponentType() == CalcCompType.OPERATION
                                || calcComponent.getComponentType() == CalcCompType.CONSTANT) {
                            Set<CalcOperation> calcOperations = CalcOperation.getCalcOperationsByCompType(CalcCompType.OPERATION);
                            if (!calcOperations.contains(calcComponent.getOperation())) {
                                errors.rejectValue("operation", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                                        new Object[] { StringUtils.join(calcOperations, ", ") }, "");
                            }
                        }
                        if (calcComponent.getComponentType() == CalcCompType.FUNCTION) {
                            Set<CalcOperation> calcFunctions = CalcOperation.getCalcOperationsByCompType(CalcCompType.FUNCTION);
                            if (!calcFunctions.contains(calcComponent.getOperation())) {
                                errors.rejectValue("operation", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                                        new Object[] { StringUtils.join(calcFunctions, ", ") }, "");
                            }
                        }
                    }
                    yukonApiValidationUtils.checkIfFieldRequired("operand", errors, calcComponent.getOperand(), "operand");
                    if (!errors.hasFieldErrors("operand")) {
                        if (calcComponent.getComponentType() == CalcCompType.OPERATION || calcComponent.getComponentType() == CalcCompType.FUNCTION) {
                            try {
                                pointDao.getPointName(calcComponent.getOperand().intValue());
                            } catch (IncorrectResultSizeDataAccessException ee) {
                                errors.rejectValue("operand", ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { "valid Point ID" }, "");
                            }
                        }
                    }
                }
                errors.popNestedPath();
            }
        }
    }

    public static void validateCalcBaseline(List<CalculationComponent> calcComponents, Integer baselineId, Errors errors) {

        boolean isBaselineAssigned = calcComponents.stream()
                                                   .anyMatch(
                                                   component -> component.getComponentType() != null && component.getComponentType() == CalcCompType.FUNCTION
                                                   && 
                                                   component.getOperation() != null && component.getOperation().getCalcOperation().equals(CalcComponentTypes.BASELINE_FUNCTION));
        if (isBaselineAssigned) {
            yukonApiValidationUtils.checkIfFieldRequired("baselineId", errors, baselineId, "baselineId");
            if (!errors.hasFieldErrors("baselineId")) {
                List<Integer> baseLineIds = cache.getAllBaselines().stream()
                                                                   .map(LiteBaseline::getBaselineID)
                                                                   .collect(Collectors.toList());
                if (!baseLineIds.contains(baselineId)) {
                    errors.rejectValue("baselineId", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(), new Object[] { baselineId }, "");
                }
            }
        }
    }
}
