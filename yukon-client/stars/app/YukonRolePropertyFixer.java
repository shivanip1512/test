import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.cannontech.stars.util.ServerUtils;

/*
 * Created on May 17, 2004
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
public class YukonRolePropertyFixer {
	
	private static final String OUTPUT_FILE = "YukonRolePropertyUpdate.sql";
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println( "Usage: java " + AlliantImportRegulator.class.getName() + " input_file [output_file]" );
			return;
		}
		
		File inputFile = new File( args[0] );
		String[] lines = ServerUtils.readFile( inputFile, false );
		if (lines == null) {
			System.out.println( "Failed to read input file '" + args[0] + "'" );
			return;
		}
		
		File outputFile = (args.length > 1)? new File(args[1]) : new File(inputFile.getParent(), OUTPUT_FILE);
		ArrayList outputLines = new ArrayList();
		
		for (int i = 0; i < lines.length; i++) {
			if (!lines[i].toUpperCase().startsWith("INSERT INTO YUKONROLEPROPERTY"))
				continue;
			
			int leftPrth = lines[i].indexOf( '(' );
			int rightPrth = lines[i].lastIndexOf( ')' );
			String insertValues = lines[i].substring( leftPrth + 1, rightPrth );
			
			StringTokenizer st = new StringTokenizer( insertValues, ",", true );
			String rolePropId = st.nextToken(); st.nextToken();
			String roleId = st.nextToken(); st.nextToken();
			String keyName = st.nextToken(); st.nextToken();
			while (!st.nextToken("'").equals("'"));
			String dftValue = "'" + st.nextToken() + "'";
			while (!st.nextToken(",").equals(","));
			String description = st.nextToken( "\n" );
			
			String line = "update YukonRoleProperty set " +
					"KeyName=" + keyName + ", " +
					"DefaultValue=" + dftValue + ", " +
					"Description=" + description +
					" where RolePropertyID=" + rolePropId + ";";
			outputLines.add( line );
		}
		
		String[] output = new String[ outputLines.size() ];
		outputLines.toArray( output );
		
		try {
			ServerUtils.writeFile( outputFile, output );
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
