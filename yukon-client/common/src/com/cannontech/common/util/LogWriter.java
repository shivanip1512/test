package com.cannontech.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

public class LogWriter
{
   public static final int NONE = 0;
   public static final int ERROR = 1;
   public static final int INFO = 2;
   public static final int DEBUG = 3;

   private static final String ERROR_TEXT = "error";
   private static final String INFO_TEXT = "info";
   private static final String DEBUG_TEXT = "debug";
	
   private PrintWriter pw;
   private String owner;
   private int logLevel;
	
   public LogWriter(String owner, int logLevel)
   {
	  this(owner, logLevel, null);
   }
   public LogWriter(String owner, int logLevel, PrintWriter pw)
   {
	  this.pw = pw;
	  this.owner = owner;
	  this.logLevel = logLevel;
   }
   public int getLogLevel()
   {
	  return logLevel;
   }
   public PrintWriter getPrintWriter()
   {
	  return pw;
   }
   private String getSeverityString(int severityLevel)
   {
	  switch (severityLevel)
	  {
		 case ERROR:
			return ERROR_TEXT;
		 case INFO:
			return INFO_TEXT;
		 case DEBUG:
			return DEBUG_TEXT;
		 default:
			return "Unknown";
	  }
   }
   public void log(String msg, int severityLevel)
   {
	  if (pw != null)
	  {
		 if (severityLevel <= logLevel)
		 {
			pw.println("[" + new Date() + "]  " +
			getSeverityString(severityLevel) + ": " +
			owner + ": " + msg);
		 }
	  }
   }
   public void log(Throwable t, String msg, int severityLevel)
   {
	  log(msg + " : " + toTrace(t), severityLevel);
   }
   public void setLogLevel(int logLevel)
   {
	  this.logLevel = logLevel;
   }
   public void setPrintWriter(PrintWriter pw)
   {
	  this.pw = pw;
   }
   private String toTrace(Throwable e)
   {
	  StringWriter sw = new StringWriter();
	  PrintWriter pw = new PrintWriter(sw);
	  com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	  pw.flush();
	  return sw.toString();
   }
}
