package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;

/**
 * If the user is authorized for the permission on the pao then the body of the tag is evaluated, 
 * otherwise it is skipped.
 */
@Configurable("checkPaoAuthorizationTagPrototype")
public class CheckPaoAuthorizationTag extends YukonTagSupport {

    private Permission permission = null;
    private YukonPao pao = null;
    private boolean invert = false;
    
    // injected dependencies
    private PaoAuthorizationService paoAuthorizationService;
    
    @Override
    public void doTag() throws JspException, IOException {
        LiteYukonUser yukonUser = ServletUtil.getYukonUser(getPageContext().getRequest());
        boolean authorized = paoAuthorizationService.isAuthorized(yukonUser, permission, pao);
        if ((invert)? !authorized : authorized) {
            getJspBody().invoke(null);
        }
    }
    
    public void setPermission(String permission) {
        this.permission = Permission.valueOf(permission);
    }
    
    public void setPao(YukonPao pao) {
        this.pao = pao;
    }
    
    public void setInvert(boolean invert) {
        this.invert = invert;
    }
    
    public void setPaoAuthorizationService(PaoAuthorizationService paoAuthorizationService) {
        this.paoAuthorizationService = paoAuthorizationService;
    }

}
