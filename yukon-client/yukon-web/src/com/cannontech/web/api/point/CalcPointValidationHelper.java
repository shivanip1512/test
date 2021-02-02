package com.cannontech.web.api.point;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LiteBaseline;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.points.model.CalcCompType;
import com.cannontech.web.tools.points.model.CalcOperation;
import com.cannontech.web.tools.points.model.CalculationComponent;
import com.cannontech.yukon.IDatabaseCache;

public class CalcPointValidationHelper {

    @Autowired private static IDatabaseCache cache;
    @Autowired private static PointDao pointDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    protected static final String baseKey = "yukon.web.api.error";
    private final static String pointBaseKey = "yukon.web.modules.tools.point.calculation.";
    private static MessageSourceAccessor accessor;
    @PostConstruct
    public void init() {
        accessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);
    }

    @Autowired
    public CalcPointValidationHelper(IDatabaseCache cache, PointDao pointDao) {
        CalcPointValidationHelper.cache = cache;
        CalcPointValidationHelper.pointDao = pointDao;
    }

    public static void ValidateCalcComponent(List<CalculationComponent> calcComponents, Errors errors) {
        String operationI18nText = accessor.getMessage(pointBaseKey + "operation");
        String operandI18nText = accessor.getMessage(pointBaseKey + "operand");

        if (CollectionUtils.isNotEmpty(calcComponents)) {
            for (int i = 0; i < calcComponents.size(); i++) {
                errors.pushNestedPath("calcComponents[" + i + "]");
                CalculationComponent calcComponent = calcComponents.get(i);
                if (calcComponent != null) {
                    YukonApiValidationUtils.checkIfFieldRequired("componentType", errors, calcComponent.getComponentType(), "componentType");
                    YukonApiValidationUtils.checkIfFieldRequired("operation", errors, calcComponent.getOperation(), "operation");
                    if (!errors.hasFieldErrors("operation")) {
                        if (calcComponent.getComponentType() == CalcCompType.OPERATION
                                || calcComponent.getComponentType() == CalcCompType.CONSTANT) {
                            Set<CalcOperation> calcOperations = CalcOperation.getCalcOperationsByCompType(CalcCompType.OPERATION);
                            if (!calcOperations.contains(calcComponent.getOperation())) {
                                errors.rejectValue("operation", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                                        new Object[] { operationI18nText }, "");
                            }
                        }
                        if (calcComponent.getComponentType() == CalcCompType.FUNCTION) {
                            Set<CalcOperation> calcFunctions = CalcOperation.getCalcOperationsByCompType(CalcCompType.FUNCTION);
                            if (!calcFunctions.contains(calcComponent.getOperation())) {
                                errors.rejectValue("operation", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                                        new Object[] { operationI18nText }, "");
                            }
                        }
                    }
                    YukonApiValidationUtils.checkIfFieldRequired("operand", errors, calcComponent.getOperand(), "operand");
                    if (!errors.hasFieldErrors("operand")) {
                        if (calcComponent.getComponentType() == CalcCompType.OPERATION || calcComponent.getComponentType() == CalcCompType.FUNCTION) {
                            try {
                                pointDao.getPointName(calcComponent.getOperand().intValue());
                            } catch (IncorrectResultSizeDataAccessException ee) {
                                errors.rejectValue("operand", ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { operandI18nText }, "");
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
            YukonApiValidationUtils.checkIfFieldRequired("baselineId", errors, baselineId, "baselineId");
            if (!errors.hasFieldErrors("baselineId")) {
                List<Integer> baseLineIds = cache.getAllBaselines().stream()
                                                                   .map(LiteBaseline::getBaselineID)
                                                                   .collect(Collectors.toList());
                if (!baseLineIds.contains(baselineId)) {
                    String baselineI18nText = accessor.getMessage(pointBaseKey + "baseline");
                    errors.rejectValue("baselineId", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(), new Object[] { baselineI18nText }, "");
                }
            }
        }
    }
}
