/*
 * Created on Feb 17, 2005
 */
package com.cannontech.analysis.pentahoreport;

import java.io.Serializable;
import java.math.BigDecimal;

import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent ;
import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.jfree.util.Log;

/**
 * @author stacey
 */
public class ItemMaxValueFunction extends AbstractFunction implements Serializable
{ 
  private String group; 
  private String field; 
  private String dataField; 
  /** The maximum value. */ 
  private transient Comparable max; 
  private Object dataFieldValue; 

  /** 
   * Constructs an unnamed function. Make sure to set a Name or function initialization 
   * will fail. 
   */ 
  public ItemMaxValueFunction() 
  { 
  	this.max = null; 
	dataFieldValue = null; 
  } 

  /** 
   * Constructs a named function. 
   * <P> 
   * The field must be defined before using the function. 
   * 
   * @param name The function name. 
   */ 
  public ItemMaxValueFunction(final String name) 
  { 
	this(); 
	setName(name); 
  } 

  public String getDataField () 
  { 
	return dataField; 
  } 

  public void setDataField (final String dataField) 
  { 
      this.dataField = dataField; 
  } 

  /** 
   * Receives notification that a new report is about to start. 
   * <P> 
   * Does nothing. 
   * 
   * @param event Information about the event. 
   * 
   */ 
  public void reportInitialized(final ReportEvent event) 
  { 
	this.max = null; 
	dataFieldValue = null; 
  } 

  /** 
   * Receives notification that a new group is about to start.  If this is the group defined for 
   * the function, then the maximum value is reset to zero. 
   * 
   * @param event Information about the event. 
   */ 
  public void groupStarted(final ReportEvent event) 
  { 
	final String mygroup = getGroup(); 
	if (mygroup == null) 
	{ 
	  return; 
	} 

	final Group group = event.getReport().getGroup(event.getState().getCurrentGroupIndex()); 
	if (getGroup().equals(group.getName())) 
	{ 
	  this.max = null; 
	  dataFieldValue = null; 
	} 
  } 

  /** 
   * Returns the group name. 
   * 
   * @return The group name. 
   */ 
  public String getGroup() 
  { 
	return group; 
  } 

  /** 
   * Sets the group name. 
   * <P> 
   * If a group is defined, the maximum value is reset to zero at the start of every instance of 
   * this group. 
   * 
   * @param name  the group name (null permitted). 
   */ 
  public void setGroup(final String name) 
  { 
      this.group = name; 
  } 

  /** 
   * Returns the field used by the function. 
   * <P> 
   * The field name corresponds to a column name in the report's TableModel. 
   * 
   * @return The field name. 
   */ 
  public String getField() 
  { 
	return field; 
  } 

  /** 
   * Sets the field name for the function. 
   * <P> 
   * The field name corresponds to a column name in the report's TableModel. 
   * 
   * @param field  the field name (null not permitted). 
   */ 
  public void setField(final String field) 
  { 
	if (field == null) 
	{ 
	  throw new NullPointerException(); 
	} 
	this.field = field; 
  } 

  /** 
   * Receives notification that a row of data is being processed.  Reads the data from the field 
   * defined for this function and calculates the maximum value. 
   * 
   * @param event Information about the event. 
   */ 
  public void itemsAdvanced(final ReportEvent event) 
  { 
	final Object fieldValue = event.getDataRow().get(getField()); 
	final Number n = (Number) fieldValue; 
	try 
	{ 
	  final BigDecimal compare = new BigDecimal(n.doubleValue()); 
	  if (max == null) 
	  { 
		max = compare; 
		dataFieldValue = event.getDataRow().get(getDataField()); 
	  } 
	  else if (max.compareTo(compare) < 0) 
	  { 
		max = compare; 
		dataFieldValue = event.getDataRow().get(getDataField()); 
	  } 
	} 
	catch (Exception e) 
	{ 
	  Log.error("ItemMaxFunction.advanceItems(): problem adding number."); 
	} 
  } 

  /** 
   * Returns the function value, in this case the running total of a specific column in the 
   * report's TableModel. 
   * 
   * @return The function value. 
   */ 
  public Object getValue() 
  { 
	return dataFieldValue; 
  }

  /** 
   * Return a completely separated copy of this function. The copy does no 
   * longer share any changeable objects with the original function. 
   * 
   * @return a copy of this function. 
   */ 
  public Expression getInstance() 
  { 
	final ItemMaxValueFunction function = (ItemMaxValueFunction) super.getInstance(); 
	function.max = null; 
	function.dataFieldValue = null; 
	return function; 
  } 
} 
