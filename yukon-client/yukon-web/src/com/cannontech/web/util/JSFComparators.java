package com.cannontech.web.util;

import java.util.Comparator;

import org.apache.myfaces.custom.tree2.TreeNodeBase;

import com.cannontech.database.data.point.CapBankMonitorPointParams;

public class JSFComparators {

	public static Comparator<CapBankMonitorPointParams> monitorPointComparator = new java.util.Comparator<CapBankMonitorPointParams>()
	{
		public int compare(CapBankMonitorPointParams o1, CapBankMonitorPointParams o2)
		{
			
			String thisVal = o1.getPointName();
			String anotherVal = o2.getPointName();
			return ( thisVal.compareToIgnoreCase(anotherVal) );
		}
		public boolean equals(Object obj)
		{
			return false;
		}
	};
	public static Comparator<TreeNodeBase> treeNodeDescriptionComparator = new java.util.Comparator<TreeNodeBase>()
	{
		public int compare(TreeNodeBase o1, TreeNodeBase o2)
		{
			String thisVal = o1.getDescription();
			String anotherVal = o2.getDescription();
			return ( thisVal.compareToIgnoreCase(anotherVal) );
		}
		public boolean equals(Object obj)
		{
			return false;
		}
	};
	public static Comparator<CapBankMonitorPointParams> monitorPointDisplayOrderComparator = new Comparator<CapBankMonitorPointParams>() 
	{
		public int compare(CapBankMonitorPointParams o1, CapBankMonitorPointParams o2)
		{
			int thisVal = o1.getDisplayOrder();
			int anotherVal = o2.getDisplayOrder();
			return ( thisVal  - anotherVal );
		}
		public boolean equals(Object obj)
		{
			return false;
		}
	};

}
