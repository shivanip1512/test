package com.cannontech.multispeak.db;

import java.util.Comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;

public class MspLMMappingComparator implements
		Comparator<MspLMInterfaceMapping> {
	
	private MspLmMappingColumn column;
	private boolean ascending;
	
	public MspLMMappingComparator(MspLmMappingColumn column, boolean ascending) {
		
		this.column = column;
		this.ascending = ascending;
	}
	
	@Override
	public int compare(MspLMInterfaceMapping o1, MspLMInterfaceMapping o2) {
		
		MspLMInterfaceMapping obj1 = o1;
		MspLMInterfaceMapping obj2 = o2;
		if (ascending) {
		    obj1 = o2;
			obj2 = o1;
		}
		
		if (column.equals(MspLmMappingColumn.STRATEGY)) {
			
			return new CompareToBuilder()
		        .append(obj1.getStrategyName().toLowerCase(), obj2.getStrategyName().toLowerCase())
		        .toComparison();
			
		} else if (column.equals(MspLmMappingColumn.SUBSTATION)) {
			
			return new CompareToBuilder()
		        .append(obj1.getSubstationName().toLowerCase(), obj2.getSubstationName().toLowerCase())
		        .toComparison();
			
		} else if (column.equals(MspLmMappingColumn.PAO)) {
			
			return new CompareToBuilder()
		        .append(obj1.getPaoName().toLowerCase(), obj2.getPaoName().toLowerCase())
		        .toComparison();
		} else {
			
			throw new IllegalArgumentException("Unsupported column type: " + column);
		}
	}
}
