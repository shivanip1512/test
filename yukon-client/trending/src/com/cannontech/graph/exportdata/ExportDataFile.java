package com.cannontech.graph.exportdata;

/**
 * Insert the type's description here.
 * Creation date: (8/21/2002 9:48:51 AM)
 * @author: 
 */
public class ExportDataFile implements com.cannontech.graph.GraphDefines
{
	private int exportPane = 0;		//Trend Pane index.  Specifies which types are valid for each tab.
	/*  VALID PANES ARE::::
	 * com.cannontech.graph.GraphDefines.TABULAR_PANE;
	 * com.cannontech.graph.GraphDefines.GRAPH_PANE
	 *  com.cannontech.graph.GraphDefines.SUMMARY_PANE;
	*/
		
	private String fileName = "Chart.csv";
	private String extension = "csv";
//	private java.io.File file = new java.io.File("C:/yukon/client/export/Chart.csv");
	
	private Object exportObject = null;
//	private String csvExportType = com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES;
//	private int csvExportMask = com.cannontech.database.db.graph.GraphDataSeries.VALID_INTERVAL_MASK;
	//Object items that are only valid if csv exportting is selected.
	private com.cannontech.graph.model.TrendModel trendModel = null;	
	private int csvColumnLength = 0;
	private int csvRowLength = 0;


	/**
	 * SaveAsJFileChooser constructor comment.
	 */
	public ExportDataFile() {
		super();
	}
	/**
	 * SaveAsJFileChooser constructor comment.
	 * @param currentDirectory java.io.File
	 */
	public ExportDataFile(int selectedPane, Object expObj, String newFileName)
	{
		exportPane = selectedPane;
		exportObject = expObj;
		fileName = newFileName;
	}

	public ExportDataFile(int selectedPane, Object expObj, String newFileName, com.cannontech.graph.model.TrendModel tModel)
	{
		exportPane = selectedPane;
		exportObject = expObj;
		fileName = newFileName;
		trendModel = tModel;
	}
	
	/**
 * Insert the method's description here.
 * Creation date: (8/2/2001 10:46:02 AM)
 */
	private java.util.TreeMap buildTreeMap(int validSeriesLength)
	{
		java.util.TreeMap tree = new java.util.TreeMap();
		
		int validIndex = 0;
		for( int k = 0; k < trendModel.getTrendSeries().length; k++ )
		{
			com.cannontech.graph.model.TrendSerie serie = trendModel.getTrendSeries()[k];
			if(com.cannontech.database.db.graph.GraphDataSeries.isValidIntervalType( serie.getTypeMask()))
			{
//				if( serie.getDataPairArray() != null)// With this check, null data is not represented with correct point
				{
			 		long[] timeStamp= serie.getPeriodsArray();
			 		double[] values = serie.getValuesArray();
			
			 		for( int l = 0; timeStamp != null && values != null &&  l < timeStamp.length && l < values.length; l++ )
			 		{
				 		Long ts = new Long(timeStamp[l]);
				 		Double[] objectValues = (Double[]) tree.get(ts);
				 		if( objectValues == null )
				 		{	
					 		//objectValues is not in the key already
					 		objectValues = new Double[ validSeriesLength ];
					 		tree.put(ts,objectValues);
				 		}
			
				 		objectValues[validIndex] = new Double(values[l]);
					}
					validIndex++;
				}
			}
		}
		return tree;	
	}
	
	public String [] createCSVFormat( )
	{
		if( trendModel == null)
		{
			return null;
		}

		String [] data = getExportArray();
		String[] buffer = new String[ data.length ];
		int temp = data.length/csvColumnLength;
		
		int length = buffer.length;
		int bufferIndex = 0;
	
		
		for (int i = 0; i < temp ;i++)
		{
			int j = 0;
			if (i == (temp - 1) )
			{
				for (j = 0; j < (csvColumnLength - 1); j++)
				{
					buffer[bufferIndex] = data[i+(temp * j)] + ",";
					bufferIndex++;
				}
					buffer[bufferIndex] = data[i+(temp * j)] + "\r\n";		
				break;	//don't want a comma on last one
			}
	
			else
			{
				for (j = 0; j < csvColumnLength; j++)
				{
					buffer[bufferIndex] = data[i+(temp * j)] + ",";
					bufferIndex++;
				}
					buffer[bufferIndex - 1] += "\r\n";
			}
		}
	return buffer;
	}
	
	public void createSVGFormat(java.io.File file )
	{
		/**** not functional yet.
		java.io.Writer writer = null;
		try
		{
//			java.io.OutputStream out = new java.io.FileOutputStream(file);
			com.jrefinery.chart.JFreeChart fChart = (com.jrefinery.chart.JFreeChart)exportObject;
			com.jrefinery.chart.ChartPanel cp = new com.jrefinery.chart.ChartPanel(fChart);	
	
		  // Get a DOMImplementation
	        org.w3c.dom.DOMImplementation domImpl =
	            org.apache.batik.dom.GenericDOMImplementation.getDOMImplementation();
	
	        // Create an instance of org.w3c.dom.Document
	        org.w3c.dom.Document document = domImpl.createDocument(null, "svg", null);
	
	        // Create an instance of the SVG Generator
	        org.apache.batik.svggen.SVGGraphics2D svgGenerator = 
	          new org.apache.batik.svggen.SVGGraphics2D(document);
	
	
	        // Ask the test to render into the SVG Graphics2D implementation       
	    	  cp.paint(svgGenerator);
	
	        // Finally, stream out SVG to the standard output 
	        // is this a good encoding to use?
	        writer = new java.io.FileWriter(file);
	        svgGenerator.stream(writer, true);
//	        out.flush();
		}
		catch (java.io.IOException ioe)
		{
			ioe.printStackTrace();
		}
		finally 
		{
			try
			{
				if( writer != null)
					writer.close();
			}
			catch(java.io.IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
		*/
	}
	public void createGIFFormat( )
	{
		/****   This has not been fully tested.  
		 * It causes 'too many for a gif' error.
		
		java.io.OutputStream out = null;
		try
		{
			com.jrefinery.chart.JFreeChart fChart = (com.jrefinery.chart.JFreeChart)exportObject;
			Acme.JPM.Encoders.GifEncoder encoder = new Acme.JPM.Encoders.GifEncoder(fChart.createBufferedImage(700, 500), out);
			encoder.encode();
		}
		catch (java.io.IOException ioe)
		{
			ioe.printStackTrace();
		}
		*/
	}//end encodeJPEGFormat()


	public char[] createHTMLFormat() 
	{
		return ((StringBuffer)exportObject).toString().toCharArray();
	}	
	
	public void createJPEGFormat(java.io.File file)
	{
		try
		{
			com.jrefinery.chart.JFreeChart fChart = (com.jrefinery.chart.JFreeChart)exportObject;			
			com.jrefinery.chart.ChartUtilities cu = new com.jrefinery.chart.ChartUtilities();
			cu.saveChartAsJPEG(file,fChart, 700, 500 );
		}
		catch (java.io.IOException ioe)
		{
			ioe.printStackTrace();
		}
		catch (ClassCastException cce)
		{
			cce.printStackTrace();
		}
	}//end encodeJPEGFormat()
	
	
	public void createPDFFormat(java.io.File file)
	{
		java.io.BufferedOutputStream out = null;

		try
		{
			com.jrefinery.chart.JFreeChart fChart = (com.jrefinery.chart.JFreeChart)exportObject;
			out = new java.io.BufferedOutputStream(new java.io.FileOutputStream(file));

			com.klg.jclass.util.swing.encode.page.PDFEncoder encoder = new com.klg.jclass.util.swing.encode.page.PDFEncoder();
			encoder.encode(fChart.createBufferedImage(556, 433), out);
			out.flush();	
		}		
		catch( java.io.IOException io )
		{
			io.printStackTrace();
		}
		catch( com.klg.jclass.util.swing.encode.EncoderException ee )
		{
			ee.printStackTrace();
		}
		finally
		{
			try
			{
				if( out != null )
					out.close();
			} 
			catch( java.io.IOException ioe )
			{
				ioe.printStackTrace();
			}
		}
	}

	public void createPNGFormat(java.io.File file )
	{
		try
		{
			com.jrefinery.chart.JFreeChart fChart = (com.jrefinery.chart.JFreeChart)exportObject;
			com.jrefinery.chart.ChartUtilities cu = new com.jrefinery.chart.ChartUtilities();
			cu.saveChartAsPNG(file,fChart, 700, 500 );
		}
		catch (java.io.IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	public int getExportPane()
	{
		return exportPane;
	}
	public String getExtension()
	{
		return extension;
	}

	public String getFileName()
	{
		return fileName;
	}
	public String getFileNameAndExtension()
	{
		return getFileName() + "." + getExtension();
	}
	/*
	public void setCSVExportMask(int newExportMask)
	{
		csvExportMask = newExportMask;
	}
	*/
	/*
	public void setCSVExportType(String newExportType)
	{
		csvExportType = newExportType;
	}*/
	public void setExtension(String newExtension)
	{
		extension = newExtension;
	}

	public void setFileName(String newFileName)
	{
		fileName = newFileName;
	}

	private String[] getExportArray()
	{
		String [] exportArray = null;
		valueFormat.setGroupingUsed(false);
		try
		{
			// Build a tree map of the current model
			csvColumnLength = 0;
			for( int i = 0; i < trendModel.getTrendSeries().length; i++)
			{
				if(com.cannontech.database.db.graph.GraphDataSeries.isValidIntervalType( trendModel.getTrendSeries()[i].getTypeMask() ))
				{
					csvColumnLength++;
				}
			}

			
			java.util.TreeMap tree = new java.util.TreeMap();
			tree = buildTreeMap(csvColumnLength);
			
			//time values
			java.util.Set keySet = tree.keySet();
			Long[] ts_KeyArray = new Long[keySet.size()];
			keySet.toArray(ts_KeyArray);

			// setup size and labels for export array 
			csvRowLength = ts_KeyArray.length + 1;	// +1 rows -> 1 for headings.
			csvColumnLength += 2;				// +2 cols -> 1 for date, 1 for time

			//csvColCount always > 0 (because of +2)		
			if( csvRowLength > 0 )
				exportArray = new String[csvRowLength * csvColumnLength];
			else
				exportArray = new String[csvColumnLength];

			// label the DATE and TIME data cols
			exportArray[0] = "DATE";	// column 1
			exportArray[csvRowLength] = "TIME";	//column 2
			for( int x = 1; x < ts_KeyArray.length + 1; x++ )
			{
				Long ts1 = ts_KeyArray[x - 1]; // use x-1 to include the 0 position of the keyarray.
				
				// date data for exportArray
				exportArray[x] = (String) (dateFormat.format(new java.util.Date(ts1.longValue())).toString());
				// time data for exportArray			
				exportArray[csvRowLength + x] = (String) (timeFormat.format(new java.util.Date(ts1.longValue())).toString());
			}

			int validIndex = 0;
			//Go through all the points one by one and add them in the array
			for( int z = 0; z < trendModel.getTrendSeries().length; z++ )
			{
				com.cannontech.graph.model.TrendSerie serie = trendModel.getTrendSeries()[z];
				if( com.cannontech.database.db.graph.GraphDataSeries.isValidIntervalType(serie.getTypeMask()))
				{
					valueFormat.setMaximumFractionDigits(3);//serie.getDecimalPlaces());
					valueFormat.setMinimumFractionDigits(3);//serie.getDecimalPlaces());

					// label the export file columns with their device + model name
					exportArray[csvRowLength * (validIndex+2)]  = (serie.getLabel());
//						(trendModel.getTrendSeries()[z].getDeviceName() + " " + serie.getLabel());
						

					for( int x = 1; x < ts_KeyArray.length + 1;x++ )	//go one extra for the header row?
					{
						Double[] values = (Double[])tree.get(ts_KeyArray[x - 1]);
						if(values[validIndex] == null)
							exportArray[(csvRowLength * (validIndex+2)) + x ] = "";
						else
						{
							exportArray[(csvRowLength * (validIndex+2)) + x ] = valueFormat.format(values[validIndex]);
						}
					}
					validIndex++;
				}
			}
		}//end try
		catch(Exception e)
		{
			com.cannontech.clientutils.CTILogger.info(" Exception caught in Graph.setExportArray()");
			e.printStackTrace();
		}
		valueFormat.setGroupingUsed(true);
		return exportArray;
	}

	public void writeFile(java.io.File file)
	{
		java.io.FileWriter writer = null;
		try
		{
			writer = new java.io.FileWriter(file);
			if( extension.equalsIgnoreCase("jpeg"))
				createJPEGFormat( file );
			else if( extension.equalsIgnoreCase("png"))
				createPNGFormat( file );
			else if( extension.equalsIgnoreCase("csv"))
			{
				String data[] = createCSVFormat();
				for( int i = 0; i < data.length; i++ )
				{
					if( data[i] != null)
						writer.write( data[ i ] );
				}
			}

			//  Formats not (fully) implemented yet.
//			else if( extension.equalsIgnoreCase("gif"))
//				createGIFFormat();
			else if( extension.equalsIgnoreCase("pdf"))
				createPDFFormat(file);
//			else if( extension.equalsIgnoreCase("svg"))
//				createSVGFormat();
			else if( extension.equalsIgnoreCase("html"))
			{
				char data[] = createHTMLFormat();
				writer.write(data);
			}
			else if( extension.equalsIgnoreCase("csv"))
			{
				String data[] = createCSVFormat();
				for( int i = 0; i < data.length; i++ )
				{
					if( data[i] != null)
						writer.write( data[ i ] );
				}
			}
		}
		catch(java.io.IOException ioe)
		{
			ioe.printStackTrace();
		}
		finally 
		{
			try
			{
				if( writer != null)
					writer.close();
			}
			catch(java.io.IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
	}

}
