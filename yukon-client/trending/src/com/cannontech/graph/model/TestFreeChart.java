package com.cannontech.graph.model;

/**
 * Insert the type's description here.
 * Creation date: (6/17/2002 4:57:35 PM)
 * @author: 
 */
//package com.jrefinery.chart.demo;

import com.jrefinery.data.BasicTimeSeries;
import com.jrefinery.data.Quarter;
import com.jrefinery.data.TimeSeriesCollection;
import com.jrefinery.ui.ApplicationFrame;
import com.jrefinery.chart.JFreeChart;
import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.TextTitle;

public class TestFreeChart extends com.jrefinery.ui.ApplicationFrame
{

    protected BasicTimeSeries series;
    protected FreeChartModel [] chartModelsArray = null;

    /**
     * A demonstration application showing a quarterly time series containing a null value.
     */
    public TestFreeChart(String title) {

        super(title);
		java.util.Vector data = hitDatabase();
		
        series = new BasicTimeSeries("Minute Data", com.jrefinery.data.Second.class);

		com.jrefinery.data.TimeSeriesCollection dataset = new com.jrefinery.data.TimeSeriesCollection();
		
        if( chartModelsArray != null)
        {
	        for ( int i = 0; i < chartModelsArray.length; i++)
	        {
		        for (int j = 0; j < chartModelsArray.length; j++)
				{
					series.add((com.jrefinery.data.TimeSeriesDataPair)((FreeChartModel)chartModelsArray[i]).getDataPairArray()[j]);
				}
				dataset.addSeries(series);
				series = new BasicTimeSeries("LALALA data", com.jrefinery.data.Second.class);
	        }
        }
        //this.series.add(new Quarter(1, 2001), 500.2);
        //this.series.add(new Quarter(2, 2001), 694.1);
        //this.series.add(new Quarter(3, 2001), 734.4);
        //this.series.add(new Quarter(4, 2001), 453.2);
        //this.series.add(new Quarter(1, 2002), 500.2);
        //this.series.add(new Quarter(2, 2002), null);
        //this.series.add(new Quarter(3, 2002), 734.4);
        //this.series.add(new Quarter(4, 2002), 453.2);
        //TimeSeriesCollection dataset = new TimeSeriesCollection(series);
        // create a title with Unicode characters (currency symbols in this case) to see if it works
        String chartTitle = "TEST";
        JFreeChart chart = ChartFactory.createTimeSeriesChart(chartTitle, "Time", "Value",
                                                              dataset, true);

        //chart.addTitle(new TextTitle(subtitle));
        chart.getXYPlot().addHorizontalLine(new Double(550));
        ChartPanel chartPanel = new ChartPanel(chart);
        this.setContentPane(chartPanel);

    }
/**
 * Retrieves the data for the given point list for the date
 * range indicated in the startDate and endDate.
 * Creation date: (10/3/00 5:53:52 PM)
 */
public java.util.Vector hitDatabase() 
{
	return null;
	/*
	java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("dd-MMM-yyyy");
	StringBuffer sql = new StringBuffer("SELECT distinct pointid, TimeStamp,Value FROM RawPointHistory where pointid = 5" + 
		" AND TIMESTAMP > '"  + format.format(com.cannontech.util.ServletUtil.getToday()) + 
		"' order by pointid, timestamp");
	
		//+ new java.util.Date() + " and pointid = 44");
		
	java.sql.Connection conn = null;
	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;

	java.util.Vector dataSeriesVector = new java.util.Vector(0);
	
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());

		if( conn == null )
		{
			System.out.println(getClass() + ":  Error getting database connection.");
			return null;
		}
		else
		{
			System.out.println("Executing:  " + sql.toString() );

			FreeChartModel model = new FreeChartModel();
			
			com.jrefinery.data.TimeSeriesDataPair dataPair = null;
			stmt = conn.createStatement();	
			rset = stmt.executeQuery(sql.toString());

			chartModelsArray = new FreeChartModel[2];	//guessing on the size during testing.
			
			long lastPointId = -1;
			
			while( rset.next() )
			{
				long pointID = rset.getLong(1);
				
				if( pointID != lastPointId)
				{
					if( lastPointId != -1)	//not the first one!
					{
						com.jrefinery.data.TimeSeriesDataPair[] dataPairArray =
							new com.jrefinery.data.TimeSeriesDataPair[dataSeriesVector.size()];
						dataSeriesVector.toArray(dataPairArray);
						model.setDataPairArray(dataPairArray);
						dataSeriesVector.clear();
						chartModelsArray[0] = model;
				
						lastPointId = pointID;
					}
					//(re)-initialize for a new freechartmodel.
					model = new FreeChartModel();
					model.setPointId(pointID);
				}
								
				//new pointid in rset.
				//init everything, a new freechartmodel will be created with the change of pointid.
				java.sql.Timestamp ts = rset.getTimestamp(2);
				com.jrefinery.data.TimePeriod tp = new com.jrefinery.data.Second(new java.util.Date(ts.getTime()));
				double val = rset.getDouble(3);
				dataPair = new com.jrefinery.data.TimeSeriesDataPair(tp, val);
				
				System.out.println(tp + " and " +val);
				dataSeriesVector.add(dataPair);
			}
			model.setPointId(pointID);
			com.jrefinery.data.TimeSeriesDataPair[] dataPairArray = new com.jrefinery.data.TimeSeriesDataPair[dataSeriesVector.size()];
			dataSeriesVector.toArray(dataPairArray);		
			model.setDataPairArray(dataPairArray);
			dataSeriesVector.clear();
			chartModelsArray[0] = model;			
			lastPointId = pointID;
		
			chartModelsArray[0] = model;
		}
	}
	catch( java.sql.SQLException e )
	{
		e.printStackTrace();
	}
	finally
	{
		try
		{
			if( stmt != null ) stmt.close();
			if( conn != null ) conn.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			e2.printStackTrace();//sometin is up
			return null;
		}	
	}
	return dataSeriesVector;
	*/
}
    /**
     * Starting point for the demonstration application.
     */
    public static void main(String[] args) {

        TestFreeChart demo = new TestFreeChart("Time Series Demo 1");

        demo.addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent e)
			{
				System.exit(0);
			};
		});
        
        demo.pack();
        demo.setVisible(true);

    }
}
