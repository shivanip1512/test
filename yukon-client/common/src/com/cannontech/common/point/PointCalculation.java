package com.cannontech.common.point;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.bulk.model.ImportCalculationType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.point.calculation.CalcComponent;
import com.google.common.collect.Lists;

/**
 * An object representing a full calculation for a calculated point. Constants, functions and
 * operations can be added, in order. When complete, the PointCalculation can be converted into a
 * List of CalcComponents for database insertion.
 */
public class PointCalculation {
    private int componentOrderIndex = 1;
    private List<CalcComponent> components = Lists.newArrayList();
    
    public void addOperation(int calculationPointId, String operation) {
        CalcComponent newComponent = getNewComponent();
        newComponent.setComponentType(ImportCalculationType.OPERATION.getDatabaseRepresentation());
        newComponent.setComponentPointID(calculationPointId);
        newComponent.setOperation(operation);
        newComponent.setConstant(0.0);
        newComponent.setFunctionName(CtiUtilities.STRING_NONE);
        components.add(newComponent);
    }
    
    public void addConstant(String operation, double constant) {
        if(StringUtils.isEmpty(operation)) {
            throw new IllegalArgumentException("Function name cannot be empty.");
        }      
        CalcComponent newComponent = getNewComponent();
        newComponent.setComponentType(ImportCalculationType.CONSTANT.getDatabaseRepresentation());
        newComponent.setComponentPointID(0);
        newComponent.setOperation(operation);
        newComponent.setConstant(constant);
        newComponent.setFunctionName(CtiUtilities.STRING_NONE);
        components.add(newComponent);
    }
    
    public void addFunction(int calculationPointId, String functionName) {
        if(StringUtils.isEmpty(functionName)) {
            throw new IllegalArgumentException("Function name cannot be empty.");
        }
        CalcComponent newComponent = getNewComponent();
        newComponent.setComponentType(ImportCalculationType.FUNCTION.getDatabaseRepresentation());
        newComponent.setComponentPointID(calculationPointId);
        newComponent.setOperation(CtiUtilities.STRING_NONE);
        newComponent.setConstant(0.0);
        newComponent.setFunctionName(functionName);
        components.add(newComponent);
    }
    
    public List<CalcComponent> copyComponentsAndInsertPointId(int pointId) {
        List<CalcComponent> copyList = Lists.newArrayList();
        for(CalcComponent component : components) {
            CalcComponent copyComponent = new CalcComponent();
            copyComponent.setPointID(pointId);
            copyComponent.setComponentOrder(component.getComponentOrder());
            copyComponent.setComponentType(component.getComponentType());
            copyComponent.setComponentPointID(component.getComponentPointID());
            copyComponent.setOperation(component.getOperation());
            copyComponent.setConstant(component.getConstant());
            copyComponent.setFunctionName(component.getFunctionName());
            copyList.add(copyComponent);
        }
        return copyList;
    }
    
    private CalcComponent getNewComponent() {
        CalcComponent newComponent = new CalcComponent();
        newComponent.setComponentOrder(componentOrderIndex);
        componentOrderIndex++;
        
        return newComponent;
    }
}
