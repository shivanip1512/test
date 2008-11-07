package com.cannontech.core.dao;

import org.springframework.dao.DataAccessException;

import java.util.List;

import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface YukonUserDao {

    public void changeUsername(int userId, String username);

    public LiteYukonUser getLiteYukonUser(int userId);

    public LiteYukonUser getLiteYukonUser(String userName);

    public LiteContact getLiteContact(int userId);

    public void addLiteYukonUserWithPassword(LiteYukonUser user, String password, LiteEnergyCompany energyCompany, List<LiteYukonGroup> groups) throws DataAccessException;

    public void deleteUser(Integer userId);
}