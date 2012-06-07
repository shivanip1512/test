package com.cannontech.core.dao;


public interface DatabaseManagementDao {
	    /**
     * Returns the number of duplicates deleted. Will delete a maximum of 1000000 (1 million) rows.
     */
    public int deleteRphDuplicates();
    
	/**
	 * Returns the number of entries deleted. Will delete a maximum of 1000000 (1 million) RawPointHistory entries (those where RPH.PointId is not in Point).
	 */
    public int deleteRphDanglingEntries();
    
    /**
	 * Returns the number of entries deleted. Will delete a maximum of 1000000 (1 million) SystemLog entries (those where SystemLog.PointId is not in Point).
	 */
    public int deleteSystemLogDanglingEntries();
}
