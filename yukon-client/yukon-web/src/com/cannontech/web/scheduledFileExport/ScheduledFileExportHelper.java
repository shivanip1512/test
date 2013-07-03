package com.cannontech.web.scheduledFileExport;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.scheduledFileExport.ScheduledFileExportData;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class ScheduledFileExportHelper {
    @Autowired private GlobalSettingDao globalSettingDao;

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
