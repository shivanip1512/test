package com.cannontech.web.app;

/**
 * Trending bean stores the state of the trending web application.
 * Creation date: (11/3/2001 11:27:07 AM)
 * @author: Aaron Lauinger
 */
import java.util.Date;

import com.cannontech.database.data.graph.GraphDefinition;

public class Trending {

	// CONSTANTS
	public static final int GRAPH 	= 1;
	public static final int TABULAR = 2;
	public static final int SUMMARY = 3;

	public static final int LINE_GRAPH 			= 10;
	public static final int BAR_GRAPH 			= 20;
	public static final int LOAD_DURATION_GRAPH = 30;

	// Trend(graph) we are currently interested in
	private GraphDefinition currentGraphDefinition = null;

	// Start and end of the time period we are interested in
	private Date startingDate 	= null;
	private Date endingDate 	= null;
	
	// GRAPH | TABULAR | SUMMARY
	private int graphOptions = GRAPH;

	// LINE_GRAPH | BAR_GRAPH | LOAD_DURATION_GRAPH	
	private int viewOptions = LINE_GRAPH;
/**
 * Trending constructor comment.
 */
public Trending() {
	super();
}
/**
 * Creation date: (11/3/2001 11:37:32 AM)
 * @return com.cannontech.database.data.graph.GraphDefinition
 */
public com.cannontech.database.data.graph.GraphDefinition getCurrentGraphDefinition() {
	return currentGraphDefinition;
}
/**
 * Creation date: (11/3/2001 11:37:32 AM)
 * @return java.util.Date
 */
public java.util.Date getEndingDate() {
	return endingDate;
}
/**
 * Creation date: (11/3/2001 11:37:32 AM)
 * @return int
 */
public int getGraphOptions() {
	return graphOptions;
}
/**
 * Creation date: (11/3/2001 11:37:32 AM)
 * @return java.util.Date
 */
public java.util.Date getStartingDate() {
	return startingDate;
}
/**
 * Creation date: (11/3/2001 11:37:32 AM)
 * @return int
 */
public int getViewOptions() {
	return viewOptions;
}
/**
 * Creation date: (11/3/2001 11:37:32 AM)
 * @param newCurrentGraphDefinition com.cannontech.database.data.graph.GraphDefinition
 */
public void setCurrentGraphDefinition(com.cannontech.database.data.graph.GraphDefinition newCurrentGraphDefinition) {
	currentGraphDefinition = newCurrentGraphDefinition;
}
/**
 * Creation date: (11/3/2001 11:37:32 AM)
 * @param newEndingDate java.util.Date
 */
public void setEndingDate(java.util.Date newEndingDate) {
	endingDate = newEndingDate;
}
/**
 * Creation date: (11/3/2001 11:37:32 AM)
 * @param newGraphOptions int
 */
public void setGraphOptions(int newGraphOptions) {
	graphOptions = newGraphOptions;
}
/**
 * Creation date: (11/3/2001 11:37:32 AM)
 * @param newStartingDate java.util.Date
 */
public void setStartingDate(java.util.Date newStartingDate) {
	startingDate = newStartingDate;
}
/**
 * Creation date: (11/3/2001 11:37:32 AM)
 * @param newViewOptions int
 */
public void setViewOptions(int newViewOptions) {
	viewOptions = newViewOptions;
}
}
