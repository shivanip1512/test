package com.cannontech.web.stars.action;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.web.navigation.CtiNavObject;

public abstract class StarsImportManagerActionController extends AbstractBaseActionController {

	@Override
    public String getRedirect(HttpServletRequest request) throws Exception {
		boolean isMultiPartRequest = this.isMultiPartRequest(request);
		
		String redirect = (isMultiPartRequest) ?
				this.getFormField(this.getItemList(request), ServletUtils.ATT_REDIRECT ) : ServletRequestUtils.getStringParameter(request,ServletUtils.ATT_REDIRECT );
		
	    if (redirect == null) {
	    	redirect = this.getReferer(request);
	    }
		
		return redirect;
	}
	
	@Override
    public String getReferer(HttpServletRequest request) throws Exception {
		boolean isMultiPartRequest = this.isMultiPartRequest(request);
		
		String referer = (isMultiPartRequest) ?
				this.getFormField(this.getItemList(request), ServletUtils.ATT_REFERRER ) : ServletRequestUtils.getStringParameter(request, ServletUtils.ATT_REFERRER); 

		if (referer == null) {
			HttpSession session = request.getSession(false);
			referer = ((CtiNavObject) session.getAttribute(ServletUtils.NAVIGATE)).getPreviousPage();
		}
		
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
	
	@SuppressWarnings("unchecked")
	public List<FileItem> getItemList(final HttpServletRequest request) {
		try {
			DiskFileUpload upload = new DiskFileUpload();
			List<FileItem> items = upload.parseRequest( request );
			return items;
		} catch (FileUploadException e) {
			CTILogger.error( e.getMessage(), e );
			HttpSession session = request.getSession(false);
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to parse the form data");
		}
		return Collections.emptyList();
	}
	
	public boolean isMultiPartRequest(final HttpServletRequest request) {
		boolean result = DiskFileUpload.isMultipartContent(request);
		return result;
	}
	
}
