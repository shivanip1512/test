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
		if (args.length < 1) {
			System.out.println( "Usage: java " + IdahoImport.class.getName() + " input_file [output_file]" );
			return;
		}
		
		File inputFile = new File( args[0] );
		String[] lines = ServerUtils.readFile( inputFile, false );
		if (lines == null) {
			System.out.println( "Failed to read input file '" + args[0] + "'" );
			return;
		}
		
		File outputFile = (args.length > 1)? new File(args[1]) : inputFile;
		String[] output = new String[ lines.length ];
		
		try {
			// Lowercase the passwords
			for (int i = 0; i < lines.length; i++) {
				String[] columns = ServerUtils.splitString( lines[i], "," );
				
				output[i] = "";
				for (int j = 0; j < columns.length - 1; j++)
					output[i] += columns[j] + ",";
				output[i] += columns[columns.length - 1].toLowerCase();
			}
			
			ServerUtils.writeFile( outputFile, output );
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
