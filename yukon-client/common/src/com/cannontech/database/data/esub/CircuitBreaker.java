package com.cannontech.database.data.esub;

/**
 * Insert the type's description here.
 * Creation date: (12/15/2000 2:53:30 PM)
 * @author: 
 */
public class CircuitBreaker {
	private int graphDefinition;
	private String[] labels;
	private String[] units;
	private int[] pids;
	private boolean[] editLimits;	
/**
 * CircuitBreaker constructor comment.
 */
public CircuitBreaker() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (12/15/2000 2:57:55 PM)
 * @return boolean[]
 */
public boolean[] getEditLimits() {
	return editLimits;
}
/**
 * Insert the method's description here.
 * Creation date: (12/15/2000 2:58:36 PM)
 * @return int
 */
public int getGraphDefinition() {
	return graphDefinition;
}
/**
 * Insert the method's description here.
 * Creation date: (12/15/2000 2:55:22 PM)
 * @return java.lang.String[]
 */
public java.lang.String[] getLabels() {
	return labels;
}
/**
 * Insert the method's description here.
 * Creation date: (12/15/2000 2:57:55 PM)
 * @return int[]
 */
public int[] getPids() {
	return pids;
}
/**
 * Insert the method's description here.
 * Creation date: (12/15/2000 2:57:55 PM)
 * @return java.lang.String[]
 */
public java.lang.String[] getUnits() {
	return units;
}
/**
 * Insert the method's description here.
 * Creation date: (12/15/2000 2:57:55 PM)
 * @param newEditLimits boolean[]
 */
public void setEditLimits(boolean[] newEditLimits) {
	editLimits = newEditLimits;
}
/**
 * Insert the method's description here.
 * Creation date: (12/15/2000 2:58:36 PM)
 * @param newGraphDefinition int
 */
public void setGraphDefinition(int newGraphDefinition) {
	graphDefinition = newGraphDefinition;
}
/**
 * Insert the method's description here.
 * Creation date: (12/15/2000 2:55:22 PM)
 * @param newLabels java.lang.String[]
 */
public void setLabels(java.lang.String[] newLabels) {
	labels = newLabels;
}
/**
 * Insert the method's description here.
 * Creation date: (12/15/2000 2:57:55 PM)
 * @param newPids int[]
 */
public void setPids(int[] newPids) {
	pids = newPids;
}
/**
 * Insert the method's description here.
 * Creation date: (12/15/2000 2:57:55 PM)
 * @param newUnits java.lang.String[]
 */
public void setUnits(java.lang.String[] newUnits) {
	units = newUnits;
}
}
