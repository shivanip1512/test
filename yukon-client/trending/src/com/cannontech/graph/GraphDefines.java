package com.cannontech.graph;

/**
 * Insert the type's description here.
 * Creation date: (6/7/2002 2:40:14 PM)
 * @author: 
 */
public interface GraphDefines 
{
	public static final String HELP_FILE = com.cannontech.common.util.CtiUtilities.getHelpDirPath() + "Yukon Trending Help.chm";
	public static final String VERSION = "2.1.15";

	public static final int GRAPH_MASK = 0x01;
	public static final int PEAK_MASK = 0x02;
	public static final int USAGE_MASK = 0x04;

	public static final int GRAPH_PANE = 0;
	public static final int TABULAR_PANE = 1;
	public static final int SUMMARY_PANE = 2;

	public static final String GRAPH_PANE_STRING = "graph";
	public static final String TABULAR_PANE_STRING = "tab";
	public static final String SUMMARY_PANE_STRING = "summary";

}
