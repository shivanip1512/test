package com.cannontech.yukon.api.loadManagement.mocks;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.api.loadManagement.adapters.RolePropertyDaoAdapter;

/**
 * Mock object used to simulate the RolePropertyDao
 */
public class MockRolePropertyDao extends RolePropertyDaoAdapter {

	private static final LiteYukonUser notAuthorizedUser = new LiteYukonUser(5, "notAuthUser", LoginStatusEnum.ENABLED);
	private static final LiteYukonUser authorizedUser = new LiteYukonUser(4, "authUser", LoginStatusEnum.ENABLED);
	
	/**
	 * Method to get a user that this mock role property dao will throw a NotAuthorizedException for
	 * @return Unauthorized user
	 */
	public static final LiteYukonUser getUnAuthorizedUser() {
		return MockRolePropertyDao.notAuthorizedUser;
	}

	public static final LiteYukonUser getAuthorizedUser() {
		return MockRolePropertyDao.authorizedUser;
	}
	
	@Override
	public void verifyProperty(YukonRoleProperty property, LiteYukonUser user)
			throws NotAuthorizedException {
		if (user == MockRolePropertyDao.notAuthorizedUser) {
			throw new NotAuthorizedException("Mock role property dao not authorized");
		} else if (user == MockRolePropertyDao.authorizedUser) {
			return;
		} else {	//allow every other user, too
			return;
		}
	}
}
