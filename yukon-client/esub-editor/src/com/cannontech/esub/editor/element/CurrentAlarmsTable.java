package com.cannontech.esub.editor.element;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.cannontech.esub.editor.Drawing;
import com.cannontech.esub.model.PointAlarmTableModel;
import com.cannontech.esub.table.Table;
import com.loox.jloox.LxAbstractRectangle;
import com.loox.jloox.LxSaveUtils;

/**
 * @author alauinger
 */
public class CurrentAlarmsTable extends LxAbstractRectangle implements DrawingElement {	
	private static final String TABLE_TITLE = "Current Alarms";
	private static final int DEFAULT_WIDTH = 1000;
	private static final int DEFAULT_HEIGHT = 600;

	private transient Drawing drawing = null;
	private String linkTo = null;
	
	private Properties props = new Properties();

	private Table table;
	
	public CurrentAlarmsTable() {
		getTable().setTitle(TABLE_TITLE);
		
		PointAlarmTableModel model = new PointAlarmTableModel();	
		model.refresh();
		getTable().setModel(model);
		
		setSize(DEFAULT_WIDTH,DEFAULT_HEIGHT);
		setPaint(Color.WHITE); 
		setLineThickness(1);
		setLineColor(Color.WHITE);	
	}
	
	
		/**
	 * @see com.cannontech.esub.editor.element.DrawingElement#getDrawing()
	 */
	public Drawing getDrawing() {
		return drawing;
	}

	/**
	 * @see com.cannontech.esub.editor.element.DrawingElement#setDrawing(Drawing)
	 */
	public void setDrawing(Drawing d) {
		drawing = d;
	}

	protected void paintElement(Graphics2D g) {					
		super.paintElement(g);
		
		double w = getWidth();
		double h = getHeight();
		g.translate( (w/2.0)*-1, (h/2.0)*-1.0);
		getTable().draw(g, new Rectangle((int) w,(int) h));
	} 

	/**
	 * Returns the alarmTable.
	 * @return Table
	 */
	public Table getTable() {
		if(table == null) {
			table = new Table();
		}
		return table;
	}

	/**
	 * Sets the alarmTable.
	 * @param alarmTable The alarmTable to set
	 */
	public void setTable(Table alarmTable) {
		this.table = alarmTable;
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
    		
        setDeviceID(LxSaveUtils.readInt(in));    
		LxSaveUtils.readEndOfPart(in);
    }


	/**
     * @see com.loox.jloox.LxComponent#saveAsJLX(OutputStream)
     */
    public void saveAsJLX(OutputStream out) throws IOException {
        super.saveAsJLX(out);    
        LxSaveUtils.writeInt(out,getDeviceID());
   		LxSaveUtils.writeEndOfPart(out);
    }



	/**
	 * Returns the deviceID.
	 * @return int
	 */
	public int getDeviceID() {
		return ((PointAlarmTableModel) getTable().getModel()).getDeviceID();
	}

	/**
	 * Sets the deviceID.
	 * @param deviceID The deviceID to set
	 */
	public void setDeviceID(int deviceID) {
		((PointAlarmTableModel) getTable().getModel()).setDeviceID(deviceID);
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

}
