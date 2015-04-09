package com.cannontech.capcontrol.export;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.cannontech.user.YukonUserContext;

public interface RegulatorPointMappingExportService {

    /**
     * Generates a CSV file which contains a list of regulator point mappings.
     * Each regulator id passed in generates a complete list of its associated point mappings/regulator attributes.
     * 
     * @param filename The partial filename of the generated file. It will have a timestamp and .csv extension appended.
     * @param regulatorIds The list of regulator ids to be included in the file
     * @param userContext The user context used when creating the file timestamp.
     * @return The File object containing the CSV data.
     * @throws IOException thrown if there is a problem creating the File. 
     * stored 
     */
    File generateCsv(String filename, List<Integer> regulatorIds, YukonUserContext userContext) throws IOException;

}
