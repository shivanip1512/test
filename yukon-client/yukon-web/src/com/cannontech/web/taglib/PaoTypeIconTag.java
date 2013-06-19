package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.definition.model.PaoTypeIcon;

@Configurable("paoTypeIconTagPrototype")
public class PaoTypeIconTag extends YukonTagSupport {
    private PaoDefinitionDao paoDefinitionDao;

    private YukonPao yukonPao = null;

    @Override
    public void doTag() throws JspException, IOException {
        PaoType paoType = yukonPao.getPaoIdentifier().getPaoType();

        String classname = null;
        if (paoDefinitionDao.isTagSupported(paoType, PaoTag.DEVICE_ICON_TYPE)) {
            classname = PaoTypeIcon.valueOf(paoDefinitionDao.getValueForTagString(paoType, PaoTag.DEVICE_ICON_TYPE).toUpperCase()).getIcon();
        }

        JspWriter out = getJspContext().getOut();
        out.print("<span class=\"icon "+classname+"\"></span>");
        
    }
    
    public void setYukonPao(YukonPao yukonPao) {
        this.yukonPao = yukonPao;
    }

    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }
}
