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

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.GraphFuncs;
import com.cannontech.database.data.graph.GraphDefinition;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.element.persist.PersistDynamicGraphElement;
import com.cannontech.graph.Graph;
import com.cannontech.graph.model.TrendModelType;
import com.cannontech.util.ServletUtil;
import com.loox.jloox.LxAbstractRectangle;
 
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
	
	private static final String ELEMENT_ID = "dynamicGraph";
		
	private static final int CURRENT_VERSION = 1;
	private static final int INVALID_GRAPH_DEFINITION = -1;
	private static final Dimension DEFAULT_SIZE = new Dimension(640,480);
	private static final Color DEFAULT_COLOR = Color.white;
		
	private Graph ctiGraph;
	private Date currentStartDate;
	private Date lastUpdated;
	private boolean dirty; //flag to determine if update is required
	
	//persistent fields	
	private int graphDefinitionID = -1;
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
		return GraphFuncs.getLiteGraphDefinition(getGraphDefinitionID());
	}

	public int getGraphDefinitionID() {
		return graphDefinitionID;
	}
	
	/**
	 * Sets the graphDefinition.
	 * @param graphDefinition The graphDefinition to set
	 */
	public void setGraphDefinition(LiteGraphDefinition graphDefinition) {
		if(graphDefinitionID == -1 ||
			graphDefinitionID != graphDefinition.getLiteID()) {		
			setGraphDefinitionID(graphDefinition.getLiteID());	
			setDirty(true);
		}
	}

	public void setGraphDefinitionID(int id) {
		graphDefinitionID = id;
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
		LiteGraphDefinition lGDef = GraphFuncs.getLiteGraphDefinition(getGraphDefinitionID());		
		if(lGDef == null) {
			CTILogger.info(getClass().getName() + "::updateGraph() - Couldn't find a graph in the cache with graphdefinitionid: " + getGraphDefinitionID() + ", perhaps it has been deleted?");
			return;
		}

		GraphDefinition gDef = (GraphDefinition) LiteFactory.createDBPersistent(lGDef);
		
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
			Date start = ServletUtil.getStartingDateOfInterval(getCurrentStartDate(),getDisplayPeriod());
			Date end = ServletUtil.getEndingDateOfInterval(getCurrentStartDate(),getDisplayPeriod()); 
			//System.out.println(start);
			//System.out.println(end);
					
			//gDef.getGraphDefinition().setStartDate(start);
			//gDef.getGraphDefinition().setStopDate(end);
				
			Graph graph = getCTIGraph();
			graph.setStartDate(getCurrentStartDate());
			graph.setPeriod(getDisplayPeriod());
			
			graph.setSize((int) getWidth(), (int) getHeight());
			graph.setGraphDefinition(gDef);

			graph.setViewType(getTrendType());
						
			graph.setUpdateTrend(true);
			graph.update();
		
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
        		PersistDynamicGraphElement.getInstance().readFromJLX(this,in);
                resetDisplayRange();
        }

        /**
         * @see com.loox.jloox.LxComponent#saveAsJLX(OutputStream)
         */
        public void saveAsJLX(OutputStream out) throws IOException {
                super.saveAsJLX(out);
				PersistDynamicGraphElement.getInstance().saveAsJLX(this,out);        
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

	public String getElementID() {
		return ELEMENT_ID;
	}
}
 