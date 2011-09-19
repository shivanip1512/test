package com.cannontech.web.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Months;
import org.joda.time.Period;
import org.joda.time.ReadablePeriod;
import org.joda.time.Weeks;
import org.joda.time.Years;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.support.service.SupportBundleWriter;
import com.google.common.collect.Lists;

public class SupportBundle {
    public static enum BundleRangeSelection implements DisplayableEnum {
        ONE_WEEK(Weeks.ONE),
        TWO_WEEKS(Weeks.TWO),
        ONE_MONTH(Months.ONE),
        EVERYTHING(Years.years(100));

        private static final String keyPrefix =
            "yukon.web.modules.support.supportBundle.bundleRangeSelection.";

        final private Period period;

        BundleRangeSelection(ReadablePeriod period) {
            this.period = period.toPeriod();
        }

        @Override
        public String getFormatKey() {
            return keyPrefix + name();
        }

        public Period getPeriod() {
            return period;
        }
    }

    private BundleRangeSelection bundleRangeSelection;
    private String comments;
    private String customerName;
    private List<String> optionalWritersToInclude = Lists.newArrayList();

    public SupportBundle() {
    }

    public SupportBundle(List<SupportBundleWriter> writerList) {
        // Default to one month
        this.bundleRangeSelection = BundleRangeSelection.ONE_MONTH;
        for (SupportBundleWriter writer : writerList) {
            if (writer.isOptional()) {
                optionalWritersToInclude.add(writer.getName());
            }
        }
    }

    public BundleRangeSelection getBundleRangeSelection() {
        return bundleRangeSelection;
    }

    public void setBundleRangeSelection(BundleRangeSelection bundleRangeSelection) {
        this.bundleRangeSelection = bundleRangeSelection;
    }

    public String getComments() {
        if (StringUtils.isEmpty(comments)) {
            return "";
        }
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public List<String> getOptionalWritersToInclude() {
        return optionalWritersToInclude;
    }

    public void setOptionalWritersToInclude(List<String> optionalWritersToInclude) {
        this.optionalWritersToInclude = optionalWritersToInclude;
        if (optionalWritersToInclude == null) {
            this.optionalWritersToInclude = Collections.emptyList();
        }
    }

    public void setOptionalWritersToInclude(String[] optionalWritersToInclude) {
        this.optionalWritersToInclude = Arrays.asList(optionalWritersToInclude);
    }
}
