package com.cannontech.stars.util.task;

import java.io.File;
import java.util.SortedSet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.jobs.support.YukonTaskBase;

public class ImportCustAccountsSchedulerTask extends YukonTaskBase {

    private Logger logger = YukonLogManager.getLogger(ImportCustAccountsSchedulerTask.class);

    // Injected variables
    private Integer energyCompanyId = null;
    private String email = null;

    // Injected Dependencies
    private StarsDatabaseCache starsDbCacheInstance;
    
    @Override
    public void start() {
            try {
                startTask();
            } catch (NotFoundException e){
                logger.error(e);
            }
    }
    
    private void startTask() {
        logger.info("Starting cust account task.");
        
        LiteStarsEnergyCompany liteStarsEnergyCompany = null;
        if(energyCompanyId != null){
            liteStarsEnergyCompany = starsDbCacheInstance.getEnergyCompany(energyCompanyId);
        } else {
            throw new NotFoundException("energyCompanyID was not found during the import customer process");
        }
            
        // Checks to see if any files where found in the wanted directory
        final String fs = System.getProperty( "file.separator" );
        File baseDir = ImportCustAccountsTask.getBaseDir(energyCompanyId);
        File importDirectory = ImportCustAccountsTask.getImportDirectory(baseDir);
        SortedSet<String> automationImportFileList = ImportCustAccountsTask.getAutomationImportFileList(baseDir, importDirectory);
        
        if (automationImportFileList != null) {
            for (String fileName : automationImportFileList) {
                File custFile = new File(importDirectory, fs + fileName);
                int userId = getYukonUser().getLiteID();
                ImportCustAccountsTask importCustAccountsTask = new ImportCustAccountsTask(liteStarsEnergyCompany, custFile, null, email, false, userId, true);
                importCustAccountsTask.run();
            }
        }
    }

    // Setters for injected parameters
    public Integer getEnergyCompany() {
        return energyCompanyId;
    }

    public void setEnergyCompany(Integer energyCompanyID) {
        this.energyCompanyId = energyCompanyID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Autowired
    public void setStarsDbCacheInstance(StarsDatabaseCache starsDbCacheInstance) {
        this.starsDbCacheInstance = starsDbCacheInstance;
    }

}
