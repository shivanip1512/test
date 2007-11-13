package com.cannontech.core.authorization.service.impl;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.core.authorization.support.AuthorizationService;
import com.cannontech.core.authorization.support.CommandPermissionConverter;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * PaoCommandAuthorizationService implementation
 */
public class PaoCommandAuthorizationServiceImpl implements PaoCommandAuthorizationService {

    private AuthorizationService<LiteYukonPAObject> authorizationService = null;
    private CommandPermissionConverter converter = null;
    private PaoDao paoDao = null;
    
    public void setConverter(CommandPermissionConverter converter) {
        this.converter = converter;
    }

    public void setAuthorizationService(AuthorizationService<LiteYukonPAObject> authorizationService) {
        this.authorizationService = authorizationService;
    }
    
    public void setPaoDao(PaoDao paoDao) {
		this.paoDao = paoDao;
	}
    
    public boolean isAuthorized(LiteYukonUser user, String command, YukonDevice device) {
    	LiteYukonPAObject pao = paoDao.getLiteYukonPAO(device.getDeviceId());
    	return isAuthorized(user, command, pao);
    }

    public void verifyAuthorized(LiteYukonUser user, String command, YukonDevice device) throws PaoAuthorizationException {
    	LiteYukonPAObject pao = paoDao.getLiteYukonPAO(device.getDeviceId());
    	verifyAuthorized(user, command, pao);
    }
    	
    public boolean isAuthorized(LiteYukonUser user, String command, LiteYukonPAObject pao) {
        Permission permission = converter.getPermission(command);
        boolean authorized = authorizationService.isAuthorized(user, permission, pao);
        return authorized;
    }
    
    public void verifyAuthorized(LiteYukonUser user, String command, LiteYukonPAObject pao) throws PaoAuthorizationException {
        Permission permission = converter.getPermission(command);
        boolean authorized = authorizationService.isAuthorized(user, permission, pao);
        if (!authorized) {
            throw new PaoAuthorizationException("User " + user + " does not have " + permission 
                                                + " permission required to execute '" + command + "' on " + pao);
        }
    }
}
