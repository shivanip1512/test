import java.io.File;
import java.io.IOException;

import com.cannontech.stars.util.StarsUtils;

/*
 * Created on May 26, 2004
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
public class ShakopeeImport {
	
	private static final String OUTPUT_FILE = "shakopee_import.csv";
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println( "Usage: java " + ShakopeeImport.class.getName() + " input_file [output_file]" );
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
		
		output[0] = "COLUMN_NAMES:ACCOUNT_NO,FIRST_NAME,LAST_NAME,STREET_ADDR1,CITY,STATE,ZIP_CODE,HOME_PHONE,SERIAL_NO,DEVICE_TYPE,PROGRAM_NAME";
		
		try {
			for (int i = 1; i < lines.length; i++) {
				String[] columns = StarsUtils.splitString( lines[i], "," );;
				output[i] = columns[1] + "," +
						columns[2] + "," +
						columns[3] + "," +
						columns[4] + "," +
						columns[5] + "," +
						columns[6] + "," +
						columns[7] + "," +
						columns[8] + "," +
						columns[1] + "," +
						"LCR-5000,Shakopee Res Cooling";
			}
			
			StarsUtils.writeFile( outputFile, output );
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
