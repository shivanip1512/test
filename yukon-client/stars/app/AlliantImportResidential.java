import java.io.File;
import java.io.IOException;

import com.cannontech.stars.util.ServerUtils;

/*
 * Created on May 24, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class AlliantImportResidential {
	
	private static final String OUTPUT_FILE = "alliant_import_res.csv";
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println( "Usage: java " + AlliantImportResidential.class.getName() + " input_file [output_file]" );
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
		
		output[0] = "COLUMN_NAMES:LAST_NAME,STREET_ADDR1,CITY,STATE,ZIP_CODE,HOME_PHONE,ACCOUNT_NO,MAP_NO,SERIAL_NO,DEVICE_TYPE,PROGRAM_NAME,ADDR_GROUP,USERNAME,PASSWORD";
		
		try {
			for (int i = 1; i < lines.length; i++) {
				String[] columns = ServerUtils.splitString( lines[i], "," );;
				output[i] = lines[i] + "," +
						"ExpressStat," +
						"AE Residential StatSaver," +
						"AE Residential " + columns[2] + "," +
						columns[8] + "," + columns[8];	// username = password = serial #
			}
			
			ServerUtils.writeFile( outputFile, output );
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
