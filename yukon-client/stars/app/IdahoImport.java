import java.io.File;
import java.io.IOException;

import com.cannontech.stars.util.ServerUtils;

/*
 * Created on Jun 1, 2004
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
public class IdahoImport {
	
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println( "Usage: java " + IdahoImport.class.getName() + " cust_file hw_file [cust_file_out]" );
			return;
		}
		
		File custFile = new File( args[0] );
		String[] custLines = ServerUtils.readFile( custFile, false );
		if (custLines == null) {
			System.out.println( "Failed to read customer file '" + args[0] + "'" );
			return;
		}
		
		File hwFile = new File( args[1] );
		String[] hwLines = ServerUtils.readFile( hwFile, false );
		if (hwLines == null) {
			System.out.println( "Failed to read hardware file '" + args[1] + "'" );
			return;
		}
		
		File outputFile = (args.length > 2)? new File(args[2]) : custFile;
		String[] output = new String[ custLines.length ];
		
		try {
			// username = serial #, password = lowercase(first letter of first name + last name)
			String[][] hwCols = new String[hwLines.length][];
			for (int i = 0; i < hwLines.length; i++)
				hwCols[i] = ServerUtils.splitString( hwLines[i], "," );
			
			for (int i = 0; i < custLines.length; i++) {
				String[] custCols = ServerUtils.splitString( custLines[i], "," );
				
				output[i] = "";
				for (int j = 0; j < custCols.length - 2; j++)
					output[i] += custCols[j] + ",";
				
				// Username = Serial #
				for (int j = 0; j < hwCols.length; j++) {
					if (hwCols[j][0].equals( custCols[0] )) {
						output[i] += hwCols[j][3];
						break;
					}
				}
				
				// Password = Lowercase(first letter of first name + last name)
				output[i] += "," + custCols[3].substring(0, 1).toLowerCase() + custCols[2].toLowerCase();
			}
			
			ServerUtils.writeFile( outputFile, output );
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
