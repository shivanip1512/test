/*
 * Created on Feb 25, 2004
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
public class FdrCygnet2Telegyr {
	
	private static final String OUTPUT_FILE = "cygnet2telegyr.sql";
	
	private static final int EMS_TREN_NAME = 0;
	private static final int YUKON_POINT_ID = 1;
	private static final int API_GROUP_NAME = 2;
	
	private static final java.text.DecimalFormat grpNumFormat =
			new java.text.DecimalFormat( "000" );	// three digits
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println( "Usage: java " + FdrCygnet2Telegyr.class.getName() + " input_file [output_file]" );
			return;
		}
		
		File inputFile = new File( args[0] );
		String[] lines = StarsUtils.readFile( inputFile, false );
		if (lines == null) {
			System.out.println( "Failed to read input file '" + args[0] + "'" );
			return;
		}
		
		File outputFile = (args.length > 1)? new File(args[1]) : new File(inputFile.getParent(), OUTPUT_FILE);
		ArrayList sqlStmts = new ArrayList();
		//TreeMap telegyrGrps = new TreeMap();
		int maxGrpNum = 0;
		
		for (int i = 1; i < lines.length; i++) {
			String[] fields = lines[i].split(",");
			
			char pointType = fields[EMS_TREN_NAME].charAt(0);
			int pointID = Integer.parseInt( fields[YUKON_POINT_ID] );
			int grpNum = (pointID - 1) / 128 + 1;
			String grpName = ((pointType == 'A')? "Analog" : "Status") + grpNumFormat.format( grpNum );
			
//			if (!telegyrGrps.containsKey( grpName ))
//				telegyrGrps.put( grpName, ((pointType == 'A')? "analog" : "digital") );
			
//			if (grpNum > maxGrpNum) maxGrpNum = grpNum;
			
			String sql = "update FDRTranslation set InterfaceType='TELEGYR', destination='TELEGYR', Translation='Point:" +
					fields[EMS_TREN_NAME] + ";Group:" + grpName + ";POINTTYPE:" + ((pointType == 'A')? "Analog" : "Status") +
					"' where PointID=" + fields[YUKON_POINT_ID] + ";";
			sqlStmts.add( sql );
		}
		
//		Object[] grpNames = telegyrGrps.keySet().toArray();
//		for (int i = 0; i < grpNames.length; i++) {
//			String grpName = (String) grpNames[i];
//			String grpType = (String) telegyrGrps.get( grpName );
//			
//			String sql = "insert into FDRTelegyrGroup values (" + (i+1) + ",'" + grpName + "',120,'" + grpType + "');";
//			sqlStmts.add( i, sql );
//		}
		
		maxGrpNum = 50;	// We will generate 50 groups each
		
		for (int i = 1; i <= maxGrpNum; i++) {
			String grpNum = grpNumFormat.format( i );
			String sql = "insert into FDRTelegyrGroup values (" + i + ",'Analog" + grpNum + "',120,'analog');";
			sqlStmts.add( i-1, sql );
		}
		
		for (int i = maxGrpNum + 1; i <= maxGrpNum * 2; i++) {
			String grpNum = grpNumFormat.format( i - maxGrpNum );
			String sql = "insert into FDRTelegyrGroup values (" + i + ",'Status" + grpNum + "',120,'digital');";
			sqlStmts.add( i-1, sql );
		}
		
		try {
			String[] sql = new String[ sqlStmts.size() ];
			sqlStmts.toArray( sql );
			StarsUtils.writeFile( outputFile, sql );
		}
		catch (IOException e) {
			System.out.println("Failed to write to output file '" + outputFile.getPath() + "'");
		}
	}

}
