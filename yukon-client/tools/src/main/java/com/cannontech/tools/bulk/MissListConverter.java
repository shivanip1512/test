package com.cannontech.tools.bulk;

import java.io.File;
import java.io.FileNotFoundException;

import javax.naming.InvalidNameException;

import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.ApplicationId;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.tools.bulk.service.MissListConverterService;
import com.cannontech.tools.bulk.service.impl.MissListConverterServiceImpl;

public class MissListConverter {

	public static void main(String[] args) {
                CtiUtilities.setClientAppName(ApplicationId.MISSED_LIST_CONVERTER);

		ClientSession session = ClientSession.getInstance();
		if (!session.establishSession()) {
			System.exit(-1);
		}

		if (session == null)
			System.exit(-1);

		if (args.length >= 2 && args.length <= 4) {
			MissListConverterService missListConverterService = new MissListConverterServiceImpl();

			File missListFileName = getMissListFileName(args[0]);
			File bulkImporterFile = getBulkImporterFile(args[1]);
			
			String routeName = null;
			boolean importFlag = false;
			if (args.length > 2) {
				routeName = args[2];
				if (args.length > 3) {
					if (args[3].equalsIgnoreCase("Import")) {
						importFlag = true;
					} else {
						System.out.println("To automatically import into the bulk importer please use the \"Import\" command as shown." );
						printUsage();
						System.out.println("\n --  Process Canceled --");
						System.exit(-1);
					}
				}
			}

			missListConverterService.convert(missListFileName, bulkImporterFile, routeName, importFlag);
			
			if (args.length > 3){
				System.out.println("\n The generated file has been passed to the bulk importer for processing.");
			} else {
				System.out.println("\n -- Process Complete --");
			}
			System.exit(0);
		}
		printUsage();
		
		System.exit(-1);
	}

	private static void printUsage(){
		System.out.println("\nUsage: missListConverter.bat $missListFileName $bulkImporterFile $routeName(optional) Import(optional) \n");
		System.out.println("Ex1: missListConverter.bat \"C:\\TestMissList.txt\" \"C:\\NewBulkFileWithRoutes.csv\"");
		System.out.println("Ex2: missListConverter.bat \"C:\\TestMissList.txt\" \"C:\\NewBulkFileWithRoutes.csv\" tempRoute");
		System.out.println("Ex3: missListConverter.bat \"C:\\TestMissList.txt\" \"C:\\NewBulkFileWithRoutes.csv\" tempRoute Import");
	}
	
	// Validation Methods
	/**
	 * 
	 * @param filePath
	 * @return
	 * @throws InvalidNameException
	 * @throws FileNotFoundException
	 */
	private static File getMissListFileName(String filePath) {
		if (!filePath.endsWith(".txt")) {
			System.out.println("The file you have supplied (" + filePath + ") is not a valid format.  " +
							   "Please supply a file with csv file format. \n");
			System.exit(-1);
		}
		File file = new File(filePath);
		if (!file.exists() || !file.isFile()) {
			System.out.println("The file you have supplied (" + filePath + ") does not exist.  " +
							   "Please supply an existing txt file. \n");
			System.exit(-1);
		}
		return file;
	}

	/**
	 * 
	 * @param filePath
	 * @return
	 * @throws InvalidNameException
	 */
	private static File getBulkImporterFile(String filePath) {

		if (!filePath.endsWith(".csv")) {
			System.out.println("The file you have supplied ("+ filePath +") is not a valid format.  " +
					           "Please supply a file with csv file format. \n");
			System.exit(-1);
		}
		File file = new File(filePath);
		return file;
	}
}