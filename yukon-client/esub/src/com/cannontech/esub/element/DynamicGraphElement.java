package com.cannontech.esub.element;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Properties;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.GraphFuncs;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.esub.editor.Drawing;
import com.cannontech.graph.Graph;
import com.cannontech.graph.model.TrendModelType;
import com.cannontech.util.ServletUtil;
import com.loox.jloox.LxAbstractRectangle;
import com.loox.jloox.LxSaveUtils;
 
/**
 * Description Here
 * @author alauinger
 */
public class DynamicGraphElement extends LxAbstractRectangle implements DrawingElement {

	// Possible date ranges
	/*public static final int TODAY = 0;
	public static final int YESTERDAY = 1;
	public static final int PREV2DAYS = 2;
	public static final int PREV3DAYS = 3;
	public static final int PREV7DAYS = 4;
	*/
	private static final int CURRENT_VERSION = 1;
	private static final int INVALID_GRAPH_DEFINITION = -1;
	private static final Dimension DEFAULT_SIZE = new Dimension(640,480);
	private static final Color DEFAULT_COLOR = Color.white;
		
	private Graph ctiGraph;
	private Date currentStartDate;
	private Date lastUpdated;
	private boolean dirty; //flag to determine if update is required
	
	//persistent fields	
	private LiteGraphDefinition graphDefinition;
	private int viewType; // see com.cannontech.graph.model.TrendModelType;
	private String displayPeriod; 
	private transient Drawing drawing = null;
	private String linkTo = null;
	private Properties props = new Properties();
 	private int version = CURRENT_VERSION;
 	
	/** 
	 * Returns the drawing.
	 * @return Drawing
	 */
	public Drawing getDrawing() {
		return drawing;
	}
	
	public DynamicGraphElement() {
		super();
		initialize();
	}
	private void initialize() {
		setGraphDefinition(new LiteGraphDefinition(INVALID_GRAPH_DEFINITION));
		setTrendType(TrendModelType.LINE_VIEW);
		setDisplayPeriod(ServletUtil.TODAY);
		resetDisplayRange();
		setSize(DEFAULT_SIZE.getWidth(), DEFAULT_SIZE.getHeight());
		setPaint(DEFAULT_COLOR);
		setLineThickness(1);
		setLineColor(DEFAULT_COLOR);
		setLastUpdated(new Date(0));
	}

	/**
	 * Sets the drawing.
	 * @param drawing The drawing to set
	 */
	public void setDrawing(Drawing drawing) {
		this.drawing = drawing;
	}
	
	/**
	 * Returns the graphDefinition.
	 * @return LiteGraphDefinition
	 */
	public LiteGraphDefinition getGraphDefinition() {
		return graphDefinition;
	}

	public int getGraphDefinitionID() {
		return getGraphDefinition().getLiteID();
	}
	
	
	/**
	 * Sets the graphDefinition.
	 * @param graphDefinition The graphDefinition to set
	 */
	public void setGraphDefinition(LiteGraphDefinition graphDefinition) {
		
		if( this.graphDefinition == null ||
		    !this.graphDefinition.equals(graphDefinition) ) {
			this.graphDefinition = graphDefinition;
			setDirty(true);
		}
	}

	public void setGraphDefinitionID(int id) {
		setGraphDefinition(GraphFuncs.getLiteGraphDefinition(id));	
	}

	/**
	 * @see com.loox.jloox.LxComponent#paintHandles(Graphics2D, double, double, double, double)
	 */
	public void paintHandles(
		Graphics2D arg0,
		double arg1,
		double arg2,
		double arg3,
		double arg4) {		
		super.paintHandles(arg0, arg1, arg2, arg3, arg4);
	}

	/**
	 * @see com.loox.jloox.LxComponent#paintHandles(Graphics2D)
	 */
	public void paintHandles(Graphics2D arg0) {		
		super.paintHandles(arg0);
	}

	/**
	 * @see com.loox.jloox.LxComponent#paintStandardHandles(Graphics2D, double, double, double, double)
	 */
	public void paintStandardHandles(
		Graphics2D arg0,
		double arg1,
		double arg2,
		double arg3,
		double arg4) {
		super.paintStandardHandles(arg0, arg1, arg2, arg3, arg4);
	}

	/**
	 * @see com.loox.jloox.LxComponent#postpaint(Graphics2D)
	 */
	protected void postpaint(Graphics2D arg0) {
		super.postpaint(arg0);
	}

	/**
	 * @see com.loox.jloox.LxElement#paintElement(Graphics2D)
	 */
	protected void paintElement(Graphics2D g) {		
		super.paintElement(g);
			
		if( getGraphDefinitionID() == INVALID_GRAPH_DEFINITION ) {		
			return;
		}	
		
		//updateGraph();
			
		double w = getWidth();
		double h = getHeight();
		g.translate( (w/2.0)*-1, (h/2.0)*-1.0);
		getCTIGraph().getFreeChart().draw(g, new Rectangle((int) w,(int) h));
	}

	/**
	 * @see com.loox.jloox.LxComponent#prepaint(Graphics2D)
	 */
	protected boolean prepaint(Graphics2D arg0) {		
		return super.prepaint(arg0);
	}

	/**
	 * Returns the graph.
	 * @return Graph
	 */
	public Graph getCTIGraph() {
		if( ctiGraph == null ) {
			ctiGraph = new Graph();
		}
		return ctiGraph;
	}

	/**
	 * Sets the graph.
	 * @param graph The graph to set
	 */
	private void setCTIGraph(Graph graph) {
		this.ctiGraph = graph;
	}

	/**
	 * Returns the trendType.
	 * @return int
	 */
	public int getTrendType() {
		return viewType;
	}

	/**
	 * Sets the trendType.
	 * @param trendType The trendType to set
	 */
	public void setTrendType(int trendType) {
		if( this.viewType != trendType) {
			this.viewType = trendType;	
			setDirty(true);
		}
	}
	
	public void updateGraph() {
		
		if( getGraphDefinitionID() == INVALID_GRAPH_DEFINITION ) 
			return;
			
		com.cannontech.database.data.graph.GraphDefinition gDef = new com.cannontech.database.data.graph.GraphDefinition();
		gDef.getGraphDefinition().setGraphDefinitionID(new Integer(getGraphDefinitionID()));
		
		java.sql.Connection conn = null;
			try
			{
				conn = com.cannontech.database.PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
				gDef.setDbConnection(conn);
				gDef.retrieve();

				// Lose the reference to the connection
				gDef.setDbConnection(null);
			}
			catch( java.sql.SQLException e )
			{
				e.printStackTrace();		 
			}
			finally
			{   //make sure to close the connection
				try { if( conn != null ) conn.close(); } catch( java.sql.SQLException e2 ) { e2.printStackTrace(); };
			}

			resetDisplayRange();
					
			gDef.getGraphDefinition()
				.setStartDate(ServletUtil.getStartingDateOfInterval(getCurrentStartDate(),getDisplayPeriod()));
			gDef.getGraphDefinition()
				.setStopDate(ServletUtil.getEndingDateOfInterval(getCurrentStartDate(),getDisplayPeriod()));
						
			Graph graph = getCTIGraph();
			
			graph.setDatabaseAlias(CtiUtilities.getDatabaseAlias());
			graph.setSize((int) getWidth(), (int) getHeight());
			graph.setGraphDefinition(gDef);
			//graph.setSeriesType( com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES);

			graph.setViewType(getTrendType());
						
			// Define the peak series....
			/*for (int i = 0; i < gDef.getGraphDataSeries().size(); i++)
			{
				com.cannontech.database.db.graph.GraphDataSeries gds = (com.cannontech.database.db.graph.GraphDataSeries) gDef.getGraphDataSeries().get(i);

				if ( graph.isPeakSeries( gds.getType()) )
				{
					graph.setHasPeakSeries( true );
					break;
				}
			}*/
		getCTIGraph().setUpdateTrend(true);
		getCTIGraph().update();
		
		setLastUpdated(new Date());
		setDirty(false);
	} 

	/**
	 * Returns the currentStartDate.
	 * @return Date
	 */
	public Date getCurrentStartDate() {
		return currentStartDate;
	}

	/**
	 * Sets the currentEndDate.
	 * @param currentEndDate The currentEndDate to set
	 */
	/*public void setCurrentEndDate(Date currentEndDate) {
		if( this.currentEndDate == null ||
			!this.currentEndDate.equals(currentEndDate) ) {
			this.currentEndDate = currentEndDate;
			setDirty(true);
		}
	}*/

	/**
	 * Sets the currentStartDate.
	 * @param currentStartDate The currentStartDate to set
	 */
	public void setCurrentStartDate(Date currentStartDate) {
		if( this.currentStartDate == null ||
		    !this.currentStartDate.equals(currentStartDate) ) {
			this.currentStartDate = currentStartDate;
			setDirty(true);
		}
	}

	

	/**
	 * Set the current start and stop dates to correspond with
	 * the graphs display range
	 */
	private void resetDisplayRange() {		
		Date start = ServletUtil.getToday();
		String period = getDisplayPeriod();
		setCurrentStartDate(start);		
		
	}
	
	public boolean shouldUpdate() {			
			return 
					(isDirty() ||
			 		(getGraphDefinitionID() != INVALID_GRAPH_DEFINITION &&					 
					 System.currentTimeMillis() > (getLastUpdated().getTime() + getCTIGraph().getMinIntervalRate()*1000) ));
	}

	/**
	 * Returns the dirty.
	 * @return boolean
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * Sets the dirty.
	 * @param dirty The dirty to set
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	/**
	 * @see com.cannontech.esub.editor.element.DrawingElement#getElementProperties()
	 */
	public Properties getElementProperties() {
		return props;
	}

	/**
	 * @see com.cannontech.esub.editor.element.DrawingElement#setElementProperties(Properties)
	 */
	public void setElementProperties(Properties props) {
		this.props = props;
	}
	
	    /**
         * @see com.loox.jloox.LxComponent#readFromJLX(InputStream, String)
         */
        public void readFromJLX(InputStream in, String version) throws IOException {
                super.readFromJLX(in, version);
        
                setGraphDefinitionID(LxSaveUtils.readInt(in));
                setTrendType(LxSaveUtils.readInt(in));
                setDisplayPeriod(LxSaveUtils.readString(in));
                
                /* KEEP THIS AFTER SWITCH TO PERSIST STUFF!!!*/
                resetDisplayRange();
                
                //read link
                //setLinkTo( LxSaveUtils.readString(in));
        
                LxSaveUtils.readEndOfPart(in);
        }

        /**
         * @see com.loox.jloox.LxComponent#saveAsJLX(OutputStream)
         */
        public void saveAsJLX(OutputStream out) throws IOException {
                super.saveAsJLX(out);
        
                LxSaveUtils.writeInt(out, getGraphDefinitionID());
                LxSaveUtils.writeInt(out, getTrendType());
                LxSaveUtils.writeString(out, getDisplayPeriod());
                
                //save link
                //LxSaveUtils.writeString(out, getLinkTo() );
        
                LxSaveUtils.writeEndOfPart(out);
        }



	/**
	 * Returns the displayPeriod.
	 * @return String
	 */
	public String getDisplayPeriod() {
		return displayPeriod;
	}

	/**
	 * Sets the displayPeriod.
	 * @param displayPeriod The displayPeriod to set
	 */
	public void setDisplayPeriod(String displayPeriod) {
		if(!displayPeriod.equals(this.displayPeriod)) {
			this.displayPeriod = displayPeriod;
			setDirty(true);
		}
	}

	/**
	 * Returns the lastUpdated.
	 * @return Date
	 */
	public Date getLastUpdated() {
		return lastUpdated;
	}

	/**
	 * Sets the lastUpdated.
	 * @param lastUpdated The lastUpdated to set
	 */
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	/**
	 * Returns the linkTo.
	 * @return String
	 */
	public String getLinkTo() {
		return linkTo;
	}

	/**
	 * Sets the linkTo.
	 * @param linkTo The linkTo to set
	 */
	public void setLinkTo(String linkTo) {
		this.linkTo = linkTo;
	}
	
	public boolean isCopyable() {
		return false;
	}

	/**
	 * @see com.cannontech.esub.element.DrawingElement#getVersion()
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @see com.cannontech.esub.element.DrawingElement#setVersion(int)
	 */
	public void setVersion(int newVer) {
		this.version = newVer;
	}

}
 