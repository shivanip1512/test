package com.cannontech.common.fileExportHistory.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.fileExportHistory.ExportHistoryEntry;
import com.cannontech.common.fileExportHistory.FileExportType;
import com.cannontech.common.fileExportHistory.dao.FileExportHistoryDao;
import com.cannontech.common.fileExportHistory.service.FileExportHistoryService;
import com.cannontech.common.util.CtiUtilities;
import com.google.common.io.Files;

public class FileExportHistoryServiceImpl implements FileExportHistoryService {
	@Autowired public FileExportHistoryDao fileExportHistoryDao;
	
	@Override
	public File getArchivedFile(int exportHistoryEntryId) throws FileNotFoundException {
		ExportHistoryEntry entry = fileExportHistoryDao.getEntry(exportHistoryEntryId);
		File archiveFile = new File(getArchivePath(), entry.getFileName());
		if(!archiveFile.exists()) {
			String error = "Archive file \"" + entry.getFileName() + "\" not found for entry with id " + entry.getId();
			throw new FileNotFoundException(error);
		}
		return archiveFile;
	}

	@Override
	public ExportHistoryEntry addHistoryEntry(File archiveFile, File exportFile, String fileName, FileExportType type, String initiator) 
	            throws FileNotFoundException, IOException {
	    if (!archiveFile.exists()) {
	        throw new FileNotFoundException("Unable to locate archive file: " + archiveFile.getPath());
	    }
	    String filePathWithoutFileName = null;
	    if (exportFile != null) {
	        String fullFilePath = exportFile.getCanonicalPath();
	        filePathWithoutFileName = fullFilePath.substring(0, fullFilePath.lastIndexOf(File.separator));
	    }
        
        int entryId = fileExportHistoryDao.insertEntry(fileName, archiveFile.getName(), type, initiator, filePathWithoutFileName);
        return fileExportHistoryDao.getEntry(entryId);
	    
	}

	private File getArchivePath() {
		return new File(CtiUtilities.getArchiveDirPath());
	}
}
