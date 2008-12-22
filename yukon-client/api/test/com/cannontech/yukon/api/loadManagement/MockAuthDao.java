package com.cannontech.yukon.api.loadManagement;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Mock object used to simulate the AuthDao
 */
public class MockAuthDao extends AuthDaoAdapter {

	private static String UNAUTHORIZED_STATUS = "UNAUTHORIZED";
	
	/**
	 * Method to get a user that this mock auth dao will throw a NotAuthorizedException for
	 * @return Unauthorized user
	 */
	public static final LiteYukonUser getUnAuthorizedUser() {
		LiteYukonUser user = new LiteYukonUser();
		user.setStatus(UNAUTHORIZED_STATUS);
		
		return user;
	}
	
	@Override
	public void verifyTrueProperty(LiteYukonUser user, int... rolePropertyIds)
			throws NotAuthorizedException {
		if (UNAUTHORIZED_STATUS.equals(user.getStatus())) {
			throw new NotAuthorizedException("Mock auth dao not authorized");
		}
	}
}
