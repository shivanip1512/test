package com.cannontech.datagenerator;

/**
 * Command Line entry point for FDRLight
 * @author alauinger
 */
public class FDRLightMain {

	public static void main(String[] args) 
	{
		System.setProperty("cti.app.name", "FDRLight");
		FDRLightArgs fdrArgs = new FDRLightArgs();
		
		if( args.length != 4 ) {
			System.out.println("Incorrect number of arguments here are some example arguments: ");
			System.out.println("yukon_src 1510 yukon_dest 1510");
			System.out.println("where yukon_src and yukon_dest are yukon servers");
			System.exit(-1);
		}
		
		fdrArgs.setSrcHost(args[0]);		
		fdrArgs.setSrcPort(Integer.parseInt(args[1]));
		System.out.println("Source Dispatch: " + fdrArgs.getSrcHost() + " " + fdrArgs.getSrcPort());
				
		fdrArgs.setDestHost(args[2]);
		fdrArgs.setDestPort(Integer.parseInt(args[3]));
		System.out.println("Destination Dispatch: " + fdrArgs.getDestHost() + " " + fdrArgs.getDestPort());
				
		FDRLight fdrLight = new FDRLight(fdrArgs);
		
		System.out.println("Starting up....");
		fdrLight.run();
		System.out.println("...Finished");
	}
}
