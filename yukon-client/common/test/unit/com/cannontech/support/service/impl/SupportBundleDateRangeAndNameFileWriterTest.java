package com.cannontech.support.service.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.ReadableInstant;
import org.joda.time.Weeks;
import org.junit.Assert;
import org.junit.Test;

import com.cannontech.common.util.TimeUtil;

public class SupportBundleDateRangeAndNameFileWriterTest {

	@Test
	public void testAddCandidate() throws Exception {
        LocalDate stopDate = new LocalDate(DateTimeZone.getDefault());
        LocalDate startDate = stopDate.minus(Weeks.TWO);

        ReadableInstant start = TimeUtil.toMidnightAtBeginningOfDay(startDate, DateTimeZone.getDefault());
        ReadableInstant stop = TimeUtil.toMidnightAtEndOfDay(stopDate, DateTimeZone.getDefault());

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

		SupportBundleDateRangeAndNameFileWriter writer = new SupportBundleDateRangeAndNameFileWriter();

		// Testing with "checkBoth" set to false
		File testFile = new TestFile("foo20130108.log",true,formatter.parse("20130108"));
		Assert.assertTrue("File name should be valid no-hyphen example",writer.addCandidate(testFile, start, stop));

		testFile = new TestFile("foo2013-01-08.log",true,formatter.parse("20130108"));
        Assert.assertTrue("File name should be valid with-hyphen example",writer.addCandidate(testFile, start, stop));

		testFile = new TestFile("foo20130108.txt",true,formatter.parse("20130108"));
        Assert.assertFalse("File name should NOT be valid due to suffix",writer.addCandidate(testFile, start, stop));

		testFile = new TestFile("foo20120108.log",true,formatter.parse("20120108"));
        Assert.assertFalse("File name should NOT be valid due to date range",writer.addCandidate(testFile, start, stop));

		testFile = new TestFile("foo2012-01-08.log",true,formatter.parse("20120108"));
        Assert.assertFalse("File name should NOT be valid due to date range",writer.addCandidate(testFile, start, stop));


		// Testing with "checkBoth" set to false
        writer.setCheckBoth(true);

		testFile = new TestFile("foo20130108.log",true,formatter.parse("20130108"));
		Assert.assertTrue("File name should be valid no-hyphen example",writer.addCandidate(testFile, start, stop));

		testFile = new TestFile("foo20130108.log",true,formatter.parse("20120108"));
		Assert.assertFalse("File name should NOT be valid no-hyphen due to date",writer.addCandidate(testFile, start, stop));

		testFile = new TestFile("foo20120108.log",true,formatter.parse("20130108"));
		Assert.assertFalse("File name should NOT be valid no-hyphen due to filename",writer.addCandidate(testFile, start, stop));

		testFile = new TestFile("foo2013-01-08.log",true,formatter.parse("20130108"));
        Assert.assertTrue("File name should be valid with-hyphen example",writer.addCandidate(testFile, start, stop));

		testFile = new TestFile("foo2013-01-08.log",true,formatter.parse("20120108"));
        Assert.assertFalse("File name should NOT be valid with-hyphen due to date",writer.addCandidate(testFile, start, stop));

		testFile = new TestFile("foo2012-01-08.log",true,formatter.parse("20130108"));
        Assert.assertFalse("File name should NOT be valid with-hyphen due to filename",writer.addCandidate(testFile, start, stop));

        testFile = new TestFile("foo20130108.txt",true,formatter.parse("20130108"));
        Assert.assertFalse("File name should NOT be valid due to suffix",writer.addCandidate(testFile, start, stop));

		testFile = new TestFile("foo20120108.log",true,formatter.parse("20120108"));
        Assert.assertFalse("File name should NOT be valid due to date range",writer.addCandidate(testFile, start, stop));

		testFile = new TestFile("foo2012-01-08.log",true,formatter.parse("20120108"));
        Assert.assertFalse("File name should NOT be valid due to date range",writer.addCandidate(testFile, start, stop));
}
		
	@Test
	public void testIsMatch() {
        LocalDate stopDate = new LocalDate(DateTimeZone.getDefault());
        LocalDate startDate = stopDate.minus(Weeks.TWO);

        ReadableInstant start = TimeUtil.toMidnightAtBeginningOfDay(startDate, DateTimeZone.getDefault());
        ReadableInstant stop = TimeUtil.toMidnightAtEndOfDay(stopDate, DateTimeZone.getDefault());

        Assert.assertTrue("File name should be valid no-hyphen example",isMatch("foo20130108.log", start, stop));

        Assert.assertTrue("File name should be valid with-hyphen example",isMatch("foo2013-01-08.log", start, stop));

        Assert.assertFalse("File name should NOT be valid due to suffix",isMatch("foo20130108.txt", start, stop));

        Assert.assertFalse("File name should NOT be valid due to date range",isMatch("foo20120108.log", start, stop));

        Assert.assertFalse("File name should NOT be valid due to date range",isMatch("foo2012-01-08.log", start, stop));
	}
	
	private boolean isMatch(String filename, ReadableInstant start, ReadableInstant stop) {
		SupportBundleDateRangeAndNameFileWriter writer = new SupportBundleDateRangeAndNameFileWriter();
		return writer.isMatch(filename, start, stop);
	}
	
	@SuppressWarnings("serial")
	public class TestFile extends java.io.File {
		
		private String name;
		private boolean isFile;
		private Date lastModified = new Date();
		
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
