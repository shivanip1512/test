/*
 * Created on Feb 4, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.analysis;

import java.awt.BasicStroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Date;

import org.jfree.report.Element;
import org.jfree.report.ElementAlignment;
import org.jfree.report.GroupFooter;
import org.jfree.report.GroupHeader;
import org.jfree.report.ItemBand;
import org.jfree.report.ReportFooter;
import org.jfree.report.ReportHeader;
import org.jfree.report.ShapeElement;
import org.jfree.report.elementfactory.DateFieldElementFactory;
import org.jfree.report.elementfactory.LabelElementFactory;
import org.jfree.report.elementfactory.NumberFieldElementFactory;
import org.jfree.report.elementfactory.StaticShapeElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;
import org.jfree.report.style.BandStyleSheet;
import org.jfree.report.style.ElementStyleSheet;
import org.jfree.report.style.FontDefinition;
import org.jfree.ui.FloatDimension;

import com.cannontech.analysis.tablemodel.ReportModelBase;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ReportFactory
{
	public static final String NAME_LABEL = " Label";
	public static final String NAME_HIDDEN = " Hidden";
	public static final String NAME_GROUP = " Group";
	public static final String NAME_ELEMENT = " Element";
	public static final String NAME_GROUP_ELEMENT = " Group Element";
	public static final String NAME_GROUP_LABEL_ELEMENT = " Group Label Element";

	public static final String DEFAULT_FONT = "Serif";
	
	public static final FontDefinition REPORT_HEADER_BAND_FONT = new FontDefinition(DEFAULT_FONT, 20, true, false, false, false); 
	public static final FontDefinition REPORT_FOOTER_BAND_FONT = new FontDefinition(DEFAULT_FONT, 12, true, false, false, false); 
	public static final FloatDimension REPORT_STYLE_DIMENSION = new FloatDimension(0, 48);

	public static final FontDefinition GROUP_HEADER_BAND_FONT = new FontDefinition(DEFAULT_FONT, 10, true, false, false, false); 
	public static final FloatDimension GROUP_HEADER_STYLE_DIMENSION = new FloatDimension(0, 30);
	
	public static final FontDefinition ITEM_BAND_FONT = new FontDefinition(DEFAULT_FONT, 10, false, false, false, false);
	public static final FloatDimension ITEM_BAND_STYLE_DIMENSION = new FloatDimension(0, 10);
	
	public static GroupHeader createGroupHeaderDefault()
	{
		GroupHeader header = new GroupHeader();
		header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, GROUP_HEADER_STYLE_DIMENSION);
		header.getBandDefaults().setFontDefinitionProperty(GROUP_HEADER_BAND_FONT);
		header.getStyle().setStyleProperty(BandStyleSheet.REPEAT_HEADER, Boolean.TRUE);
		header.setDynamicContent(true);
		
		return header;
	}
	
	public static GroupFooter createGroupFooterDefault()
	{
		GroupFooter footer = new GroupFooter();
		footer.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, GROUP_HEADER_STYLE_DIMENSION);
		footer.getBandDefaults().setFontDefinitionProperty(GROUP_HEADER_BAND_FONT);
		footer.setDynamicContent(true);
		return footer;
	}
	
	public static ReportFooter createReportFooterDefault()
	{
		ReportFooter footer = new ReportFooter();
		footer.getStyle().setStyleProperty( ElementStyleSheet.MINIMUMSIZE, REPORT_STYLE_DIMENSION);
		footer.getBandDefaults().setFontDefinitionProperty(REPORT_FOOTER_BAND_FONT);
		return footer;
	}

	public static ReportHeader createReportHeaderDefault()
	{
		ReportHeader header = new ReportHeader();
		header.getStyle().setStyleProperty( ElementStyleSheet.MINIMUMSIZE, REPORT_STYLE_DIMENSION);
		header.getBandDefaults().setFontDefinitionProperty(REPORT_HEADER_BAND_FONT);
		return header;
	}
	
	public static ShapeElement createBasicLine(String name, float strokeWidth, float posY)
	{
		return StaticShapeElementFactory.createLineShapeElement(name, null, new BasicStroke(strokeWidth), new Line2D.Float(0, posY, 0, posY));
	}
	
	public static ItemBand createItemBandDefault()
	{
		ItemBand items = new ItemBand();
		items.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, ITEM_BAND_STYLE_DIMENSION);
		items.getBandDefaults().setFontDefinitionProperty(ITEM_BAND_FONT);
		items.setDynamicContent(true);
		return items;
	}
	/**
	 * 
	 */
	public static LabelElementFactory createGroupLabelElementDefault(ReportModelBase model, int index)
	{
		return createGroupLabelElementDefault(model.getColumnName(index), model.getColumnProperties(index).getPositionX(), model.getColumnProperties(index).getPositionY(), model.getColumnProperties(index).getWidth());
	}
	/**
	 * 
	 */
	public static LabelElementFactory createGroupLabelElementDefault(String name, float posX, float posY, float width)
	{
		LabelElementFactory factory = new LabelElementFactory();
		factory.setText(name);
		factory.setAbsolutePosition(new Point2D.Float(posX, posY));
		factory.setMinimumSize(new FloatDimension(width, 18));
		factory.setDynamicHeight(Boolean.TRUE);
		factory.setHorizontalAlignment(ElementAlignment.LEFT);
		factory.setVerticalAlignment(ElementAlignment.BOTTOM);
		factory.setBold(new Boolean(true));
		factory.setName( name  + NAME_GROUP_LABEL_ELEMENT);
		
		return factory;
	}
	
	public static TextFieldElementFactory createGroupTextFieldElementDefault(ReportModelBase model, int index)
	{
		TextFieldElementFactory factory = new TextFieldElementFactory();
		
		if( model.getColumnClass(index).equals(Integer.class) || model.getColumnClass(index).equals(Double.class))
			factory = createNumberFieldElementDefault(model.getColumnName(index), model.getColumnProperties(index));
			
		else if( model.getColumnClass(index).equals(Date.class))
			factory = createDateFieldElementDefault(model.getColumnName(index), model.getColumnProperties(index));
			
		else	//make it a text field on all other occasions.
			factory = createTextFieldElementDefault(model.getColumnName(index), model.getColumnProperties(index));
		
		factory = setGroupTextFieldElementDefaults(factory, model.getColumnName(index), model.getColumnProperties(index));
		return factory;
	}


	/**
	 * 
	 */
	public static LabelElementFactory createLabelElementDefault(ReportModelBase model, int index)
	{
		return createLabelElementDefault(model.getColumnName(index), model.getColumnProperties(index).getPositionX(), model.getColumnProperties(index).getPositionY(), model.getColumnProperties(index).getWidth());
	}
	/**
	 * 
	 */
	public static LabelElementFactory createLabelElementDefault(String name, float posX, float posY, float width)
	{
		LabelElementFactory factory = new LabelElementFactory();
		factory.setText(name);
		factory.setAbsolutePosition(new Point2D.Float(posX, posY));
		factory.setMinimumSize(new FloatDimension(width, 12));
		factory.setDynamicHeight(Boolean.TRUE);
		factory.setHorizontalAlignment(ElementAlignment.LEFT);
		factory.setVerticalAlignment(ElementAlignment.BOTTOM);
		factory.setName( name  + NAME_LABEL);
		
		return factory;
	}

	public static TextFieldElementFactory createTextFieldElementDefault(ReportModelBase model, int index)
	{
		
		TextFieldElementFactory factory = new TextFieldElementFactory();
		if( model.getColumnClass(index).equals(Integer.class) || model.getColumnClass(index).equals(Double.class))
			factory = createNumberFieldElementDefault(model.getColumnName(index), model.getColumnProperties(index));
			
		else if( model.getColumnClass(index).equals(Date.class))
			factory = createDateFieldElementDefault(model.getColumnName(index), model.getColumnProperties(index));
		
		else	//make it a text field on all other occasions.
			factory = createTextFieldElementDefault(model.getColumnName(index), model.getColumnProperties(index));
		
		factory = setTextFieldElementDefaults(factory, model.getColumnName(index), model.getColumnProperties(index));
		return factory;
	}

	private static TextFieldElementFactory createTextFieldElementDefault(String name, ColumnProperties props)
	{
		TextFieldElementFactory factory = new TextFieldElementFactory();
		factory = setTextFieldElementDefaults(factory, name, props);
		return factory;
	}

	private static NumberFieldElementFactory createNumberFieldElementDefault(String name, ColumnProperties props)
	{
		NumberFieldElementFactory factory = new NumberFieldElementFactory();
		factory.setFormatString(props.getValueFormat());
		factory = (NumberFieldElementFactory)setTextFieldElementDefaults(factory, name, props);
		return factory;
	}

	private static DateFieldElementFactory createDateFieldElementDefault(String name, ColumnProperties props)
	{
		DateFieldElementFactory factory = new DateFieldElementFactory();
		factory.setVerticalAlignment(ElementAlignment.BOTTOM);
		factory.setFormatString(props.getValueFormat());
		return factory;
	}
	
				
	private static TextFieldElementFactory setTextFieldElementDefaults(TextFieldElementFactory factory, String name, ColumnProperties props)
	{

		factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(props.getPositionX(), props.getPositionY()));
		factory.setMinimumSize(new FloatDimension(props.getWidth(), 12));
		factory.setDynamicHeight(Boolean.TRUE);
		factory.setHorizontalAlignment(ElementAlignment.LEFT);
		factory.setVerticalAlignment(ElementAlignment.MIDDLE);
		factory.setNullString("  ---  ");
		factory.setName(name + NAME_ELEMENT);
		factory.setFieldname(name);

		return factory;
	}
	
	private static TextFieldElementFactory setGroupTextFieldElementDefaults(TextFieldElementFactory factory, String name, ColumnProperties props)
	{
		factory.setAbsolutePosition(new java.awt.geom.Point2D.Float(props.getPositionX(), props.getPositionY()));
		factory.setMinimumSize(new FloatDimension(props.getWidth(), 18));
		factory.setDynamicHeight(Boolean.TRUE);
		factory.setHorizontalAlignment(ElementAlignment.LEFT);
		factory.setVerticalAlignment(ElementAlignment.BOTTOM);
		factory.setNullString("  ---  ");
		factory.setBold(new Boolean(true));
		factory.setName(name + NAME_GROUP_ELEMENT);
		factory.setFieldname(name);

		return factory;
	}
}

