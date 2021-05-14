package com.cannontech.web.layout;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.SimpleTemplateProcessor;
import com.cannontech.common.util.StringUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.menu.PageInfo;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;

public class PageDetailProducer {
    
    private final static Logger log = YukonLogManager.getLogger(PageDetailProducer.class);

    private final static String MODULE_NAME = "moduleName";
    private final static String NAVIGATION_TITLE = "navigationTitle"; // used for left side menu
    private final static String CRUMB_TITLE = "crumbTitle";
    private final static String PAGE_TITLE = "pageTitle";
    private final static String PAGE_HEADING = "pageHeading";
    private final static String PAGE_NAME = "pageName";
    private final static String PAGE_DESCRIPTION = "pageDescription";
    private final static String CONTEXTUAL_PAGE_NAME = "contextualPageName";

    @Autowired private HttpExpressionLanguageResolver expressionLanguageResolver;
    @Autowired private RolePropertyDao rolePropertyDao;
    
    public PageDetail render(PageInfo pageInfo, HttpServletRequest request, MessageSourceAccessor messageSourceAccessor) {
        PageDetail pageDetail = new PageDetail();

        PageContext pageContext = createPageContext(pageInfo, request, messageSourceAccessor);

        String pageTitle = getPagePart(PAGE_TITLE, pageContext, messageSourceAccessor);
        pageDetail.setPageTitle(pageTitle);

        String pageHeading = getPagePart(PAGE_HEADING, pageContext, messageSourceAccessor);
        pageDetail.setPageHeading(StringUtils.elideCenter(pageHeading, 60));

        String crumbs = renderCrumbsFinal(pageContext, request, messageSourceAccessor);

        StringBuffer buf = new StringBuffer();
        // the following should replicate com.cannontech.web.taglib.nav.BreadCrumbsTag
        buf.append("<ol class=\"breadcrumb\">");
        buf.append(crumbs);
        buf.append("</ol>");
        buf.append("\n");

        pageDetail.setBreadCrumbText(buf.toString());

        if (pageInfo.isShowContextualNavigation()) {
            pageDetail.setContextualNavigationText(renderContextualNavigation(pageContext, request,
                messageSourceAccessor));

            PageInfo contextualNavigationRoot = pageInfo.findContextualNavigationRoot();
            if (contextualNavigationRoot != null) {
                String infoInclude = contextualNavigationRoot.getDetailInfoIncludePath();
                pageDetail.setDetailInfoIncludePath(infoInclude);
            }
        }

        return pageDetail;
    }

    private String renderContextualNavigation(PageContext pageContext, HttpServletRequest request,
            MessageSourceAccessor messageSourceAccessor) {
        PageInfo root = pageContext.pageInfo.findContextualNavigationRoot();

        List<PageInfo> pageInfosForMenu = Lists.newArrayListWithExpectedSize(15);
        collectPageInfosForMenu(root, pageInfosForMenu);

        LiteYukonUser yukonUser = ServletUtil.getYukonUser(request);

        List<PageContext> pageContextsForMenu = Lists.newArrayListWithCapacity(pageInfosForMenu.size());
        for (PageInfo pageInfo : pageInfosForMenu) {
            // apply security checks
            if (pageInfo.getUserChecker().check(yukonUser)) {
                pageContextsForMenu.add(createPageContext(pageInfo, request, messageSourceAccessor));
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
            String label = getPagePart(NAVIGATION_TITLE, page, messageSourceAccessor);

            String link = expressionLanguageResolver.resolveElExpression(page.pageInfo.getLinkExpression(), request);

            String linkText = createLink(request, label, link);
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

    public String renderCrumbsFinal(PageContext pageContext, HttpServletRequest request,
            MessageSourceAccessor messageSourceAccessor) {
        String previousCrumbs = renderCrumbs(pageContext.parent, request, messageSourceAccessor);

        String label = getPagePart(CRUMB_TITLE, pageContext, messageSourceAccessor);

        String thisCrumb = createLink(request, label, null);
        String result = previousCrumbs + "<li class=\"breadcrumb-item active\">" + thisCrumb + "</li>";
        return result;
    }

    private String renderCrumbs(PageContext pageContext, HttpServletRequest request,
            MessageSourceAccessor messageSourceAccessor) {
        if (pageContext == null) {
            return renderHomeCrumb(request, messageSourceAccessor);
        }
        String previousCrumbs = renderCrumbs(pageContext.parent, request, messageSourceAccessor);

        String label = getPagePart(CRUMB_TITLE, pageContext, messageSourceAccessor);
        String link = null;
        try {
            link = expressionLanguageResolver.resolveElExpression(pageContext.pageInfo.getLinkExpression(), request);
        } catch (Exception e) {
            // Sometimes we can't resolve a link for a crumb, ie collection actions link
            // for a collection that was just deleted.
            log.debug("renderCrumbs(..) failed getting/using pageInfo's link expression.", e);
        }

        String thisCrumb = createLink(request, label, link);
        String result = previousCrumbs + "<li class='breadcrumb-item'>" + thisCrumb + "</li>";
        return result;
    }
    
    public String getPagePart(String pagePart, PageContext pageContext, MessageSourceAccessor messageSourceAccessor) {
        // look for specific override message
        PageInfo pageInfo = pageContext.pageInfo;
        String pagePrefix = "yukon.web.modules." + pageInfo.getModuleName() + "." + pageInfo.getName() + ".";
        String specificLabelKey = pagePrefix + pagePart;
        MessageSourceResolvable resolvable =
            YukonMessageSourceResolvable.createSingleCodeWithArgumentList(specificLabelKey, pageContext.labelArguments);
        try {
            String result = messageSourceAccessor.getMessage(resolvable);
            return result;
        } catch (NoSuchMessageException e) {
            log.trace(String.format("no specific label found for %s on %s", pagePart, pageInfo));
        }
        
        String pagePartTemplate =
            messageSourceAccessor.getMessage("yukon.web.layout.standard.pageType." + pageInfo.getPageType()
                + ".pagePart." + pagePart);
        
        String result = new SimpleTemplateProcessor().process(pagePartTemplate, pageContext.pageLabels);
        return result;
    }
    
    private String renderHomeCrumb(HttpServletRequest request, MessageSourceAccessor accessor) {
        
        String message = accessor.getMessage("yukon.web.menu.home");
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        String homeUrl = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.HOME_URL, user);
        
        return "<li class='breadcrumb-item'>" + createLink(request, message, homeUrl) + "</li>";
    }
    
    private String createLink(HttpServletRequest request, String label, String link) {
        // abbreviate label to prevent bread crumbs from being too long
        label = StringUtils.elideCenter(label, 60);
        String safeLabel = StringEscapeUtils.escapeHtml4(label);
        if (org.apache.commons.lang3.StringUtils.isEmpty(link)) {
            return safeLabel;
        }
        String safeLink = StringEscapeUtils.escapeHtml4(link);
        
        return "<a href=\"" + request.getContextPath() + safeLink + "\">" + safeLabel + "</a>";
    }
    
    private PageContext createPageContext(PageInfo pageInfo, HttpServletRequest request,
            MessageSourceAccessor messageSourceAccessor) {
        if (pageInfo == null) {
            return null;
        }

        PageContext result = new PageContext();
        result.pageInfo = pageInfo;
        result.parent = createPageContext(pageInfo.getParent(), request, messageSourceAccessor);

        result.labelArguments =
            expressionLanguageResolver.resolveElExpressions(pageInfo.getLabelArgumentExpressions(), request);
        fillInPageLabels(result, messageSourceAccessor);

        return result;
    }

    // use standard.xml to figure out the relevant page labels
    public void fillInPageLabels(PageContext pageContext, MessageSourceAccessor messageSourceAccessor) {
        Builder<String, String> resultBuilder = ImmutableMap.builder();

        PageInfo pageInfo = pageContext.pageInfo;
        String pagePrefix = "yukon.web.modules." + pageInfo.getModuleName() + "." + pageInfo.getName() + ".";

        String pageNameKey = pagePrefix + PAGE_NAME;
        String pageName = messageSourceAccessor.getMessageWithDefault(pageNameKey, pageInfo.getName());
        resultBuilder.put(PAGE_NAME, pageName);

        String contextualPageNameKey = pagePrefix + CONTEXTUAL_PAGE_NAME;
        String contextualPageName =
            messageSourceAccessor.getMessageWithDefault(contextualPageNameKey, pageName,
                pageContext.labelArguments.toArray());
        resultBuilder.put(CONTEXTUAL_PAGE_NAME, contextualPageName);

        String pageDescriptionKey = pagePrefix + PAGE_DESCRIPTION;
        MessageSourceResolvable pageDescriptionResolvable =
            YukonMessageSourceResolvable.createSingleCodeWithArgumentList(pageDescriptionKey,
                pageContext.labelArguments);
        String pageDescription;
        try {
            pageDescription = messageSourceAccessor.getMessage(pageDescriptionResolvable);
        } catch (NoSuchMessageException e) {
            pageDescription = findParentLabel(pageContext.parent, PAGE_DESCRIPTION);
        }
        if (pageDescription == null) {
            pageDescription = "";
        }
        resultBuilder.put(PAGE_DESCRIPTION, pageDescription);

        String moduleName =
            messageSourceAccessor.getMessage("yukon.web.modules." + pageInfo.getModuleName() + ".moduleName");
        resultBuilder.put(MODULE_NAME, moduleName);

        pageContext.pageLabels = resultBuilder.build();
    }

    private String findParentLabel(PageContext pageContext, String labelName) {
        if (pageContext == null) {
            return null;
        }

        return pageContext.pageLabels.get(labelName);
    }

    public static class PageContext {
        public List<String> labelArguments;
        public PageContext parent;
        public PageInfo pageInfo;
        public Map<String, String> pageLabels;
    }
}
