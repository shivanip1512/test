package com.cannontech.web.i18n;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.NoSuchMessageException;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.taglib.YukonTagSupport;

/**
 * Tag used to output the right side logo for a page if one exists
 */
@Configurable("logoTagPrototype")
public class LogoTag extends YukonTagSupport {

    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private String key;

    public void setMessageSourceResolver(
            YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public void doTag() throws JspException, IOException {

        YukonUserContext userContext = getUserContext();

        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        try {
            String url = messageSourceAccessor.getMessage(key);
            ServletRequest request = this.getRequest();
            String safeUrl = ServletUtil.createSafeUrl(request, url);
            JspWriter out = getJspContext().getOut();

            // Create image html
            StringBuilder sb = new StringBuilder();
            sb.append("<img src=\"");
            sb.append(safeUrl);
            sb.append("\"");

            // Add alt text if it exists
            try {
                String altText = messageSourceAccessor.getMessage(key + ".alt");

                sb.append(" alt=\"");
                sb.append(altText);
                sb.append("\"");
            } catch (NoSuchMessageException e) {
                // no alt text
            }

            sb.append(">");

            out.write(sb.toString());

        } catch (NoSuchMessageException e) {
            // message not found - do nothing
        }

    }
}
