package com.cannontech.web.i18n;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.JavaScriptUtils;
import org.springframework.web.util.TagUtils;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.web.taglib.MessageScopeHelper;
import com.cannontech.web.taglib.YukonTagSupport;
import com.google.common.collect.Maps;

@Configurable("msg2TagPrototype")
public class Msg2Tag extends YukonTagSupport {
    private final static Logger log = YukonLogManager.getLogger(Msg2Tag.class);

    private ObjectFormattingService objectFormattingService;

    private Object arguments;
    private String var;
    private String scope = TagUtils.SCOPE_PAGE;
    private Object key;
    private boolean javaScriptEscape = false;
    private boolean htmlEscape = false;
    private boolean htmlEscapeArguments = true;
    private boolean fallback = false;
    private boolean debug = false;
    private boolean blankIfMissing = false;
    
    @Override
    public void doTag() throws JspException, IOException {
        Object[] resolvedArguments = resolveArguments(arguments);
        if (htmlEscapeArguments && arguments != null) {
            for (int index = 0; index < resolvedArguments.length; index++) {
                if (resolvedArguments[index] instanceof String) {
                    resolvedArguments[index] = HtmlUtils.htmlEscape((String) resolvedArguments[index]);
                }
            }
        }

        MessageSourceResolvable resolvable;
        if (key instanceof String) {
            String baseCode = (String) key;
            resolvable = MessageScopeHelper.forRequest(getRequest()).generateResolvable(baseCode, resolvedArguments);
        } else {
            resolvable = objectFormattingService.formatObjectAsResolvable(key, getUserContext(), resolvedArguments);
        }

        String message;
        try {
            message = getMessageSource().getMessage(resolvable);
        } catch (NoSuchMessageException e) {
            if (blankIfMissing) {
                message = "";
            } else if (fallback) {
                message = resolvable.toString();
            } else {
                if (key instanceof String) {
                    log.error("unable to resolve message for key [" + key + "] using scope " +
                              MessageScopeHelper.forRequest(getRequest()));
                }
                throw e;
            }
        }
        
        if (debug) {
            String[] codes = resolvable.getCodes();
            Map<String,String> debugMap = Maps.newLinkedHashMap();
            if (StringUtils.isNotBlank(resolvable.getDefaultMessage())) {
                debugMap.put("[default]", resolvable.getDefaultMessage());
            }
            if (codes != null) {
                for (int i = 0; i < codes.length; i++) {
                    String specificCode = codes[i];
                    String specificCodeMessage = "[undefined]";
                    try {
                        specificCodeMessage = getMessageSource().getMessage(specificCode, resolvedArguments);
                    } catch (NoSuchMessageException e) {
                        // debug mode
                    }
                    debugMap.put(specificCode, specificCodeMessage);
                }
            }
            getJspContext().setAttribute("msg2TagDebugMap", debugMap, TagUtils.getScope(TagUtils.SCOPE_PAGE));
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
        
        if (arguments instanceof Object[]) {
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
     * Set HTML escaping for this tag.  The default is false.  Escaping
     * happens after arguments are added.  Setting this to true will force
     * htmlEscapeArguments to false so arguments don't get double-encoded.
     */
    public void setHtmlEscape(boolean htmlEscape) throws JspException {
        this.htmlEscape = htmlEscape;
        if (htmlEscape) {
            htmlEscapeArguments = false;
        }
    }

    /**
     * Set HTML escaping for arguments of this tag.  The default is true.
     * Setting this to true does nothing if htmlEscape is true since that
     * will already cause escaping of arguments to happen.
     * 
     */
    public void setHtmlEscapeArguments(boolean htmlEscapeArguments) {
        if (!htmlEscape) {
            this.htmlEscapeArguments = htmlEscapeArguments;
        }
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    
    public void setFallback(boolean fallback) {
        this.fallback = fallback;
    }
    
    public void setBlankIfMissing(boolean blankIfMissing) {
        this.blankIfMissing = blankIfMissing;
    }
    
    public void setObjectFormattingService(ObjectFormattingService objectFormattingService) {
        this.objectFormattingService = objectFormattingService;
    }
}