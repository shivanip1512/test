package com.cannontech.esub.editor.element;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.GraphFuncs;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.esub.editor.Drawing;
import com.cannontech.graph.Graph;
import com.loox.jloox.LxAbstractRectangle;
import com.loox.jloox.LxLayers;
import com.loox.jloox.LxSaveUtils;
 
/**
 * Description Here
 * @author alauinger
 */
public class DynamicGraphElement extends LxAbstractRectangle implements DrawingElement {

	private static final int INVALID_GRAPH_DEFINITION = -1;
	
	private LiteGraphDefinition graphDefinition;
	private transient Drawing drawing = null;
	private String linkTo = "";
 	
	/**
	 * Returns the drawing.
	 * @return Drawing
	 */
	public Drawing getDrawing() {
		return drawing;
	}
	
	private void initialize() {
		graphDefinition = new LiteGraphDefinition(INVALID_GRAPH_DEFINITION);
		setSize(640,400);
		setLineColor(Color.white);
	}

	/**
	 * Returns the linkTo.
	 * @return String
	 */
	public String getLinkTo() {
		return linkTo;
	}

	/**
	 * Sets the drawing.
	 * @param drawing The drawing to set
	 */
	public void setDrawing(Drawing drawing) {
		this.drawing = drawing;
	}

	/**
	 * Sets the linkTo.
	 * @param linkTo The linkTo to set
	 */
	public void setLinkTo(String linkTo) {
		this.linkTo = linkTo;
	}

	/**
	 * @see com.loox.jloox.LxComponent#readFromJLX(InputStream, String)
	 */
	public void readFromJLX(InputStream in, String version) throws IOException {
		super.readFromJLX(in, version);
	
		setGraphDefinitionID(LxSaveUtils.readInt(in));
		
		//read link
		setLinkTo( LxSaveUtils.readString(in));
	
		LxSaveUtils.readEndOfPart(in);
	}

	/**
	 * @see com.loox.jloox.LxComponent#saveAsJLX(OutputStream)
	 */
	public void saveAsJLX(OutputStream out) throws IOException {
		super.saveAsJLX(out);
	
		LxSaveUtils.writeInt(out, getGraphDefinitionID());
	
		//save link
		LxSaveUtils.writeString(out, getLinkTo() );
	
		LxSaveUtils.writeEndOfPart(out);
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
		this.graphDefinition = graphDefinition;
	}

	public void setGraphDefinitionID(int id) {
		setGraphDefinition(GraphFuncs.getLiteGraphDefinition(id));	
	}


	/**
	 * @see com.loox.jloox.LxElement#paint(Graphics2D, AffineTransform)
	 * @deprecated
	 */
	protected void paint(Graphics2D arg0, AffineTransform arg1) {
		System.out.println("paint arg0, arg1");
		super.paint(arg0, arg1);
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
		System.out.println("paintHandles 1,2,3,4");
		super.paintHandles(arg0, arg1, arg2, arg3, arg4);
	}

	/**
	 * @see com.loox.jloox.LxComponent#paintHandles(Graphics2D)
	 */
	public void paintHandles(Graphics2D arg0) {
		System.out.println("paintHandles 1");
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
		System.out.println("paintStandardHandles 1,2,3,4");
		super.paintStandardHandles(arg0, arg1, arg2, arg3, arg4);
	}

	/**
	 * @see com.loox.jloox.LxComponent#postpaint(Graphics2D)
	 */
	protected void postpaint(Graphics2D arg0) {
		System.out.println("postpaint");
		super.postpaint(arg0);
	}

	/**
	 * @see com.loox.jloox.LxElement#paintElement(Graphics2D)
	 */
	protected void paintElement(Graphics2D g) {
		System.out.println("paintElement");
		super.paintElement(g);
		g.setColor(Color.WHITE);
		g.drawLine(0,0, 20, 20);
		
		//here
		com.cannontech.database.data.graph.GraphDefinition gDef = new com.cannontech.database.data.graph.GraphDefinition();
		gDef.getGraphDefinition().setGraphDefinitionID(new Integer(601));
		
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
			
			
		gDef.getGraphDefinition().setStartDate( new Date("9/6/2002") );
			gDef.getGraphDefinition().setStopDate( new Date("9/8/2002") );
						
			Graph graph = new com.cannontech.graph.Graph();
			graph.setDatabaseAlias(CtiUtilities.getDatabaseAlias());
			graph.setSize(640, 400);
			graph.setCurrentGraphDefinition(gDef);
			graph.setSeriesType( com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES);

			graph.setModelType(com.cannontech.graph.model.TrendModelType.LINE_MODEL);

			// Define the peak series....
			for (int i = 0; i < gDef.getGraphDataSeries().size(); i++)
			{
				com.cannontech.database.db.graph.GraphDataSeries gds = (com.cannontech.database.db.graph.GraphDataSeries) gDef.getGraphDataSeries().get(i);

				if ( graph.isPeakSeries( gds.getType()) )
				{
					graph.setHasPeakSeries( true );
					break;
				}
			}
			
			graph.update();			

			graph.getFreeChart().draw(g, new Rectangle(640,480));

	}

	/**
	 * @see com.loox.jloox.LxComponent#prepaint(Graphics2D)
	 */
	protected boolean prepaint(Graphics2D arg0) {
		System.out.println("prepaint");
		return super.prepaint(arg0);
	}

}
 