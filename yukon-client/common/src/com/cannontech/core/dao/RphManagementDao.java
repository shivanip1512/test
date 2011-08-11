package com.cannontech.core.dao;


public interface RphManagementDao {
    /**
     * Returns the number of duplicates deleted. Will delete a maximum of 1000000 (1 million) rows.
     */
    public int deleteDuplicates();
}
