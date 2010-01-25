package com.cannontech.web.layout;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.FunctionMapper;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ValueExpression;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.el.ExpressionFactoryImpl;
import org.apache.jasper.el.ELContextImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.util.SimpleTemplateProcessor;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.menu.PageInfo;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;

public class PageDetailProducer {
    private static final Logger log = YukonLogManager.getLogger(PageDetailProducer.class);
    private ObjectFormattingService objectFormattingService;
    private static final String CRUMB_SEPERATOR = " &gt; ";
    private static final CompositeELResolver compositeElResolver = new CompositeELResolver();
    private static final ExpressionFactoryImpl factory = new ExpressionFactoryImpl();
    static {
        compositeElResolver.add(new HttpRequestElResolver()); // our custom resolver
        compositeElResolver.add(new MapELResolver()); // this and the following are standard
        compositeElResolver.add(new ListELResolver());
        compositeElResolver.add(new ArrayELResolver());  
        compositeElResolver.add(new BeanELResolver());
    }

    public PageDetail render(PageInfo pageInfo, HttpServletRequest request, MessageSourceAccessor messageSourceAccessor) {
        PageDetail pageDetail = new PageDetail();

        PageContext pageContext = createPageContext(pageInfo, request, messageSourceAccessor);
        
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
        return pageDetail;
    }
    
    public String renderCrumbsFinal(PageContext pageContext, HttpServletRequest request, MessageSourceAccessor messageSourceAccessor) {
        String previousCrumbs = renderCrumbs(pageContext.parent, request, messageSourceAccessor);
        previousCrumbs += CRUMB_SEPERATOR;
        
        String label = getPagePart("crumbTitle", pageContext, messageSourceAccessor);
        
        String result = previousCrumbs + label;
        return result;
    }

    private String renderCrumbs(PageContext pageContext, HttpServletRequest request, MessageSourceAccessor messageSourceAccessor) {
        if (pageContext == null) {
            return renderHomeCrumb(messageSourceAccessor);
        }
        String previousCrumbs = renderCrumbs(pageContext.parent, request, messageSourceAccessor);
        previousCrumbs += CRUMB_SEPERATOR;
        
        String label = getPagePart("crumbTitle", pageContext, messageSourceAccessor);
        
        String link = resolveElExpression(pageContext.pageInfo.getLinkExpression(), request);
        
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
    
    private List<String> resolveElExpressions(List<String> labelArgumentExpressions,
            HttpServletRequest request) {
        List<String> result = Lists.newArrayListWithExpectedSize(labelArgumentExpressions.size());
        for (String expression : labelArgumentExpressions) {
            String text = resolveElExpression(expression, request);
            result.add(text);
        }
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
        return "<a href=\"" + link + "\">" + safeLabel + "</a>";
    }
    
    private String resolveElExpression(String expression, HttpServletRequest request) {
        if (StringUtils.isBlank(expression)) return null;
        ELContextImpl context = new ELContextImpl(compositeElResolver);
        context.putContext(ServletRequest.class, request);
        context.setFunctionMapper(new FunctionMapper() {
            public Method resolveFunction(String prefix, String localName) {
                if (!StringUtils.equals(prefix, "local")) return null;

                // look for a method on this class
                Method[] methods = PageDetailProducer.class.getMethods();
                for (Method method : methods) {
                    if (!Modifier.isStatic(method.getModifiers())) continue;
                    
                    if (StringUtils.equals(localName, method.getName())) {
                        return method;
                    }
                }
                
                return null;
            }
            
        });
        
        ValueExpression valueExpression = factory.createValueExpression(context, expression, Object.class);
        Object value = valueExpression.getValue(context);
        String result = objectFormattingService.formatObjectAsString(value, YukonUserContextUtils.getYukonUserContext(request));
        return result;
    }
    
    public static String convertMapToQueryString(Map<String, String> map) {
        return ServletUtil.buildQueryStringFromMap(map, true);
    }
    
    @Autowired
    public void setObjectFormattingService(ObjectFormattingService objectFormattingService) {
        this.objectFormattingService = objectFormattingService;
    }
    
    private PageContext createPageContext(PageInfo pageInfo, HttpServletRequest request, MessageSourceAccessor messageSourceAccessor) {
        if (pageInfo == null) return null;
        
        PageContext result = new PageContext();
        result.pageInfo = pageInfo;
        result.parent = createPageContext(pageInfo.getParent(), request, messageSourceAccessor);
        
        result.labelArguments = resolveElExpressions(pageInfo.getLabelArgumentExpressions(), request);
        fillInPageLabels(result, messageSourceAccessor);
        
        return result;
    }
    
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

    private static class PageContext {
        List<String> labelArguments;
        PageContext parent;
        PageInfo pageInfo;
        Map<String, String> pageLabels;
    }
    
}
