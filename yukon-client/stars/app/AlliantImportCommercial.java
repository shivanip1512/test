/*
 * Created on Mar 29, 2004
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
public class AlliantImportCommercial {
	
	private static final String OUTPUT_FILE = "alliant_import_com.csv";
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println( "Usage: java " + AlliantImportCommercial.class.getName() + " input_file [output_file]" );
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
		
		output[0] = "COLUMN_NAMES:LAST_NAME,STREET_ADDR1,CITY,STATE,ZIP_CODE,HOME_PHONE,ACCOUNT_NO,METER_NO,PROJECT,ADDR_GROUP,PROGRAM_NAME,DEVICE_TYPE,SERIAL_NO,USERNAME,PASSWORD";
		
		try {
			for (int i = 1; i < lines.length; i++) {
				String[] columns = ServerUtils.splitString( lines[i], "," );;
				output[i] = lines[i] + "," +
						"AE Commercial StatSaver," +
						"ExpressStat," +
						String.valueOf(i) + "," +
						columns[6] + "," +	// use account # as username
						columns[5];			// use phone # as password
			}
			
			ServerUtils.writeFile( outputFile, output );
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
