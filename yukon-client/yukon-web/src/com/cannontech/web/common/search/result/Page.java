package com.cannontech.web.common.search.result;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.userpage.model.UserPage;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.support.SiteMapPage;

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

    /**
     * A temporary hack to get around the fact that some statically searched pages aren't in module_config.xml.
     * (They live in the SiteMapPage enum in SiteMapHelper and have keys for their page names hard-coded.)
     * For normal searches, this will be null.  For page searches (pages currently in SiteMapPage), this will be
     * set but userPage will be null.
     */
    private final SiteMapPage siteMapPage;

    public Page(UserPage userPage, Object... summaryArgs) {
        this(userPage, null, summaryArgs);
    }

    public Page(SiteMapPage siteMapPage) {
        this(null, siteMapPage);
    }

    private Page(UserPage userPage, SiteMapPage siteMapPage, Object... summaryArgs) {
        this.userPage = userPage;
        this.siteMapPage = siteMapPage;
        this.summaryArgs = summaryArgs;
    }

    public UserPage getUserPage() {
        return userPage;
    }

    public MessageSourceResolvable getSummary() {
        if (siteMapPage != null) {
            return new YukonMessageSourceResolvable(siteMapPage.getFormatKey());
        }
        return new YukonMessageSourceResolvable("yukon.web.modules.search." + userPage.getModule() + "."
                + userPage.getName() + ".summary", summaryArgs);
    }

    public Object[] getSummaryArgs() {
        return summaryArgs;
    }

    public boolean isBackedBySiteMapPage() {
        return siteMapPage != null;
    }

    public SiteMapPage getSiteMapPage() {
        return siteMapPage;
    }

    public String getPath() {
        return siteMapPage == null ? userPage.getPath() : siteMapPage.getLink();
    }
}
