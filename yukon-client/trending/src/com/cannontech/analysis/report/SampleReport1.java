/**
 * ========================================
 * JFreeReport : a free Java report library
 * ========================================
 *
 * Project Info:  http://www.jfree.org/jfreereport/index.html
 * Project Lead:  Thomas Morgner;
 *
 * (C) Copyright 2000-2003, by Simba Management Limited and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * ------------------
 * SampleReport1.java
 * ------------------
 * (C)opyright 2000-2003, by Thomas Morgner and Contributors.
 *
 * Original Author:  Thomas Morgner;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: SampleReport1.java,v 1.1 2004/03/12 17:56:36 snebben Exp $
 *
 * Changes:
 * --------
 * 19-Jun-2002 : Initial version
 * 28-Nov-2002 : Added vertical alignment parameter (DG);
 * 10-Dec-2002 : Minor Javadoc changes (DG);
 *
 */

package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jfree.report.ElementAlignment;
import org.jfree.report.Group;
import org.jfree.report.GroupFooter;
import org.jfree.report.GroupHeader;
import org.jfree.report.GroupList;
import org.jfree.report.ItemBand;
import org.jfree.report.JFreeReport;
import org.jfree.report.PageFooter;
import org.jfree.report.PageHeader;
import org.jfree.report.ReportFooter;
import org.jfree.report.ReportHeader;
import org.jfree.report.Boot;
import org.jfree.report.elementfactory.DateFieldElementFactory;
import org.jfree.report.elementfactory.LabelElementFactory;
import org.jfree.report.elementfactory.NumberFieldElementFactory;
import org.jfree.report.elementfactory.StaticShapeElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;
import org.jfree.report.function.ElementVisibilitySwitchFunction;
import org.jfree.report.function.ExpressionCollection;
import org.jfree.report.function.FunctionInitializeException;
import org.jfree.report.function.ItemSumFunction;
import org.jfree.report.modules.gui.base.PreviewDialog;
import org.jfree.report.style.ElementStyleSheet;
import org.jfree.report.style.FontDefinition;
import org.jfree.ui.FloatDimension;

import com.cannontech.analysis.data.*;

/**
 * This creates a report similar to the one defined by report1.xml.
 *
 * @author Thomas Morgner
 */
public class SampleReport1
{
  /**
   * Creates the page header.
   *
   * @return the page header.
   */
  private PageHeader createPageHeader()
  {
    final PageHeader header = new PageHeader();
    header.getStyle().setStyleProperty(ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 18));
    header.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Serif", 10));
    header.setDisplayOnFirstPage(true);
    header.setDisplayOnLastPage(false);

    // is by default true, but it is defined in the xml template, so I add it here too.
    header.addElement(
        StaticShapeElementFactory.createRectangleShapeElement(
            null, Color.decode("#AFAFAF"), null,
            new Rectangle2D.Float(0, 0, -100, -100),
            false, true)
    );
    final DateFieldElementFactory factory = new DateFieldElementFactory();
    factory.setName("date1");
    factory.setAbsolutePosition(new Point2D.Float(0, 0));
    factory.setMinimumSize(new FloatDimension(-100, 14));
    factory.setHorizontalAlignment(ElementAlignment.RIGHT);
    factory.setVerticalAlignment(ElementAlignment.MIDDLE);
    factory.setNullString("<null>");
    factory.setFormatString("d-MMM-yyyy");
    factory.setFieldname("report.date");
    header.addElement(factory.createElement());

    header.addElement(
        StaticShapeElementFactory.createLineShapeElement(
            "line1", Color.decode("#CFCFCF"),
            new BasicStroke(2), new Line2D.Float(0, 16, 0, 16))
    );
    return header;
  }

  /**
   * Creates a page footer.
   *
   * @return The page footer.
   */
  private PageFooter createPageFooter()
  {
    final PageFooter pageFooter = new PageFooter();
    pageFooter.getStyle().setStyleProperty
        (ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 30));
    pageFooter.getBandDefaults().setFontDefinitionProperty(new FontDefinition("Dialog", 10));

    pageFooter.addElement(StaticShapeElementFactory.createRectangleShapeElement
        (null, Color.black, null, new Rectangle2D.Float(0, 0, -100, -100), true, false));

    final LabelElementFactory factory = new LabelElementFactory();
    factory.setName("Label 2");
    factory.setAbsolutePosition(new Point2D.Float(0, 0));
    factory.setMinimumSize(new FloatDimension(-100, 0));
    factory.setHorizontalAlignment(ElementAlignment.LEFT);
    factory.setVerticalAlignment(ElementAlignment.TOP);
    factory.setText("Some Text for the page footer");
    factory.setDynamicHeight(Boolean.TRUE);
    pageFooter.addElement(factory.createElement());
    return pageFooter;
  }

  /**
   * Creates the report footer.
   *
   * @return the report footer.
   */
  private ReportFooter createReportFooter()
  {
    final ReportFooter footer = new ReportFooter();
    footer.getStyle().setStyleProperty
        (ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 48));
    footer.getBandDefaults().setFontDefinitionProperty
        (new FontDefinition("Serif", 16, true, false, false, false));

    final LabelElementFactory factory = new LabelElementFactory();
    factory.setName("Label 2");
    factory.setAbsolutePosition(new Point2D.Float(0, 0));
    factory.setMinimumSize(new FloatDimension(-100, 24));
    factory.setHorizontalAlignment(ElementAlignment.CENTER);
    factory.setVerticalAlignment(ElementAlignment.MIDDLE);
    factory.setText("*** END OF REPORT ***");
    footer.addElement(factory.createElement());
    return footer;
  }

  /**
   * Creates the report header.
   *
   * @return the report header.
   */
  private ReportHeader createReportHeader()
  {
    final ReportHeader header = new ReportHeader();
    header.getStyle().setStyleProperty
        (ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 48));
    header.getBandDefaults().setFontDefinitionProperty
        (new FontDefinition("Serif", 20, true, false, false, false));

    final LabelElementFactory factory = new LabelElementFactory();
    factory.setName("Label 1");
    factory.setAbsolutePosition(new Point2D.Float(0, 0));
    factory.setMinimumSize(new FloatDimension(-100, 24));
    factory.setHorizontalAlignment(ElementAlignment.CENTER);
    factory.setVerticalAlignment(ElementAlignment.MIDDLE);
    factory.setText("LIST OF CONTINENTS BY COUNTRY");
    header.addElement(factory.createElement());
    return header;
  }


  /**
   * Creates the itemBand.
   *
   * @return the item band.
   */
  private ItemBand createItemBand()
  {
    final ItemBand items = new ItemBand();
    items.getStyle().setStyleProperty
        (ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 10));
    items.getBandDefaults().setFontDefinitionProperty
        (new FontDefinition("Monospaced", 10));


    items.addElement(StaticShapeElementFactory.createRectangleShapeElement
        ("background", Color.decode("#DFDFDF"), new BasicStroke(0),
            new Rectangle2D.Float(0, 0, -100, -100), false, true));
    items.addElement(StaticShapeElementFactory.createLineShapeElement
        ("top", Color.decode("#DFDFDF"), new BasicStroke(0.1f),
            new Line2D.Float(0, 0, 0, 0)));
    items.addElement(StaticShapeElementFactory.createLineShapeElement
        ("bottom", Color.decode("#DFDFDF"), new BasicStroke(0.1f),
            new Line2D.Float(0, 10, 0, 10)));

    TextFieldElementFactory factory = new TextFieldElementFactory();
    factory.setName("Country Element");
    factory.setAbsolutePosition(new Point2D.Float(0, 0));
    factory.setMinimumSize(new FloatDimension(176, 10));
    factory.setHorizontalAlignment(ElementAlignment.LEFT);
    factory.setVerticalAlignment(ElementAlignment.MIDDLE);
    factory.setNullString("<null>");
    factory.setFieldname("Country");
    items.addElement(factory.createElement());

    factory = new TextFieldElementFactory();
    factory.setName("Code Element");
    factory.setAbsolutePosition(new Point2D.Float(180, 0));
    factory.setMinimumSize(new FloatDimension(76, 10));
    factory.setHorizontalAlignment(ElementAlignment.LEFT);
    factory.setVerticalAlignment(ElementAlignment.MIDDLE);
    factory.setNullString("<null>");
    factory.setFieldname("ISO Code");
    items.addElement(factory.createElement());

    final NumberFieldElementFactory nfactory = new NumberFieldElementFactory();
    nfactory.setName("Population Element");
    nfactory.setAbsolutePosition(new Point2D.Float(260, 0));
    nfactory.setMinimumSize(new FloatDimension(76, 10));
    nfactory.setHorizontalAlignment(ElementAlignment.LEFT);
    nfactory.setVerticalAlignment(ElementAlignment.MIDDLE);
    nfactory.setNullString("<null>");
    nfactory.setFieldname("Population");
    nfactory.setFormatString("#,##0");
    items.addElement(nfactory.createElement());
    return items;
  }

  /**
   * Creates the function collection. The xml definition for this construct:
   *
   <pre>
   <functions>
   <function name="sum" class="org.jfree.report.function.ItemSumFunction">
   <properties>
   <property name="field">Population</property>
   <property name="group">Continent Group</property>
   </properties>
   </function>
   <function name="backgroundTrigger"
   class="org.jfree.report.function.ElementVisibilitySwitchFunction">
   <properties>
   <property name="element">background</property>
   </properties>
   </function>
   </functions>
   </pre>
   *
   * @return the functions.
   *
   * @throws FunctionInitializeException if there is a problem initialising the functions.
   */
  private ExpressionCollection createFunctions() throws FunctionInitializeException
  {
    final ExpressionCollection functions = new ExpressionCollection();

    final ItemSumFunction sum = new ItemSumFunction();
    sum.setName("sum");
    sum.setProperty("field", "Population");
    sum.setProperty("group", "Continent Group");
    functions.add(sum);

    final ElementVisibilitySwitchFunction backgroundTrigger = new ElementVisibilitySwitchFunction();
    backgroundTrigger.setName("backgroundTrigger");
    backgroundTrigger.setProperty("element", "background");
    functions.add(backgroundTrigger);
    return functions;
  }

  /**
   <pre>
   <groups>

   ... create the groups and add them to the list ...

   </groups>
   </pre>
   *
   * @return the groups.
   */
  private GroupList createGroups()
  {
    final GroupList list = new GroupList();
    list.add(createContinentGroup());
    return list;
  }

  /**
   <pre>
   <group name="Continent Group">
   <groupheader height="18" fontname="Monospaced" fontstyle="bold" fontsize="9" pagebreak="false">
   <label name="Label 5" x="0" y="1" width="76" height="9" alignment="left">CONTINENT:</label>
   <string-field name="Continent Element" x="96" y="1" width="76" height="9" alignment="left"
   fieldname="Continent"/>
   <line name="line1" x1="0" y1="12" x2="0" y2="12" weight="0.5"/>
   </groupheader>
   <groupfooter height="18" fontname="Monospaced" fontstyle="bold" fontsize="9">
   <label name="Label 6" x="0" y="0" width="450" height="12" alignment="left"
   baseline="10">Population:</label>
   <number-function x="260" y="0" width="76" height="12" alignment="right" baseline="10"
   format="#,##0" function="sum"/>
   </groupfooter>
   <fields>
   <field>Continent</field>
   </fields>
   </group>
   </pre>
   *
   * @return the continent group.
   */
  private Group createContinentGroup()
  {
    final Group continentGroup = new Group();
    continentGroup.setName("Continent Group");
    continentGroup.addField("Continent");

    final GroupHeader header = new GroupHeader();

    header.getStyle().setStyleProperty
        (ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 18));
    header.getBandDefaults().setFontDefinitionProperty
        (new FontDefinition("Monospaced", 9, true, false, false, false));

    LabelElementFactory factory = new LabelElementFactory();
    factory.setName("Label 5");
    factory.setAbsolutePosition(new Point2D.Float(0, 1));
    factory.setMinimumSize(new FloatDimension(76, 9));
    factory.setHorizontalAlignment(ElementAlignment.LEFT);
    factory.setVerticalAlignment(ElementAlignment.MIDDLE);
    factory.setText("CONTINENT:");
    header.addElement(factory.createElement());

    final TextFieldElementFactory tfactory = new TextFieldElementFactory();
    tfactory.setName("Continent Element");
    tfactory.setAbsolutePosition(new Point2D.Float(96, 1));
    tfactory.setMinimumSize(new FloatDimension(76, 9));
    tfactory.setHorizontalAlignment(ElementAlignment.LEFT);
    tfactory.setVerticalAlignment(ElementAlignment.MIDDLE);
    tfactory.setNullString("<null>");
    tfactory.setFieldname("Continent");
    header.addElement(tfactory.createElement());

    header.addElement(StaticShapeElementFactory.createLineShapeElement
        ("line1", null, new BasicStroke(0.5f), new Line2D.Float(0, 12, 0, 12)));
    continentGroup.setHeader(header);

    final GroupFooter footer = new GroupFooter();
    footer.getStyle().setStyleProperty
        (ElementStyleSheet.MINIMUMSIZE, new FloatDimension(0, 18));
    footer.getBandDefaults().setFontDefinitionProperty
        (new FontDefinition("Monospaced", 9, true, false, false, false));

    factory = new LabelElementFactory();
    factory.setName("Label 6");
    factory.setAbsolutePosition(new Point2D.Float(0, 0));
    factory.setMinimumSize(new FloatDimension(100, 12));
    factory.setHorizontalAlignment(ElementAlignment.LEFT);
    factory.setVerticalAlignment(ElementAlignment.MIDDLE);
    factory.setText("Population:");
    footer.addElement(factory.createElement());

    final NumberFieldElementFactory nfactory = new NumberFieldElementFactory();
    nfactory.setName("anonymous");
    nfactory.setAbsolutePosition(new Point2D.Float(260, 0));
    nfactory.setMinimumSize(new FloatDimension(76, 12));
    nfactory.setHorizontalAlignment(ElementAlignment.LEFT);
    nfactory.setVerticalAlignment(ElementAlignment.MIDDLE);
    nfactory.setNullString("<null>");
    nfactory.setFieldname("sum");
    nfactory.setFormatString("#,##0");
    footer.addElement(nfactory.createElement());
    continentGroup.setFooter(footer);
    return continentGroup;
  }

  /**
   * Creates the report.
   *
   * @return the constructed report.
   *
   * @throws FunctionInitializeException if there was a problem initialising any of the functions.
   */
  public JFreeReport createReport() throws FunctionInitializeException
  {
    final JFreeReport report = new JFreeReport();
    report.setName("Sample Report 1");
    report.setReportFooter(createReportFooter());
    report.setReportHeader(createReportHeader());
    report.setPageFooter(createPageFooter());
    report.setPageHeader(createPageHeader());
    report.setGroups(createGroups());
    report.setItemBand(createItemBand());
    report.setFunctions(createFunctions());
    report.setPropertyMarked("report.date", true);
    return report;
  }

  /**
   * Default constructor.
   */
  public SampleReport1()
  {
  }

  /**
   * Runs this report and shows a preview dialog.
   *
   * @param args the arguments (ignored).
   * @throws Exception if an error occurs (default: print a stack trace)
   */
  public static void main(final String[] args) throws Exception
  {
    // initialize JFreeReport
    Boot.start();

    final JFreeReport report = new SampleReport1().createReport();
    report.setData(new SampleData1());

    final PreviewDialog dialog = new PreviewDialog(report);
    dialog.setModal(true);
    dialog.pack();
    dialog.setVisible(true);
  }
}