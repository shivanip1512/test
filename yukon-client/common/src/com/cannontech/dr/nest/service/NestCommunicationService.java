package com.cannontech.dr.nest.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.cannontech.dr.nest.model.CriticalEvent;
import com.cannontech.dr.nest.model.NestControlHistory;
import com.cannontech.dr.nest.model.NestError;
import com.cannontech.dr.nest.model.NestException;
import com.cannontech.dr.nest.model.NestExisting;
import com.cannontech.dr.nest.model.NestUploadInfo;
import com.cannontech.dr.nest.model.StandardEvent;

public interface NestCommunicationService{

    /**
     * Uploads existing file
     * 
     * @param existing - file rows
     */
    NestUploadInfo uploadExisting(List<NestExisting> existing);

    /**
     * Sends standard event to Nest
     * 
     * @param event to send to Nest
     * @return optional error we got from Nest
     */
    Optional<NestError> sendStandardEvent(StandardEvent event);

    /**
     * Sends critical event to Nest
     * 
     * @param event to send to Nest
     * @return optional error we got from Nest
     */
    Optional<NestError> sendCriticalEvent(CriticalEvent event);

    /**
     * Attempts to cancel event with Nest
     * 
     * @param history contains the information needed to cancel event
     * @return true if success
     */
    boolean cancelEvent(NestControlHistory history);

    /**
     * Downloads Nest existing file
     * 
     * @return file data
     */
    List<NestExisting> downloadExisting();
    
    /**
     * Parses file. Used by simulator.
     * 
     * @return file data
     */
    List<NestExisting> parseExistingCsvFile(InputStream inputStream);
      
    public static final SimpleDateFormat FILE_NAME_DATE_FORMATTER = new SimpleDateFormat("YYYYMMddHHmm");

    static File createFile(String path, String name) {
        String fileName = FILE_NAME_DATE_FORMATTER.format(new Date()) + "_" + name + ".csv";
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(path, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new NestException("Failed to create file:" + fileName, e);
            }
        }
        return file;
    }
    
    static Date parseDateFromFileName(String date) {
        try {
            return FILE_NAME_DATE_FORMATTER.parse(date);
        } catch (ParseException e) {
            throw new NestException("Unable to parse date:" + date, e);
        }
    }

    /**
     * Sends event to Nest and gets a response. This methods is used by simulator as we want to display the
     * not formated response we get from Nest.
     */
    String getNestResponse(String url, CriticalEvent event);
}
