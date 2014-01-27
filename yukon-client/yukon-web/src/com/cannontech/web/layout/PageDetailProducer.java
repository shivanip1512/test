package com.cannontech.web.layout;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.SimpleTemplateProcessor;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.menu.PageInfo;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;

public class PageDetailProducer {

    private static final String MODULE_NAME = "moduleName";
    private static final String NAVIGATION_TITLE = "navigationTitle"; // used for left side menu
    private static final String CRUMB_TITLE = "crumbTitle";
    private static final String PAGE_TITLE = "pageTitle";
    private static final String PAGE_HEADING = "pageHeading";
    private static final String PAGE_NAME = "pageName";
    private static final String PAGE_DESCRIPTION = "pageDescription";
    private static final String CONTEXTUAL_PAGE_NAME = "contextualPageName";
    
    private static final Logger log = YukonLogManager.getLogger(PageDetailProducer.class);
    @Autowired private HttpExpressionLanguageResolver resolver;

    public PageDetail render(PageInfo pageInfo, HttpServletRequest req, MessageSourceAccessor accessor) {
        
        PageDetail pageDetail = new PageDetail();

        PageContext pageContext = createPageContext(pageInfo, req, accessor);
        
        String pageTitle = getPagePart(PAGE_TITLE, pageContext, accessor);
        pageDetail.setPageTitle(pageTitle);
        
        String pageHeading = getPagePart(PAGE_HEADING, pageContext, accessor);
        pageDetail.setPageHeading(pageHeading);
        
        String crumbs = renderCrumbsFinal(pageContext, req, accessor);
        
        StringBuffer buf = new StringBuffer();
        // the following should replicate com.cannontech.web.taglib.nav.BreadCrumbsTag
        buf.append("<ol class=\"breadcrumb\">");
        buf.append(crumbs);
        buf.append("</ol>");
        buf.append("\n");
        
        pageDetail.setBreadCrumbText(buf.toString());
        
        if (pageInfo.isShowContextualNavigation()) {
            pageDetail.setContextualNavigationText(renderContextualNavigation(pageContext, req, accessor));

            PageInfo contextualNavigationRoot = pageInfo.findContextualNavigationRoot();
            if (contextualNavigationRoot != null) {
                String infoInclude = contextualNavigationRoot.getDetailInfoIncludePath();
                pageDetail.setDetailInfoIncludePath(infoInclude);
            }
        }
        
        return pageDetail;
    }
    
    private String renderContextualNavigation(PageContext pageContext, HttpServletRequest req, MessageSourceAccessor accessor) {
        
        PageInfo root = pageContext.pageInfo.findContextualNavigationRoot();
        
        List<PageInfo> pageInfosForMenu = Lists.newArrayListWithExpectedSize(15);
        collectPageInfosForMenu(root, pageInfosForMenu);
        
        LiteYukonUser yukonUser = ServletUtil.getYukonUser(req);
        
        List<PageContext> pageContextsForMenu = Lists.newArrayListWithCapacity(pageInfosForMenu.size());
        for (PageInfo pageInfo : pageInfosForMenu) {
            // apply security checks
            if (pageInfo.getUserChecker().check(yukonUser)) {
                pageContextsForMenu.add(createPageContext(pageInfo, req, accessor));
            }
        }
        
        PageContext pageToSelect = null;
        for (PageContext pageToEvaluate : Lists.reverse(pageContextsForMenu)) {
            if (isPageDescendantOf(pageContext.pageInfo, pageToEvaluate.pageInfo)) {
                pageToSelect = pageToEvaluate;
                break;
            }
        }
        StringBuilder result = new StringBuilder();
        result.append("<ul>\n");
        for (PageContext page : pageContextsForMenu) {
            String label = getPagePart(NAVIGATION_TITLE, page, accessor);

            String link = resolver.resolveElExpression(page.pageInfo.getLinkExpression(), req);

            String linkText = createLink(label, link);
            if (page == pageToSelect) {
                result.append("<li class=\"selected\">");
            } else {
                result.append("<li>");
            }
            result.append("<div>");
            result.append(linkText);
            result.append("</div></li>\n");
        }
        result.append("</ul>\n");
        
        return result.toString();
    }

    private void collectPageInfosForMenu(PageInfo root, Collection<PageInfo> pageInfosForMenu) {
        
        if (root.isContributeToMenu()) {
            pageInfosForMenu.add(root);
        }
        
        List<PageInfo> childPages = root.getChildPages();
        for (PageInfo pageInfo : childPages) {
            if (!pageInfo.isNavigationMenuRoot()) {
                collectPageInfosForMenu(pageInfo, pageInfosForMenu);
            }
        }
    }

    private boolean isPageDescendantOf(PageInfo one, PageInfo two) {
        if (one == null) {
            return false;
        }
        return one.equals(two) || isPageDescendantOf(one.getParent(), two);
    }

    public String renderCrumbsFinal(PageContext pageContext, HttpServletRequest req, MessageSourceAccessor accessor) {
        
        String previousCrumbs = renderCrumbs(pageContext.parent, req, accessor);

        String label = getPagePart(CRUMB_TITLE, pageContext, accessor);
        
        String thisCrumb = createLink(label, null);
        String result = previousCrumbs + "<li class=\"active\">" + thisCrumb + "</li>";
        return result;
    }

    private String renderCrumbs(PageContext pageContext, HttpServletRequest req, MessageSourceAccessor accessor) {
        
        if (pageContext == null) {
            return renderHomeCrumb(accessor);
        }
        String previousCrumbs = renderCrumbs(pageContext.parent, req, accessor);
        
        String label = getPagePart(CRUMB_TITLE, pageContext, accessor);
        String link = null;
        try {
            link = resolver.resolveElExpression(pageContext.pageInfo.getLinkExpression(), req);
        } catch (Exception e) {
            // Sometimes we can't resolve a link for a crumb, ie collection actions link
            // for a collection that was just deleted.
            log.debug("renderCrumbs(..) failed getting/using pageInfo's link expression.", e);
        }
            
        String thisCrumb;
        thisCrumb = createLink(label, link);
        String result = previousCrumbs + "<li>" + thisCrumb + "</li>";
        return result;
    }
    
    public String getPagePart(String pagePart, PageContext pageContext, MessageSourceAccessor accessor) {
        
        // look for specific override message
        PageInfo pageInfo = pageContext.pageInfo;
        String pagePrefix = "yukon.web.modules." + pageInfo.getModuleName() + "." + pageInfo.getName() + ".";
        String specificLabelKey = pagePrefix + pagePart;
        MessageSourceResolvable resolvable = YukonMessageSourceResolvable.createSingleCodeWithArgumentList(specificLabelKey, pageContext.labelArguments);
        try {
            String result = accessor.getMessage(resolvable);
            return result;
        } catch (NoSuchMessageException e) {
            LogHelper.trace(log, "no specific label found for %s on %s", pagePart, pageInfo);
        }
        
        String pagePartTemplate = accessor.getMessage("yukon.web.layout.standard.pageType." + pageInfo.getPageType() + ".pagePart." + pagePart);
        
        String result = new SimpleTemplateProcessor().process(pagePartTemplate, pageContext.pageLabels);
        return result;
    }
    
    private String renderHomeCrumb(MessageSourceAccessor accessor) {
        
        String message = accessor.getMessage("yukon.web.menu.home");
        String link = "/home";
        
        return "<li>" + createLink(message, link) + "</li>";
    }

    private String createLink(String label, String link) {
        
        // abbreviate label to prevent bread crumbs from being too long
        label = com.cannontech.common.util.StringUtils.elideCenter(label, 60);
        String safeLabel = StringEscapeUtils.escapeHtml(label);
        if (link == null) return safeLabel;
        String safeLink = StringEscapeUtils.escapeHtml(link);
        
        return "<a href=\"" + safeLink + "\">" + safeLabel + "</a>";
    }
    
    private PageContext createPageContext(PageInfo pageInfo, HttpServletRequest req, MessageSourceAccessor accessor) {
        
        if (pageInfo == null) return null;
        
        PageContext result = new PageContext();
        result.pageInfo = pageInfo;
        result.parent = createPageContext(pageInfo.getParent(), req, accessor);
        
        result.labelArguments = resolver.resolveElExpressions(pageInfo.getLabelArgumentExpressions(), req);
        fillInPageLabels(result, accessor);
        
        return result;
    }
    
    // use standard.xml to figure out the relevant page labels
    public void fillInPageLabels(PageContext pageContext, MessageSourceAccessor accessor) {
        
        Builder<String, String> resultBuilder = ImmutableMap.builder();
        
        PageInfo pageInfo = pageContext.pageInfo;
        String pagePrefix = "yukon.web.modules." + pageInfo.getModuleName() + "." + pageInfo.getName() + ".";
        
        String pageNameKey = pagePrefix + PAGE_NAME;
        String pageName = accessor.getMessageWithDefault(pageNameKey, pageInfo.getName());
        resultBuilder.put(PAGE_NAME, pageName);
        
        String contextualPageNameKey = pagePrefix + CONTEXTUAL_PAGE_NAME;
        String contextualPageName = accessor.getMessageWithDefault(contextualPageNameKey, pageName, pageContext.labelArguments.toArray());
        resultBuilder.put(CONTEXTUAL_PAGE_NAME, contextualPageName);
        
        String pageDescriptionKey = pagePrefix + PAGE_DESCRIPTION;
        MessageSourceResolvable pageDescriptionResolvable = YukonMessageSourceResolvable.createSingleCodeWithArgumentList(pageDescriptionKey, pageContext.labelArguments);
        String pageDescription;
        try {
            pageDescription = accessor.getMessage(pageDescriptionResolvable);
        } catch (NoSuchMessageException e) {
            pageDescription = findParentLabel(pageContext.parent, PAGE_DESCRIPTION);
        }
        if (pageDescription == null) {
            pageDescription = "";
        }
        resultBuilder.put(PAGE_DESCRIPTION, pageDescription);
        
        String moduleName = accessor.getMessage("yukon.web.modules." + pageInfo.getModuleName() + ".moduleName");
        resultBuilder.put(MODULE_NAME, moduleName);
        
        pageContext.pageLabels = resultBuilder.build();
    }
    
    private String findParentLabel(PageContext pageContext, String labelName) {
        
        if (pageContext == null) return null;
        
        return pageContext.pageLabels.get(labelName);
    }
    
    public static class PageContext {
        public List<String> labelArguments;
        public PageContext parent;
        public PageInfo pageInfo;
        public Map<String, String> pageLabels;
    }
    
}