package com.cannontech.multispeak.db;

import java.util.Comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;

public class MspLMInterfaceMappingStrategyNameComparator implements
		Comparator<MspLMInterfaceMapping> {
	
	private MspLmInterfaceMappingColumnEnum column;
	private boolean ascending;
	
	public MspLMInterfaceMappingStrategyNameComparator(MspLmInterfaceMappingColumnEnum column, boolean ascending) {
		
		this.column = column;
		this.ascending = ascending;
	}
	
	@Override
	public int compare(MspLMInterfaceMapping o1, MspLMInterfaceMapping o2) {
		
		MspLMInterfaceMapping obj1 = null;
		MspLMInterfaceMapping obj2 = null;
		if (this.ascending) {
			obj1 = o1;
			obj2 = o2;
		} else {
			obj1 = o2;
			obj2 = o1;
		}
		
		if (this.column.equals(MspLmInterfaceMappingColumnEnum.STRATEGY)) {
			
			return new CompareToBuilder()
		        .append(obj1.getStrategyName().toLowerCase(), obj2.getStrategyName().toLowerCase())
		        .toComparison();
			
		} else if (this.column.equals(MspLmInterfaceMappingColumnEnum.SUBSTATION)) {
			
			return new CompareToBuilder()
		        .append(obj1.getSubstationName().toLowerCase(), obj2.getSubstationName().toLowerCase())
		        .toComparison();
			
		} else if (this.column.equals(MspLmInterfaceMappingColumnEnum.PAO)) {
			
			return new CompareToBuilder()
		        .append(obj1.getPaoName().toLowerCase(), obj2.getPaoName().toLowerCase())
		        .toComparison();
		} else {
			
			throw new IllegalArgumentException("Unsupported column type: " + column);
		}
	}
}
