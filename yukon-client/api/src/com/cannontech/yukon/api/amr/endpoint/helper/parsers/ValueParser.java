package com.cannontech.yukon.api.amr.endpoint.helper.parsers;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.yukon.api.amr.endpoint.helper.PointValueSelector;

public abstract class ValueParser {
    protected Integer parseLimit(SimpleXPathTemplate template) {
        String limitStr = template.evaluateAsString("@limit");
        if ("unbounded".equals(limitStr) || StringUtils.isBlank(limitStr)) {
            return null;
        }
        return template.evaluateAsInt("@limit");
    }

    public final PointValueSelector parse(SimpleXPathTemplate template) {
        PointValueSelector selector = new PointValueSelector();

        String label = template.evaluateAsString("@label");
        selector.setLabel(label);
        parseOther(template, selector);

        return selector;
    }

    /**
     * Parse things that are specific to a given type of selector.  Base classes should override
     * this and parse anything not common to all ValueParsers.
     */
    protected abstract void parseOther(SimpleXPathTemplate template, PointValueSelector selector);
}
