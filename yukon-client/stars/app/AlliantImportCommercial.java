/*
 * Created on Mar 29, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.cannontech.stars.util.StarsUtils;

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
		String[] lines = StarsUtils.readFile( inputFile, false );
		if (lines == null) {
			System.out.println( "Failed to read input file '" + args[0] + "'" );
			return;
		}
		
		File outputFile = (args.length > 1)? new File(args[1]) : new File(inputFile.getParent(), OUTPUT_FILE);
		ArrayList output = new ArrayList();
		
		output.add( "COLUMN_NAMES:LAST_NAME,STREET_ADDR1,CITY,STATE,ZIP_CODE,HOME_PHONE,ACCOUNT_NO,ACCOUNT_NOTES,DEVICE_TYPE,SERIAL_NO,PROGRAM_NAME,ADDR_GROUP,USERNAME,PASSWORD" );
		
		try {
			for (int i = 1; i < lines.length; i++) {
				String[] columns = StarsUtils.splitString( lines[i], "," );
				
				String notes = "";
				if (columns[8].length() > 0)
					notes += "Meter Number: " + columns[8] + "<br>";
				if (columns[9].length() > 0)
					notes += "Project: " + columns[9] + "<br>";
				if (columns[10].length() > 0)
					notes += "Contact: " + columns[10];
				
				String[] serialNos = columns[13].split(",");
				for (int j = 0; j < serialNos.length; j++) {
					String line = "\"" + columns[1] + "\"," +
							columns[2] + "," +
							columns[3] + "," +
							columns[4] + "," +
							columns[5] + "," +
							columns[6] + "," +
							columns[7] + "," +
							notes + "," +
							"Commercial ExpressStat," +
							serialNos[j] + "," +
							"AE Commercial StatSaver," +
							columns[17] + "," +
							columns[7] + "," +	// use account # as username
							columns[6];			// use phone # as password
					output.add( line );
				}
			}
			
			lines = new String[ output.size() ];
			output.toArray( lines );
			StarsUtils.writeFile( outputFile, lines );
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
