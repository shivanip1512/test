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
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;

public class PageDetailProducer {
    private static final Logger log = YukonLogManager.getLogger(PageDetailProducer.class);
    private static final String CRUMB_SEPERATOR = " &gt; ";
    
    private HttpExpressionLanguageResolver expressionLanguageResolver;

    public PageDetail render(PageInfo pageInfo, HttpServletRequest request, MessageSourceAccessor messageSourceAccessor) {
        PageDetail pageDetail = new PageDetail();

        PageContext pageContext = createPageContext(pageInfo, request, messageSourceAccessor, null);
        
        String pageTitle = getPagePart("pageTitle", pageContext, messageSourceAccessor);
        pageDetail.setPageTitle(pageTitle);
        
        String pageHeading = getPagePart("pageHeading", pageContext, messageSourceAccessor);
        pageDetail.setPageHeading(pageHeading);
        
        String crumbs = renderCrumbsFinal(pageContext, request, messageSourceAccessor);
        
        StringBuffer buf = new StringBuffer();
        // the following should replicate com.cannontech.web.taglib.nav.BreadCrumbsTag
        buf.append("<div class=\"breadCrumbs\">");
        buf.append(crumbs);
        buf.append("</div>");
        buf.append("\n");
        
        pageDetail.setBreadCrumbText(buf.toString());
        
        if (pageInfo.isShowContextualNavigation()) {
            pageDetail.setContextualNavigationText(renderContextualNavigation(pageContext, request, messageSourceAccessor));

            PageInfo contextualNavigationRoot = pageInfo.findContextualNavigationRoot();
            if (contextualNavigationRoot != null) {
                String infoInclude = contextualNavigationRoot.getDetailInfoIncludePath();
                pageDetail.setDetailInfoIncludePath(infoInclude);
            }
        }
        
        return pageDetail;
    }
    
    private String renderContextualNavigation(PageContext pageContext, HttpServletRequest request, MessageSourceAccessor messageSourceAccessor) {
        PageInfo root = pageContext.pageInfo.findContextualNavigationRoot();
        
        List<PageInfo> pageInfosForMenu = Lists.newArrayListWithExpectedSize(15);
        collectPageInfosForMenu(root, pageInfosForMenu);
        
        LiteYukonUser yukonUser = ServletUtil.getYukonUser(request);
        
        List<PageContext> pageContextsForMenu = Lists.newArrayListWithCapacity(pageInfosForMenu.size());
        for (PageInfo pageInfo : pageInfosForMenu) {
            // apply security checks
            if (pageInfo.getUserChecker().check(yukonUser)) {
                pageContextsForMenu.add(createPageContext(pageInfo, request, messageSourceAccessor, null));
            }
        }
        
        PageContext pageToSelect = null;
        for (PageContext pageToEvaluate : Iterables.reverse(pageContextsForMenu)) {
            if (isPageDescendantOf(pageContext.pageInfo, pageToEvaluate.pageInfo)) {
                pageToSelect = pageToEvaluate;
                break;
            }
        }
        StringBuilder result = new StringBuilder();
        result.append("<ul>\n");
        for (PageContext page : pageContextsForMenu) {
            String label = getPagePart("navigationTitle", page, messageSourceAccessor);

            String link = expressionLanguageResolver.resolveElExpression(page.pageInfo.getLinkExpression(), request);

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

    public String renderCrumbsFinal(PageContext pageContext, HttpServletRequest request, MessageSourceAccessor messageSourceAccessor) {
        String previousCrumbs = renderCrumbs(pageContext.parent, request, messageSourceAccessor);
        previousCrumbs += CRUMB_SEPERATOR;
        
        String label = getPagePart("crumbTitle", pageContext, messageSourceAccessor);
        
        String thisCrumb = createLink(label, null);
        String result = previousCrumbs + thisCrumb;
        return result;
    }

    private String renderCrumbs(PageContext pageContext, HttpServletRequest request, MessageSourceAccessor messageSourceAccessor) {
        if (pageContext == null) {
            return renderHomeCrumb(messageSourceAccessor);
        }
        String previousCrumbs = renderCrumbs(pageContext.parent, request, messageSourceAccessor);
        previousCrumbs += CRUMB_SEPERATOR;
        
        String label = getPagePart("crumbTitle", pageContext, messageSourceAccessor);
        String link = expressionLanguageResolver.resolveElExpression(pageContext.pageInfo.getLinkExpression(), request);
        
        String thisCrumb;
        thisCrumb = createLink(label, link);
        String result = previousCrumbs + thisCrumb;
        return result;
    }
    
    private String getPagePart(String pagePart, PageContext pageContext, MessageSourceAccessor messageSourceAccessor) {
        // look for specific override message
        PageInfo pageInfo = pageContext.pageInfo;
        String pagePrefix = "yukon.web.modules." + pageInfo.getModuleName() + "." + pageInfo.getName() + ".";
        String specificLabelKey = pagePrefix + pagePart;
        MessageSourceResolvable resolvable = YukonMessageSourceResolvable.createSingleCodeWithArgumentList(specificLabelKey, pageContext.labelArguments);
        try {
            String result = messageSourceAccessor.getMessage(resolvable);
            return result;
        } catch (NoSuchMessageException e) {
            LogHelper.trace(log, "no specific label found for %s on %s", pagePart, pageInfo);
        }
        
        String pagePartTemplate = messageSourceAccessor.getMessage("yukon.web.layout.standard.pageType." + pageInfo.getPageType() + ".pagePart." + pagePart);
        
        String result = new SimpleTemplateProcessor().process(pagePartTemplate, pageContext.pageLabels);
        return result;
    }
    
    private String renderHomeCrumb(MessageSourceAccessor messageSourceAccessor) {
        String message = messageSourceAccessor.getMessage("yukon.web.menu.home");
        
        String link = "/home";
        return createLink(message, link );
    }

    private String createLink(String label, String link) {
        // abbreviate label to prevent bread crumbs from being too long
        label = com.cannontech.common.util.StringUtils.elideCenter(label, 60);
        String safeLabel = StringEscapeUtils.escapeHtml(label);
        if (link == null) return safeLabel;
        String safeLink = StringEscapeUtils.escapeHtml(link);
        return "<a href=\"" + safeLink + "\">" + safeLabel + "</a>";
    }
    
    private PageContext createPageContext(PageInfo pageInfo, HttpServletRequest request, MessageSourceAccessor messageSourceAccessor, PageContext parent) {
        if (pageInfo == null) return null;
        
        PageContext result = new PageContext();
        result.pageInfo = pageInfo;
        if (parent != null) {
            result.parent = parent;
        } else {
            result.parent = createPageContext(pageInfo.getParent(), request, messageSourceAccessor, null);
        }
        
        result.labelArguments = expressionLanguageResolver.resolveElExpressions(pageInfo.getLabelArgumentExpressions(), request);
        fillInPageLabels(result, messageSourceAccessor);
        
        return result;
    }
    
    // use standard.xml to figure out the relevant page labels
    private void fillInPageLabels(PageContext pageContext, MessageSourceAccessor messageSourceAccessor) {
        Builder<String, String> resultBuilder = ImmutableMap.builder();
        
        PageInfo pageInfo = pageContext.pageInfo;
        String pagePrefix = "yukon.web.modules." + pageInfo.getModuleName() + "." + pageInfo.getName() + ".";
        
        String pageNameKey = pagePrefix + "pageName";
        String pageName = messageSourceAccessor.getMessageWithDefault(pageNameKey, pageInfo.getName());
        resultBuilder.put("pageName", pageName);
        
        String contextualPageNameKey = pagePrefix + "contextualPageName";
        String contextualPageName = messageSourceAccessor.getMessageWithDefault(contextualPageNameKey, pageName);
        resultBuilder.put("contextualPageName", contextualPageName);
        
        String pageDescriptionKey = pagePrefix + "pageDescription";
        MessageSourceResolvable pageDescriptionResolvable = YukonMessageSourceResolvable.createSingleCodeWithArgumentList(pageDescriptionKey, pageContext.labelArguments);
        String pageDescription;
        try {
            pageDescription = messageSourceAccessor.getMessage(pageDescriptionResolvable);
        } catch (NoSuchMessageException e) {
            pageDescription = findParentLabel(pageContext.parent, "pageDescription");
        }
        if (pageDescription == null) {
            pageDescription = "";
        }
        resultBuilder.put("pageDescription", pageDescription);
        
        String moduleName = messageSourceAccessor.getMessage("yukon.web.modules." + pageInfo.getModuleName() + ".moduleName");
        resultBuilder.put("moduleName", moduleName);
        
        pageContext.pageLabels = resultBuilder.build();
    }
    
    private String findParentLabel(PageContext pageContext, String labelName) {
        if (pageContext == null) return null;
        return pageContext.pageLabels.get(labelName);
    }
    
    @Autowired
    public void setExpressionLanguageResolver(
            HttpExpressionLanguageResolver expressionLanguageResolver) {
        this.expressionLanguageResolver = expressionLanguageResolver;
    }

    private static class PageContext {
        List<String> labelArguments;
        PageContext parent;
        PageInfo pageInfo;
        Map<String, String> pageLabels;
    }
    
}
