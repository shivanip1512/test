package com.cannontech.web.scheduledFileExport;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.scheduledFileExport.ScheduledFileExportData;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class ScheduledFileExportHelper {
    @Autowired private GlobalSettingDao globalSettingDao;

    /**
     * Used to create the File Extension choice list in the Schedule Parameters section of the schedule export 
     * for Archive Data Export, Billing, Meter Events, and Water Leak reports. 
     * @return A list of strings used to create the File Extension drop-down list.
     */
    public List<String> setupFileExtChoices(ScheduledFileExportData exportData) {
        List<String> fileExtChoices = null;
        String globalFileExtensions = globalSettingDao.getString(GlobalSettingType.SCHEDULE_PARAMETERS_AVAILABLE_FILE_EXTENSIONS);
        fileExtChoices = com.cannontech.common.util.StringUtils.parseStringsForList(globalFileExtensions, ",");
        if (exportData.getExportFileExtension() == null) {
            // set the default value, initial selection
            exportData.setExportFileExtension(fileExtChoices.get(0));
        }
        return fileExtChoices;
    }
    
    /**
     * Used to create the Export Path choice list in the Schedule Parameters section of the schedule export 
     * for Archive Data Export, Billing, Meter Events, and Water Leak reports. 
     * @return A list of strings used to create the Export Path drop-down list.
     */
    public List<String> setupExportPathChoices(ScheduledFileExportData exportData) {
        List<String> exportPathChoices = null;
        String curExportPath = exportData.getExportPath(); 
        String globalExportPaths = globalSettingDao.getString(GlobalSettingType.SCHEDULE_PARAMETERS_EXPORT_PATH);
        exportPathChoices = com.cannontech.common.util.StringUtils.parseStringsForList(globalExportPaths, ",");
        if (curExportPath == null) {
            exportData.setExportPath(exportPathChoices.get(0));
        } else {
            exportData.setExportPath(curExportPath);
        }
        return exportPathChoices;
    }

}
