package com.cannontech.esub.util;

import java.io.File;

import com.cannontech.esub.editor.Drawing;
//import com.cannontech.esub.editor.element.StaticText;
import com.loox.jloox.LxComponent;

/**
 * @author aaron
 */
public class ConvertElement1 {

/*	public static void main(String[] args) throws Exception {

		visitFile(new File(args[0]));		
	System.exit(0);
		
	}
	
	public static void visitFile(File f) throws Exception {
		if(f.isDirectory()) {
			File[] list = f.listFiles();
			for(int i = 0; i < list.length; i++) {
				visitFile(list[i]);
			}
		}
		else
		if(f.getName().endsWith("jlx")) {
			convertFile(f);
		}
	}
	
	public static void convertFile(File f) throws Exception {
		System.out.println("loading " + f.getCanonicalPath());
		Drawing d = new Drawing();
		d.load(f.getCanonicalPath());
		
		LxComponent[] comps = d.getLxGraph().getComponents();
		for(int i = 0; i < comps.length; i++) {
			d.getLxGraph().remove(comps[i]);
			d.getLxGraph().add(convertComponent(comps[i]))	;
		}
		
		System.out.println("writing " + f.getCanonicalPath());
		d.save(f.getCanonicalPath());
		
	}
	
	public static LxComponent convertComponent(LxComponent comp) {
		
		LxComponent newElem = comp;
		
		if(comp instanceof com.cannontech.esub.editor.element.CurrentAlarmsTable) {
			newElem = convert((com.cannontech.esub.editor.element.CurrentAlarmsTable) comp);
		}
		else
		if(comp instanceof com.cannontech.esub.editor.element.DrawingMetaElement) {
			newElem = convert((com.cannontech.esub.editor.element.DrawingMetaElement) comp);
		}
		else
		if(comp instanceof com.cannontech.esub.editor.element.DynamicGraphElement) {
			newElem = convert((com.cannontech.esub.editor.element.DynamicGraphElement) comp);
		}
		else
		if( comp instanceof com.cannontech.esub.editor.element.DynamicText) {
			newElem = convert((com.cannontech.esub.editor.element.DynamicText) comp);
		}
		else
		if( comp instanceof com.cannontech.esub.editor.element.StateImage) {
			newElem = convert((com.cannontech.esub.editor.element.StateImage) comp);
		}
		else
		if(comp instanceof com.cannontech.esub.editor.element.StaticImage) {
			newElem = convert((com.cannontech.esub.editor.element.StaticImage) comp);
		}
		else
		if(comp instanceof com.cannontech.esub.editor.element.StaticText) {
			newElem = convert((com.cannontech.esub.editor.element.StaticText) comp);
		}
		
		return newElem;
	}
	
	public static com.cannontech.esub.element.CurrentAlarmsTable convert(com.cannontech.esub.editor.element.CurrentAlarmsTable elem) {
		com.cannontech.esub.element.CurrentAlarmsTable newElem = new com.cannontech.esub.element.CurrentAlarmsTable();
		copyComponent(elem, newElem);
		
		newElem.setDeviceID(elem.getDeviceID());
		
		return newElem;
	}
	
	public static com.cannontech.esub.element.DrawingMetaElement convert(com.cannontech.esub.editor.element.DrawingMetaElement elem) {
		com.cannontech.esub.element.DrawingMetaElement newElem = 
			new com.cannontech.esub.element.DrawingMetaElement();
			
		copyComponent(elem,newElem);
		
		newElem.setDrawingHeight(elem.getDrawingHeight());
		newElem.setDrawingWidth(elem.getDrawingWidth());
		newElem.setRoleID(elem.getRoleID());
		newElem.setLinkTo(elem.getLinkTo());
		
		return newElem;
	}
	
	public static com.cannontech.esub.element.DynamicGraphElement convert(com.cannontech.esub.editor.element.DynamicGraphElement elem) {
		com.cannontech.esub.element.DynamicGraphElement newElem = new com.cannontech.esub.element.DynamicGraphElement();
		copyComponent(elem,newElem);
		
		newElem.setGraphDefinitionID(elem.getGraphDefinitionID());
		newElem.setTrendType(elem.getTrendType());
		newElem.setDisplayPeriod(elem.getDisplayPeriod());
		newElem.setLinkTo(elem.getLinkTo());
		
		return newElem;
	}
	
	public static com.cannontech.esub.element.DynamicText convert(com.cannontech.esub.editor.element.DynamicText elem){		
		com.cannontech.esub.element.DynamicText newElem = new com.cannontech.esub.element.DynamicText();
		copyComponent(elem,newElem);
		
		newElem.setFont( new java.awt.Font( elem.getFont().getFontName(), java.awt.Font.PLAIN, elem.getFont().getSize()));
		
		newElem.setPointID(elem.getPointID());
		newElem.setDisplayAttribs(elem.getDisplayAttribs());
		newElem.setLinkTo(elem.getLinkTo());
		
		return newElem;
	}
	
	public static com.cannontech.esub.element.StateImage convert(com.cannontech.esub.editor.element.StateImage elem) {
		com.cannontech.esub.element.StateImage newElem = new com.cannontech.esub.element.StateImage();
		copyComponent(elem,newElem);
		
		newElem.setPoint(elem.getPoint());
		newElem.setLinkTo(elem.getLinkTo());
		
		return newElem;
	}
	
	public static com.cannontech.esub.element.StaticImage convert(com.cannontech.esub.editor.element.StaticImage elem) {
		com.cannontech.esub.element.StaticImage newElem = new com.cannontech.esub.element.StaticImage();
		copyComponent(elem,newElem);
		
		newElem.setYukonImage(elem.getYukonImage())	;
		newElem.setLinkTo(elem.getLinkTo());
		
		return newElem;
	}
	
	public static com.cannontech.esub.element.StaticText convert(com.cannontech.esub.editor.element.StaticText elem) {
		com.cannontech.esub.element.StaticText newElem = new com.cannontech.esub.element.StaticText();
		copyComponent(elem,newElem);
		
		newElem.setFont(elem.getFont().getFontName(),elem.getFont().getSize());
		
		newElem.setText(elem.getText());
		//newElem.setFont(elem.getFont());
//		newElem.setLineColor(elem.getLineColor());
		newElem.setPaint(elem.getPaint());
		newElem.setLinkTo(elem.getLinkTo());
		
		return newElem;
	}
	
	public static void copyComponent(LxComponent oldComp, LxComponent newComp) {
			newComp.setLocation(oldComp.getLocation());
			
			if( !(oldComp instanceof com.cannontech.esub.editor.element.StaticText) &&
				 !(oldComp instanceof com.cannontech.esub.editor.element.DynamicText) ) {
				newComp.setSize(oldComp.getSize());			
			}
	}
*/	
}
