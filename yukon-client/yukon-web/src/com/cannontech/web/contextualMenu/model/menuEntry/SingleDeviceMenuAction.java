package com.cannontech.web.contextualMenu.model.menuEntry;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.contextualMenu.CollectionCategory;

public class SingleDeviceMenuAction extends DeviceMenuAction {

    public SingleDeviceMenuAction(String baseUrl) {
        this(baseUrl, defaultDeviceIdParamName, defaultDeviceIdParamName, null);
    }

    public SingleDeviceMenuAction(String baseUrl, Boolean appendParamName) {
        this(baseUrl, appendParamName, defaultDeviceIdParamName, defaultDeviceIdParamName, null);
    }

    public SingleDeviceMenuAction(String baseUrl, YukonRole requiredRole) {
        this(baseUrl, defaultDeviceIdParamName, defaultDeviceIdParamName, requiredRole);
    }

    public SingleDeviceMenuAction(String baseUrl, YukonRoleProperty... requiredRoleProperties) {
        this(baseUrl, defaultDeviceIdParamName, defaultDeviceIdParamName, null, requiredRoleProperties);
    }

    public SingleDeviceMenuAction(String baseUrl, YukonRole requiredRole,
            YukonRoleProperty... requiredRoleProperties) {
        this(baseUrl, defaultDeviceIdParamName, defaultDeviceIdParamName, requiredRole, requiredRoleProperties);
    }

    public SingleDeviceMenuAction(String baseUrl, String inputParamName,
            YukonRole requiredRole,
            YukonRoleProperty... requiredRoleProperties) {
        this(baseUrl, inputParamName, inputParamName, requiredRole, requiredRoleProperties);
    }
    
    public SingleDeviceMenuAction(String baseUrl, String inputParamName, String outputParamName,
            YukonRole requiredRole,
            YukonRoleProperty... requiredRoleProperties) {
        this(baseUrl, true, inputParamName, outputParamName, requiredRole, requiredRoleProperties);
    }

    public SingleDeviceMenuAction(String baseUrl, Boolean appendParamName, String inputParamName, String outputParamName,
            YukonRole requiredRole,
            YukonRoleProperty... requiredRoleProperties) {
        super(baseUrl, appendParamName, inputParamName, outputParamName, requiredRole, requiredRoleProperties);
    }

    /*
     * This method only supports inputs containing the deviceId that needs to be turned into a single-device-specific url
     * ("deviceId={deviceId}")
     */
    @Override
    public String getUrl(CollectionCategory collectionCategory, HttpServletRequest req) {
        if (collectionCategory == CollectionCategory.PAO_ID) {

            String paramValue = ServletUtil.getParameter(req, inputParamName);
            String encodedParamValue = ServletUtil.urlEncode(paramValue);
            String url;
            if (appendParamName) {
                url = baseUrl + outputParamName + "=" + encodedParamValue;
            } else {
                url = baseUrl + "/" + encodedParamValue;
            }
            String safeUrl = ServletUtil.createSafeUrl(req, url);

            return safeUrl;
        }
        throw new RuntimeException("this url doesn't support " + collectionCategory);
    }

}
