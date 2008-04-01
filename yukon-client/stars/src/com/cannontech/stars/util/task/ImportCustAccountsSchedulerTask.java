package com.cannontech.stars.util.task;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.jobs.support.YukonTask;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.user.YukonUserContext;
import com.sun.corba.se.impl.orbutil.threadpool.TimeoutException;

public class ImportCustAccountsSchedulerTask implements YukonTask {

    private Logger logger = YukonLogManager.getLogger(ImportCustAccountsSchedulerTask.class);

    // Injected variables
    private Integer energyCompanyID = null;
    private String email = null;
    private YukonUserContext userContext = null;
    
    public void start() {
            try {
                startTask();
            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }
    
    private void startTask() throws TimeoutException, InterruptedException{
        logger.info("Starting cust account task.");
        int timeoutCounterMax = 15*60*60; // 15 mins
        int timeoutCounter = 0;
        while(true){
            if (StarsDatabaseCache.allCacheLoaded.get() == true) {
                break;
            }
            Thread.sleep(1000);
            if (timeoutCounterMax < timeoutCounter) {
                throw new TimeoutException();
            }
            timeoutCounter++;
        }
        
        LiteStarsEnergyCompany liteStarsEnergyCompany = StarsDatabaseCache.getInstance().getEnergyCompany(energyCompanyID);
        
        final String fs = System.getProperty( "file.separator" );
        File baseDir = ImportCustAccountsTask.getBaseDir(liteStarsEnergyCompany);
        File importInputDir = null;

        if (baseDir != null) {
            importInputDir = new File(baseDir, fs+ServerUtils.IMPORT_INPUT_DIR);
        }
        
        String[] fileList = null;
        if (importInputDir != null && importInputDir.exists()) {
            if (importInputDir.isDirectory()) {
                FilenameFilter csvFilter = new FilenameFilter(){
                    public boolean accept(File dir, String name){
                        return name.endsWith(".csv");
                    }
                };
                fileList = importInputDir.list(csvFilter);
            }
        }
        
        // This takes care of ordering the files if there are more than one found
        // in the importInput directory
        Comparator<String> fileNameComparator = new Comparator<String>(){

            @Override
            public int compare(String s1, String s2) {
                if ( s1.length() > 10){
                    // Gets the date portion of the file name (MMDDYY)
                    String tempKey1 = s1.substring(s1.length()-10, s1.length()-4);
                    
                    // Changes the key to a (YYMMDD) format
                    String sortingKey1 = tempKey1.substring(4,6)+
                                         tempKey1.substring(0,2)+
                                         tempKey1.substring(2,4);
                    if (s2.length() > 10){
                        // Gets the date portion of the file name (MMDDYY)
                        String tempKey2 = s2.substring(s2.length()-10, s2.length()-4);
                        
                        // Changes the key to a (YYMMDD) format
                        String sortingKey2 = tempKey2.substring(4,6)+
                                             tempKey2.substring(0,2)+
                                             tempKey2.substring(2,4);
                        return sortingKey1.compareTo(sortingKey2);
                    } else {
                        return 1;
                    }
                } else {
                    if (s2.length() > 10){
                        return -1;
                    } else {
                        return s1.compareTo(s2);
                    }
                }
            }
            
        };

        SortedSet<String> sortedFileList = new TreeSet<String>(fileNameComparator);
        for (String fileName : fileList) {
            sortedFileList.add(fileName);
        }
        
        // Checks to see if any files where found in the wanted directory
        if(sortedFileList != null) {
            for (String fileName : sortedFileList) {
                File custFile = new File(importInputDir, fs + fileName);
                int userId = userContext.getYukonUser().getLiteID();
                ImportCustAccountsTask importCustAccountsTask = new ImportCustAccountsTask(liteStarsEnergyCompany, custFile, null, email, false, userId, true);
                importCustAccountsTask.run();
                
                // This while loop forces the tread to finish in order to start another thread.
                timeoutCounter = 0;
                while (true) {
                    if (importCustAccountsTask.getStatus() != ImportCustAccountsTask.STATUS_RUNNING) {
                        break;
                    }
                    Thread.sleep(1000);
                    timeoutCounter++;
                    if (timeoutCounterMax < timeoutCounter) {
                        throw new TimeoutException();
                    }
                }
            }
        }
    }

    public void stop() throws UnsupportedOperationException {
        // TODO Auto-generated method stub
    }

    // Setters for injected parameters
    public Integer getEnergyCompany() {
        return energyCompanyID;
    }

    public void setEnergyCompany(Integer energyCompanyID) {
        this.energyCompanyID = energyCompanyID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public void setUserContext(YukonUserContext userContext) {
        this.userContext = userContext;
    }

}
