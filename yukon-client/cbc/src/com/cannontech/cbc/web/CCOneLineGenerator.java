/*
 * Created on May 9, 2003
 * 
 * Example/Test program to generate a JLoox (.jlx) drawing 
 */
package com.cannontech.cbc.web;

import java.awt.Color;
import java.awt.Font;
import java.util.Vector;

import com.cannontech.esub.Drawing;
import com.cannontech.esub.PointAttributes;
import com.cannontech.esub.element.DrawingMetaElement;
import com.cannontech.esub.element.DynamicText;
import com.cannontech.esub.element.StateImage;
import com.cannontech.esub.element.StaticImage;
import com.cannontech.esub.element.StaticText;
import com.cannontech.esub.util.DrawingUpdater;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.SubBus;

import com.loox.jloox.LxGraph;
import com.loox.jloox.LxLine;

/**
 * @author alauinger
  */
class CCOneLineGenerator
{
    private static String DEF_CAPCONTROL_HOME = "../subs.jsp";
    private static String DEF_FEEDERS_HOME = "../feeders.jsp";
    private static String DEF_CONTROLS_HOME = "../capcontrols.jsp";

	private static final Font DEFAULT_FONT = new java.awt.Font("arial", java.awt.Font.BOLD, 12);
	
	public static Drawing generateSVGFileFromSubBus(SubBus subBus, String thisURL )
    {
		Drawing d = new Drawing();
		LxGraph graph = d.getLxGraph();
		
		DrawingMetaElement me = d.getMetaElement();		
		me.setDrawingWidth(1024);
		me.setDrawingHeight(800);

		StaticImage staticImage1 = new StaticImage();
		staticImage1.setYukonImage("CapControlLogo.gif");
		staticImage1.setX(20.0);
		staticImage1.setY(20.0);
		graph.add(staticImage1);

		StaticImage staticImage2 = new StaticImage();
		staticImage2.setYukonImage("YukonLogoBlackBackground.gif");
		staticImage2.setX(872.0);
		staticImage2.setY(20.0);
		graph.add(staticImage2);

		double halfAcross = 512.0;
		double halfDown = 400.0;
		double labelTextHorzOffset = 20.0;
		double valueTextHorzOffset = labelTextHorzOffset + 70.0;
		double labelTextVertOffset = 15.0;
		double subLineStart = 30.0;
		double subLineLength = 150.0;
		double subLineLevel = subLineStart + subLineLength;
		double feederHorzLineLength = 768.0;
		double feederHorzLineStart = (1024.0 - feederHorzLineLength)/2;
		double feederHorzLineStop = feederHorzLineStart + feederHorzLineLength;
		if( subBus.getCcFeeders().size() == 1 )
		{//special case when there is only feeder off the sub.
			feederHorzLineStop = halfAcross;
			feederHorzLineLength = feederHorzLineStop - feederHorzLineStart  ;
		}
		double feederVertLineLength = 790 - subLineStart - subLineLevel;
		double feederVertLineStart = subLineLevel;
		double feederVertLineStop = feederVertLineStart + feederVertLineLength;

		//bus transformer
		StaticImage staticImage3 = new StaticImage();
		staticImage3.setYukonImage("TransformerBY.gif");
		staticImage3.setCenter(halfAcross,subLineStart-10.0);
		graph.add(staticImage3);

		//sub bus line
		LxLine subInjectionLine = new LxLine();
		subInjectionLine.setPoint1(halfAcross,subLineStart);
		subInjectionLine.setPoint2(halfAcross,subLineLevel);
		subInjectionLine.setLineColor(Color.YELLOW);
		graph.add(subInjectionLine);

		//sub bus point data start
		double labelTextHorzLeft = halfAcross+labelTextHorzOffset;
		double valueTextHorzLeft = halfAcross+valueTextHorzOffset;
		double textVertUpper = subLineStart*1.1;
		if( subBus.getPowerFactorPointId().intValue() > 0 )
		{
			StaticText subBusPFLabel = new StaticText();
			subBusPFLabel.setX(labelTextHorzLeft);
			subBusPFLabel.setY(textVertUpper);
			subBusPFLabel.setFont(DEFAULT_FONT);
			subBusPFLabel.setPaint(Color.GREEN);
			subBusPFLabel.setText("PF:");
			graph.add(subBusPFLabel);
	
			DynamicText subBusPFValue = new DynamicText();
			subBusPFValue.setX(valueTextHorzLeft);
			subBusPFValue.setY(textVertUpper);
			subBusPFValue.setPointID(subBus.getPowerFactorPointId().intValue());
			subBusPFValue.setDisplayAttribs(PointAttributes.VALUE);
			subBusPFValue.setFont(DEFAULT_FONT);
			subBusPFValue.setPaint(Color.GREEN);
			graph.add(subBusPFValue);
			textVertUpper += labelTextVertOffset;
		}

		if( subBus.getEstimatedPowerFactorPointId().intValue() > 0 )
		{
			StaticText subBusEstPFLabel = new StaticText();
			subBusEstPFLabel.setX(labelTextHorzLeft);
			subBusEstPFLabel.setY(textVertUpper);
			subBusEstPFLabel.setFont(DEFAULT_FONT);
			subBusEstPFLabel.setPaint(Color.GREEN);
			subBusEstPFLabel.setText("Est PF:");
			graph.add(subBusEstPFLabel);
	
			DynamicText subBusEstPFValue = new DynamicText();
			subBusEstPFValue.setX(valueTextHorzLeft);
			subBusEstPFValue.setY(textVertUpper);
			subBusEstPFValue.setPointID(subBus.getEstimatedPowerFactorPointId().intValue());
			subBusEstPFValue.setDisplayAttribs(PointAttributes.VALUE);
			subBusEstPFValue.setFont(DEFAULT_FONT);
			subBusEstPFValue.setPaint(Color.GREEN);
			graph.add(subBusEstPFValue);
			textVertUpper += labelTextVertOffset;
		}

		if( subBus.getCurrentVarLoadPointID().intValue() > 0 )
		{
			StaticText subBusKVARLabel = new StaticText();
			subBusKVARLabel.setX(labelTextHorzLeft);
			subBusKVARLabel.setY(textVertUpper);
			subBusKVARLabel.setFont(DEFAULT_FONT);
			subBusKVARLabel.setPaint(Color.GREEN);
			subBusKVARLabel.setText("KVAR:");
			graph.add(subBusKVARLabel);
	
			DynamicText subBusKVARValue = new DynamicText();
			subBusKVARValue.setX(valueTextHorzLeft);
			subBusKVARValue.setY(textVertUpper);
			subBusKVARValue.setPointID(subBus.getCurrentVarLoadPointID().intValue());
			subBusKVARValue.setDisplayAttribs(PointAttributes.VALUE);
			subBusKVARValue.setFont(DEFAULT_FONT);
			subBusKVARValue.setPaint(Color.GREEN);
			graph.add(subBusKVARValue);
			textVertUpper += labelTextVertOffset;
		}

		if( subBus.getEstimatedVarLoadPointID().intValue() > 0 )
		{
			StaticText subBusEstKVARLabel = new StaticText();
			subBusEstKVARLabel.setX(labelTextHorzLeft);
			subBusEstKVARLabel.setY(textVertUpper);
			subBusEstKVARLabel.setFont(DEFAULT_FONT);
			subBusEstKVARLabel.setPaint(Color.GREEN);
			subBusEstKVARLabel.setText("Est KVAR:");
			graph.add(subBusEstKVARLabel);
	
			DynamicText subBusEstKVARValue = new DynamicText();
			subBusEstKVARValue.setX(valueTextHorzLeft);
			subBusEstKVARValue.setY(textVertUpper);
			subBusEstKVARValue.setPointID(subBus.getEstimatedVarLoadPointID().intValue());
			subBusEstKVARValue.setDisplayAttribs(PointAttributes.VALUE);
			subBusEstKVARValue.setFont(DEFAULT_FONT);
			subBusEstKVARValue.setPaint(Color.GREEN);
			graph.add(subBusEstKVARValue);
			textVertUpper += labelTextVertOffset;
		}

		if( subBus.getCurrentWattLoadPointID().intValue() > 0 )
		{
			StaticText subBusKWLabel = new StaticText();
			subBusKWLabel.setX(labelTextHorzLeft);
			subBusKWLabel.setY(textVertUpper);
			subBusKWLabel.setFont(DEFAULT_FONT);
			subBusKWLabel.setPaint(Color.GREEN);
			subBusKWLabel.setText("KW:");
			graph.add(subBusKWLabel);
	
			DynamicText subBusKWValue = new DynamicText();
			subBusKWValue.setX(valueTextHorzLeft);
			subBusKWValue.setY(textVertUpper);
			subBusKWValue.setPointID(subBus.getCurrentWattLoadPointID().intValue());
			subBusKWValue.setDisplayAttribs(PointAttributes.VALUE);
			subBusKWValue.setFont(DEFAULT_FONT);
			subBusKWValue.setPaint(Color.GREEN);
			graph.add(subBusKWValue);
			textVertUpper += labelTextVertOffset;
		}

		if( subBus.getDailyOperationsAnalogPointId().intValue() > 0 )
		{
			StaticText subBusDailyOpsLabel = new StaticText();
			subBusDailyOpsLabel.setX(labelTextHorzLeft);
			subBusDailyOpsLabel.setY(textVertUpper);
			subBusDailyOpsLabel.setFont(DEFAULT_FONT);
			subBusDailyOpsLabel.setPaint(Color.GREEN);
			subBusDailyOpsLabel.setText("Ops:");
			graph.add(subBusDailyOpsLabel);

			DynamicText subBusDailyOpsValue = new DynamicText();
			subBusDailyOpsValue.setX(valueTextHorzLeft);
			subBusDailyOpsValue.setY(textVertUpper);
			subBusDailyOpsValue.setPointID(subBus.getDailyOperationsAnalogPointId().intValue());
			subBusDailyOpsValue.setDisplayAttribs(PointAttributes.VALUE);
			subBusDailyOpsValue.setFont(DEFAULT_FONT);
			subBusDailyOpsValue.setPaint(Color.GREEN);
			graph.add(subBusDailyOpsValue);
			textVertUpper += labelTextVertOffset;
		}
		//sub bus point data end

		//label for the sub bus info like name and control method, i.e. Bus Optimized, Individual Feeder, etc.
		double subInfoHorzPosition = feederHorzLineStart+((halfAcross-feederHorzLineStart)/2);
		double subInfoVertOffset = 30.0;

		StaticText controlMethodString = new StaticText();
		graph.add(controlMethodString);
		controlMethodString.setFont(new java.awt.Font("arial", java.awt.Font.BOLD, 14));
		controlMethodString.setPaint(Color.WHITE);
		controlMethodString.setText("Control Method: "+subBus.getControlMethod());

		StaticText nameString = new StaticText();
		graph.add(nameString);
		nameString.setFont(new java.awt.Font("arial", java.awt.Font.BOLD, 16));
		nameString.setPaint(Color.WHITE);
		nameString.setText("Sub Bus: "+subBus.getCcName());

/*
		StaticImage allSubsLinkBackButton = new StaticImage();
		allSubsLinkBackButton.setYukonImage("SubListButton.gif");
		allSubsLinkBackButton.setCenter(1000-subInfoHorzPosition,subLineLevel-(subInfoVertOffset*1.5));
		allSubsLinkBackButton.setLinkTo(DEF_CAPCONTROL_HOME);
		graph.add(allSubsLinkBackButton);

        StaticImage feedersLinkBackButton = new StaticImage();
        feedersLinkBackButton.setYukonImage("FeederListButton.gif");
        feedersLinkBackButton.setCenter(1130-subInfoHorzPosition,subLineLevel-(subInfoVertOffset*1.5));
        feedersLinkBackButton.setLinkTo(DEF_FEEDERS_HOME + "?paoID=" + subBus.getCcId() );
        graph.add(feedersLinkBackButton);
*/

		DrawingUpdater updater = new DrawingUpdater(d);
		updater.updateDrawing();
		controlMethodString.setCenter(subInfoHorzPosition,subLineLevel-subInfoVertOffset);
		nameString.setCenter(subInfoHorzPosition,subLineLevel-subInfoVertOffset-subInfoVertOffset);


		Vector feederVector =  subBus.getCcFeeders();
		double feederSpacing = feederHorzLineLength / ((double)feederVector.size()-1);
		if( feederVector.size() == 1 )
		{
			feederSpacing = 0.0;
		}

		//horizontal feeder line
		LxLine feederDistributionLine = new LxLine();
		feederDistributionLine.setPoint1(feederHorzLineStart,subLineLevel);
		feederDistributionLine.setPoint2(feederHorzLineStop,subLineLevel);
		feederDistributionLine.setLineColor(Color.YELLOW);
		graph.add(feederDistributionLine);

		for(int i=0;i<feederVector.size();i++)
		{
			Feeder currentFeeder = (Feeder)feederVector.get(i);

			//feeder lines
			double feederPosition = (feederHorzLineStart+(feederSpacing*((double)i)));
			LxLine currentFeederLine = new LxLine();
			currentFeederLine.setPoint1(feederPosition,feederVertLineStart);
			currentFeederLine.setPoint2(feederPosition,feederVertLineStop+40.0);
			currentFeederLine.setLineArrow(LxLine.ARROW_END);
			currentFeederLine.setLineColor(Color.YELLOW);
			graph.add(currentFeederLine);

			//feeder point data start
			labelTextHorzLeft = feederPosition+labelTextHorzOffset;
			valueTextHorzLeft = feederPosition+valueTextHorzOffset;
			textVertUpper = subLineLevel+labelTextVertOffset;
			if( currentFeeder.getPowerFactorPointID().intValue() > 0 )
			{
		        StaticText feederPFLabel = new StaticText();
		        feederPFLabel.setX(labelTextHorzLeft);
				feederPFLabel.setY(textVertUpper);
		        feederPFLabel.setFont(DEFAULT_FONT);
		        feederPFLabel.setPaint(Color.GREEN);
		        feederPFLabel.setText("PF:");
		        graph.add(feederPFLabel);
		
		        DynamicText feederPFValue = new DynamicText();
		        feederPFValue.setX(valueTextHorzLeft);
				feederPFValue.setY(textVertUpper);
		        feederPFValue.setPointID(currentFeeder.getPowerFactorPointID().intValue());
		        feederPFValue.setDisplayAttribs(PointAttributes.VALUE);
		        feederPFValue.setFont(DEFAULT_FONT);
		        feederPFValue.setPaint(Color.GREEN);
		        graph.add(feederPFValue);
		        textVertUpper += labelTextVertOffset;
			}

			if( currentFeeder.getEstimatedPowerFactorPointID().intValue() > 0 )
			{
		        StaticText feederEstPFLabel = new StaticText();
		        feederEstPFLabel.setX(labelTextHorzLeft);
				feederEstPFLabel.setY(textVertUpper);
		        feederEstPFLabel.setFont(DEFAULT_FONT);
		        feederEstPFLabel.setPaint(Color.GREEN);
		        feederEstPFLabel.setText("Est PF:");
		        graph.add(feederEstPFLabel);
		
		        DynamicText feederEstPFValue = new DynamicText();
		        feederEstPFValue.setX(valueTextHorzLeft);
				feederEstPFValue.setY(textVertUpper);
		        feederEstPFValue.setPointID(currentFeeder.getEstimatedPowerFactorPointID().intValue());
		        feederEstPFValue.setDisplayAttribs(PointAttributes.VALUE);
		        feederEstPFValue.setFont(DEFAULT_FONT);
		        feederEstPFValue.setPaint(Color.GREEN);
		        graph.add(feederEstPFValue);
		        textVertUpper += labelTextVertOffset;
			}

			if( currentFeeder.getCurrentVarLoadPointID().intValue() > 0 )
			{
		        StaticText feederKVARLabel = new StaticText();
		        feederKVARLabel.setX(labelTextHorzLeft);
				feederKVARLabel.setY(textVertUpper);
		        feederKVARLabel.setFont(DEFAULT_FONT);
		        feederKVARLabel.setPaint(Color.GREEN);
		        feederKVARLabel.setText("KVAR:");
		        graph.add(feederKVARLabel);
		
		        DynamicText feederKVARValue = new DynamicText();
		        feederKVARValue.setX(valueTextHorzLeft);
				feederKVARValue.setY(textVertUpper);
		        feederKVARValue.setPointID(currentFeeder.getCurrentVarLoadPointID().intValue());
		        feederKVARValue.setDisplayAttribs(PointAttributes.VALUE);
		        feederKVARValue.setFont(DEFAULT_FONT);
		        feederKVARValue.setPaint(Color.GREEN);
		        graph.add(feederKVARValue);
		        textVertUpper += labelTextVertOffset;
			}

			if( currentFeeder.getEstimatedVarLoadPointID().intValue() > 0 )
			{
		        StaticText feederEstKVARLabel = new StaticText();
		        feederEstKVARLabel.setX(labelTextHorzLeft);
				feederEstKVARLabel.setY(textVertUpper);
		        feederEstKVARLabel.setFont(DEFAULT_FONT);
		        feederEstKVARLabel.setPaint(Color.GREEN);
		        feederEstKVARLabel.setText("Est KVAR:");
		        graph.add(feederEstKVARLabel);
		
		        DynamicText feederEstKVARValue = new DynamicText();
		        feederEstKVARValue.setX(valueTextHorzLeft);
				feederEstKVARValue.setY(textVertUpper);
		        feederEstKVARValue.setPointID(currentFeeder.getEstimatedVarLoadPointID().intValue());
		        feederEstKVARValue.setDisplayAttribs(PointAttributes.VALUE);
		        feederEstKVARValue.setFont(DEFAULT_FONT);
		        feederEstKVARValue.setPaint(Color.GREEN);
		        graph.add(feederEstKVARValue);
		        textVertUpper += labelTextVertOffset;
			}

			if( currentFeeder.getCurrentWattLoadPointID().intValue() > 0 )
			{
		        StaticText feederKWLabel = new StaticText();
		        feederKWLabel.setX(labelTextHorzLeft);
				feederKWLabel.setY(textVertUpper);
		        feederKWLabel.setFont(DEFAULT_FONT);
		        feederKWLabel.setPaint(Color.GREEN);
		        feederKWLabel.setText("KW:");
		        graph.add(feederKWLabel);
		
		        DynamicText feederKWValue = new DynamicText();
		        feederKWValue.setX(valueTextHorzLeft);
				feederKWValue.setY(textVertUpper);
		        feederKWValue.setPointID(currentFeeder.getCurrentWattLoadPointID().intValue());
		        feederKWValue.setDisplayAttribs(PointAttributes.VALUE);
		        feederKWValue.setFont(DEFAULT_FONT);
		        feederKWValue.setPaint(Color.GREEN);
		        graph.add(feederKWValue);
		        textVertUpper += labelTextVertOffset;
			}

			if( currentFeeder.getDailyOperationsAnalogPointID().intValue() > 0 )
			{
				StaticText feederDailyOpsLabel = new StaticText();
				feederDailyOpsLabel.setX(labelTextHorzLeft);
				feederDailyOpsLabel.setY(textVertUpper);
				feederDailyOpsLabel.setFont(DEFAULT_FONT);
				feederDailyOpsLabel.setPaint(Color.GREEN);
				feederDailyOpsLabel.setText("Ops:");
				graph.add(feederDailyOpsLabel);
	
				DynamicText feederDailyOpsValue = new DynamicText();
				feederDailyOpsValue.setX(valueTextHorzLeft);
				feederDailyOpsValue.setY(textVertUpper);
				feederDailyOpsValue.setPointID(currentFeeder.getDailyOperationsAnalogPointID().intValue());
				feederDailyOpsValue.setDisplayAttribs(PointAttributes.VALUE);
				feederDailyOpsValue.setFont(DEFAULT_FONT);
				feederDailyOpsValue.setPaint(Color.GREEN);
				graph.add(feederDailyOpsValue);
				textVertUpper += labelTextVertOffset;
			}
			//feeder point data end

			//cap banks
			Vector capBankVector =  currentFeeder.getCcCapBanks();

			double capBankNameHorzOffset = 15.0;
			double capBankNameVertOffset = 5.0;

			if( capBankVector.size() > 1 )
			{//make sure we don't try to divide by zero
				double capBanksStart = feederVertLineStart+140.0;
				double capBankSpacing = (feederVertLineStop-capBanksStart) / (capBankVector.size()-1);
				for(int j=0;j<capBankVector.size();j++)
				{
					double capBankPosition = capBanksStart+(capBankSpacing*((double)j));
					CapBankDevice currentCapBank = (CapBankDevice)capBankVector.get(j);

					LxLine capacitorAndGroundLine = new LxLine();
					capacitorAndGroundLine.setPoint1(feederPosition,capBankPosition);
					capacitorAndGroundLine.setPoint2(feederPosition-30.0,capBankPosition);
					capacitorAndGroundLine.setLineColor(Color.YELLOW);
					graph.add(capacitorAndGroundLine);

					StaticImage capacitorImage = new StaticImage();
					capacitorImage.setYukonImage("Capacitor.gif");
					capacitorImage.setCenter(feederPosition-30.0,capBankPosition+10.0);
					graph.add(capacitorImage);

					StaticImage groundImage = new StaticImage();
					groundImage.setYukonImage("Ground.gif");
					groundImage.setCenter(feederPosition-30.0,capBankPosition+30.0);
					graph.add(groundImage);

					StateImage stateImage = new StateImage();
					stateImage.setPointID(currentCapBank.getStatusPointID().intValue());
					graph.add(stateImage);
					updater.updateDrawing();
					stateImage.setCenter(feederPosition,capBankPosition);
                    stateImage.setLinkTo( DEF_CONTROLS_HOME +
                            "?paoID=" + currentCapBank.getCcId() +
                            "&lastSubID=" + subBus.getCcId() +
                            "&controlType=" + CapControlWebAnnex.CMD_CAPBANK +
                            "&redirectURL=" + thisURL);                

					StaticText capBankNameString = new StaticText();
					capBankNameString.setX(feederPosition+capBankNameHorzOffset);
					capBankNameString.setY(capBankPosition-capBankNameVertOffset-10.0);
					capBankNameString.setFont(DEFAULT_FONT);
					capBankNameString.setPaint(Color.LIGHT_GRAY);
					capBankNameString.setText(currentCapBank.getCcName());
					graph.add(capBankNameString);

					StaticText capBankSizeString = new StaticText();
					capBankSizeString.setX(feederPosition+capBankNameHorzOffset);
					capBankSizeString.setY(capBankPosition+capBankNameVertOffset);
					capBankSizeString.setFont(DEFAULT_FONT);
					capBankSizeString.setPaint(Color.LIGHT_GRAY);
					capBankSizeString.setText("Size: " + Integer.toString(currentCapBank.getBankSize().intValue()) + " KVAR");
					graph.add(capBankSizeString);
				}
			}
			else if( capBankVector.size() == 1 )
			{
				double capBankPosition = feederVertLineStop;
				CapBankDevice currentCapBank = (CapBankDevice)capBankVector.get(0);

				LxLine capacitorAndGroundLine = new LxLine();
				capacitorAndGroundLine.setPoint1(feederPosition,capBankPosition);
				capacitorAndGroundLine.setPoint2(feederPosition-30.0,capBankPosition);
				capacitorAndGroundLine.setLineColor(Color.YELLOW);
				graph.add(capacitorAndGroundLine);

				StaticImage capacitorImage = new StaticImage();
				capacitorImage.setYukonImage("Capacitor.gif");
				capacitorImage.setCenter(feederPosition-30.0,capBankPosition+10.0);
				graph.add(capacitorImage);

				StaticImage groundImage = new StaticImage();
				groundImage.setYukonImage("Ground.gif");
				groundImage.setCenter(feederPosition-30.0,capBankPosition+30.0);
				graph.add(groundImage);

				StateImage stateImage = new StateImage();
				stateImage.setPointID(currentCapBank.getStatusPointID().intValue());
				graph.add(stateImage);
				updater.updateDrawing();
				stateImage.setCenter(feederPosition,capBankPosition);
                stateImage.setLinkTo( DEF_CONTROLS_HOME +
                        "?paoID=" + currentCapBank.getCcId() +
                        "&lastSubID=" + subBus.getCcId() +
                        "&controlType=" + CapControlWebAnnex.CMD_CAPBANK +
                        "&redirectURL=" + thisURL);                
                
				StaticText capBankNameString = new StaticText();
				capBankNameString.setX(feederPosition+capBankNameHorzOffset);
				capBankNameString.setY(capBankPosition-capBankNameVertOffset-10.0);
				capBankNameString.setFont(DEFAULT_FONT);
				capBankNameString.setPaint(Color.LIGHT_GRAY);
				capBankNameString.setText(currentCapBank.getCcName());
				graph.add(capBankNameString);

				StaticText capBankSizeString = new StaticText();
				capBankSizeString.setX(feederPosition+capBankNameHorzOffset);
				capBankSizeString.setY(capBankPosition+capBankNameVertOffset);
				capBankSizeString.setFont(DEFAULT_FONT);
				capBankSizeString.setPaint(Color.LIGHT_GRAY);
				capBankSizeString.setText("Size: " + Integer.toString(currentCapBank.getBankSize().intValue()) + " KVAR");
				graph.add(capBankSizeString);
			}
		}
		
		return d;
	}
}
