package com.cannontech.yukon.api.loadManagement.adapters;

import java.util.List;
import java.util.TimeZone;

import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonUser;

public class AuthDaoAdapter implements AuthDao {

	@Override
	public LiteYukonRole getRole(LiteYukonUser user, int roleID) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public LiteYukonRole getRole(int roleid) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public String getRolePropertyValue(int userID, int rolePropertyID) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List getRoles(String category) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public TimeZone getUserTimeZone(LiteYukonUser user)
			throws BadConfigurationException, IllegalArgumentException {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean hasExlusiveAccess(LiteYukonUser user, int paoID) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean hasPAOAccess(LiteYukonUser user) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isAdminUser(String username_) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isAdminUser(LiteYukonUser user) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public LiteYukonUser login(String username, String password) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean userHasAccessPAO(LiteYukonUser user, int paoID) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void verifyAdmin(LiteYukonUser user) throws NotAuthorizedException {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void verifyTrueProperty(LiteYukonUser user, int... rolePropertyIds)
			throws NotAuthorizedException {
		throw new UnsupportedOperationException("not implemented");
	}
	
	@Override
	public String getFirstNotificationPin(LiteContact contact) {
	    throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public LiteYukonUser voiceLogin(int contactid, String pin) {
		throw new UnsupportedOperationException("not implemented");
	}

}
