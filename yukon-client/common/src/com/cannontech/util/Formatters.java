package com.cannontech.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * @author ryan
 *
 */
public abstract class Formatters
{
	public static SimpleDateFormat DATE_PART = new SimpleDateFormat("MM/dd/yyyy");
	public static SimpleDateFormat TIME_PART = new SimpleDateFormat("HH:mm z");
	public static SimpleDateFormat DATE_DEF = new SimpleDateFormat("MM:dd:yyyy:HH:mm:ss");
	public static SimpleDateFormat TIME_DEF = new SimpleDateFormat("HH:mm");
	public static SimpleDateFormat DATETIME = new SimpleDateFormat("MM/dd/yy HH:mm");
	public static SimpleDateFormat TZ_DATETIME = new SimpleDateFormat("MM/dd/yy HH:mm z");
	public static SimpleDateFormat AMPM_TIME = new SimpleDateFormat("hh:mm a");
	
	public static DecimalFormat DEC_3 = new DecimalFormat("#0.000");
	public static DecimalFormat PERIOD_SECS = new DecimalFormat("#0 secs");
	
}
