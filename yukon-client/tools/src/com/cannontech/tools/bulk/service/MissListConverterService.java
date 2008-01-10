package com.cannontech.tools.bulk.service;

import java.io.File;

public interface MissListConverterService {

	public void convert(File missListFile, File bulkImporterFile, String routeName, boolean importFlag);
	
}