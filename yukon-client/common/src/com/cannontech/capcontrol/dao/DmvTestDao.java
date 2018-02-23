package com.cannontech.capcontrol.dao;

import java.util.List;

import com.cannontech.database.db.capcontrol.DmvTest;

public interface DmvTestDao {

    int updateDmvTest(DmvTest dmvTest);

    boolean isUniqueDmvTestName(String name, int id);

    List<DmvTest> getAllDmvTest();

    DmvTest getDmvTestById(int id);

    boolean delete(int id);

    /**
     * Returns list of DmvTest for given testNames
     */
    List<DmvTest> getDmvTestByTestNames(List<String> testNames);

}
