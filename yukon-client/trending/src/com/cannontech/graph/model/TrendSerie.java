package com.cannontech.graph.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.util.Vector;

import com.cannontech.common.YukonColorPalette;
import com.cannontech.common.util.Pair;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.db.graph.GraphDataSeries;
import com.cannontech.spring.YukonSpringHook;

/**
 * @author snebben
 *
 * TrendSerie is a container for information about a data object that may be trended.
 * TrendModel uses TrendSerie(s) as the infromation of a data object to actually draw.
 */
public class TrendSerie
{
	//Contains values of org.jfree.data.time.TimeSeriesDataItem values.
	//TimeSeriesDataItem has a value and timestamp item.

    private Vector timeAndValueVector = null;
	private GraphDataSeries gds = null;

	// Flag for using graph multiplier
	public boolean useMultiplier = false;
	public long resolution = 1;

	private Pair minTimePair = null;
	private Pair maxTimePair = null;

	// Load factor values computed for each point in the model, stored in an array that
	//  is in accordance to the order of the pointIds.
	private double areaOfSet = 0.0; //Load Factor, area under the curve
	private double maxArea = 0.0;  //Load Factor, total area (using max point value)

	//contains dataItemArray.value(s).  (Readings)
	private double[] valuesArray = null;
	//contains dataItemArray.period(s). (Timestamps in millis).
	private long[] periodsArray = null;
	
	/**
	 * FreeChartModel constructor comment.
	 */
	public TrendSerie() {
		super();

		//creates a dummy GraphDataSeries instance of later usage
		setGraphDataSeries( new GraphDataSeries() );
	}
	
	/**
	 * @param gds
	 */
	public TrendSerie(GraphDataSeries gds) {
		super();
		setGraphDataSeries(gds);
	}

	/**
	 * @return
	 */
	public double getAreaOfSet() {
		if( areaOfSet == 0) {
			for (int i = 0; i < getTimeAndValueVector().size(); i++) {
				Pair tv = (Pair)getTimeAndValueVector().get(i);
				areaOfSet += getAdjustedValue((Double)tv.getSecond()).doubleValue();
			}
		}
		return areaOfSet;
	}


	public Double getAdjustedValue(Double valueToAdjust) {
		Double returnVal = valueToAdjust;
		if( getUseMultiplier() )
		{
			returnVal = Double.valueOf(returnVal.doubleValue() * getMultiplier().doubleValue());
		}
		return returnVal;
	}

	/**
	 * Returns the axis value.
	 * @return java.lang.Character
	 */
	public Character getAxis() {
		return getGraphDataSeries().getAxis();
	}

	/**
	 * Returns the color value.
	 * @return java.awt.Color
	 */
	public Color getColor() {
		return YukonColorPalette.getColor(getGraphDataSeries().getColor().intValue()).getAwtColor();
	}
	/**
	 * Returns the color value.
	 * @return java.awt.Color
	 */
	public Paint getTexture() {
	    
	    //Create a buffered image texture patch of size 5x5
		BufferedImage bi = new BufferedImage(5, 5,   
							BufferedImage.TYPE_INT_RGB);
		Graphics2D big = bi.createGraphics();
		// Render into the BufferedImage graphics to create the texture
		big.setColor(Color.white);
		big.fillRect(0,0,5,5);
		big.setColor(YukonColorPalette.getColor(getGraphDataSeries().getColor().intValue()).getAwtColor());
		big.fillOval(0,0,4,4);
		
		// Create a texture paint from the buffered image
		Rectangle r = new Rectangle(0,0,5,5);
		TexturePaint tp = new TexturePaint(bi,r);
		// Add the texture paint to the graphics context.

		return tp;
	}

	/**
	 * Returns the decimalPlaces value.
	 * @return int
	 */
	public int getDecimalPlaces() {
	    try {
	        LitePointUnit pointUnit = YukonSpringHook.getBean(PointDao.class).getPointUnit(gds.getPointID());
	        return pointUnit.getDecimalPlaces();
	    }catch (NotFoundException e) {
	        return 3;  //default to three if not available.
	    }
	}

	/**
	 * Returns the deviceName value.
	 * @return java.lang.String
	 */
	public String getDeviceName() {
		return getGraphDataSeries().getDeviceName();
	}
	
	/**
	 * Returns the label value.
	 * @return java.lang.String
	 */
	public String getLabel() {
		return getGraphDataSeries().getLabel();
	}
	
	/**
	 * Returns the load factor value.
	 * Load factor computed by totalAreaOfSet / maxArea.  -1 when no data.
	 * @return double
	 */
	public double getLoadFactor() {

		if( getTimeAndValueVector() != null) {
			if (getMaxArea() != 0.0 ) {
				return getAreaOfSet() / getMaxArea();
			}
		}
		return -1;
	}
	
	/**
	 * Returns the maxArea value.
	 * Multiplies the maximumValue by the dataItemArray length.
	 * Area value at the maximumValue curve.
	 * @return double
	 */
	public double getMaxArea() {
		if( maxArea == 0) {
			if( getMaximumValue() != null)
				maxArea = getMaximumValue().doubleValue() * getTimeAndValueVector().size();//getDataItemArray().length;
		}
		return maxArea;
	}
	
	/**
	 * Returns the maximumTSDataItem value.
	 * Searches through the entire dataItemArray for the MAX value and respective timeStamp.
	 * @return org.jfree.data.time.TimeSeriesDataItem
	 */
	public Pair getMaxTimePair() {
	    
		if( maxTimePair == null) {
			double max = -1;

			if( getTimeAndValueVector() != null) {
			    
				for (int i = 0; i < getTimeAndValueVector().size(); i++) {
				    
					Pair tv = (Pair)getTimeAndValueVector().get(i);
					double val = getAdjustedValue((Double)tv.getSecond()).doubleValue();
					if( val > max) {
						max = (val);
						maxTimePair = tv;
					}
				}
			}
		}
		return maxTimePair;
	}
	
	/**
	 * Returns the minimumTSDataItem value.
	 * Searches through the entire dataItemArray for the MIN value and respective timeStamp.
	 * @return org.jfree.data.time.TimeSeriesDataItem
	 */
	public Pair getMinTimePair() {
	    
		if( minTimePair == null) {
			double min = Double.MAX_VALUE;

			if( getTimeAndValueVector() != null) {
			    
				for (int i = 0; i < getTimeAndValueVector().size(); i++) {
				    
					Pair tv = (Pair)getTimeAndValueVector().get(i);
					double val = getAdjustedValue((Double)tv.getSecond()).doubleValue();
					if( val < min) {
						min = (val);
						minTimePair = tv;
					}
				}				
			}
		}
		return minTimePair;
	}

	/**
	 * Returns the value from maximumTSDataItem.
	 * @return java.lang.Double
	 */
	public Double getMaximumValue() {

		if( getMaxTimePair() != null) {
			return (Double)getMaxTimePair().getSecond();
		}
		return null;
	}
	/**
	 * Returns the value from minimumTSDataItem.
	 * @return java.lang.Double
	 */
	public Double getMinimumValue() {

		if( getMinTimePair() != null) {
			return (Double)getMinTimePair().getSecond();
		}
		return null;
	}

	/**
	 * Returns the multiplier value.
	 * @return java.lang.Double
	 */
	public Double getMultiplier() {
		return getGraphDataSeries().getMultiplier();
	}

	/**
	 * Returns the useMultiplier boolean.
	 * @return boolean
	 */
	public boolean getUseMultiplier() {
		return useMultiplier;
	}
	
	/**
	 * Returns the pointIDvalue.
	 * @return java.lang.Integer
	 */
	public Integer getPointId() {
		return getGraphDataSeries().getPointID();
	}

	/**
	 * Returns the typeMask value.
	 * @return int
	 */
	public int getTypeMask() {
		return getGraphDataSeries().getType().intValue();
	}
	
	/**
	 * Returns the valuesArray value.
	 * An array of only TimeSeriesDataItem.value(s) is created from dataItemArray.
	 * * Actually populates both the values and periods array if values array is null.
	 *   We are already iterating through the entrySet, we might as well store everything we can. 
	 * @return double[]
	 */
	public double[] getValuesArray() {
	    
		if( valuesArray == null) {

		    if( getTimeAndValueVector() == null) {
				return null;
		    }

			loadPeriodAndValueArrays();
		}
		return valuesArray;
	}
	
	private synchronized void loadPeriodAndValueArrays() {
		valuesArray = new double[getTimeAndValueVector().size()];
		periodsArray = new long[getTimeAndValueVector().size()];

		for (int i = 0; i < getTimeAndValueVector().size(); i++) {
			Pair tv = (Pair)getTimeAndValueVector().get(i);
			valuesArray[i] = getAdjustedValue((Double)tv.getSecond()).doubleValue();
			long time = ((Long)tv.getFirst()).longValue();
			long round = time % resolution;
			time = time - round;
			periodsArray[i] = time;
		}
	}
	
	/**
	 * Returns the periodsArray value.
	 * An array of only TimeSeriesDataItem.period(s) is created from dataItemArray.
	 * * Actually populates both the values and periods array if periods array is null.
	 *   We are already iterating through the entrySet, we might as well store everything we can. 
	 * @return long[]
	 */	
	public long[] getPeriodsArray() {
	    
		if( periodsArray == null) {
			if( getTimeAndValueVector() == null) {
				return null;
			}
			loadPeriodAndValueArrays();
		}
		return periodsArray;
	}	
	
	/**
	 * Sets the axis value.
	 * Valid options are 'L'[eft] 'R'[ight].
	 * @param newAxis java.lang.Character
	 */
	public void setAxis(Character newAxis) {
		getGraphDataSeries().setAxis(newAxis);
	}
	
	/**
	 * Sets the color value.
	 * @param newColor java.awt.Color
	 */
	protected void setColor(YukonColorPalette newColor) {
	    getGraphDataSeries().setColor(newColor.getColorId());
	}

	/**
	 * Sets the deviceName value
	 * @param newDeviceName java.lang.String
	 */
	protected void setDeviceName(String newDeviceName) {
		getGraphDataSeries().setDeviceName(newDeviceName);
	}
	
	/**
	 * Sets the label value
	 * @param newLabel java.lang.String
	 */
	protected void setLabel(String newLabel) {
		getGraphDataSeries().setLabel(newLabel);
	}
	
	/**
	 * Sets the multiplier value.
	 * @param newMultiplier java.lang.Double
	 */
	protected void setMultiplier(Double newMultiplier) {
		getGraphDataSeries().setMultiplier(newMultiplier);
	}
	
	/**
	 * Sets the useMultipier value
	 * @param selected boolean
	 */
	public void setUseMultiplier(boolean selected) {
		useMultiplier = selected;
//		Reset the values/periods arrays and gather the data with multiplier affected.
		valuesArray = null;
		periodsArray = null;
//		Reset the max/min dataItems when multiplier selection changes.
		minTimePair = null;
		maxTimePair = null;
	}	
	/**
	 * Sets the pointID value.
	 * @param newPointId java.lang.Integer
	 */
	protected void setPointId(Integer newPointId) {
		getGraphDataSeries().setPointID(newPointId);
	}
	/**
	 * Sets the typeMask value
	 * @param newTypeMask int
	 */
	protected void setTypeMask(int newTypeMask) {
		getGraphDataSeries().setType(new Integer(newTypeMask));
	}
	/**
	 * @param l
	 */
	public void setResolution(long l) {
		resolution = l;
	}

	/**
	 * @return
	 */
	public String getMoreData() {
		return getGraphDataSeries().getMoreData();
	}

	/**
	 * @param double1
	 */
	public void setMoreData(String string) {
		getGraphDataSeries().setMoreData( string);
	}

	/**
	 * @return
	 */
	public int getRenderer() {
		return getGraphDataSeries().getRenderer().intValue();
	}
    
    /**
     * @return
     */
    public GraphDataSeries getGraphDataSeries() {
    	return gds;
    }
    
    /**
     * @param series
     */
    public void setGraphDataSeries(GraphDataSeries series) {
    	gds = series;
    }

	/**
	 * @return
	 */
	public Vector getTimeAndValueVector() {
		return timeAndValueVector;
	}

	/**
	 * @param vector
	 */
	public void setTimeAndValueVector(Vector vector) {
		timeAndValueVector = vector;
	}

}
