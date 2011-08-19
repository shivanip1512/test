package com.cannontech.support.model;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
        if (comments == null)
            return "";
        return comments;
    }

    public String getInfo() {
        String commentsAndMore = "";
        commentsAndMore += "Time Range Selected: "
                           + bundleRangeSelection + "\r\n";
        commentsAndMore += "Optional Sources Chosen:\r\n";
        if (optionalSourcesToInclude.length > 0) {
            for (int num=0;num<optionalSourcesToInclude.length;num++) {
                commentsAndMore += num +"." + optionalSourcesToInclude[num] + "\r\n";
            }
        } else {
            commentsAndMore += "none\r\n";
        }
        commentsAndMore += "\r\n";
        if (comments == null) {
            return commentsAndMore;
        }
        commentsAndMore += "\r\nComments:\r\n\r\n" + comments;
        return commentsAndMore;
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
        if (this.optionalSourcesToInclude == null) {
            return new String[0];
        }
        return optionalSourcesToInclude;
    }

    public void setOptionalSourcesToInclude(String[] optionalSourcesToInclude) {
        this.optionalSourcesToInclude = optionalSourcesToInclude;
    }

}
