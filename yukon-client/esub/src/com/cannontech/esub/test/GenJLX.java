/*
 * Created on May 9, 2003
 * 
 * Example/Test program to generate a JLoox (.jlx) drawing 
 */
package com.cannontech.esub.test;

import java.awt.Color;
import java.awt.Font;

import com.cannontech.esub.Drawing;
import com.cannontech.esub.PointAttributes;
import com.cannontech.esub.element.CurrentAlarmsTable;
import com.cannontech.esub.element.DrawingMetaElement;
import com.cannontech.esub.element.DynamicGraphElement;
import com.cannontech.esub.element.DynamicText;
import com.cannontech.esub.element.StateImage;
import com.cannontech.esub.element.StaticImage;
import com.cannontech.esub.element.StaticText;

import com.loox.jloox.LxGraph;
import com.loox.jloox.LxLine;

/**
 * @author alauinger
  */
public class GenJLX {

	private static final Font DEFAULT_FONT = new java.awt.Font("arial", java.awt.Font.BOLD, 12);
	
	public static void main(String[] args) {
		
		Drawing d = new Drawing();
		LxGraph graph = d.getLxGraph();
		
		DrawingMetaElement me = d.getMetaElement();		
		me.setDrawingWidth(1024);
		me.setDrawingHeight(800);
				
		LxLine line = new LxLine();		
		line.setPoint1(10.0,10.0);
		line.setPoint2(150.0,150.0);
		line.setLineColor(Color.WHITE);
		graph.add(line);		
		
		StaticText staticText = new StaticText();
		staticText.setCenter(300.0,80.0);
		staticText.setFont(DEFAULT_FONT);
		staticText.setPaint(Color.GREEN);
		staticText.setText("This is some static text, click on it to go somewhere");
		staticText.setLinkTo("somewhere.html");
		graph.add(staticText);
		
		StaticImage staticImage = new StaticImage();
		staticImage.setYukonImage("EsubLogo.gif");
		staticImage.setCenter(400.0,45.0);
		graph.add(staticImage);
		
		DynamicText dynamicText = new DynamicText();
		dynamicText.setCenter(400.0,95.0);
		dynamicText.setPointID(146);
		dynamicText.setDisplayAttribs(PointAttributes.VALUE | PointAttributes.UOFM);
		dynamicText.setFont(DEFAULT_FONT);
		dynamicText.setPaint(Color.PINK);		
		dynamicText.setLinkTo("anotherpage.html");		
		graph.add(dynamicText);
		
		StateImage stateImage = new StateImage();
		stateImage.setCenter(50,120);
		stateImage.setPointID(119);
		stateImage.setLinkTo("somedetailspage.html");
		graph.add(stateImage);
		
		DynamicGraphElement dynamicGraph = new DynamicGraphElement();
		dynamicGraph.setCenter(500, 355);
		dynamicGraph.setWidth(400);
		dynamicGraph.setHeight(300);
		dynamicGraph.setGraphDefinitionID(13);
		graph.add(dynamicGraph);
		
		CurrentAlarmsTable alarmsTable = new CurrentAlarmsTable();
		alarmsTable.setCenter(506,600);
		alarmsTable.setDeviceID(7);
		graph.add(alarmsTable);
		
		d.save("c:/temp/test.jlx");
		
		System.exit(0);
	}
}
