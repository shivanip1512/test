package com.cannontech.database.db.user;

import java.sql.SQLException;
import java.util.Date;

import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.spring.YukonSpringHook;

public class YukonUser extends DBPersistent  {
    
    public static final String TABLE_NAME = "YukonUser";	
		
	private Integer userID = null;
	private String username = null;
	private LoginStatusEnum loginStatus = LoginStatusEnum.DISABLED;
    private AuthType authType = AuthType.PLAIN;
    private Date lastChangedDate = null;
    private boolean forceReset = false;
//    private Integer userGroupId = null;

	private static final String[] SELECT_COLUMNS = 
	{ 	
		"Username", 
		"Status",
        "AuthType",
		"LastChangedDate",
		"ForceReset"
//		"UserGroupId",
	};
	
	
    /**
     * @see com.cannontech.database.db.DBPersistent#add()
     */
    public void add() throws SQLException {
        // Because the addValues must include a value for every column, a blank value
        // for the password is included here even though the rest of this class ignores
        // that column.
        String dummyPasswordValue = "";
        Object[] addValues = { getUserID(), getUsername(), dummyPasswordValue, getLoginStatus().getDatabaseRepresentation(), 
                               getAuthType().name(), getLastChangedDate(), isForceReset()};

        add(TABLE_NAME, addValues);
    }

	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Integer
	 */
	public static final Integer getNextUserID( java.sql.Connection conn )
	{
        NextValueHelper nextValueHelper = YukonSpringHook.getNextValueHelper();
        int nextValue = nextValueHelper.getNextValue(TABLE_NAME);
        return nextValue;
	}
	
	
	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		delete(TABLE_NAME, "UserID", getUserID());
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		String[] constraintColumns = { "UserID" };
		Object[] constraintValues = { getUserID() };
		
		Object[] results = retrieve(SELECT_COLUMNS, TABLE_NAME, constraintColumns, constraintValues);
		
		if(results.length == SELECT_COLUMNS.length) {
			setUsername((String) results[0]);
			setLoginStatus(LoginStatusEnum.retrieveLoginStatus((String) results[1]));
			setAuthType(AuthType.valueOf((String) results[2]));
			setLastChangedDate((Date) results[3]);
			setForceReset("Y".equals((String) results[4]));
		}
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		Object[] setValues = { getUsername(), getLoginStatus().getDatabaseRepresentation(), 
		                       getAuthType().name(), getLastChangedDate(), isForceReset()};
		
		String[] constraintColumns = { "UserID" };
		Object[] constraintValues = { getUserID() };
		
		update(TABLE_NAME, SELECT_COLUMNS, setValues, constraintColumns, constraintValues);
	}

	public Integer getUserID() {
		return userID;
	}
	public void setUserID(Integer userID) {
	    this.userID = userID;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public LoginStatusEnum getLoginStatus() {
		return loginStatus;
	}
	public void setLoginStatus(LoginStatusEnum loginStatus) {
		this.loginStatus = loginStatus;
	}

    public AuthType getAuthType() {
        return authType;
    }
    public void setAuthType(AuthType authType) {
        this.authType = authType;
    }

    public Date getLastChangedDate() {
        return lastChangedDate;
    }
    public void setLastChangedDate(Date lastChangedDate) {
        this.lastChangedDate = lastChangedDate;
    }

    public boolean isForceReset() {
        return forceReset;
    }
    public void setForceReset(boolean forceReset) {
        this.forceReset = forceReset;
    }

//    public Integer getUserGroupId() {
//        return userGroupId;
//    }
//    public void setUserGroupId(Integer userGroupId) {
//        this.userGroupId = userGroupId;
//    }
}