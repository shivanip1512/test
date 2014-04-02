package com.cannontech.analysis.report;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.jfree.report.ElementAlignment;
import org.jfree.report.Group;
import org.jfree.report.GroupFooter;
import org.jfree.report.GroupHeader;
import org.jfree.report.GroupList;
import org.jfree.report.ItemBand;
import org.jfree.report.JFreeReport;
import org.jfree.report.ReportFooter;
import org.jfree.report.elementfactory.DateFieldElementFactory;
import org.jfree.report.elementfactory.LabelElementFactory;
import org.jfree.report.elementfactory.NumberFieldElementFactory;
import org.jfree.report.elementfactory.StaticShapeElementFactory;
import org.jfree.report.elementfactory.TextElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;
import org.jfree.report.function.Expression;
import org.jfree.report.function.ExpressionCollection;
import org.jfree.report.function.FunctionInitializeException;
import org.jfree.ui.FloatDimension;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.function.AggregateFooterFieldFactory;
import com.cannontech.analysis.function.ExpressionFieldFactory;
import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.BareReportModelAdapter;
import com.cannontech.analysis.tablemodel.DatedModelAttributes;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.ReportModelDelegate;
import com.cannontech.analysis.tablemodel.ReportModelLayout;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * This class is meant to be used as a base class for reports that have a very simple
 * layout. It uses either a BareReportModel or a ReportModelBase as input.
 * Overriding classes only need to indicate which columns are in the body, their order, 
 * and their widths.
 */
public abstract class SimpleYukonReportBase extends YukonReportBase {
    private final ReportModelBase model;
    private BareReportModel bareModel = null;

    protected Map<ColumnLayoutData,Point2D> columnProperties = new HashMap<ColumnLayoutData, Point2D>();
    protected Map<String,Integer> columIndexLookup = new HashMap<String, Integer>();
    
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm z");
    public static String columnDateFormat = "MM/dd/yyyy HH:mm:ss";


    public SimpleYukonReportBase(BareReportModel bareModel) {
        Validate.notNull(bareModel, "BareReportModel must not be null");
        this.bareModel = bareModel;
        model = new BareReportModelAdapter(bareModel, new ReportModelLayout() {
            public ColumnProperties getColumnProperties(int i) {
                throw new UnsupportedOperationException();
            }
        });
        setModel(model);
    }

    public SimpleYukonReportBase(ReportModelBase model) {
        if (model instanceof BareReportModelAdapter) {
            throw new IllegalArgumentException("The BareReportModelAdapter is used internally and should not be instantiated by the calling code.");
        }
        this.model = new ReportModelDelegate(model, new ReportModelLayout() {
            public ColumnProperties getColumnProperties(int i) {
                throw new UnsupportedOperationException();
            }
        });
        setModel(model);
        
    }
    
    protected void initialize() {
        initializeColumns();
    }
    
    @Override
    public JFreeReport createReport() throws FunctionInitializeException {
        initialize();
        return super.createReport();
    }

    protected void initializeColumns() {
        Iterator<ColumnLayoutData> bodyColumns = getBodyColumns().iterator();
        int accumulativeWidth = 0;
        while (bodyColumns.hasNext()) {
            ColumnLayoutData data = bodyColumns.next();
            int width = data.getWidth();
            Float position = new Point2D.Float(accumulativeWidth, 1);
            columnProperties.put(data, position);
            accumulativeWidth += width + getExtraFieldSpacing();
        }
        
        buildColumnIndexLookup();
    }
    
    protected abstract List<ColumnLayoutData> getBodyColumns();
    protected List<? extends AggregateFooterFieldFactory> getFooterColumns() {
        // extending classes can choose to implement
        return Collections.emptyList();
    }
    protected List<? extends AggregateFooterFieldFactory> getReportFooterColumns() {
        // extending classes can choose to implement
        return Collections.emptyList();
    }    
    protected List<? extends ExpressionFieldFactory> getBodyExpressions() {
    	// extending classes can choose to implement
    	return Collections.emptyList();    	
    }
    
    protected int getExtraFieldSpacing() {
        return 0;
    }
    
    
    protected ItemBand createItemBand() {
        ItemBand items = ReportFactory.createItemBandDefault();
        
        applyBackgroundColor(items);
        
        Iterator<ColumnLayoutData> bodyColumns = getBodyColumns().iterator();
        while (bodyColumns.hasNext()) {
            ColumnLayoutData layoutData = bodyColumns.next();
            TextFieldElementFactory factory;
            int modelIndex = getModelIndex(layoutData);
            Class<?> columnClass = getModel().getColumnClass(modelIndex);
            if (Number.class.isAssignableFrom(columnClass)) {
                NumberFieldElementFactory numFactory = new NumberFieldElementFactory();
                numFactory.setFormatString(layoutData.getFormat());
                factory = numFactory;
            } else if (Date.class.isAssignableFrom(columnClass)) {
                DateFieldElementFactory dateFactory = new DateFieldElementFactory();
                dateFactory.setVerticalAlignment(ElementAlignment.BOTTOM);
                dateFactory.setFormatString(layoutData.getFormat());
                factory = dateFactory;
            } else {
                // make it a text field on all other occasions.
                factory = new TextFieldElementFactory();
            }
            
            applyFieldProperties(factory, layoutData);
            factory.setName(layoutData.getColumnName());
            factory.setFieldname(layoutData.getFieldName());
            
            items.addElement(factory.createElement());
        }
        
        return items;
    }

    private int getModelIndex(ColumnLayoutData layoutData) {
        Integer value = columIndexLookup.get(layoutData.getFieldName());
        if (value == null) {
            throw new RuntimeException("Field name doesn't exist in model: " + layoutData.getFieldName());
        }
        return value;
    }
    
    protected void applyElementProperties(TextElementFactory factory, ColumnLayoutData layoutData) {
        Point2D point2D = columnProperties.get(layoutData);
        factory.setAbsolutePosition(point2D);
        factory.setMinimumSize(new FloatDimension(layoutData.getWidth(), 12));
        factory.setDynamicHeight(Boolean.TRUE);
        factory.setHorizontalAlignment(ElementAlignment.LEFT);
        factory.setVerticalAlignment(ElementAlignment.MIDDLE);
        if (layoutData.getHorizontalAlignment() != null) {
            factory.setHorizontalAlignment(layoutData.getHorizontalAlignment());
        }
        
    }

    protected void applyFieldProperties(TextFieldElementFactory factory, ColumnLayoutData layoutData) {
        applyElementProperties(factory, layoutData);
        factory.setNullString("  ---  ");
    }

    protected void applyLabelProperties(LabelElementFactory labelFactory, ColumnLayoutData layoutData) {
        applyElementProperties(labelFactory, layoutData);
        labelFactory.setBold(true);
    }
    
    private void applyBackgroundColor(ItemBand items) {
        if ( showBackgroundColor ) {
            items.addElement(StaticShapeElementFactory.createRectangleShapeElement
                ("background", Color.decode("#DFDFDF"), new BasicStroke(0),
                    new Rectangle2D.Float(0, 0, -100, -100), false, true));
            items.addElement(StaticShapeElementFactory.createHorizontalLine
                ("top", Color.decode("#DFDFDF"), new BasicStroke(0.1f), 0));
            items.addElement(StaticShapeElementFactory.createHorizontalLine
                ("bottom", Color.decode("#DFDFDF"), new BasicStroke(0.1f), 10));
        }
    }
    

    private Group createSingleGroup() {
        final Group collHdgGroup = new Group();
        collHdgGroup.setName(getSingleGroupName());
    
        GroupHeader header = ReportFactory.createGroupHeaderDefault();
        createGroupLabels(header);
        header.addElement(StaticShapeElementFactory.createHorizontalLine("line1", null, new BasicStroke(0.5f), 22));
        collHdgGroup.setHeader(header);
    
        GroupFooter footer = ReportFactory.createGroupFooterDefault();
        createFooterFields(footer);
        collHdgGroup.setFooter(footer);

        return collHdgGroup;
    }
    
    protected void createFooterFields(GroupFooter footer) {
        List<? extends AggregateFooterFieldFactory> totalColumns = getFooterColumns();
        for (AggregateFooterFieldFactory factory : totalColumns) {
            TextElementFactory elementFactory = factory.createElementFactory();
            applyElementProperties(elementFactory, factory.getSourceColumn());
            footer.addElement(elementFactory.createElement());
        }
    }

    protected String getSingleGroupName() {
        return "Column Heading";
    }

    protected void createGroupLabels(GroupHeader header) {
        Iterator<ColumnLayoutData> bodyColumns = getBodyColumns().iterator();
        while (bodyColumns.hasNext()) {
            ColumnLayoutData layoutData = bodyColumns.next();
            LabelElementFactory labelFactory = new LabelElementFactory();
            labelFactory.setText(layoutData.getColumnName());
            applyLabelProperties(labelFactory, layoutData);
            labelFactory.setName(layoutData.getColumnName() + ReportFactory.NAME_GROUP_LABEL_ELEMENT);
            
            header.addElement(labelFactory.createElement());
        }
    }

    private void buildColumnIndexLookup() {
        int columnCount = model.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            String columnName = model.getColumnName(i);
            columIndexLookup.put(columnName, i);
        }
    }

    protected GroupList createGroups() {
      GroupList list = new GroupList();
      list.add(createSingleGroup());
      return list;
    }
    
    @Override
    protected ReportFooter createReportFooter() {
    	ReportFooter reportFooter = super.createReportFooter();
    	
        List<? extends AggregateFooterFieldFactory> totalColumns = getReportFooterColumns();
        for (AggregateFooterFieldFactory factory : totalColumns) {
            TextElementFactory elementFactory = factory.createElementFactory();
            applyElementProperties(elementFactory, factory.getSourceColumn());
            reportFooter.addElement(elementFactory.createElement());
        }

		return reportFooter;		
    }
    
    @Override
    protected ExpressionCollection getExpressions() throws FunctionInitializeException {
        ExpressionCollection expressionCollection = super.getExpressions();

        List<? extends ExpressionFieldFactory> bodyExpressions = getBodyExpressions();
        for (ExpressionFieldFactory factory : bodyExpressions) {
            Expression expression = factory.createExpression();
            if (expression != null) {
                expressionCollection.add(expression);
            }
        }

        List<? extends AggregateFooterFieldFactory> allFooterColumns = 
        	Lists.newArrayList(Iterables.concat(getFooterColumns(), getReportFooterColumns()));
        
        for (AggregateFooterFieldFactory factory : allFooterColumns) {
            Expression expression = factory.createExpression();
            if (expression != null) {
                expressionCollection.add(expression);
            }
        }
        return expressionCollection;
    }
    
    @Override
    protected String getDateRangeString() {
        if (bareModel == null) {
            return model.getDateRangeString();
        }
        
        if (bareModel instanceof DatedModelAttributes) {
            DatedModelAttributes datedModel = (DatedModelAttributes) bareModel;
            Date startDate = datedModel.getStartDate();
            Date stopDate = datedModel.getStopDate();
            if(startDate == null || stopDate == null){
                /* Must be a report that can disable the date fields as an option. */
                /* Some browsers will not return inputs that are disabled. */ 
                /* Just print out the date the report was run. */
                return getDateFormat().format(new Date()); 
            } else {
                return getDateFormat().format(startDate) + " through " + getDateFormat().format(stopDate);
            }
        }
        
        // won't work because report is "created" before data is loaded
//        if (bareModel instanceof LoadableModel) {
//            LoadableModel loadModel = (LoadableModel) bareModel;
//            return getDateFormat().format(loadModel.getLoadDate());
//        }
        
        return getDateFormat().format(new Date());
    }

    private DateFormat getDateFormat() {
        return simpleDateFormat;
    }

}
