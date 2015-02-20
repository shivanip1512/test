package com.cannontech.core.service.durationFormatter;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DurationFieldType;
import org.joda.time.format.PeriodFormatterBuilder;

public enum DurationFormatSymbol {

	// SECONDS
	S(DurationFieldType.seconds(), secondsPeriodFormatterBuilderAppender(false, 2)),
	S_FULL(DurationFieldType.seconds(), secondsPeriodFormatterBuilderAppender(true, 1)),
    S_SHORT(DurationFieldType.seconds(), secondsPeriodFormatterBuilderAppender(true, 1)),
	S_ABBR(DurationFieldType.seconds(), secondsPeriodFormatterBuilderAppender(false, 1)),
	
	// MINUTES
	M(DurationFieldType.minutes(), minutesPeriodFormatterBuilderAppender(false, 2)),
	M_FULL(DurationFieldType.minutes(), minutesPeriodFormatterBuilderAppender(true, 1)),
    M_SHORT(DurationFieldType.minutes(), minutesPeriodFormatterBuilderAppender(true, 1)),
	M_ABBR(DurationFieldType.minutes(), minutesPeriodFormatterBuilderAppender(false, 1)),
	
	// HOURS
	H(DurationFieldType.hours(), hoursPeriodFormatterBuilderAppender(false, 2)),
	H_FULL(DurationFieldType.hours(), hoursPeriodFormatterBuilderAppender(true, 1)),
    H_SHORT(DurationFieldType.hours(), hoursPeriodFormatterBuilderAppender(true, 1)),
	H_ABBR(DurationFieldType.hours(), hoursPeriodFormatterBuilderAppender(false, 1)),
	
	// DAYS
	D(DurationFieldType.days(), daysPeriodFormatterBuilderAppender(false)),
	D_FULL(DurationFieldType.days(), daysPeriodFormatterBuilderAppender(true)),
    D_SHORT(DurationFieldType.days(), daysPeriodFormatterBuilderAppender(true)),
	D_ABBR(DurationFieldType.days(), daysPeriodFormatterBuilderAppender(false)),
	
	// MONTHS
	MO(DurationFieldType.months(), monthsPeriodFormatterBuilderAppender(false)),
	MO_FULL(DurationFieldType.months(), monthsPeriodFormatterBuilderAppender(true)),
    MO_SHORT(DurationFieldType.months(), monthsPeriodFormatterBuilderAppender(true)),
	MO_ABBR(DurationFieldType.months(), monthsPeriodFormatterBuilderAppender(false)),
	
	// YEARS
	Y(DurationFieldType.years(), yearsPeriodFormatterBuilderAppender(false)),
	Y_FULL(DurationFieldType.years(), yearsPeriodFormatterBuilderAppender(true)),
    Y_SHORT(DurationFieldType.years(), yearsPeriodFormatterBuilderAppender(true)),
	Y_ABBR(DurationFieldType.years(), yearsPeriodFormatterBuilderAppender(false)),
	;
	
	
	// private
	DurationFieldType durationFieldType;
	PeriodFormatterBuilderAppender periodFormatterBuilderAppender;
	String keyPrefix = "yukon.common.durationFormatting.symbol.";
	
	DurationFormatSymbol(DurationFieldType durationFieldType, PeriodFormatterBuilderAppender periodFormatterBuilderAppender) {
		this.durationFieldType = durationFieldType;
		this.periodFormatterBuilderAppender = periodFormatterBuilderAppender;
	}
	
	public DurationFieldType getDurationFieldType() {
		return durationFieldType;
	}
	
	public void applyAppenderToBuilder(PeriodFormatterBuilder periodFormatterBuilder, String singularSuffix, String pluralSuffix) {
		this.periodFormatterBuilderAppender.appendToBuilder(periodFormatterBuilder, singularSuffix, pluralSuffix);
	}
	
	public String getSingularSuffixKey() {
		return keyPrefix + this + ".suffix.singular";
	}
	
	public String getPluralSuffixKey() {
		return keyPrefix + this + ".suffix.plural";
	}
	
	
	// SECONDS APPENDER
	private static PeriodFormatterBuilderAppender secondsPeriodFormatterBuilderAppender(final boolean padSuffix, final int minimumPrintedDigits) {
		return new PeriodFormatterBuilderAppender() {
			public void appendToBuilder(PeriodFormatterBuilder periodFormatterBuilder, String singularSuffix, String pluralSuffix) {
				periodFormatterBuilder.minimumPrintedDigits(minimumPrintedDigits);
				periodFormatterBuilder.appendSeconds();
				applySuffixToBuilder(periodFormatterBuilder, padSuffix, singularSuffix, pluralSuffix);
			}
		};
	}
	
	// MINUTES APPENDER
	private static PeriodFormatterBuilderAppender minutesPeriodFormatterBuilderAppender(final boolean padSuffix, final int minimumPrintedDigits) {
		return new PeriodFormatterBuilderAppender() {
			public void appendToBuilder(PeriodFormatterBuilder periodFormatterBuilder, String singularSuffix, String pluralSuffix) {
				periodFormatterBuilder.minimumPrintedDigits(minimumPrintedDigits);
				periodFormatterBuilder.appendMinutes();
				applySuffixToBuilder(periodFormatterBuilder, padSuffix, singularSuffix, pluralSuffix);
			}
		};
	}
	
	// HOURS APPENDER
	private static PeriodFormatterBuilderAppender hoursPeriodFormatterBuilderAppender(final boolean padSuffix, final int minimumPrintedDigits) {
		return new PeriodFormatterBuilderAppender() {
			public void appendToBuilder(PeriodFormatterBuilder periodFormatterBuilder, String singularSuffix, String pluralSuffix) {
				periodFormatterBuilder.minimumPrintedDigits(minimumPrintedDigits);
				periodFormatterBuilder.appendHours();
				applySuffixToBuilder(periodFormatterBuilder, padSuffix, singularSuffix, pluralSuffix);
			}
		};
	}
	
	// DAYS APPENDER
	private static PeriodFormatterBuilderAppender daysPeriodFormatterBuilderAppender(final boolean padSuffix) {
		return new PeriodFormatterBuilderAppender() {
			public void appendToBuilder(PeriodFormatterBuilder periodFormatterBuilder, String singularSuffix, String pluralSuffix) {
				periodFormatterBuilder.minimumPrintedDigits(1);
				periodFormatterBuilder.appendDays();
				applySuffixToBuilder(periodFormatterBuilder, padSuffix, singularSuffix, pluralSuffix);
			}
		};
	}
	
	// MONTHS APPENDER
	private static PeriodFormatterBuilderAppender monthsPeriodFormatterBuilderAppender(final boolean padSuffix) {
		return new PeriodFormatterBuilderAppender() {
			public void appendToBuilder(PeriodFormatterBuilder periodFormatterBuilder, String singularSuffix, String pluralSuffix) {
				periodFormatterBuilder.minimumPrintedDigits(1);
				periodFormatterBuilder.appendMonths();
				applySuffixToBuilder(periodFormatterBuilder, padSuffix, singularSuffix, pluralSuffix);
			}
		};
	}
	
	// YEARS APPENDER
	private static PeriodFormatterBuilderAppender yearsPeriodFormatterBuilderAppender(final boolean padSuffix) {
		return new PeriodFormatterBuilderAppender() {
			public void appendToBuilder(PeriodFormatterBuilder periodFormatterBuilder, String singularSuffix, String pluralSuffix) {
				periodFormatterBuilder.minimumPrintedDigits(1);
				periodFormatterBuilder.appendYears();
				applySuffixToBuilder(periodFormatterBuilder, padSuffix, singularSuffix, pluralSuffix);
			}
		};
	}
	
	private static void applySuffixToBuilder(PeriodFormatterBuilder periodFormatterBuilder, boolean padSuffix, String singularSuffix, String pluralSuffix) {
		if (StringUtils.isNotBlank(singularSuffix) && StringUtils.isNotBlank(pluralSuffix)) {
			String preSuffixPadding = padSuffix ? " " : "";
			periodFormatterBuilder.appendSuffix(preSuffixPadding + singularSuffix, preSuffixPadding + pluralSuffix);
		}
	}
}
