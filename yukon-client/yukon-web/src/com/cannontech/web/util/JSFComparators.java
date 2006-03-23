package com.cannontech.web.util;

import java.util.Comparator;

import org.apache.myfaces.custom.tree2.TreeNodeBase;

import com.cannontech.database.data.point.CapBankMonitorPointParams;

public class JSFComparators {

	public static Comparator monitorPointComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			String thisVal = ((CapBankMonitorPointParams)o1).getPointName();
			String anotherVal = ((CapBankMonitorPointParams)o2).getPointName();
			return ( thisVal.compareToIgnoreCase(anotherVal) );
		}
		public boolean equals(Object obj)
		{
			return false;
		}
	};
	public static Comparator treeNodeDescriptionComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			String thisVal = ((TreeNodeBase)o1).getDescription();
			String anotherVal = ((TreeNodeBase)o2).getDescription();
			return ( thisVal.compareToIgnoreCase(anotherVal) );
		}
		public boolean equals(Object obj)
		{
			return false;
		}
	};

}
