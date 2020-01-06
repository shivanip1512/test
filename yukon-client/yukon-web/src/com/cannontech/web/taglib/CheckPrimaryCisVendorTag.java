package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.spring.YukonSpringHook;

public class CheckPrimaryCisVendorTag extends YukonTagSupport {

    private String path = null;
    private String selectedVendorId = null;

    public void setPath(String path) {
        this.path = path;
    }

    public void setSelectedVendorId(String selectedVendorId) {
        this.selectedVendorId = selectedVendorId;
    }

    @Override
    public void doTag() throws JspException, IOException {
        MultispeakFuncs multispeakFuncs = YukonSpringHook.getBean(MultispeakFuncs.class);

        JspWriter out = getJspContext().getOut();
        // Write out all of the parameters as hidden fields
        out.write("<select name='" + path + "'>");
        for (MultispeakVendor vendor : multispeakFuncs.getPrimaryCisVendorList()) {
            // Display the selected vendor in the drop down
            if (StringUtils.equals(vendor.getVendorID().toString(), selectedVendorId)) {
                out.append("<option selected = \"selected\"" + "value=\"" + vendor.getVendorID() + "\" >"
                        + StringEscapeUtils.escapeXml10(vendor.getCompanyName()) + "</option>");
            } else {
                out.append("<option value=\"" + vendor.getVendorID() + "\" >"
                        + StringEscapeUtils.escapeXml10(vendor.getCompanyName()) + "</option>");
            }
        }
        out.append("</select>");
    }
}
