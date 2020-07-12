package com.cannontech.web.tools.points.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.cannontech.database.data.point.CalcStatusPoint;
import com.cannontech.database.db.point.calculation.CalcComponent;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.collect.Lists;
@JsonInclude(Include.NON_NULL)
public class CalcStatusPointModel extends StatusPointModel<CalcStatusPoint> {

    private CalculationBase calculationBase;
    private List<CalculationComponent> calcComponents;
    private Integer baselineId;

    public CalculationBase getCalculationBase() {
        if (calculationBase == null) {
            calculationBase = new CalculationBase();
        }
        return calculationBase;
    }

    public void setCalculationBase(CalculationBase calculationBase) {
        this.calculationBase = calculationBase;
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
    public void buildDBPersistent(CalcStatusPoint calcStatusPoint) {
        super.buildDBPersistent(calcStatusPoint);
        getCalculationBase().buildDBPersistent(calcStatusPoint.getCalcBase());
        int order = 1;
        calcStatusPoint.getCalcComponents().clear();
        if (CollectionUtils.isNotEmpty(getCalcComponents())) {
            boolean isBaselineAssigned = getCalcComponents().stream()
                                                            .anyMatch(component -> component.getComponentType() == CalcCompType.FUNCTION
                                                            && component.getOperation() == CalcOperation.BASELINE_FUNCTION);
            if (isBaselineAssigned) {
                calcStatusPoint.setBaselineAssigned(true);
                if (getBaselineId() != null) {
                    calcStatusPoint.getCalcBaselinePoint().setBaselineID(getBaselineId());
                    calcStatusPoint.setPointID(getPointId());
                }
            } else {
                calcStatusPoint.setBaselineAssigned(false);
            }
            List<CalcComponent> calcComponents = calcStatusPoint.getCalcComponents();
            for (CalculationComponent calcComponentModel : getCalcComponents()) {
                CalcComponent calcComponent = new CalcComponent();
                calcComponent.setPointID(getPointId());
                calcComponent.setComponentOrder(order++);
                calcComponentModel.buildDBPersistent(calcComponent);
                calcComponents.add(calcComponent);
            }
        }
    }

    @Override
    public void buildModel(CalcStatusPoint calcStatusPoint) {
        super.buildModel(calcStatusPoint);
        getCalculationBase().buildModel(calcStatusPoint.getCalcBase());

        if (calcStatusPoint.getBaselineAssigned()) {
            setBaselineId(calcStatusPoint.getCalcBaselinePoint().getBaselineID());
        } else {
            setBaselineId(null);
        }

        List<CalculationComponent> calcComponentList = new ArrayList<>();
        for (CalcComponent calcComponent : calcStatusPoint.getCalcComponents()) {
            CalculationComponent calculationComponent = new CalculationComponent();
            calculationComponent.buildModel(calcComponent);
            calcComponentList.add(calculationComponent);
        }

        if (CollectionUtils.isNotEmpty(calcComponentList)) {
            setCalcComponents(calcComponentList);
        }
    }
}
