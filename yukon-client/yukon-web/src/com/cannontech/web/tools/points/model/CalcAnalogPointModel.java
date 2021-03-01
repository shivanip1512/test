package com.cannontech.web.tools.points.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.cannontech.database.data.point.CalculatedPoint;
import com.cannontech.database.db.point.calculation.CalcComponent;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.collect.Lists;

@JsonInclude(Include.NON_NULL)
public class CalcAnalogPointModel extends ScalarPointModel<CalculatedPoint> {

    private CalcAnalogBase calcAnalogBase;
    private List<CalculationComponent> calcComponents;
    private Integer baselineId;

    public CalcAnalogBase getCalcAnalogBase() {
        if (calcAnalogBase == null) {
            calcAnalogBase = new CalcAnalogBase();
        }
        return calcAnalogBase;
    }

    public void setCalcAnalogBase(CalcAnalogBase calcAnalogBase) {
        this.calcAnalogBase = calcAnalogBase;
    }

    public List<CalculationComponent> getCalcComponents() {
        if (calcComponents == null) {
            calcComponents = Lists.newArrayList();
        }
        return calcComponents;
    }

    public void setCalcComponents(List<CalculationComponent> calcComponents) {
        this.calcComponents = calcComponents;
    }

    public Integer getBaselineId() {
        return baselineId;
    }

    public void setBaselineId(Integer baselineId) {
        this.baselineId = baselineId;
    }

    @Override
    public void buildDBPersistent(CalculatedPoint calculatedPoint) {
        super.buildDBPersistent(calculatedPoint);
        getCalcAnalogBase().buildDBPersistent(calculatedPoint.getCalcBase());
        int order = 1;
        calculatedPoint.getCalcComponents().clear();
        if (CollectionUtils.isNotEmpty(getCalcComponents())) {
            boolean isBaselineAssigned = getCalcComponents().stream()
                                                            .anyMatch(component -> component.getComponentType() == CalcCompType.FUNCTION
                                                            && component.getOperation() == CalcOperation.BASELINE_FUNCTION);
            if (isBaselineAssigned) {
                calculatedPoint.setBaselineAssigned(true);
                if (getBaselineId() != null) {
                    calculatedPoint.getCalcBaselinePoint().setBaselineID(getBaselineId());
                    calculatedPoint.setPointID(getPointId());
                }
            } else {
                calculatedPoint.setBaselineAssigned(false);
            }

            List<CalcComponent> calcComponents = calculatedPoint.getCalcComponents();
            for (CalculationComponent calculationComponent : getCalcComponents()) {
                CalcComponent calcComponent = new CalcComponent();
                calcComponent.setPointID(getPointId());
                calcComponent.setComponentOrder(order++);
                calculationComponent.buildDBPersistent(calcComponent);
                calcComponents.add(calcComponent);
            }
        }
    }

    @Override
    public void buildModel(CalculatedPoint calculatedPoint) {
        super.buildModel(calculatedPoint);
        getCalcAnalogBase().buildModel(calculatedPoint.getCalcBase());
        if (calculatedPoint.getBaselineAssigned()) {
            setBaselineId(calculatedPoint.getCalcBaselinePoint().getBaselineID());
        } else {
            setBaselineId(null);
        }
        List<CalculationComponent> calculationComponentList = new ArrayList<>();
        for (CalcComponent calcComponent : calculatedPoint.getCalcComponents()) {
            CalculationComponent calculationComponent = new CalculationComponent();
            calculationComponent.buildModel(calcComponent);
            calculationComponentList.add(calculationComponent);
        }

        if (CollectionUtils.isNotEmpty(calculationComponentList)) {
            setCalcComponents(calculationComponentList);
        }
    }
}
