/*
 * Created on Apr 15, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

import java.io.File;
import java.io.IOException;

import com.cannontech.stars.util.ServerUtils;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CPSImportRegulator {
	
	private static final String OUTPUT_FILE = "cps_import.csv";
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println( "Usage: java " + CPSImportRegulator.class.getName() + " input_file [output_file]" );
			return;
		}
		
		File inputFile = new File( args[0] );
		String[] lines = ServerUtils.readFile( inputFile, false );
		if (lines == null) {
			System.out.println( "Failed to read input file '" + args[0] + "'" );
			return;
		}
		
		File outputFile = (args.length > 1)? new File(args[1]) : new File(inputFile.getParent(), OUTPUT_FILE);
		String[] output = new String[ lines.length ];
		
		output[0] = "COLUMN_NAMES:ACCOUNT_NO,LAST_NAME,FIRST_NAME,HOME_PHONE,WORK_PHONE,EMAIL,STREET_ADDR1,STREET_ADDR2,CITY,STATE,ZIP_CODE,MAP_NO,SERIAL_NO,INSTALL_DATE,REMOVE_DATE,USERNAME,PASSWORD,ACTION,DEVICE_TYPE,PROGRAM_NAME";
		
		try {
			// Remove the action field, then add the device type and program name fields
			for (int i = 1; i < lines.length; i++)
				output[i] = lines[i].substring(0, lines[i].length() - 6) + ",ExpressStat,San Antonio (CPS) Residential Cooling";
			
			ServerUtils.writeFile( outputFile, output );
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
