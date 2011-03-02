package com.cannontech.yukon.api.loadManagement.mocks;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.api.loadManagement.adapters.RolePropertyDaoAdapter;

/**
 * Mock object used to simulate the RolePropertyDao
 */
public class MockRolePropertyDao extends RolePropertyDaoAdapter {

	private static LiteYukonUser user = new LiteYukonUser();
	
	/**
	 * Method to get a user that this mock role property dao will throw a NotAuthorizedException for
	 * @return Unauthorized user
	 */
	public static final LiteYukonUser getUnAuthorizedUser() {
		return MockRolePropertyDao.user;
	}
	
	@Override
	public void verifyProperty(YukonRoleProperty property, LiteYukonUser user)
			throws NotAuthorizedException {
		if (user == MockRolePropertyDao.user) {
			throw new NotAuthorizedException("Mock role property dao not authorized");
		}
	}
}
