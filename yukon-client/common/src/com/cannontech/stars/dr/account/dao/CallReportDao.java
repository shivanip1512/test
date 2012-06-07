package com.cannontech.stars.dr.account.dao;

import java.util.List;

import com.cannontech.stars.dr.account.model.CallReport;

public interface CallReportDao {
    
	public void insert(CallReport callReport, int energyCompanyId);
    public void update(CallReport callReport, int energyCompanyId);
    public void delete(int callId);
    
    public CallReport getCallReportByCallId(int callId);
    public List<CallReport> getAllCallReportByAccountId(int accountId);
    
    /**
     * Method to delete ALL callreports for an account
     * @param accountId
     */
    public void deleteAllCallsByAccount(int accountId);

    /**
     * Method to get call reports for an account
     * @param accountId
     * @return
     */
    public List<Integer> getAllCallIdsByAccount(int accountId);
    
    /**
     * Finds existing callId for a given callNumber and energyCompanyId.
     * Returns null if no call with given callNumber exists.
     * @param callNumber
     * @param energyCompanyId
     * @return
     */
    public Integer findCallIdByCallNumber(String callNumber, int energyCompanyId);

}
