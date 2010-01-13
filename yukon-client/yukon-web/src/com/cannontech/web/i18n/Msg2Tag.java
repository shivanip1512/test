package com.cannontech.web.i18n;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang.Validate;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.tags.HtmlEscapeTag;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.JavaScriptUtils;
import org.springframework.web.util.TagUtils;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.taglib.MessageScopeHelper;
import com.cannontech.web.taglib.StandardPageInfo;
import com.cannontech.web.taglib.StandardPageTag;
import com.cannontech.web.taglib.YukonTagSupport;

public class Msg2Tag extends YukonTagSupport {

    private Object arguments;
    private String argumentSeparator;
    private String var;
    private String scope = TagUtils.SCOPE_PAGE;
    private Object key;
    private boolean javaScriptEscape = false;
    private boolean htmlEscape = true;
    private boolean fallback = false;
    private boolean debug = false;
    
    @Override
    public void doTag() throws JspException, IOException {
        StandardPageInfo info = StandardPageTag.getStandardPageInfo(getRequest());
        Validate.notNull(info, "Must be called from within the scope of a StandardPageTag.");
        Object[] resolvedArguments = resolveArguments(arguments);

        MessageSourceResolvable resolvable;
        if (key instanceof String) {
            String baseCode = (String) key;
            resolvable = MessageScopeHelper.forRequest(getRequest()).generateResolvable(baseCode, resolvedArguments);
        } else if (key instanceof MessageSourceResolvable) {
            resolvable = (MessageSourceResolvable) key;
        } else if (key instanceof DisplayableEnum) {
            DisplayableEnum displayableEnum = (DisplayableEnum) key;
            resolvable = new YukonMessageSourceResolvable(displayableEnum.getFormatKey(), resolvedArguments);
        } else if (key == null) {
            throw new IllegalArgumentException("Expected a String MessageSourceResolvable, got a null");
        } else {
            throw new IllegalArgumentException("Expected a String or MessageSourceResolvable, got a " + key.getClass().getName());
        }
        
        String message;
        try {
            if (debug) {
                message = org.apache.commons.lang.StringUtils.join(resolvable.getCodes(), ", \n");
            } else {
                message = getMessageSource().getMessage(resolvable);
            }
        } catch (NoSuchMessageException e) {
            if (fallback) {
                message = resolvable.toString();
            } else {
                throw e;
            }
        }
        
        // HTML and/or JavaScript escape, if demanded.
        message = htmlEscape ? HtmlUtils.htmlEscape(message) : message;
        message = javaScriptEscape ? JavaScriptUtils.javaScriptEscape(message) : message;
        if (var == null) {
            getJspContext().getOut().print(message);
        } else {
            getJspContext().setAttribute(var, message, TagUtils.getScope(scope));
        }
    }    
    

    /**
     * Resolve the given arguments Object into an arguments array.
     * @param arguments the specified arguments Object
     * @return the resolved arguments as array
     * @throws JspException if argument conversion failed
     * @see #setArguments
     * @see org.springframework.web.servlet.tags.MessageTag#resolveArguments(Object)
     */
    protected Object[] resolveArguments(Object arguments) throws JspException {
        if (arguments instanceof String) {
            String[] stringArray = StringUtils.delimitedListToStringArray((String) arguments,
                                                                          this.argumentSeparator);
            return stringArray;
        } else if (arguments instanceof Object[]) {
            return (Object[]) arguments;
        } else if (arguments instanceof Collection<?>) {
            return ((Collection<?>) arguments).toArray();
        } else if (arguments != null) {
            // Assume a single argument object.
            return new Object[] { arguments };
        } else {
            return null;
        }
    }
    
    public void setArgument(Object argument) {
        setArguments(new Object[]{argument});
    }

    public void setKey(Object key) {
        this.key = key;
    }

    /**
     * Set optional message arguments for this tag, as a comma-delimited
     * String (each String argument can contain JSP EL), an Object array
     * (used as argument array), or a single Object (used as single argument).
     */
    public void setArguments(Object arguments) {
        this.arguments = arguments;
    }

    /**
     * Set the separator to use for splitting an arguments String.
     * Default is a comma (",").
     * @see #setArguments
     */
    public void setArgumentSeparator(String argumentSeparator) {
        this.argumentSeparator = argumentSeparator;
    }

    /**
     * Set PageContext attribute name under which to expose
     * a variable that contains the resolved message.
     * @see #setScope
     * @see javax.servlet.jsp.PageContext#setAttribute
     */
    public void setVar(String var) {
        this.var = var;
    }

    /**
     * Set the scope to export the variable to.
     * Default is SCOPE_PAGE ("page").
     * @see #setVar
     * @see org.springframework.web.util.TagUtils#SCOPE_PAGE
     * @see javax.servlet.jsp.PageContext#setAttribute
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Set JavaScript escaping for this tag, as boolean value.
     * Default is "false".
     */
    public void setJavaScriptEscape(boolean javaScriptEscape) throws JspException {
        this.javaScriptEscape = javaScriptEscape;
    }

    /**
     * Set HTML escaping for this tag, as boolean value.
     * Overrides the default HTML escaping setting for the current page.
     * @see HtmlEscapeTag#setDefaultHtmlEscape
     */
    public void setHtmlEscape(boolean htmlEscape) throws JspException {
        this.htmlEscape = htmlEscape;
    }
    
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    
    public void setFallback(boolean fallback) {
        this.fallback = fallback;
    }
}
