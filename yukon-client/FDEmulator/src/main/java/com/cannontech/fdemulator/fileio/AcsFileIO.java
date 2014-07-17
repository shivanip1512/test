/*
 * Created on May 20, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.fdemulator.fileio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

import com.cannontech.fdemulator.protocols.ACSPoint;
/**
 * @author ASolberg
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class AcsFileIO
{
	public static String acsFile = "resource/acs_points.cfg";
	private static final String LF = System.getProperty("line.separator");
	private BufferedReader file;
	
	public AcsFileIO(String filename)
	{
		acsFile = filename;
	}
	
	//	write the acs_points.cfg file (file of points we are sending to yukon)with changed data
	public void writeACSFileUpdate(String[] values_, File file_, int row, int col, Object value)
	{
		try
		{
			FileWriter fileWriter = new FileWriter(file_);
			

			for (int i = 0; i < values_.length; i++)
			{
				
				try
				{
					if (null == values_[i]){
						break;
					}
					
					if(i != row){
						fileWriter.write(values_[i] + LF);
					}else{
						
						StringTokenizer st = new StringTokenizer(values_[i], ";");
						String newstring = "";
						int index = 0;
						while(st.hasMoreTokens()){
							
							if(index == col){
								st.nextToken();
								newstring += (value.toString()+ ";");
							}else {newstring += (st.nextToken()+";");} 
							index++;
						}
						// strip off last ";"
						newstring = newstring.substring(0,newstring.length()-1);
						
						fileWriter.write(newstring + LF);
					}
				} catch (Exception e)
				{
					if ("java.lang.NullPointerException".equalsIgnoreCase(e.toString()))
					{
						System.out.println("got to end of file");
						break;
					}
				}

			}

			fileWriter.write("EOF\n");
			fileWriter.write("# Structure: <string point_type>;<int remote#>;<int point#>;<char category>;<int sending_interval>;<string function>;<int min>;<int max>; + function variables\n");
			fileWriter.write("#\n");
			fileWriter.write("#	Sending Intervals (10,30,60,300,900,3600)\n");
			fileWriter.write("#\n");
			fileWriter.write("#	Functions: (RANDOM,PYRAMID,DROPOFF)\n");
			fileWriter.write("#\n");
			fileWriter.write("#	RANDOM:\n");
			fileWriter.write("#	PYRAMID:	;<int delta>\n");
			fileWriter.write("#	DROPOFF:	;<int delta>;<boolean maxstart>\n");
			fileWriter.write("#\n");
			fileWriter.write("# Examples:\n");
			fileWriter.write("#\n");
			fileWriter.write("#  Random: 	'Value;1;1;P;10;RANDOM;0;100'\n");
			fileWriter.write("#  Pyramid:	'Value;2;2;P;30;PYRAMID;0;100;10'\n");
			fileWriter.write("#  Dropoff:	'Value;3;3;P;60;DROPOFF;0;100;10;true'\n");

			fileWriter.close();
		} catch (Exception e)
		{
			if (e.toString().equalsIgnoreCase("java.lang.NullPointerException"))
			{
			}
			System.out.println("error when writing point file");
			e.printStackTrace(System.out);
		}

	}
	
	//	write the acs_points.cfg file (file of points we are sending to yukon)
	public void writeACSFile(final String[] values_, final File file_)
	{
		try
		{
			FileWriter fileWriter = new FileWriter(file_);

			for (int i = 0; i < values_.length; i++)
			{
				try
				{
					
					fileWriter.write(values_[i].toString() + LF);
				} catch (Exception e)
				{
					if ("java.lang.NullPointerException".equalsIgnoreCase(e.toString()))
					{
						
						break;
					}
				}

			}

			fileWriter.write("EOF\n");
			fileWriter.write("# Structure: <string point_type>;<int remote#>;<int point#>;<char category>;<int sending_interval>;<string function>;<int min>;<int max>; + function variables\n");
			fileWriter.write("#\n");
			fileWriter.write("#	Sending Intervals (10,30,60,300,900,3600)\n");
			fileWriter.write("#\n");
			fileWriter.write("#	Functions: (RANDOM,PYRAMID,DROPOFF)\n");
			fileWriter.write("#\n");
			fileWriter.write("#	RANDOM:\n");
			fileWriter.write("#	PYRAMID:	;<int delta>\n");
			fileWriter.write("#	DROPOFF:	;<int delta>;<boolean maxstart>\n");
			fileWriter.write("#\n");
			fileWriter.write("# Examples:\n");
			fileWriter.write("#\n");
			fileWriter.write("#  Random: 	'Value;1;1;P;10;RANDOM;0;100'\n");
			fileWriter.write("#  Pyramid:	'Value;2;2;P;30;PYRAMID;0;100;10'\n");
			fileWriter.write("#  Dropoff:	'Value;3;3;P;60;DROPOFF;0;100;10;true'\n");
			fileWriter.flush();
			fileWriter.close();
		} catch (Exception e)
		{
			if (e.toString().equalsIgnoreCase("java.lang.NullPointerException"))
			{
			}
			System.out.println("error when writing point file");
			e.printStackTrace(System.out);
		}

	}
	
	public void addAcsPointToFile(ACSPoint newpoint)
	{
		String[] points = new String[500];

		points = readFile(new File(acsFile));
		String newline = "";

		newline = (newpoint.getPointType() + ";" + newpoint.getPointRemote() + ";" + newpoint.getPointNumber() + ";" + newpoint.getPointCategory() + ";" + newpoint.getPointInterval() + ";" + newpoint.getPointFunction() + ";" + newpoint.getPointMin() + ";" + newpoint.getPointMax() + ";" + newpoint.getPointDelta() + ";" + newpoint.getPointMaxStart());

		FileWriter fileWriter = null;
		try
		{
			fileWriter = new FileWriter(new File(acsFile));
		} catch (IOException e1)
		{
			
			e1.printStackTrace();
		}

		for (int i = 0; i < points.length; i++)
		{
			try
			{
				if (null == points[i]){
					break;
				}
				fileWriter.write(points[i] + LF);
			} catch (Exception e)
			{
				if ("java.lang.NullPointerException".equalsIgnoreCase(e.toString()))
				{
					break;
				}

			}
		}
		try{
		fileWriter.write(newline + LF);
		fileWriter.write("EOF\n");
		fileWriter.write("# Structure: <string point_type>;<int remote#>;<int point#>;<char category>;<int sending_interval>;<string function>;<int min>;<int max>; + function variables\n");
		fileWriter.write("#\n");
		fileWriter.write("#	Sending Intervals (10,30,60,300,900,3600)\n");
		fileWriter.write("#\n");
		fileWriter.write("#	Functions: (RANDOM,PYRAMID,DROPOFF)\n");
		fileWriter.write("#\n");
		fileWriter.write("#	RANDOM:\n");
		fileWriter.write("#	PYRAMID:	;<int delta>\n");
		fileWriter.write("#	DROPOFF:	;<int delta>;<boolean maxstart>\n");
		fileWriter.write("#\n");
		fileWriter.write("# Examples:\n");
		fileWriter.write("#\n");
		fileWriter.write("#  Random: 	'Value;1;1;P;10;RANDOM;0;100'\n");
		fileWriter.write("#  Pyramid:	'Value;2;2;P;30;PYRAMID;0;100;10'\n");
		fileWriter.write("#  Dropoff:	'Value;3;3;P;60;DROPOFF;0;100;10;true'\n");
		fileWriter.flush();
		fileWriter.close();
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
		
	}
	
	// method for filling point array with points from point file
	public Object[] getAcsPointsFromFile()
	{
		Object[] pointarray = new Object[500];

		// fill points array with point objects from file
		try
		{
			file = new BufferedReader(new FileReader(acsFile));
		} catch (Exception e)
		{
			if ("java.io.FileNotFoundException".equalsIgnoreCase(e.toString()))
			{
				System.out.println("File not found when trying to load points from file");
			} else
				e.printStackTrace(System.out);
		}

		int i = 0;

		while (true)
		{
			try
			{
				String pointline = file.readLine();
				if ("EOF".equalsIgnoreCase(pointline))
				{
					break;
				}

				StringTokenizer st = new StringTokenizer(pointline, ";");
				String pointtype = st.nextToken();
				String pointremote = st.nextToken();
				String pointnumber = st.nextToken();
				String pointcategory = st.nextToken();
				String interval = st.nextToken();
				String function = st.nextToken();
				String min = st.nextToken();
				String max = st.nextToken();
				String delta = st.nextToken();
				String maxstart = st.nextToken();

				ACSPoint newpoint = new ACSPoint(pointtype, pointremote, pointnumber, pointcategory, interval, function, min, max, delta, maxstart);
				pointarray[i] = newpoint;

				i++;

			} catch (Exception e)
			{
				System.out.println("Error getting points from file");
				e.printStackTrace(System.out);
				break;
			}
		}
		return pointarray;
	}
	
	public String[] readFile(final File file)
	{
		String[] points = new String[512];
		if (file.exists())
		{
			try
			{

				java.io.RandomAccessFile fileReader = new java.io.RandomAccessFile(file, "r");
				String token = "";
				int i = 0;
				int exit = 0;
				while (exit != 1)
				{
					token = fileReader.readLine();

					if ("EOF".equalsIgnoreCase(token))
					{
						exit = 1;
					} else
					{
						points[i] = token;
					}
					i++;
				}

				fileReader.close();
			} catch (Exception e)
			{
				System.out.println("error reading points from file");
				e.printStackTrace(System.out);
			}
		} else
		{
			System.out.println("unable to find file: " + file);
			return null;
		}
		return points;
	}
}