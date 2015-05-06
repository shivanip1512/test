package com.cannontech.web.stars.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.stars.util.ServletUtils;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.navigation.CtiNavObject;

public abstract class StarsImportManagerActionController extends AbstractBaseActionController {

	@Override
    public String getRedirect(HttpServletRequest request) throws ServletRequestBindingException {
		String redirect = ServletRequestUtils.getStringParameter(request, ServletUtils.ATT_REDIRECT );
		
	    if (redirect == null) {
	    	redirect = this.getReferer(request);
	    }
	    redirect = ServletUtil.createSafeRedirectUrl(request, redirect);
		return redirect;
	}
	
	@Override
    public String getReferer(HttpServletRequest request) throws ServletRequestBindingException {
	    String referer = ServletRequestUtils.getStringParameter(request, ServletUtils.ATT_REFERRER );

		if (referer == null) {
			HttpSession session = request.getSession(false);
			referer = ((CtiNavObject) session.getAttribute(ServletUtils.NAVIGATE)).getPreviousPage();
		}
		referer = ServletUtil.createSafeRedirectUrl(request, referer);
		return referer;
	}
	
    public String getFormField(final List<FileItem> itemList, final String fieldName) {
    	for (FileItem item : itemList) {
    		if (item.isFormField() && item.getFieldName().equals(fieldName)) {
    			return item.getString();
    		}
    	}
    	return null;
    }
}
