package com.cannontech.capcontrol.dao;

import java.util.List;

import com.cannontech.database.db.capcontrol.DmvTest;

public interface DmvTestDao {

    int createDmvTest(DmvTest dmvTest);

    boolean isUniqueDmvTestName(String name);

    List<DmvTest> getAllDmvTest();

    DmvTest getDmvTestById(int id);

}
