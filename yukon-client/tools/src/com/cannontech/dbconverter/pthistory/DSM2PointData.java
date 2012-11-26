package com.cannontech.dbconverter.pthistory;

import com.google.common.io.LittleEndianDataInputStream;

/**
 * A single timestamp of DSM2 point data.
 * @author: Aaron Lauinger
 */
public class DSM2PointData {
	
	public int time;
	public float value;
	public short quality;

/**
 * PointData constructor comment.
 */
public DSM2PointData() {
	super();
}
public static DSM2PointData[] loadPointData(String file) throws java.io.FileNotFoundException, java.io.IOException {

	DSM2PointData[] data = null;
	java.io.BufferedInputStream bin = null;
	
	try {
		java.io.File f = new java.io.File(file);
		long fileLength = f.length();
		
		bin = new java.io.BufferedInputStream(new java.io.FileInputStream(f));		
		LittleEndianDataInputStream in = new LittleEndianDataInputStream(bin);
		data = new DSM2PointData[(int) (fileLength / (4+4+2))]; //int*float*short
				
		for( int i = 0; i < data.length; i++ ) {

			int time = in.readInt();
			float value = in.readFloat();
			short quality = in.readShort();

			//check the values, DAT files are
			//known for crazy stuff
			if( !(value > Float.MIN_VALUE &&
				  value < Float.MAX_VALUE) ) {
				//System.out.println("Found corrupt value in file, continuing...");
				continue;
			}

			//if( time < (int) (firstGood.getT

			DSM2PointData pd = new DSM2PointData();
				
			pd.time = time;
			pd.value = value;
			pd.quality = quality;
			
			data[i] = pd;
		}
	}
	catch( java.io.FileNotFoundException e ) {
		e.printStackTrace();
		throw e;
	}	
	finally	{
		try {
		if( bin != null ) bin.close();
		} catch(java.io.IOException e2) { throw e2; };

		if( data == null )
			data = new DSM2PointData[0];
			
	}
	
	return data;	
}
/**
 * Creation date: (2/26/2002 10:51:42 AM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) throws java.io.IOException {

	if( args.length != 1 ) {
		System.out.println("Usage:  PointData <.dat file>");
		System.exit(1);
	}

	String datFile = args[0];

	long start = System.currentTimeMillis();
		
	System.out.println("Loading point data from: " + datFile);
	
	DSM2PointData[] data = loadPointData(args[0]);

	long end = System.currentTimeMillis();	
	System.out.println("Read " + data.length + " values");

	System.out.println("Took " + (end-start) + " milliseconds");
}
}
