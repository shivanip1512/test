import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.cannontech.stars.util.StarsUtils;

/*
 * Created on Aug 25, 2004
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
public class AlliantCustomerName {
	
	private static final String OUTPUT_FILE = "alliant_cus_name.csv";
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println( "Usage: java " + AlliantCustomerName.class.getName() + " input_file [output_file]" );
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
		
		output.add( "COLUMN_NAMES:ACTION,FIRST_NAME,LAST_NAME,STREET_ADDR1,CITY,STATE,ZIP_CODE,HOME_PHONE,ACCOUNT_NO,MAP_NO" );
		
		try {
			for (int i = 1; i < lines.length; i++) {
				String[] columns = StarsUtils.splitString( lines[i], "," );
				String lastName = columns[0];
				String firstName = "\"\"";
				
				ArrayList nameFields = new ArrayList();
				String[] fields = columns[0].split(" ");
				for (int j = 0; j < fields.length; j++) {
					if (fields[j].length() > 1) nameFields.add( fields[j] );
				}
				
				if (nameFields.size() == 2) {
					firstName = (String) nameFields.get(0);
					lastName = (String) nameFields.get(1);
				}
				
				String line = "UPDATE," +
						firstName + "," +
						lastName + "," +
						columns[1] + "," +
						columns[2] + "," +
						columns[3] + "," +
						columns[4] + "," +
						columns[5] + "," +
						columns[6] + "," +
						columns[7];
				output.add( line );
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
