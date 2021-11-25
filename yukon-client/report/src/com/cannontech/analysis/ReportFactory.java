/*
 * Created on Feb 4, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.analysis;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;
import java.util.Date;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.GroupFooter;
import org.pentaho.reporting.engine.classic.core.GroupHeader;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.ReportFooter;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.elementfactory.DateFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.HorizontalLineElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.NumberFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.FontDefinition;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.libraries.base.util.FloatDimension;

import com.cannontech.analysis.report.ColumnLayoutData;
import com.cannontech.analysis.tablemodel.ReportModelBase;

/**
 * @author stacey
 */
public class ReportFactory {
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

    public static GroupHeader createGroupHeaderDefault() {
        GroupHeader header = new GroupHeader();

        header.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, 30f);
        header.getStyle().getStyleProperty(TextStyleKeys.FONT, GROUP_HEADER_BAND_FONT);

        header.setRepeat(true);
//If it cannot be guaranteed that the dynamic content will not consume all space of the page, dynamic elements should not be used in the page header or any repeating group header. 
//header.setDynamicContent(true);

        return header;
    }

    public static GroupFooter createGroupFooterDefault() {
        GroupFooter footer = new GroupFooter();
        footer.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, 30f);
        footer.getStyle().getStyleProperty(TextStyleKeys.FONT, GROUP_HEADER_BAND_FONT);
//...But as the effects when using dynamic elements in the page footer is unpredictable I usually recommend to avoid dynamic elements in that band. 
//footer.setDynamicContent(true);
        return footer;
    }

    public static ReportFooter createReportFooterDefault() {
        ReportFooter footer = new ReportFooter();
        footer.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, 48f);
        footer.getStyle().getStyleProperty(TextStyleKeys.FONT, REPORT_FOOTER_BAND_FONT);
        return footer;
    }

    public static ReportHeader createReportHeaderDefault() {
        ReportHeader header = new ReportHeader();
        header.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, 48f);
        header.getStyle().getStyleProperty(TextStyleKeys.FONT, REPORT_HEADER_BAND_FONT);

        return header;
    }

    public static Element createBasicLine(float strokeWidth, float posY) {
        return HorizontalLineElementFactory.createHorizontalLine(posY, null, new BasicStroke(strokeWidth));
    }

    public static ItemBand createItemBandDefault() {
        ItemBand items = new ItemBand();
        items.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, 10f);
        items.getStyle().getStyleProperty(TextStyleKeys.FONT, ITEM_BAND_FONT);
        items.setDynamicContent(true);
        return items;
    }

    public static LabelElementFactory createGroupLabelElementDefault(ReportModelBase model, int index) {
        return createGroupLabelElementDefault(model.getColumnName(index), model.getColumnProperties(index).getPositionX(),
                model.getColumnProperties(index).getPositionY(), model.getColumnProperties(index).getWidth());
    }

    public static LabelElementFactory createGroupLabelElementDefault(String name, float posX, float posY, float width) {
        LabelElementFactory factory = new LabelElementFactory();
        factory.setText(name);
        factory.setAbsolutePosition(new Point2D.Float(posX, posY));
        factory.setMinimumSize(new FloatDimension(width, 18));
//Avoid dynamic elements whenever possible. Plain Labels should never be dynamic - label contents usually do not change, if the designer carefully chooses a suitable size for the element, report processing will have less work to do. 
        factory.setDynamicHeight(Boolean.TRUE);
        factory.setHorizontalAlignment(ElementAlignment.LEFT);
        factory.setVerticalAlignment(ElementAlignment.BOTTOM);
        factory.setBold(new Boolean(true));
        factory.setName(name + NAME_GROUP_LABEL_ELEMENT);

        return factory;
    }

    public static TextFieldElementFactory createGroupTextFieldElementDefault(ReportModelBase model, int index) {
        TextFieldElementFactory factory = new TextFieldElementFactory();

        if (model.getColumnClass(index).equals(Integer.class) || model.getColumnClass(index).equals(Double.class))
            factory = createNumberFieldElementDefault(model.getColumnName(index), model.getColumnProperties(index));

        else if (model.getColumnClass(index).equals(Date.class))
            factory = createDateFieldElementDefault(model.getColumnName(index), model.getColumnProperties(index));

        else // make it a text field on all other occasions.
            factory = createTextFieldElementDefault(model.getColumnName(index), model.getColumnProperties(index));

        factory = setGroupTextFieldElementDefaults(factory, model.getColumnName(index), model.getColumnProperties(index));
        return factory;
    }

    public static TextFieldElementFactory createGroupTextFieldElementDefault(Point2D pt2D, ColumnLayoutData cld) {
        TextFieldElementFactory factory = new TextFieldElementFactory();
        factory = createTextFieldElementDefault(pt2D, cld);

        factory = setGroupTextFieldElementDefaults(factory, pt2D, cld);
        return factory;
    }

    public static LabelElementFactory createLabelElementDefault(ReportModelBase model, int index) {
        return createLabelElementDefault(model.getColumnName(index), model.getColumnProperties(index).getPositionX(),
                model.getColumnProperties(index).getPositionY(), model.getColumnProperties(index).getWidth());
    }

    public static LabelElementFactory createLabelElementDefault(String name, float posX, float posY, float width) {
        LabelElementFactory factory = new LabelElementFactory();
        factory.setText(name);
        factory.setAbsolutePosition(new Point2D.Float(posX, posY));
        factory.setMinimumSize(new FloatDimension(width, 12));
        factory.setDynamicHeight(Boolean.TRUE);
        factory.setHorizontalAlignment(ElementAlignment.LEFT);
        factory.setVerticalAlignment(ElementAlignment.BOTTOM);
        factory.setName(name + NAME_LABEL);

        return factory;
    }

    public static TextFieldElementFactory createTextFieldElementDefault(ReportModelBase model, int index) {

        TextFieldElementFactory factory = new TextFieldElementFactory();
        Class<?> columnClass = model.getColumnClass(index);
        if (Number.class.isAssignableFrom(columnClass)) {
            factory = createNumberFieldElementDefault(model.getColumnName(index), model.getColumnProperties(index));
        } else if (Date.class.isAssignableFrom(columnClass)) {
            factory = createDateFieldElementDefault(model.getColumnName(index), model.getColumnProperties(index));
        } else {
            // make it a text field on all other occasions.
            factory = createTextFieldElementDefault(model.getColumnName(index), model.getColumnProperties(index));
        }

        factory = setTextFieldElementDefaults(factory, model.getColumnName(index), model.getColumnProperties(index));
        return factory;
    }

    public static TextFieldElementFactory createTextFieldElementDefault(Point2D pt2D, ColumnLayoutData cld) {

        TextFieldElementFactory factory = new TextFieldElementFactory();
        factory = setTextFieldElementDefaults(factory, pt2D, cld);
        return factory;
    }

    private static TextFieldElementFactory createTextFieldElementDefault(String name, ColumnProperties props) {
        TextFieldElementFactory factory = new TextFieldElementFactory();
        factory = setTextFieldElementDefaults(factory, name, props);
        return factory;
    }

    private static NumberFieldElementFactory createNumberFieldElementDefault(String name, ColumnProperties props) {
        NumberFieldElementFactory factory = new NumberFieldElementFactory();
        factory.setFormatString(props.getValueFormat());
        factory = (NumberFieldElementFactory) setTextFieldElementDefaults(factory, name, props);
        return factory;
    }

    private static DateFieldElementFactory createDateFieldElementDefault(String name, ColumnProperties props) {
        DateFieldElementFactory factory = new DateFieldElementFactory();
        factory.setVerticalAlignment(ElementAlignment.BOTTOM);
        factory.setFormatString(props.getValueFormat());
        return factory;
    }

    private static TextFieldElementFactory setTextFieldElementDefaults(TextFieldElementFactory factory, String name,
            ColumnProperties props) {

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

    private static TextFieldElementFactory setTextFieldElementDefaults(TextFieldElementFactory factory, Point2D pt2D,
            ColumnLayoutData cld) {
        factory.setAbsolutePosition(pt2D);
        factory.setMinimumSize(new FloatDimension(cld.getWidth(), 12));
        factory.setDynamicHeight(Boolean.TRUE);
        factory.setHorizontalAlignment(ElementAlignment.LEFT);
        factory.setVerticalAlignment(ElementAlignment.MIDDLE);
        factory.setNullString("  ---  ");
        factory.setName(cld.getColumnName() + NAME_ELEMENT);
        factory.setFieldname(cld.getFieldName());

        return factory;
    }

    private static TextFieldElementFactory setGroupTextFieldElementDefaults(TextFieldElementFactory factory, String name,
            ColumnProperties props) {
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

    private static TextFieldElementFactory setGroupTextFieldElementDefaults(TextFieldElementFactory factory, Point2D pt2D,
            ColumnLayoutData cld) {
        factory.setAbsolutePosition(pt2D);
        factory.setMinimumSize(new FloatDimension(cld.getWidth(), 18));
        factory.setDynamicHeight(Boolean.TRUE);
        factory.setHorizontalAlignment(ElementAlignment.LEFT);
        factory.setVerticalAlignment(ElementAlignment.BOTTOM);
        factory.setNullString("  ---  ");
        factory.setBold(new Boolean(true));
        factory.setName(cld.getColumnName() + NAME_GROUP_ELEMENT);
        factory.setFieldname(cld.getFieldName());

        return factory;
    }
}
