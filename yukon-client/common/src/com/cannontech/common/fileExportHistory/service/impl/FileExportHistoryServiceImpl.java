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
	public ExportHistoryEntry copyFile(File originalFile, FileExportType type, String initiator) throws FileNotFoundException, IOException{
		if(!originalFile.exists()) {
			throw new FileNotFoundException("Unable to copy file " + originalFile.getPath());
		}
		String originalName = originalFile.getName();
		
		//Create an archive copy in case the original is altered.
		//This file is named "original name + unique identifier"
		File archiveCopy = new File(getArchivePath(), originalName + "_" + CtiUtilities.getUuidString());
		Files.copy(originalFile, archiveCopy);
		
		String fullFilePath = originalFile.getCanonicalPath();
		String filePathWithoutFileName = fullFilePath.substring(0, fullFilePath.lastIndexOf(File.separator));
		
		int entryId = fileExportHistoryDao.insertEntry(originalName, archiveCopy.getName(), type, initiator, filePathWithoutFileName);
		return fileExportHistoryDao.getEntry(entryId);
	}
	
	private File getArchivePath() {
		String base = CtiUtilities.getYukonBase();
		return new File(base + File.separator + "ExportArchive");
	}
}
