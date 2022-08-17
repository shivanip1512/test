package com.cannontech.simplereport;

import java.beans.PropertyEditor;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jfree.report.ElementAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.report.ColumnLayoutData;
import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.LoadableModel;
import com.cannontech.analysis.tablemodel.ReportModelMetaInfo;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.input.InputRoot;
import com.cannontech.web.input.InputSource;
import com.cannontech.web.input.InputUtil;
import com.cannontech.web.input.type.InputType;

public class SimpleReportServiceImpl implements SimpleReportService {
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private PointFormattingService pointFormattingService;
    @Autowired private ObjectFormattingService objectFormattingService;

    private YukonReportDefinitionFactory<BareReportModel> reportDefinitionFactory;

    private Set<String> dateFormats = new HashSet<String>();
    private Set<String> pointValueFormats = new HashSet<String>();
    
    
    public SimpleReportServiceImpl() {
        // setup list of know DateFormattingService formats as list of strings
        for(DateFormattingService.DateFormatEnum df : DateFormattingService.DateFormatEnum.values()) {
            dateFormats.add(df.toString());
        }
        
        // setup list of know PointFormattingService formats as list of strings
        for(PointFormattingService.Format pf : PointFormattingService.Format.values()) {
            pointValueFormats.add(pf.toString());
        }
    }
    
    @Override
    public String formatData(String format, Object data, YukonUserContext userContext, PointFormattingService cachedPointFormatter) {
        if (data == null) {
            return "";
        }

        String formattedData = null;

        if (StringUtils.isBlank(format)) {
            formattedData = objectFormattingService.formatObjectAsString(data, userContext);
        }

        // Date (known DateFormatEnum)
        else if (data instanceof Date) {
            if (dateFormats.contains(format)) {
                DateFormatEnum enumValue = DateFormattingService.DateFormatEnum.valueOf(format);
                formattedData = dateFormattingService.format(data,
                                                                 enumValue,
                                                                 userContext);
            } else {
                SimpleDateFormat formatter = new SimpleDateFormat(format);
                formatter.setTimeZone(userContext.getTimeZone());
                formattedData = formatter.format(data);
            }
        }

        // Number
        else if (data instanceof Number) {
            DecimalFormat formatter = new DecimalFormat(format);
            formattedData = formatter.format(data);
        }

        // String
        else if (data instanceof String) {
            formattedData = String.format(format, data);
        }

        // PointValueHolder
        else if (data instanceof PointValueHolder) {
            try {
                Format formatEnum = PointFormattingService.Format.valueOf(format);
                formattedData = cachedPointFormatter.getValueString((PointValueHolder)data, formatEnum, userContext);
            } catch (IllegalArgumentException e) {
                formattedData = cachedPointFormatter.getValueString((PointValueHolder)data, format, userContext);
            }
        }

        else {
            formattedData = objectFormattingService.formatObjectAsString(data, userContext);
        }

        return formattedData;
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.simplereport.SimpleReportService#getReportDefinition(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public YukonReportDefinition<BareReportModel> getReportDefinition(HttpServletRequest request) throws Exception {
        
        String definitionName = ServletRequestUtils.getRequiredStringParameter(request, "def");

        // BEAN AWARE
        //YukonReportDefinition<BareReportModel> reportDefinition = (YukonReportDefinition<BareReportModel>) beanFactory.getBean(reportBeanName);
        // OTHER WAY
        YukonReportDefinition<BareReportModel> reportDefinition = reportDefinitionFactory.getReportDefinition(definitionName);
        
        return reportDefinition;
    }

    /* (non-Javadoc)
     * @see com.cannontech.simplereport.SimpleReportService#getReportModel(com.cannontech.simplereport.YukonReportDefinition, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public BareReportModel getReportModel(YukonReportDefinition<? extends BareReportModel> reportDefinition, Map<String, String> parameterMap, boolean loadData, YukonUserContext userContext) throws Exception {
        BareReportModel reportModel = reportDefinition.createBean();
        
        // set parameters on model
        InputRoot inputRoot = reportDefinition.getInputs();

        InputUtil.applyProperties(inputRoot, reportModel, parameterMap);
        
        // if model instanceof ReportModelMetaInfo, get meta info & set the user context.
        if (reportModel instanceof ReportModelMetaInfo){
            ((ReportModelMetaInfo)reportModel).getMetaInfo(userContext);
        }
        
        // if model instanceof LoadalbleModel, load it.
        if(loadData && reportModel instanceof LoadableModel) {
            ((LoadableModel)reportModel).loadData();
        }
        
        return reportModel;
    }

    /* (non-Javadoc)
     * @see com.cannontech.simplereport.SimpleReportService#getReportModel(com.cannontech.simplereport.YukonReportDefinition, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public BareReportModel getStringReportModel(final YukonReportDefinition<? extends BareReportModel> reportDefinition,
            final BareReportModel reportModel, final YukonUserContext userContext) {
        final PointFormattingService cachedInstance = pointFormattingService.getCachedInstance();
        
        BareReportModel stringReportModel = new BareReportModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }

            @Override
            public int getColumnCount() {
                return reportDefinition.getReportLayoutData().getBodyColumns().length;
            }

            @Override
            public String getColumnName(int columnIndex) {
                return reportModel.getColumnName(columnIndex);
            }

            @Override
            public int getRowCount() {
                return reportModel.getRowCount();
            }

            @Override
            public String getTitle() {
                return reportModel.getTitle();
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                Object objValue = reportModel.getValueAt(rowIndex, columnIndex);
                ColumnLayoutData columnLayoutData = reportDefinition.getReportLayoutData().getBodyColumns()[columnIndex];
                String formattedString = formatData(columnLayoutData.getFormat(), objValue, userContext, cachedInstance);
                return formattedString;
            }
            
        };
        
        return stringReportModel;
    }

    @Override
    public List<List<String>> getFormattedData(YukonReportDefinition<? extends BareReportModel> reportDefinition, BareReportModel reportModel, YukonUserContext userContext) {
        
        BareReportModel stringModel = getStringReportModel(reportDefinition, reportModel, userContext);
        
        List<List<String>> data = new ArrayList<List<String>>();
        for(int rowIdx = 0; rowIdx < stringModel.getRowCount(); rowIdx++) {
            
            List<String> colData = new ArrayList<String>();
            for(int colIdx = 0; colIdx < stringModel.getColumnCount(); colIdx++) {
                colData.add((String)stringModel.getValueAt(rowIdx, colIdx));
            }
            data.add(colData);
        }
        
        return data;
    }

    @Override
    public List<ColumnInfo> buildColumnInfoListFromColumnLayoutData(ColumnLayoutData[] bodyColumns) {
        int totalWidth = 0;
        List<ColumnInfo> columnInfo = new ArrayList<ColumnInfo>();
        for(int i = 0; i < bodyColumns.length; i++) {

            ColumnInfo ci = new ColumnInfo();

            // column name
            ci.setLabel(bodyColumns[i].getColumnName());

            // column width
            int width = bodyColumns[i].getWidth();
            totalWidth += width;
            ci.setWidth(width);

            // column alignment
            String align = "left";
            if(bodyColumns[i].getHorizontalAlignment() != null) {
                align = elementAlignmentToHtmlAlignment(bodyColumns[i].getHorizontalAlignment());
            }
            ci.setAlign(align);

            columnInfo.add(ci);
        }
        
        for(ColumnInfo ci : columnInfo) {
            ci.calculateColumnWidthPercentage(totalWidth);
        }
        
        return columnInfo;
    }
    
    @Override
    public String getReportUrl(HttpServletRequest httpRequest, String definitionName, Map<String, Object> inputValues,
            Map<String, String> optionalAttributeDefaults, String viewType, Boolean htmlOutput) throws Exception {
        YukonReportDefinition<BareReportModel> reportDefinition = reportDefinitionFactory.getReportDefinition(definitionName);
        InputRoot inputRoot = reportDefinition.getInputs();
        
        // inputs map
        Map<String,String> propertiesMap = extractPropertiesFromAttributesMap(inputRoot, inputValues);
        
        // other optional attributes
        propertiesMap.putAll(optionalAttributeDefaults);
        propertiesMap.put("def", definitionName);
        
        // build safe URL query string
        String queryString = ServletUtil.buildSafeQueryStringFromMap(propertiesMap, htmlOutput);
        
        // complete URL
        URL urlObj = new URL(httpRequest.getRequestURL().toString());

        String protocol = urlObj.getProtocol();
        String host = urlObj.getHost();
        int port = urlObj.getPort();
        
        String url = protocol + "://" + host;
        if(port > 0) {
            url += ":" + port;
        }
        url += "/reports/simple/" + viewType + "?" + queryString;

        url = ServletUtil.createSafeUrl(httpRequest, url);
        url = StringEscapeUtils.escapeHtml4(url);
        
        return url;
    }
    
    /**
     * Inspects the report definitionName for required inputs, for each of them - check if that field
     * was used as one of the dynamic attributes used in the tag. If so, use the input's property editor 
     * to format the object appropriately (as the model is going to eventually expect).
     * 
     * Return a map of <String, String> of the field name, and formattted string value
     * to be used to construct a URL string to be used to retrieve this report.
     * 
     * @param inputRoot
     * @return
     */
    @Override
    public Map<String, String> extractPropertiesFromAttributesMap(InputRoot inputRoot, Map<String, Object> attributes){
    	// look at the attributes the report input root want, and find them in the dynamic attributes
    	// if found, use inputs' prop editor to convert to text and add to the return map
        HashMap<String,String> propertiesMap = new HashMap<String,String>();
        Map<String, ? extends InputSource<?>> inputs = inputRoot.getInputMap();
        for (Entry<String, ? extends InputSource<?>> entry : inputs.entrySet()) {
            String field = entry.getKey();
            
            if (attributes.containsKey(field)) {
                InputType<?> type = entry.getValue().getType();

                PropertyEditor propertyEditor = type.getPropertyEditor();
                propertyEditor.setValue(attributes.get(field));
                String reportProperty = propertyEditor.getAsText();
                
                propertiesMap.put(field, reportProperty);
            }
        }
        
        return propertiesMap;
    }
    
    private String elementAlignmentToHtmlAlignment(ElementAlignment elementAlignment) {
        return elementAlignment.toString().toLowerCase();
    }

    @Required
    public void setReportDefinitionFactory(YukonReportDefinitionFactory<BareReportModel> reportDefinitionFactory) {
        this.reportDefinitionFactory = reportDefinitionFactory;
    }
}
