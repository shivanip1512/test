/*
 * Created on Feb 25, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import com.cannontech.stars.util.ServerUtils;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class FdrCygnet2Telegyr {
	
	private static final String OUTPUT_FILE = "cygnet2telegyr.sql";
	
	private static final int EMS_TREN_NAME = 0;
	private static final int YUKON_POINT_ID = 1;
	private static final int API_GROUP_NAME = 2;
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println( "Usage: java " + FdrCygnet2Telegyr.class.getName() + " input_file [output_file]" );
			return;
		}
		
		File inputFile = new File( args[0] );
		ArrayList lines = ServerUtils.readFile( inputFile );
		if (lines == null) {
			System.out.println( "Failed to read input file '" + args[0] + "'" );
			return;
		}
		
		File outputFile = (args.length > 1)? new File(args[1]) : new File(inputFile.getParent(), OUTPUT_FILE);
		ArrayList sqlStmts = new ArrayList();
		TreeMap telegyrGrps = new TreeMap();
		
		for (int i = 1; i < lines.size(); i++) {
			String line = (String) lines.get(i);
			String[] fields = line.split(",");
			
			char pointType = fields[EMS_TREN_NAME].charAt(0);
			
			if (!telegyrGrps.containsKey( fields[API_GROUP_NAME] ))
				telegyrGrps.put( fields[API_GROUP_NAME], ((pointType == 'A')? "analog" : "digital") );
			
			String sql = "update FDRTranslation set InterfaceType='TELEGYR', destination='TELEGYR', Translation='Point:" +
					fields[EMS_TREN_NAME] + ";Group:" + fields[API_GROUP_NAME] + ";POINTTYPE:" + ((pointType == 'A')? "Analog" : "Status") +
					"' where PointID=" + fields[YUKON_POINT_ID] + ";";
			sqlStmts.add( sql );
		}
		
		Object[] grpNames = telegyrGrps.keySet().toArray();
		for (int i = 0; i < grpNames.length; i++) {
			String grpName = (String) grpNames[i];
			String grpType = (String) telegyrGrps.get( grpName );
			
			String sql = "insert into FDRTelegyrGroup values (" + (i+1) + ",'" + grpName + "',120,'" + grpType + "');";
			sqlStmts.add( i, sql );
		}
		
		try {
			ServerUtils.writeFile( outputFile, sqlStmts );
		}
		catch (IOException e) {
			System.out.println("Failed to write to output file '" + outputFile.getPath() + "'");
		}
	}

}
