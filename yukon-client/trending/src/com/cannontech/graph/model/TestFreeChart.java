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

public class TestFreeChart //extends com.jrefinery.chart.JFreeChart// com.jrefinery.ui.ApplicationFrame
{
	private static com.cannontech.database.data.graph.GraphDefinition gDef;
    protected static BasicTimeSeries series;
    protected static FreeChartModel dataModels[] = null;
/**
 * Retrieves the data for the given point list for the date
 * range indicated in the startDate and endDate.
 * Creation date: (10/3/00 5:53:52 PM)
 */
public static FreeChartModel[] hitDatabase() 
{
	java.util.ArrayList dataSeries = gDef.getGraphDataSeries();
	java.util.Iterator iter = dataSeries.iterator();

	if( gDef.getGraphDataSeries().isEmpty())
		return null;
		
	//for ( int i = 0; i < dataSeries.size(); i ++)
	//{
		//((com.cannontech.database.db.graph.GraphDataSeries)dataSeries.get(i)).getPointID();
	
	//java.util.Vector dataSeriesVec = new java.util.Vector(gDef.get
	
	java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("dd-MMM-yyyy");
	
	StringBuffer sql = new StringBuffer("SELECT DISTINCT POINTID, TIMESTAMP, VALUE "
		+ "FROM RAWPOINTHISTORY WHERE POINTID IN (");

		int pointIdIndex = 0;
		for (pointIdIndex = 0; pointIdIndex < dataSeries.size();pointIdIndex++ )
		{
			if( ((com.cannontech.database.db.graph.GraphDataSeries)dataSeries.get(pointIdIndex)).getType().equalsIgnoreCase("graph"))
			{
				sql.append(((com.cannontech.database.db.graph.GraphDataSeries)dataSeries.get(pointIdIndex)).getPointID().toString());
				pointIdIndex++;
				break;
			}
		}
	
		for ( int i = pointIdIndex; i < dataSeries.size(); i ++)
		{
			if(((com.cannontech.database.db.graph.GraphDataSeries)dataSeries.get(i)).getType().equalsIgnoreCase("graph"))
			{
				sql.append(", " + ((com.cannontech.database.db.graph.GraphDataSeries)dataSeries.get(i)).getPointID().toString());
			}
		}
		sql.append(") AND TIMESTAMP > '" + format.format(com.cannontech.util.ServletUtil.getYesterday()) + "' ");
		sql.append(" ORDER BY POINTID, TIMESTAMP");
			
	java.sql.Connection conn = null;
	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;

	FreeChartModel[] chartModelsArray = null;
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());

		if( conn == null )
		{
			System.out.println(":  Error getting database connection.");
			return null;
		}
		else
		{
			System.out.println("Executing:  " + sql.toString() );

			com.jrefinery.data.TimeSeriesDataPair dataPair = null;
			stmt = conn.createStatement();	
			rset = stmt.executeQuery(sql.toString());

			java.util.Vector dataPairVector = new java.util.Vector(0);			
			FreeChartModel model = new FreeChartModel();
			chartModelsArray = new FreeChartModel[dataSeries.size()];	//guessing on the size during testing.
			long lastPointId = -1;
			int pointCount = 0;
			
			while( rset.next() )
			{
				long pointID = rset.getLong(1);
				
				if( pointID != lastPointId)
				{
					if( lastPointId != -1)	//not the first one!
					{
						//Save the data you've collected into the array of models (chartmodelsArray).
						com.jrefinery.data.TimeSeriesDataPair[] dataPairArray =
							new com.jrefinery.data.TimeSeriesDataPair[dataPairVector.size()];
						dataPairVector.toArray(dataPairArray);
						model.setDataPairArray(dataPairArray);
						dataPairVector.clear();
						chartModelsArray[pointCount++] = model;
					}
					lastPointId = pointID;					
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
				dataPairVector.add(dataPair);
			}

			com.jrefinery.data.TimeSeriesDataPair[] dataPairArray = 
				new com.jrefinery.data.TimeSeriesDataPair[dataPairVector.size()];
			dataPairVector.toArray(dataPairArray);		
			model.setDataPairArray(dataPairArray);
			dataPairVector.clear();
			chartModelsArray[pointCount++] = model;			
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
	return chartModelsArray;
}
    /**
     * Starting point for the demonstration application.
     */
    public static void main(String[] args) {

        //TestFreeChart demo = new TestFreeChart("Time Series Demo 1");

  /*      demo.addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent e)
			{
				System.exit(0);
			};
		});
        
        demo.pack();
        demo.setVisible(true);
*/
    }
/**
 * Insert the method's description here.
 * Creation date: (6/20/2002 8:01:46 AM)
 */
public static void showFreeChart(com.jrefinery.chart.JFreeChart fChart, com.cannontech.database.data.graph.GraphDefinition graphDef)
{
	gDef = graphDef;
	FreeChartModel [] dataModels = null;
	dataModels = hitDatabase();

	series = new BasicTimeSeries("Minute Data", com.jrefinery.data.Second.class);
	com.jrefinery.data.TimeSeriesCollection dataset = new com.jrefinery.data.TimeSeriesCollection();

	//double average = 0;
	if( dataModels != null)
	{
		for ( int i = 0; i < dataModels.length; i++)
		{
			FreeChartModel model = dataModels[i];
			if( model != null)
			{
				Number[] valArray = new Number[model.getDataPairArray().length];
				for (int j = 0; j < model.getDataPairArray().length; j++)
				{
					com.jrefinery.data.TimeSeriesDataPair dp = (com.jrefinery.data.TimeSeriesDataPair)model.getDataPairArray()[j];
					series.add(dp);
					//valArray[j] = dp.getValue();
				}
				series.setDescription("description" + i);
				series.setDomainDescription("values" + i);
				series.setRangeDescription("times" + i);
				series.setName("HHHHHHHHHHHHHHH" + i);
				dataset.addSeries(series);

				//series = new BasicTimeSeries("LALALA data", com.jrefinery.data.Second.class);
				//average = com.jrefinery.data.Statistics.getAverage(valArray);

				series = new BasicTimeSeries("LALALA data", com.jrefinery.data.Second.class);
			}
		}
	}
	// create a title with Unicode characters (currency symbols in this case) to see if it works
	String chartTitle = gDef.getGraphDefinition().getName().toString();
	//com.jrefinery.chart.data.MovingAveragePlotFitAlgorithm mavg = new com.jrefinery.chart.data.MovingAveragePlotFitAlgorithm();
	//mavg.setPeriod(30);
	//com.jrefinery.chart.data.PlotFit pf = new com.jrefinery.chart.data.PlotFit(dataset, mavg);
	//dataset = pf.getFit();

	//fChart = null;
	//fChart = ChartFactory.createTimeSeriesChart(chartTitle, "Time", "Value",dataset, true);

	//com.jrefinery.chart.Plot plot = chart.getPlot();
	//plot.setssetShapeFactory())
	//fChart.addTitle(new TextTitle(chartTitle));
	fChart.getXYPlot().setDataset(dataset);
	fChart.getXYPlot().setSeriesPaint(0,java.awt.Color.green);
	//chart.getXYPlot().addHorizontalLine(new Double(average));

	//this.setLegend()
	com.jrefinery.chart.StandardLegend legend = new com.jrefinery.chart.StandardLegend(fChart);
	legend.setAnchor(com.jrefinery.chart.Legend.SOUTH);
	legend.setItemFont(new java.awt.Font("dialog", java.awt.Font.ITALIC, 10));
	fChart.setLegend(legend);

	//setFreeChart(chart);
	//ChartPanel chartPanel = new ChartPanel(chart);
	//this.setContentPane(chartPanel);

	//this.pack();
	//this.setVisible(true);
 }
}
