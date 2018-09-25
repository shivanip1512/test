package com.cannontech.dr.nest.service;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.cannontech.dr.nest.model.CriticalEvent;
import com.cannontech.dr.nest.model.NestEventId;
import com.cannontech.dr.nest.model.NestException;
import com.cannontech.dr.nest.model.NestExisting;
import com.cannontech.dr.nest.model.StandardEvent;

public interface NestCommunicationService{

    void uploadExisting(List<NestExisting> existing, Date date);

    NestEventId createStandardEvent(StandardEvent event);

    NestEventId createCriticalEvent(CriticalEvent event);

    boolean cancelEvent(String nestEventId);

    List<NestExisting> downloadExisting();
      
    public static final SimpleDateFormat FILE_NAME_DATE_FORMATTER = new SimpleDateFormat("MM-dd-YYYY_hh-mm-ss");

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
}
