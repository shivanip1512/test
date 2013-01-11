package com.cannontech.support.service.impl;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;

import com.cannontech.clientutils.YukonLogManager;

public class SupportBundleDateRangeAndNameFileWriter extends
		SupportBundleDateRangeFileWriter {
	
    private static final Logger log =
            YukonLogManager.getLogger(SupportBundleDateRangeAndNameFileWriter.class);

    private static final String NO_HYPHEN_FORMAT = "yyyyMMdd";
	private static final String WITH_HYPHEN_FORMAT = "yyyy-MM-dd";
	
	private static final Pattern PATTERN_NO_HYPHEN = Pattern.compile("\\d{8}\\.log", Pattern.CASE_INSENSITIVE);
	private static final Pattern PATTERN_W_HYPHEN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}\\.log", Pattern.CASE_INSENSITIVE);
	
	private boolean checkBoth;

	protected boolean addCandidate(File candidate, ReadableInstant start,
            ReadableInstant stop) {
		if ( ! candidate.isFile()) {
			return false;
		}
        String filename = candidate.getName();
        
        if ( ! filename.endsWith(".log")) {
        	return false;
        }

        boolean isInDate = super.addCandidate(candidate, start, stop);

        if (log.isDebugEnabled()) {
        	log.debug("Check file " + filename + " if the filename indicates a valid date.");
        }
        // Require that both checks are valid
        if (checkBoth) {
        	return isInDate && isMatch(filename, start, stop);
        }
        // Otherwise, see if either check passes
        else {
        	// No need to check file name
        	if (isInDate) {
        		return true;
        	}
        	// If matches file name, then match
        	return isMatch(filename, start, stop);
        }
    }

	protected boolean isMatch(String filename, ReadableInstant start, ReadableInstant stop) {
        Matcher noHyphenMatcher = PATTERN_NO_HYPHEN.matcher(filename);
        while (noHyphenMatcher.find()) {
        	return isInGroup(noHyphenMatcher,NO_HYPHEN_FORMAT, start, stop);
        }
        Matcher withHyphenMatcher = PATTERN_W_HYPHEN.matcher(filename);
        while (withHyphenMatcher.find()) {
        	return isInGroup(withHyphenMatcher,WITH_HYPHEN_FORMAT, start, stop);
        }
        return false;
	}
	
	protected boolean isInGroup(Matcher matcher, String dateFormat, ReadableInstant start, ReadableInstant stop) {
        String s = matcher.group();
        if (log.isDebugEnabled()) {
        	log.debug("group = " + s);
        }
        String fileDateStr = s.split("\\.log")[0];
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);
        Date fileDate;
		try {
			fileDate = dateFormatter.parse(fileDateStr);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
        Instant fileDateInstant = new Instant(fileDate);
        return (fileDateInstant.isAfter(start)
                    && fileDateInstant.isBefore(stop));
	}
	
	public void setCheckBoth(boolean checkBoth) {
		this.checkBoth = checkBoth;
	}
	
	public boolean isCheckBoth() {
		return checkBoth;
	}

}
