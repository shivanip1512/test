/*
 * Created on Mar 23, 2004
 */
package com.cannontech.common.util;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

import com.cannontech.util.ServletUtil;

/**
 * YukonFileAppender is a FileAppender (log4j logging) that rolls over every day.
 * A file for every day of the month is kept; we always have a month worth 
 * of log files.
 * i.e.
 * if the baseFilename is set to yukon, then on the first of the month a file called
 * yukon1.log will written to.  At midnight the appender will close yukon1.log and
 * starting writing yukon2.log.  File are overwritten upon rolling over.
 * @author aaron
 */
public class YukonFileAppender extends FileAppender {
	private String _baseFileName;
	private long _nextCheck = System.currentTimeMillis() - 1;
	private Date _now = new Date();
	
	public YukonFileAppender(Layout layout, String baseFilename) throws IOException {
		_baseFileName = baseFilename;
		
		// Test if the directory exists, if not, then create it
		File dir = new File(_baseFileName).getParentFile();
		if( dir != null && !dir.exists()) 
			dir.mkdirs();
		
		setLayout(layout);
		setAppend(true);
		setFile(getTodaysFileName());
		activateOptions();
	}

		
	/* (non-Javadoc)
	 * @see org.apache.log4j.WriterAppender#subAppend(org.apache.log4j.spi.LoggingEvent)
	 */
	protected void subAppend(LoggingEvent event) {
		long n = System.currentTimeMillis();

		if (n >= _nextCheck) {
		  _now.setTime(n);
		  _nextCheck = ServletUtil.getTomorrow().getTime();

		  try {
			rollOver();
		  } catch (IOException ioe) {
			LogLog.error("rollOver() failed.", ioe);
		  }
		}

		super.subAppend(event);
	}
	
	private void rollOver() throws IOException{
		
		this.closeFile();
		
		String todaysFileName = getTodaysFileName();
		File todaysFile = new File(todaysFileName);		
		if(todaysFile.exists()) {
			todaysFile.delete();
		}
		
		this.setFile(todaysFileName, true, this.bufferedIO, this.bufferSize);
	}
	
	private String getTodaysFileName() {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(_now);
		return _baseFileName + cal.get(Calendar.DAY_OF_MONTH) + ".log"; 
		
	}
}
