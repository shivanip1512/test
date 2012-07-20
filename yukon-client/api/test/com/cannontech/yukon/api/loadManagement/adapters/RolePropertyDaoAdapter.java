package com.cannontech.yukon.api.loadManagement.adapters;

import java.util.Set;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.roleproperties.UserNotInRoleException;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleCategory;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.ImmutableSet;

public class RolePropertyDaoAdapter implements RolePropertyDao {

	@Override
	public boolean checkAllProperties(LiteYukonUser user, YukonRoleProperty firstProperty, YukonRoleProperty... otherProperties) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
    public boolean checkAllProperties(LiteYukonUser user, Iterable<YukonRoleProperty> properties) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
	public boolean checkAnyProperties(LiteYukonUser user, YukonRoleProperty firstProperty, YukonRoleProperty... otherProperties) {
		throw new UnsupportedOperationException("not implemented");
	}

    @Override
    public boolean checkAnyProperties(LiteYukonUser user, Iterable<YukonRoleProperty> properties) {
        throw new UnsupportedOperationException("not implemented");
    }

	@Override
	public boolean checkCategory(YukonRoleCategory category, LiteYukonUser user) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean checkFalseProperty(YukonRoleProperty property, LiteYukonUser user) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean checkProperty(YukonRoleProperty property, LiteYukonUser user) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean checkRole(YukonRole role, LiteYukonUser user) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean getPropertyBooleanValue(YukonRoleProperty property, LiteYukonUser user) throws UserNotInRoleException {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public <E extends Enum<E>> E getPropertyEnumValue(YukonRoleProperty property, Class<E> enumClass, LiteYukonUser user)
    throws UserNotInRoleException {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public double getPropertyDoubleValue(YukonRoleProperty property, LiteYukonUser user) throws UserNotInRoleException {
	    throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public float getPropertyFloatValue(YukonRoleProperty property, LiteYukonUser user) throws UserNotInRoleException {
	    throw new UnsupportedOperationException("not implemented");
	}
	
	@Override
	public int getPropertyIntegerValue(YukonRoleProperty property, LiteYukonUser user) throws UserNotInRoleException {
	    throw new UnsupportedOperationException("not implemented");
	}
	
	@Override
	public long getPropertyLongValue(YukonRoleProperty property, LiteYukonUser user) throws UserNotInRoleException {
	    throw new UnsupportedOperationException("not implemented");
	}
	
	@Override
	public String getPropertyStringValue(YukonRoleProperty property, LiteYukonUser user) throws UserNotInRoleException {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Set<YukonRoleCategory> getRoleCategoriesForUser(LiteYukonUser user) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean isCheckPropertyCompatible(YukonRoleProperty property) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void verifyAnyProperties(LiteYukonUser user, YukonRoleProperty firstProperty, YukonRoleProperty... otherProperties) 
    throws NotAuthorizedException {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void verifyCategory(YukonRoleCategory category, LiteYukonUser user) throws NotAuthorizedException {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void verifyFalseProperty(YukonRoleProperty property, LiteYukonUser user) throws NotAuthorizedException {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void verifyProperty(YukonRoleProperty property, LiteYukonUser user) throws NotAuthorizedException {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void verifyRole(YukonRole role, LiteYukonUser user) throws NotAuthorizedException {
		throw new UnsupportedOperationException("not implemented");
	}

    @Override
    public boolean isMissingProperty(YukonRoleProperty roleProperty) throws NotAuthorizedException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public ImmutableSet<YukonRoleProperty> getMissingProperties() throws NotAuthorizedException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public String getPropertyStringValue(LiteYukonGroup liteYukonGroup, YukonRoleProperty property) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public int getPropertyIntegerValue(LiteYukonGroup liteYukonGroup, YukonRoleProperty property) {
    	throw new UnsupportedOperationException("not implemented");
    }

	@Override
	public boolean getPropertyBooleanValue(LiteYukonGroup liteYukonGroup, YukonRoleProperty property) {
		throw new UnsupportedOperationException("not implemented");
	}

    @Override
    public boolean checkRoleForRoleGroupId(YukonRole role, int roleGroupId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Number getPropertyNumberValue(LiteYukonGroup roleGroup, YukonRoleProperty property) {
        throw new UnsupportedOperationException("not implemented");
    }
}