package com.cannontech.esub.editor.element;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.cannontech.database.cache.functions.GraphFuncs;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.esub.editor.Drawing;
import com.loox.jloox.LxAbstractRectangle;
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
}
 