package com.cannontech.esub.editor.element;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.cannontech.esub.editor.Drawing;
import com.cannontech.esub.table.Table;
import com.loox.jloox.LxAbstractRectangle;
import com.loox.jloox.LxSaveUtils;

/**
 * @author alauinger
 */
public class TableElement extends LxAbstractRectangle implements DrawingElement {

	private transient Drawing drawing = null;
	private Properties props = new Properties();

	private Table table;
	
	private boolean doInit = true;
		
	public TableElement() {
		setSize(600,300);
		setPaint(Color.WHITE);		
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

}
