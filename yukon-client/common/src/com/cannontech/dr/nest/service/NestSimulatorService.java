package com.cannontech.dr.nest.service;

import java.io.InputStream;
import java.util.List;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.nest.model.NestUploadInfo;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;

public interface NestSimulatorService {

    /**
     * Generates a file that resembles Nest "existing" file and puts it in Yukon\ExportArchive\Nest\Simulator
     * dir.
     * 
     * @param groupNames - group names to use in the file (1 - 3), each account will be assigned to random
     *        group
     * @param rows - number of rows to put in the file
     * @param maxSerialNumbers - Maximum number of thermostats for the account
     *        Example: if the maxSerialNumbers = 3, up to 3 serials numbers will be generated
     * @param isWinterProgram if true, sets the PROGRAM to WINTER_RHR and put the generated thermostat serial
     *        numbers under WINTER_RHR otherwise uses SUMMER_RHR
     * @param user - user is needed to get the EC for which to get the X number of users to generate the rows
     * @param returns file name of the file created
     */
    String generateExistingFile(List<String> groupNames, int rows, int maxSerialNumbers, boolean isWinterProgram,
            LiteYukonUser user);
    
    /**
     * Save fileName in database.
     */
    void saveFileName(String fileName);

    /**
     * Gets the saved value from database for the passed key.
     */
    String getFileName(YukonSimulatorSettingsKey key);


    NestUploadInfo upload(InputStream inputStream);
    
    /**
     * Save nest version to use in database.
     */
    void saveNestVersion(Integer versionNumber);

    /**
     * Gets the saved value from database for the passed key.
     */
    Integer getNestVersion(YukonSimulatorSettingsKey key);


}
