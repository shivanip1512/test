package com.cannontech.database.db.user;

import java.sql.SQLException;
import java.util.Date;

import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.authentication.model.AuthenticationCategory;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.spring.YukonSpringHook;

public class YukonUser extends DBPersistent {
    public static final String TABLE_NAME = "YukonUser";

    private Integer userID = null;
    private String username = null;
    private LoginStatusEnum loginStatus = LoginStatusEnum.DISABLED;
    private boolean forceReset = false;
    private Integer userGroupId;

    String[] SELECT_COLUMNS = {
        "Username",
        "Status",
        "ForceReset",
        "UserGroupId",
    };

    /**
     * Because the addValues must include a value for every column, a blank value
     * for the password is included here even though the rest of this class ignores
     * that column. This method leaves the newly created user without an authentication
     * type set up. After calling it, one should necessarily call
     * {@link AuthenticationService#setAuthenticationCategory(LiteYukonUser, AuthenticationCategory)}
     * or {@link AuthenticationService#setPassword(LiteYukonUser, AuthenticationCategory, String)}
     * 
     * @see com.cannontech.database.db.DBPersistent#add()
     */
    @Override
    public void add() throws SQLException {
        String dummyPasswordValue = "";
        Object[] addValues =
            { userID, username, dummyPasswordValue, loginStatus.getDatabaseRepresentation(),
                AuthType.NONE.name(), new Date(), forceReset ? "Y" : "N", userGroupId };

        add(TABLE_NAME, addValues);
    }

    public final static Integer getNextUserID(java.sql.Connection conn)
    {
        NextValueHelper nextValueHelper = YukonSpringHook.getNextValueHelper();
        int nextValue = nextValueHelper.getNextValue(TABLE_NAME);
        return nextValue;
    }

    /**
     * @see com.cannontech.database.db.DBPersistent#delete()
     */
    @Override
    public void delete() throws SQLException {
        delete(TABLE_NAME, "UserID", getUserID());
    }

    /**
     * @see com.cannontech.database.db.DBPersistent#retrieve()
     */
    @Override
    public void retrieve() throws SQLException {
        String[] constraintColumns = { "UserID" };
        Object[] constraintValues = { getUserID() };

        Object[] results = retrieve(SELECT_COLUMNS, TABLE_NAME, constraintColumns, constraintValues);

        if (results.length == SELECT_COLUMNS.length) {
            username = (String) results[0];
            loginStatus = LoginStatusEnum.retrieveLoginStatus((String) results[1]);
            forceReset = "Y".equals(results[2]);
            userGroupId = ((Integer) results[3]);
        }
    }

    /**
     * @see com.cannontech.database.db.DBPersistent#update()
     */
    @Override
    public void update() throws SQLException {
        Object[] setValues = { getUsername(), getLoginStatus().getDatabaseRepresentation(),
            isForceReset() ? 'Y' : 'N', getUserGroupId() };

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

    public boolean isForceReset() {
        return forceReset;
    }

    public void setForceReset(boolean forceReset) {
        this.forceReset = forceReset;
    }

    public Integer getUserGroupId() {
        return userGroupId;
    }

    public void setUserGroupId(Integer userGroupId) {
        this.userGroupId = userGroupId;
    }
}