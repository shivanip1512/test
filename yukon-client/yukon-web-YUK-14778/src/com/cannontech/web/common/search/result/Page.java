package com.cannontech.web.common.search.result;

import static com.google.common.base.Preconditions.*;

import java.util.List;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.userpage.model.UserPage;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.support.SiteMapPage;

/**
 * A representation of a page which can be used for displaying the page in a list with a short summary of the page.
 * Currently this is used to represent pages found in a search.
 */
public final class Page {
    
    private final static String baseKey = "yukon.web.modules.search.";
    
    private final UserPage userPage;
    
    /**
     * Arguments used for page summary message. These should contain some basic data for the page
     * and are documented in the tools/root.xml file. These are different from the arguments in the
     * {@link UserPage} which are the arguments for the page name (as configured in
     * module_config.xml). They should be a superset of the arguments in {@link UserPage}.
     */
    private final Object[] summaryArgs;
    
    /**
     * An instance of this class is used instead of UserPage for legacy pages. 
     */
    private final static class LegacyPage {
        
        final String path;
        final MessageSourceResolvable title;
        final MessageSourceResolvable summary;
        
        LegacyPage(String path, String module, String pageName, Object[] summaryArgs) {
            this.path = path;
            this.title = new YukonMessageSourceResolvable(baseKey + module + '.' + pageName + ".title", summaryArgs);
            this.summary = new YukonMessageSourceResolvable(baseKey + module + '.' + pageName + ".summary", summaryArgs);
        }
        
        LegacyPage(SiteMapPage siteMapPage) {
            path = siteMapPage.getLink();
            String pageName = siteMapPage.name();
            title = new YukonMessageSourceResolvable(siteMapPage.getFormatKey());
            String[] summaryCodes = new String[] { baseKey + pageName + ".summary", siteMapPage.getFormatKey() };
            summary = new YukonMessageSourceResolvable(summaryCodes);
        }
    }
    
    /**
     * A temporary hack to get around the fact that some statically searched pages aren't in module_config.xml.
     * For normal pages, this will be null.  For legacy pages, this will be set but userPage will be null.
     */
    private final LegacyPage legacyPage;
    
    public Page(UserPage userPage, List<String> summaryArgs) {
        this.userPage = userPage;
        this.legacyPage = null;
        this.summaryArgs = summaryArgs.toArray(new Object[summaryArgs.size()]);
    }
    
    public Page(SiteMapPage siteMapPage) {
        this.userPage = null;
        this.legacyPage = new LegacyPage(siteMapPage);
        this.summaryArgs = null;
    }
    
    public Page(String path, String module, String pageName, List<String> summaryArgs) {
        checkArgument(module != null);
        checkArgument(pageName != null);
        this.userPage = null;
        this.summaryArgs = summaryArgs.toArray(new Object[summaryArgs.size()]);
        this.legacyPage = new LegacyPage(path, module, pageName, this.summaryArgs);
    }
    
    public UserPage getUserPage() {
        return userPage;
    }
    
    public MessageSourceResolvable getSummary() {
        if (legacyPage != null) {
            return legacyPage.summary;
        }
        return new YukonMessageSourceResolvable(baseKey + userPage.getModule().getName() + "."
                + userPage.getName() + ".summary", summaryArgs);
    }
    
    public Object[] getSummaryArgs() {
        return summaryArgs;
    }
    
    public boolean isLegacyPage() {
        return legacyPage != null;
    }
    
    public MessageSourceResolvable getTitle() {
        return legacyPage.title;
    }
    
    public String getPath() {
        return legacyPage == null ? userPage.getPath() : legacyPage.path;
    }
    
}