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
					|| columns[2].equalsIgnoreCase("Prarie Du Sac"))
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
				
				output[i] = lines[i] + "," +
						"ExpressStat," +
						"AE Residential DegreeSaver," +
						groupName + "," +
						columns[8] + "," + columns[8];	// username = password = serial #
			}
			
			ServerUtils.writeFile( outputFile, output );
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
