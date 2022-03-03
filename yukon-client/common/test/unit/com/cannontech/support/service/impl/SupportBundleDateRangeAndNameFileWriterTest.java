package com.cannontech.support.service.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.ReadableInstant;
import org.joda.time.Weeks;
import org.junit.jupiter.api.Test;

import com.cannontech.common.util.TimeUtil;

public class SupportBundleDateRangeAndNameFileWriterTest {

	@Test
	public void testAddCandidate() throws ParseException  {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        LocalDate stopDate = new LocalDate(formatter.parse("20130110"), DateTimeZone.getDefault());
        LocalDate startDate = stopDate.minus(Weeks.TWO);

        ReadableInstant start = TimeUtil.toMidnightAtBeginningOfDay(startDate, DateTimeZone.getDefault());
        ReadableInstant stop = TimeUtil.toMidnightAtEndOfDay(stopDate, DateTimeZone.getDefault());


		SupportBundleDateRangeAndNameFileWriter writer = new SupportBundleDateRangeAndNameFileWriter();


		
        Date jan0813 = formatter.parse("20130108");
        Date jan0812 = formatter.parse("20120108");
        File testFile = new TestFile("foo20130108.log",true,jan0813);
	assertTrue(writer.isFileInDateRange(testFile, start, stop), "File name should be valid no-hyphen example");

		testFile = new TestFile("foo2013-01-08.log",true,jan0813);
        assertTrue(writer.isFileInDateRange(testFile, start, stop), "File name should be valid with-hyphen example");

		testFile = new TestFile("foo20130108.txt",true,jan0813);
        assertFalse(writer.isFileInDateRange(testFile, start, stop), "File name should NOT be valid due to suffix");

        testFile = new TestFile("foo20120108.log",true,jan0812);
        assertFalse(writer.isFileInDateRange(testFile, start, stop), "File name should NOT be valid due to date range");

		testFile = new TestFile("foo2012-01-08.log",true,jan0812);
        assertFalse(writer.isFileInDateRange(testFile, start, stop), "File name should NOT be valid due to date range");
}
		
	@Test
	public void testIsMatch() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        LocalDate stopDate = new LocalDate(formatter.parse("20130110"), DateTimeZone.getDefault());
        LocalDate startDate = stopDate.minus(Weeks.TWO);

        ReadableInstant start = TimeUtil.toMidnightAtBeginningOfDay(startDate, DateTimeZone.getDefault());
        ReadableInstant stop = TimeUtil.toMidnightAtEndOfDay(stopDate, DateTimeZone.getDefault());

        assertTrue(isMatch("foo20130108.log", start, stop), "File name should be valid no-hyphen example");

        assertTrue(isMatch("foo2013-01-08.log", start, stop), "File name should be valid with-hyphen example");

        assertFalse(isMatch("foo20130108.txt", start, stop), "File name should NOT be valid due to suffix");

        assertFalse(isMatch("foo20120108.log", start, stop), "File name should NOT be valid due to date range");

        assertFalse(isMatch("foo2012-01-08.log", start, stop), "File name should NOT be valid due to date range");
	}
	
	private boolean isMatch(String filename, ReadableInstant start, ReadableInstant stop) {
		SupportBundleDateRangeAndNameFileWriter writer = new SupportBundleDateRangeAndNameFileWriter();
		return writer.isMatch(filename, start, stop);
	}
	
	@SuppressWarnings("serial")
	public class TestFile extends java.io.File {
		
		private String name;
		private boolean isFile;
		private Date lastModified;
		
		public String getName() {
			return name;
		}
		
		public boolean isFile() {
			return isFile;
		}
		
		public long lastModified() {
			return lastModified.getTime();
		}

		public TestFile(String name, boolean isFile, Date lastModified) {
			super(name);
			this.name = name;
			this.isFile = isFile;
			this.lastModified = lastModified;
		}
		
	}

}
