/*
 * Created on Apr 15, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

import java.io.File;
import java.io.IOException;

import com.cannontech.stars.util.StarsUtils;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CPSImport {
	
	private static final String OUTPUT_FILE = "cps_import.csv";
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println( "Usage: java " + CPSImport.class.getName() + " input_file [output_file]" );
			return;
		}
		
		File inputFile = new File( args[0] );
		String[] lines = StarsUtils.readFile( inputFile, false );
		if (lines == null) {
			System.out.println( "Failed to read input file '" + args[0] + "'" );
			return;
		}
		
		File outputFile = (args.length > 1)? new File(args[1]) : new File(inputFile.getParent(), OUTPUT_FILE);
		
		String[] output = new String[ lines.length ];
		output[0] = "COLUMN_NAMES:ACCOUNT_NO,LAST_NAME,FIRST_NAME,HOME_PHONE,WORK_PHONE,EMAIL,STREET_ADDR1,STREET_ADDR2,CITY,STATE,ZIP_CODE,MAP_NO,SERIAL_NO,INSTALL_DATE,REMOVE_DATE,USERNAME,PASSWORD,ACTION,DEVICE_TYPE,PROGRAM_NAME";
		
		try {
			for (int i = 1; i < lines.length; i++) {
				output[i] = "";
				String[] columns = StarsUtils.splitString( lines[i], "," );
				
				// Clear the invalid columns (columns with number in scientific format)
				for (int j = 0; j < 17; j++) {
					if (columns[j].indexOf(',') >= 0)
						output[i] += "\"" + columns[j] + "\",";
					else if (columns[j].indexOf("E+") >= 0)
						output[i] += ",";
					else
						output[i] += columns[j] + ",";
				}
				
				// Leave the action field to non-default only if it is "REMOVE"
				if (columns[17].equalsIgnoreCase( "REMOVE" ))
					output[i] += "REMOVE";
				
				// Add device type and program name
				output[i] += ",ExpressStat,San Antonio (CPS) Residential Cooling";
			}
			
			StarsUtils.writeFile( outputFile, output );
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
