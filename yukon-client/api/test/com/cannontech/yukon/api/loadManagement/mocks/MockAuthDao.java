package com.cannontech.yukon.api.loadManagement.mocks;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.api.loadManagement.adapters.AuthDaoAdapter;

/**
 * Mock object used to simulate the AuthDao
 */
public class MockAuthDao extends AuthDaoAdapter {

	private static LiteYukonUser user = new LiteYukonUser();
	
	/**
	 * Method to get a user that this mock auth dao will throw a NotAuthorizedException for
	 * @return Unauthorized user
	 */
	public static final LiteYukonUser getUnAuthorizedUser() {
		return MockAuthDao.user;
	}
	
	@Override
	public void verifyTrueProperty(LiteYukonUser user, int... rolePropertyIds)
			throws NotAuthorizedException {
		if (user == MockAuthDao.user) {
			throw new NotAuthorizedException("Mock auth dao not authorized");
		}
	}
}
