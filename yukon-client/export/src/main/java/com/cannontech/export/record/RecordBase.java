package com.cannontech.export.record;

/**
 * @author snebben
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface RecordBase
{
	public final java.text.SimpleDateFormat parseDateFormat = new java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");	
	/**
	 * Write an instance of Record base to a comma separated String.
	 * @return String
	 */
	public abstract String dataToString();
}
