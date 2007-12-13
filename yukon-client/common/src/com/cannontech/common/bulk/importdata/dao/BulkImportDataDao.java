package com.cannontech.common.bulk.importdata.dao;

import java.util.List;

import com.cannontech.database.db.importer.ImportFail;
import com.cannontech.database.db.importer.ImportPendingComm;

public interface BulkImportDataDao {

    // GET FAILS, PENDING
    public abstract List<ImportFail> getAllDataFailures();
    public abstract List<ImportPendingComm> getAllPending();
    public abstract List<ImportFail> getAllCommunicationFailures();
    
    // DELETE FAILS, PENDING
    public abstract boolean deleteAllDataFailures();
    public abstract boolean deleteAllPending();
    public abstract boolean deleteAllCommunicationFailures();
    
    // LAST, NEXT IMPORT TIMES
    public abstract String getLastImportTime() throws Exception;
    public abstract String getNextImportTime() throws Exception;

}