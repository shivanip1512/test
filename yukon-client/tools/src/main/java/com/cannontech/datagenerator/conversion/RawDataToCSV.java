package com.cannontech.datagenerator.conversion;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;


public class RawDataToCSV {

	private File fileName;
	private int bankSize;
	private int numberOfBanks;
	private boolean invert;
	private int lead;
	private int lag;
	private int coeff;
	
	public RawDataToCSV(String fileName, int bankSize, int num, boolean invert, int lead, int lag, int coeff) {
		this.fileName = new File(fileName);
		this.bankSize = bankSize;
		this.invert = invert;
		this.lead = lead;
		this.lag = lag;
		this.coeff = coeff;
		this.numberOfBanks = num;
	}
	
	public RawDataToCSV() {
		this.fileName = new File("rawdata.csv");
		this.bankSize = 300;
		this.numberOfBanks = 3;
		this.invert = false;
		this.lead = -150;
		this.lag = 200;
		this.coeff = 1;
	}
	
	@SuppressWarnings("unchecked")
	private List<CapControlDataContainer> readFile() throws FileNotFoundException, IOException, NumberFormatException{
		BufferedReader fileReader = new BufferedReader(new FileReader(fileName));
		CSVReader reader = new CSVReader(fileReader);
		List<String[]> rawList = reader.readAll();
		List<CapControlDataContainer> rawData = new ArrayList<CapControlDataContainer>();
		
		for( String[] s : rawList ) {
			if( s.length > 2 )
				throw new IOException("CSV File not formated correctly");
			double d = Double.parseDouble(s[1]);
			if(invert)
				d = d * -1;
			CapControlDataContainer t = new CapControlDataContainer(s[0],d);
			rawData.add(t);
		}
		reader.close();
		return rawData;
	}
	
	public boolean generateFile( File outFile ) {
		List<CapControlDataContainer> rawData;
		
		try{
			rawData = readFile();
		} catch(FileNotFoundException e) {
			System.out.println(" File not Found");
			return false;
		} catch(IOException e) {
			System.out.println(" IOException, " + e.getMessage());
			return false;
		} catch(NumberFormatException e) {
			System.out.println(" NumberFormatException, " + e.getMessage());
			return false;
		}
		
		applyCapControl(rawData);
		
		try{
			CSVWriter fileOut = new CSVWriter(new BufferedWriter(new FileWriter(outFile)));
			for( CapControlDataContainer t : rawData ) {
				fileOut.writeNext(t.toStringArray());
			}
			fileOut.close();
		} catch (IOException e) {
			System.out.println(" IOException, " + e.getMessage());
			return false;			
		}
		
		return true;
	}
	
	private boolean applyCapControl(List<CapControlDataContainer> rawData) {

		double previousRawValue = 0.0F;
		int banksClosed = 0;
		double capValue = 0.0F;
		boolean first = true;
		
		for(CapControlDataContainer t : rawData) {

			if(first) {
				first = false;
				capValue = t.getValue();
				previousRawValue = t.getValue();
			}
			double difference = t.getValue() - previousRawValue;
			capValue += difference;
			previousRawValue = t.getValue();

			if(capValue >= lag) {
				if(banksClosed < numberOfBanks) {
					capValue = capValue - bankSize;
					banksClosed++;
					t.setOperation("Closed Bank");
				}else {
					t.setOperation("Need additional Bank");
				}
			} else if(capValue <= lead) {
				if(banksClosed > 0) {
					capValue += bankSize;
					banksClosed--;
					t.setOperation("Opened Bank");
				}else {
					t.setOperation("No banks to open");
				}
			}
			
			double coefValue = t.getValue() - (capValue + bankSize*banksClosed * coeff);
			t.setCapValue(capValue);
			t.setBanksClosed(banksClosed);
			t.setCoefValue(coefValue);
		}
		return true;
	}
	
	public static void main(String[] args) {
        // filename banksize numberOfBanks invertValues
		if( !validateArgs(args) ) {
            System.out.println(" Missing args:\n Enter: 'Filename' 'outFilename' 'banksize' 'number of banks' 'invert' 'leadvalue' 'lagvalue' 'coeef' ");
            return;
        } else {
            System.out.println( args[0] );
        }
		int bSize = Integer.parseInt(args[2]);
		int nBanks = Integer.parseInt(args[3]);
		int lead = Integer.parseInt(args[5]);
		int lag = Integer.parseInt(args[6]);
		int coeff = Integer.parseInt(args[7]);
		boolean invert = false;
		if( args[4].equalsIgnoreCase("yes") || args[4].equalsIgnoreCase("true")) {
			invert = true;
		}
		
		RawDataToCSV converter = new RawDataToCSV(args[0], bSize, nBanks, invert,lead,lag,coeff );
		converter.generateFile(new File(args[1]));
	}
	
	private static boolean validateArgs(String[] args) {
		if(args.length != 8) {
			return false;
		}
		
		try {
			Integer.parseInt(args[2]);
		} catch(NumberFormatException e) {
			return false;
		}
		
		try {
			Integer.parseInt(args[3]);
		} catch(NumberFormatException e) {
			return false;
		}
		
		try {
			Integer.parseInt(args[5]);
		} catch(NumberFormatException e) {
			return false;
		}
		
		try {
			Integer.parseInt(args[6]);
		} catch(NumberFormatException e) {
			return false;
		}
		
		try {
			Integer.parseInt(args[5]);
		} catch(NumberFormatException e) {
			return false;
		}
		
		String invert = args[4];
		
		if( "no".equalsIgnoreCase(invert) || "yes".equalsIgnoreCase(invert) ) {
			return true;
		} else {
			return false;
		}
	}	
}
