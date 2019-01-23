package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.GraphDao;
import com.cannontech.database.data.lite.LiteGraphDefinition;

@Configurable(value = "trendNameTagPrototype", autowire = Autowire.BY_NAME)
public class TrendNameTag extends YukonTagSupport {

    @Autowired private GraphDao graphDao;

    private Integer trendId = 0;
    private String var = null;
    
    private final static Logger log = YukonLogManager.getLogger(TrendNameTag.class);

    public Integer getTrendId() {
        return trendId;
    }

    public void setTrendId(Integer trendId) {
        this.trendId = trendId;
    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    @Override
    public void doTag() throws JspException, IOException {

        if (trendId == null) {
            throw new JspException("trendId is not set");
        }

        String formattedName = "";
        LiteGraphDefinition liteGraphDefinition = graphDao.getLiteGraphDefinition(trendId);
        if (liteGraphDefinition == null) {
            log.error("trendId: " + trendId + " is not a valid trendId");
        } else {
            formattedName = liteGraphDefinition.getName();
        }
        JspContext jspContext = getJspContext();
        if (var == null) {
            JspWriter out = jspContext.getOut();
            out.print(StringEscapeUtils.escapeHtml4(formattedName));
        } else {
            jspContext.setAttribute(var, formattedName);
        }
    }
}
