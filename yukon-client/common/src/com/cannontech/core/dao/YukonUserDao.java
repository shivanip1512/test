package com.cannontech.core.dao;

import org.springframework.dao.DataAccessException;

import java.util.List;

import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface YukonUserDao {

    public void changeUsername(int userId, String username);

    public LiteYukonUser getLiteYukonUser(int userId);

    /**
     * Gets LiteYukonUser by username.
     * @param userName
     * @return the LiteYukonUser or null if userName doesn't exist
     */
    public LiteYukonUser getLiteYukonUser(String userName);
    
    /**
     * Gets the cached contact for the given user id.
     * @param userId
     * @return
     */
    public LiteContact getLiteContact(int userId);

    /**
     * Adds a LiteYukonUser to YukonUser with the given password, energy company, to the specified user groups.
     * @param user
     * @param password
     * @param energyCompanyId
     * @param groups
     * @throws DataAccessException
     */
    public void addLiteYukonUserWithPassword(LiteYukonUser user, String password, int energyCompanyId, List<LiteYukonGroup> groups) throws DataAccessException;

    /**
     * Deletes user from YukonUser by user id.
     * @param userId
     */
    public void deleteUser(Integer userId);

    /**
     * Updates the YukonUser for a give LiteYukonUser.
     * @param user
     * @param password
     * @throws DataAccessException
     */
    public void updateYukonUserWithPassword(LiteYukonUser user, String password) throws DataAccessException;

}