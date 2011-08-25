package com.cannontech.support.model;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.cannontech.support.service.SupportBundleSource;
import com.cannontech.support.service.impl.BundleRangeSelection;

public class SupportBundle {
    private BundleRangeSelection bundleRangeSelection;
    private String comments;
    private String customerName;
    private String[] optionalSourcesToInclude;

    public SupportBundle() {

    }

    public SupportBundle(List<SupportBundleSource> sourceList) {
        // Default to one month
        this.bundleRangeSelection = BundleRangeSelection.ONE_MONTH;
        Set<String> optList = new LinkedHashSet<String>();
        for (SupportBundleSource source : sourceList) {
            if (source.isOptional()) {
                optList.add(source.getSourceName());
            }
        }
        this.optionalSourcesToInclude = optList.toArray(new String[optList.size()]);
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

    public String getInfo() {
        String eol = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        sb.append("Time Range Selected: ");
        sb.append(bundleRangeSelection);
        sb.append(eol);
        sb.append("Optional Sources Chosen:");
        sb.append(eol);
        if (optionalSourcesToInclude.length > 0) {
            for (int num = 0; num < optionalSourcesToInclude.length; num++) {
                sb.append(num);
                sb.append(".");
                sb.append(optionalSourcesToInclude[num]);
                sb.append(eol);
            }
        } else {
            sb.append("none");
            sb.append(eol);
        }
        sb.append(eol);
        
        if (StringUtils.isNotEmpty(comments)) {
            sb.append(eol);
            sb.append("Comments:");
            sb.append(eol);
            sb.append(eol);
            sb.append(comments);
        }
        
        return sb.toString();
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

    public String[] getOptionalSourcesToInclude() {
        if (ArrayUtils.isEmpty(this.optionalSourcesToInclude)) {
            return new String[0];
        }
        return optionalSourcesToInclude;
    }

    public void setOptionalSourcesToInclude(String[] optionalSourcesToInclude) {
        this.optionalSourcesToInclude = optionalSourcesToInclude;
    }

}
