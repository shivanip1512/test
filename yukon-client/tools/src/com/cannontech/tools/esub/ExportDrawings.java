/*
 * Created on Jul 28, 2003
 */
package com.cannontech.tools.esub;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Level;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.esub.util.Util;

/**
 * Utility program to export jlx files to their html, svg, and image components for static viewing.
 * @author aaron
 */
public class ExportDrawings {

	private static final String usage = "Usage:\n" + ExportDrawings.class.toString() + " inputdir outputdir\n" +
										"Where\ninputdir - Root directory containing jlx drawings\n" +
										"outputdir - Directory where output is written\n" +
										"interval - Optional, how often to write out in seconds"; 
										
	 
	public static void main(String[] args) throws IOException, InterruptedException{
		if(args.length < 2 || args.length > 3) {
			System.out.println(usage);
			System.exit(-1);
		}

		File inputDir = new File(args[0]);
		File outputDir = new File(args[1]);
		long interval = 0;
		
		if(args.length == 3) {			
			interval = Integer.parseInt(args[2]) * 1000;
		}
		
		if(!inputDir.isDirectory()){
			System.out.println("First argument must be input directory containing .jlx files");
			System.out.println(usage);
			System.exit(-1);
		}
		
		if(!outputDir.isDirectory()) {
			System.out.println("Second argument must be a writable output directory");
			System.out.println(usage);
			System.exit(-1);
		}
		
		System.out.println("Exporting drawings from " + inputDir.getCanonicalPath() + " to " + outputDir.getCanonicalPath());

		do {
			long begin = System.currentTimeMillis();		
			Util.doExport(inputDir, outputDir);
			long end = System.currentTimeMillis();
			
			System.out.println("Done in " + ((end-begin)/1000) + " seconds");
		
			Thread.sleep(Math.max(interval - (end-begin), 0));
		} while(interval != 0);
					
		System.exit(0);
	}
	
	
}
