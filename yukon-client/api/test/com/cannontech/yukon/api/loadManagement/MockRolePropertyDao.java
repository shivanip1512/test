package com.cannontech.yukon.api.loadManagement;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Mock object used to simulate the RolePropertyDao
 */
public class MockRolePropertyDao extends RolePropertyDaoAdapter {

	private static String UNAUTHORIZED_STATUS = "UNAUTHORIZED";
	
	/**
	 * Method to get a user that this mock role property dao will throw a NotAuthorizedException for
	 * @return Unauthorized user
	 */
	public static final LiteYukonUser getUnAuthorizedUser() {
		LiteYukonUser user = new LiteYukonUser();
		user.setStatus(UNAUTHORIZED_STATUS);
		
		return user;
	}
	
	@Override
	public void verifyProperty(YukonRoleProperty property, LiteYukonUser user)
			throws NotAuthorizedException {
		if (UNAUTHORIZED_STATUS.equals(user.getStatus())) {
			throw new NotAuthorizedException("Mock role property dao not authorized");
		}
	}
}
