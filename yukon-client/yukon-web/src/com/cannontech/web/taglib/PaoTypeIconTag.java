package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.definition.model.PaoTypeIcon;

@Configurable(value="paoTypeIconTagPrototype", autowire=Autowire.BY_NAME)
public class PaoTypeIconTag extends YukonTagSupport {
    
    private @Autowired PaoDefinitionDao paoDefDao;

    private YukonPao yukonPao;
    private String var;
    
    @Override
    public void doTag() throws JspException, IOException {
        
        PaoType paoType = yukonPao.getPaoIdentifier().getPaoType();
        
        String classname = "";
        if (paoDefDao.isTagSupported(paoType, PaoTag.DEVICE_ICON_TYPE)) {
            classname = PaoTypeIcon.valueOf(paoDefDao.getValueForTagString(paoType, PaoTag.DEVICE_ICON_TYPE).toUpperCase()).getIcon();
        }
        
        if (StringUtils.isNotBlank(var)) {
            getJspContext().setAttribute(var, classname);
        } else {
            if (StringUtils.isNotBlank(classname)) {
                getJspContext().getOut().print("<i class=\"icon " + classname + "\"></i>");
            }
        }
        
    }
    
    public void setYukonPao(YukonPao yukonPao) {
        this.yukonPao = yukonPao;
    }
    
    public void setVar(String var) {
        this.var = var;
    }
    
}