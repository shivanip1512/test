/*
 * Created on May 9, 2003
 * 
 * Example/Test program to generate a JLoox (.jlx) drawing 
 */
package com.cannontech.cbc.web;

import java.awt.Color;
import java.awt.Font;
import java.util.Vector;

import com.cannontech.cbc.data.CapBankDevice;
import com.cannontech.cbc.data.Feeder;
import com.cannontech.cbc.data.SubBus;
import com.cannontech.esub.PointAttributes;
import com.cannontech.esub.editor.Drawing;
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
public class CCOneLineGenerator {

	private static final Font DEFAULT_FONT = new java.awt.Font("arial", java.awt.Font.BOLD, 12);
	
	public static void main(String[] args) {
		
		System.exit(0);
	}

	public static void generateSVGFileFromSubBus(SubBus subBus) {
		
		Drawing d = new Drawing();
		LxGraph graph = d.getLxGraph();
		
		DrawingMetaElement me = d.getMetaElement();		
		me.setDrawingWidth(1024);
		me.setDrawingHeight(800);

		double halfAcross = 512.0;
		double halfDown = 400.0;
		double labelTextHorzOffset = 20.0;
		double valueTextHorzOffset = labelTextHorzOffset+20.0;
		double labelTextVertOffset = 15.0;
		double subLineStart = 20.0;
		double subLineLength = 100.0;
		double subLineLevel = subLineStart + subLineLength;
		double feederHorzLineLength = 768.0;
		double feederHorzLineStart = (1024.0 - feederHorzLineLength)/2;
		double feederHorzLineStop = feederHorzLineStart + feederHorzLineLength;
		double feederVertLineLength = 800 - subLineStart - subLineLevel;
		double feederVertLineStart = subLineLevel;
		double feederVertLineStop = feederVertLineStart + feederVertLineLength;

		//sub bus line
		LxLine subInjectionLine = new LxLine();
		subInjectionLine.setPoint1(halfAcross,subLineStart);
		subInjectionLine.setPoint2(halfAcross,subLineLevel);
		subInjectionLine.setLineColor(Color.WHITE);
		graph.add(subInjectionLine);

		//sub bus point data start
		double labelTextHorzCenter = halfAcross+labelTextHorzOffset;
		double textVertCenter = labelTextVertOffset;
		if( subBus.getPowerFactorPointId().intValue() > 0 )
		{
			StaticText subBusPFLabel = new StaticText();
			subBusPFLabel.setCenter(labelTextHorzCenter,textVertCenter);
			subBusPFLabel.setFont(DEFAULT_FONT);
			subBusPFLabel.setPaint(Color.GREEN);
			subBusPFLabel.setText("PF:");
			graph.add(subBusPFLabel);
	
			DynamicText subBusPFValue = new DynamicText();
			subBusPFValue.setCenter(labelTextHorzCenter,textVertCenter);
			subBusPFValue.setPointID(subBus.getPowerFactorPointId().intValue());
			subBusPFValue.setDisplayAttribs(PointAttributes.VALUE | PointAttributes.UOFM);
			subBusPFValue.setFont(DEFAULT_FONT);
			subBusPFValue.setPaint(Color.GREEN);
			graph.add(subBusPFValue);
			textVertCenter += labelTextVertOffset;
		}

		if( subBus.getEstimatedPowerFactorPointId().intValue() > 0 )
		{
			StaticText subBusEstPFLabel = new StaticText();
			subBusEstPFLabel.setCenter(labelTextHorzCenter,textVertCenter);
			subBusEstPFLabel.setFont(DEFAULT_FONT);
			subBusEstPFLabel.setPaint(Color.GREEN);
			subBusEstPFLabel.setText("Est PF:");
			graph.add(subBusEstPFLabel);
	
			DynamicText subBusEstPFValue = new DynamicText();
			subBusEstPFValue.setCenter(labelTextHorzCenter,textVertCenter);
			subBusEstPFValue.setPointID(subBus.getEstimatedPowerFactorPointId().intValue());
			subBusEstPFValue.setDisplayAttribs(PointAttributes.VALUE | PointAttributes.UOFM);
			subBusEstPFValue.setFont(DEFAULT_FONT);
			subBusEstPFValue.setPaint(Color.GREEN);
			graph.add(subBusEstPFValue);
			textVertCenter += labelTextVertOffset;
		}

		if( subBus.getCurrentVarLoadPointID().intValue() > 0 )
		{
			StaticText subBusKVARLabel = new StaticText();
			subBusKVARLabel.setCenter(labelTextHorzCenter,textVertCenter);
			subBusKVARLabel.setFont(DEFAULT_FONT);
			subBusKVARLabel.setPaint(Color.GREEN);
			subBusKVARLabel.setText("KVAR:");
			graph.add(subBusKVARLabel);
	
			DynamicText subBusKVARValue = new DynamicText();
			subBusKVARValue.setCenter(labelTextHorzCenter,textVertCenter);
			subBusKVARValue.setPointID(subBus.getCurrentVarLoadPointID().intValue());
			subBusKVARValue.setDisplayAttribs(PointAttributes.VALUE | PointAttributes.UOFM);
			subBusKVARValue.setFont(DEFAULT_FONT);
			subBusKVARValue.setPaint(Color.GREEN);
			graph.add(subBusKVARValue);
			textVertCenter += labelTextVertOffset;
		}

		if( subBus.getEstimatedVarLoadPointID().intValue() > 0 )
		{
			StaticText subBusEstKVARLabel = new StaticText();
			subBusEstKVARLabel.setCenter(labelTextHorzCenter,textVertCenter);
			subBusEstKVARLabel.setFont(DEFAULT_FONT);
			subBusEstKVARLabel.setPaint(Color.GREEN);
			subBusEstKVARLabel.setText("Est KVAR:");
			graph.add(subBusEstKVARLabel);
	
			DynamicText subBusEstKVARValue = new DynamicText();
			subBusEstKVARValue.setCenter(labelTextHorzCenter,textVertCenter);
			subBusEstKVARValue.setPointID(subBus.getEstimatedVarLoadPointID().intValue());
			subBusEstKVARValue.setDisplayAttribs(PointAttributes.VALUE | PointAttributes.UOFM);
			subBusEstKVARValue.setFont(DEFAULT_FONT);
			subBusEstKVARValue.setPaint(Color.GREEN);
			graph.add(subBusEstKVARValue);
			textVertCenter += labelTextVertOffset;
		}

		if( subBus.getCurrentWattLoadPointID().intValue() > 0 )
		{
			StaticText subBusKWLabel = new StaticText();
			subBusKWLabel.setCenter(labelTextHorzCenter,textVertCenter);
			subBusKWLabel.setFont(DEFAULT_FONT);
			subBusKWLabel.setPaint(Color.GREEN);
			subBusKWLabel.setText("KW:");
			graph.add(subBusKWLabel);
	
			DynamicText subBusKWValue = new DynamicText();
			subBusKWValue.setCenter(labelTextHorzCenter,textVertCenter);
			subBusKWValue.setPointID(subBus.getCurrentWattLoadPointID().intValue());
			subBusKWValue.setDisplayAttribs(PointAttributes.VALUE | PointAttributes.UOFM);
			subBusKWValue.setFont(DEFAULT_FONT);
			subBusKWValue.setPaint(Color.GREEN);
			graph.add(subBusKWValue);
			textVertCenter += labelTextVertOffset;
		}
		//sub bus point data end

		//horizontal feeder line
		LxLine feederDistributionLine = new LxLine();
		feederDistributionLine.setPoint1(feederHorzLineStart,subLineLevel);
		feederDistributionLine.setPoint2(feederHorzLineStop,subLineLevel);
		feederDistributionLine.setLineColor(Color.WHITE);
		graph.add(feederDistributionLine);

		Vector feederVector =  subBus.getCcFeeders();
		double feederSpacing = feederHorzLineLength / ((double)feederVector.size());
		for(int i=0;i<feederVector.size();i++)
		{
			Feeder currentFeeder = (Feeder)feederVector.get(i);

			//feeder lines
			double feederPosition = (feederHorzLineStart+(feederSpacing*((double)i)));
			LxLine currentFeederLine = new LxLine();
			currentFeederLine.setPoint1(feederPosition,feederVertLineStart);
			currentFeederLine.setPoint2(feederPosition,feederVertLineStop);
			currentFeederLine.setLineColor(Color.WHITE);
			graph.add(currentFeederLine);

			//feeder point data start
			labelTextHorzCenter = feederPosition+labelTextHorzOffset;
			textVertCenter = feederHorzLineStart+labelTextVertOffset;
			if( currentFeeder.getPowerFactorPointID().intValue() > 0 )
			{
		        StaticText feederPFLabel = new StaticText();
		        feederPFLabel.setCenter(labelTextHorzCenter,textVertCenter);
		        feederPFLabel.setFont(DEFAULT_FONT);
		        feederPFLabel.setPaint(Color.GREEN);
		        feederPFLabel.setText("PF:");
		        graph.add(feederPFLabel);
		
		        DynamicText feederPFValue = new DynamicText();
		        feederPFValue.setCenter(labelTextHorzCenter,textVertCenter);
		        feederPFValue.setPointID(currentFeeder.getPowerFactorPointID().intValue());
		        feederPFValue.setDisplayAttribs(PointAttributes.VALUE | PointAttributes.UOFM);
		        feederPFValue.setFont(DEFAULT_FONT);
		        feederPFValue.setPaint(Color.GREEN);
		        graph.add(feederPFValue);
		        textVertCenter += labelTextVertOffset;
			}

			if( currentFeeder.getEstimatedPowerFactorPointID().intValue() > 0 )
			{
		        StaticText feederEstPFLabel = new StaticText();
		        feederEstPFLabel.setCenter(labelTextHorzCenter,textVertCenter);
		        feederEstPFLabel.setFont(DEFAULT_FONT);
		        feederEstPFLabel.setPaint(Color.GREEN);
		        feederEstPFLabel.setText("Est PF:");
		        graph.add(feederEstPFLabel);
		
		        DynamicText feederEstPFValue = new DynamicText();
		        feederEstPFValue.setCenter(labelTextHorzCenter,textVertCenter);
		        feederEstPFValue.setPointID(currentFeeder.getEstimatedPowerFactorPointID().intValue());
		        feederEstPFValue.setDisplayAttribs(PointAttributes.VALUE | PointAttributes.UOFM);
		        feederEstPFValue.setFont(DEFAULT_FONT);
		        feederEstPFValue.setPaint(Color.GREEN);
		        graph.add(feederEstPFValue);
		        textVertCenter += labelTextVertOffset;
			}

			if( currentFeeder.getCurrentVarLoadPointID().intValue() > 0 )
			{
		        StaticText feederKVARLabel = new StaticText();
		        feederKVARLabel.setCenter(labelTextHorzCenter,textVertCenter);
		        feederKVARLabel.setFont(DEFAULT_FONT);
		        feederKVARLabel.setPaint(Color.GREEN);
		        feederKVARLabel.setText("KVAR:");
		        graph.add(feederKVARLabel);
		
		        DynamicText feederKVARValue = new DynamicText();
		        feederKVARValue.setCenter(labelTextHorzCenter,textVertCenter);
		        feederKVARValue.setPointID(currentFeeder.getCurrentVarLoadPointID().intValue());
		        feederKVARValue.setDisplayAttribs(PointAttributes.VALUE | PointAttributes.UOFM);
		        feederKVARValue.setFont(DEFAULT_FONT);
		        feederKVARValue.setPaint(Color.GREEN);
		        graph.add(feederKVARValue);
		        textVertCenter += labelTextVertOffset;
			}

			if( currentFeeder.getEstimatedVarLoadPointID().intValue() > 0 )
			{
		        StaticText feederEstKVARLabel = new StaticText();
		        feederEstKVARLabel.setCenter(labelTextHorzCenter,textVertCenter);
		        feederEstKVARLabel.setFont(DEFAULT_FONT);
		        feederEstKVARLabel.setPaint(Color.GREEN);
		        feederEstKVARLabel.setText("PF:");
		        graph.add(feederEstKVARLabel);
		
		        DynamicText feederEstKVARValue = new DynamicText();
		        feederEstKVARValue.setCenter(labelTextHorzCenter,textVertCenter);
		        feederEstKVARValue.setPointID(currentFeeder.getEstimatedVarLoadPointID().intValue());
		        feederEstKVARValue.setDisplayAttribs(PointAttributes.VALUE | PointAttributes.UOFM);
		        feederEstKVARValue.setFont(DEFAULT_FONT);
		        feederEstKVARValue.setPaint(Color.GREEN);
		        graph.add(feederEstKVARValue);
		        textVertCenter += labelTextVertOffset;
			}

			if( currentFeeder.getCurrentWattLoadPointID().intValue() > 0 )
			{
		        StaticText feederKWLabel = new StaticText();
		        feederKWLabel.setCenter(labelTextHorzCenter,textVertCenter);
		        feederKWLabel.setFont(DEFAULT_FONT);
		        feederKWLabel.setPaint(Color.GREEN);
		        feederKWLabel.setText("PF:");
		        graph.add(feederKWLabel);
		
		        DynamicText feederKWValue = new DynamicText();
		        feederKWValue.setCenter(labelTextHorzCenter,textVertCenter);
		        feederKWValue.setPointID(currentFeeder.getCurrentWattLoadPointID().intValue());
		        feederKWValue.setDisplayAttribs(PointAttributes.VALUE | PointAttributes.UOFM);
		        feederKWValue.setFont(DEFAULT_FONT);
		        feederKWValue.setPaint(Color.GREEN);
		        graph.add(feederKWValue);
		        textVertCenter += labelTextVertOffset;
			}
			//feeder point data end

			//cap banks
			Vector capBankVector =  currentFeeder.getCcCapBanks();

			if( capBankVector.size() > 0 )
			{//make sure we don't try to divide by zero
				double capBankSpacing = feederVertLineLength / capBankVector.size();
				for(int j=0;j<capBankVector.size();j++)
				{
					double capBankPosition = feederVertLineStart+(capBankSpacing*(((double)j)+1.0));
					CapBankDevice currentCapBank = (CapBankDevice)capBankVector.get(j);
					StateImage stateImage = new StateImage();
					stateImage.setCenter(feederPosition,capBankPosition);
					stateImage.setPointID(currentCapBank.getStatusPointID().intValue());
					//stateImage.setLinkTo("capbankmanualchange.html");
					graph.add(stateImage);
				}
			}
		}
		d.save("c:/Work_Files/test.jlx");

/*		
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
		d.save("c:/Work_Files/test.jlx");
		System.exit(0);
*/
	}
}
