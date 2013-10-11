package com.cannontech.web.common.search.result;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.userpage.model.UserPage;
import com.cannontech.i18n.YukonMessageSourceResolvable;

/**
 * A representation of a page which can be used for displaying the page in a list with a short summary of the page.
 * Currently this is used to represent pages found in a search.
 */
public final class Page {
    private final UserPage userPage;

    /**
     * Arguments used for page summary message. These should contain some basic data for the page
     * and are documented in the tools/root.xml file. These are different from the arguments in the
     * {@link UserPage} which are the arguments for the page name (as configured in
     * module_config.xml). They should be a superset of the arguments in {@link UserPage}.
     */
    private final Object[] summaryArgs;

    public Page(UserPage userPage, Object[] summaryArgs) {
        this.userPage = userPage;
        this.summaryArgs = summaryArgs;
    }

    public UserPage getUserPage() {
        return userPage;
    }

    public MessageSourceResolvable getSummary() {
        return new YukonMessageSourceResolvable("yukon.web.modules.search." + userPage.getModule() + "."
                + userPage.getName() + ".summary", summaryArgs);
    }

    public Object[] getSummaryArgs() {
        return summaryArgs;
    }
}
