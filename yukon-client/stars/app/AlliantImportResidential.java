import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
		ArrayList output = new ArrayList();
		
		output.add( "COLUMN_NAMES:LAST_NAME,STREET_ADDR1,CITY,STATE,ZIP_CODE,HOME_PHONE,ACCOUNT_NO,MAP_NO,SERIAL_NO,DEVICE_TYPE,PROGRAM_NAME,ADDR_GROUP,USERNAME,PASSWORD" );
		
		try {
			for (int i = 1; i < lines.length; i++) {
				String[] columns = ServerUtils.splitString( lines[i], "," );
				
				String groupName = "";
				if (columns[2].equalsIgnoreCase("Beaver Dam")
					|| columns[2].equalsIgnoreCase("Fall River")
					|| columns[2].equalsIgnoreCase("Randolph")
					|| columns[2].equalsIgnoreCase("Fox Lake")
					|| columns[2].equalsIgnoreCase("Horicon"))
				{
					groupName = "AE Residential Beaver Dam";
				}
				else if (columns[2].equalsIgnoreCase("Baraboo")
					|| columns[2].equalsIgnoreCase("Sauk City")
					|| columns[2].equalsIgnoreCase("West Baraboo")
					|| columns[2].equalsIgnoreCase("Prairie Du Sac")
					|| columns[2].equalsIgnoreCase("Oregon"))
				{
					groupName = "AE Residential Baraboo";
				}
				else if (columns[2].equalsIgnoreCase("Portage")
					|| columns[2].equalsIgnoreCase("Rio")
					|| columns[2].equalsIgnoreCase("Pardeeville")
					|| columns[2].equalsIgnoreCase("Poynette"))
				{
					groupName = "AE Residential Portage";
				}
				
				String[] serialNos = columns[8].split(",");
				for (int j = 0; j < serialNos.length; j++) {
					String line = "\"" + columns[0] + "\","
							+ columns[1] + ","
							+ columns[2] + ","
							+ columns[3] + ","
							+ columns[4] + ","
							+ columns[5] + ","
							+ columns[6] + ","
							+ columns[7] + ","
							+ serialNos[j] + ","
							+ "ExpressStat,"
							+ "AE Residential DegreeSaver,"
							+ groupName + ","
							+ serialNos[0] + ","
							+ serialNos[0];		// username = password = serial #
					output.add( line );
				}
			}
			
			lines = new String[ output.size() ];
			output.toArray( lines );
			ServerUtils.writeFile( outputFile, lines );
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
