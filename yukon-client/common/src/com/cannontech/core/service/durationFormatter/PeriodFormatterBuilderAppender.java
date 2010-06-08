package com.cannontech.core.service.durationFormatter;

import org.joda.time.format.PeriodFormatterBuilder;

public interface PeriodFormatterBuilderAppender {

	public void appendToBuilder(PeriodFormatterBuilder periodFormatterBuilder, String singularSuffix, String pluralSuffix);
}
