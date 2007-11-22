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
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.jfree.report.ElementAlignment;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.report.ColumnLayoutData;
import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.LoadableModel;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.input.InputRoot;
import com.cannontech.web.input.InputSource;
import com.cannontech.web.input.InputUtil;
import com.cannontech.web.input.type.InputType;

public class SimpleReportServiceImpl implements SimpleReportService {
//implements BeanFactoryAware
    
    private DateFormattingService dateFormattingService = null;
    private YukonUserDao yukonUserDao = null;
    private PointFormattingService pointFormattingService = null;
    // BEAN AWARE
    //private BeanFactory beanFactory; // OR...
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
    
    
    
    /* (non-Javadoc)
     * @see com.cannontech.simplereport.SimpleReportService#formatData(java.lang.String, java.lang.Object, com.cannontech.database.data.lite.LiteYukonUser)
     */
    public String formatData(String format, Object data, LiteYukonUser user, PointFormattingService cachedPointFormatter) {
        
        String formattedData = data.toString();
        
        if(StringUtils.isBlank(format)) {
            return formattedData;
        }
            
        // Date (known DateFormatEnum)
        if (data instanceof Date) {
            if (dateFormats.contains(format)) {
                DateFormatEnum enumValue = DateFormattingService.DateFormatEnum.valueOf(format);
                formattedData = dateFormattingService.formatDate((Date) data,
                                                                 enumValue,
                                                                 user);
            } else {
                SimpleDateFormat formatter = new SimpleDateFormat(format);
                formatter.setTimeZone(yukonUserDao.getUserTimeZone(user));
                formattedData = formatter.format(data);
            }
        }

        // Number
        else if(data instanceof Number) {
            DecimalFormat formatter = new DecimalFormat(format);
            formattedData = formatter.format(data);
        }

        // String
        else if(data instanceof String) {
            formattedData = String.format(format, data);
        }

        // PointValueHolder
        else if(data instanceof PointValueHolder) {
            try {
                Format formatEnum = PointFormattingService.Format.valueOf(format);
                formattedData = cachedPointFormatter.getValueString((PointValueHolder)data, formatEnum, user);
            } catch (IllegalArgumentException e) {
                formattedData = cachedPointFormatter.getValueString((PointValueHolder)data, format, user);
            }
        }

        return formattedData;
    }
    
    
    /* (non-Javadoc)
     * @see com.cannontech.simplereport.SimpleReportService#getReportDefinition(javax.servlet.http.HttpServletRequest)
     */
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
    @SuppressWarnings("unchecked")
    public BareReportModel getReportModel(YukonReportDefinition<BareReportModel> reportDefinition, Map<String, String> parameterMap) throws Exception {
        
        BareReportModel reportModel = reportDefinition.createBean();
        
        // set parameters on model
        InputRoot inputRoot = reportDefinition.getInputs();
        
        InputUtil.applyProperties(inputRoot, reportModel, parameterMap);
        
        // if model instanceof LoadalbleModel, load it
        if(reportModel instanceof LoadableModel) {
            ((LoadableModel)reportModel).loadData();
        }
        
        return reportModel;
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.simplereport.SimpleReportService#getReportModel(com.cannontech.simplereport.YukonReportDefinition, javax.servlet.http.HttpServletRequest)
     */
    @SuppressWarnings("unchecked")
    public BareReportModel getStringReportModel(final YukonReportDefinition<BareReportModel> reportDefinition,
            final BareReportModel reportModel,
            Map<String, String> parameterMap,
            final LiteYukonUser user) throws Exception {
        
        final PointFormattingService cachedInstance = pointFormattingService.getCachedInstance();
        
        BareReportModel stringReportModel = new BareReportModel() {
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }

            public int getColumnCount() {
                return reportModel.getColumnCount();
            }

            public String getColumnName(int columnIndex) {
                return reportModel.getColumnName(columnIndex);
            }

            public int getRowCount() {
                return reportModel.getRowCount();
            }

            public String getTitle() {
                return reportModel.getTitle();
            }

            public Object getValueAt(int rowIndex, int columnIndex) {
                Object objValue = reportModel.getValueAt(rowIndex, columnIndex);
                ColumnLayoutData columnLayoutData = reportDefinition.getReportLayoutData().getBodyColumns()[columnIndex];
                String formattedString = formatData(columnLayoutData.getFormat(), objValue, user, cachedInstance);
                return formattedString;
            }
            
        };
        
        return stringReportModel;
    }
   
    

    
    /* (non-Javadoc)
     * @see com.cannontech.simplereport.SimpleReportService#getFormattedData(com.cannontech.analysis.tablemodel.BareReportModel, java.util.List, com.cannontech.database.data.lite.LiteYukonUser)
     */
    public List<List<String>> getFormattedData(BareReportModel reportModel, List<ColumnInfo> ColumnInfos, LiteYukonUser user) {
        
        List<List<String>> data = new ArrayList<List<String>>();
        int columnCount = reportModel.getColumnCount();
        int rowCount = reportModel.getRowCount();
        
        PointFormattingService cachedInstance = pointFormattingService.getCachedInstance();

        for(int rowIdx = 0; rowIdx < rowCount; rowIdx++) {

            List<String> colData = new ArrayList<String>();

            for(int colIdx = 0; colIdx < columnCount; colIdx++) {

                String format = ColumnInfos.get(colIdx).getColumnFormat();
                Object dataItem = reportModel.getValueAt(rowIdx, colIdx);
                String formattedData = formatData(format, dataItem, user, cachedInstance);
                colData.add(formattedData);
            }
            data.add(colData);
        }
        return data;
    }
    
    
    /* (non-Javadoc)
     * @see com.cannontech.simplereport.SimpleReportService#buildColumnInfoListFromColumnLayoutData(com.cannontech.analysis.report.ColumnLayoutData[])
     */
    public List<ColumnInfo> buildColumnInfoListFromColumnLayoutData(ColumnLayoutData[] bodyColumns) {
        
        int totalWidth = 0;
        List<ColumnInfo> columnInfo = new ArrayList<ColumnInfo>();
        for(int i = 0; i < bodyColumns.length; i++) {

            ColumnInfo ci = new ColumnInfo();

            // column name
            ci.setColumnName(bodyColumns[i].getColumnName());

            // column width
            int width = bodyColumns[i].getWidth();
            totalWidth += width;
            ci.setColumnWidth(width);

            // column format
            ci.setColumnFormat(bodyColumns[i].getFormat());

            // column alignment
            String align = "left";
            if(bodyColumns[i].getHorizontalAlignment() != null) {
                align = elementAlignmentToHtmlAlignment(bodyColumns[i].getHorizontalAlignment());
            }
            ci.setColumnAlignment(align);

            columnInfo.add(ci);
        }
        
        for(ColumnInfo ci : columnInfo) {
            ci.calculateColumnWidthPercentage(totalWidth);
        }
        
        return columnInfo;
    }
    
    public String getReportUrl(HttpServletRequest httpRequest, String definitionName, Map<String, Object> inputValues, Map<String, String> optionalAttributeDefaults, String viewType) throws Exception{
        
        YukonReportDefinition<BareReportModel> reportDefinition = reportDefinitionFactory.getReportDefinition(definitionName);
        InputRoot inputRoot = reportDefinition.getInputs();
        
        // inputs map
        Map<String,String> propertiesMap = extractPropertiesFromAttributesMap(inputRoot, inputValues);
        
        // other optional attributes
        propertiesMap.putAll(optionalAttributeDefaults);
        
        // build safe URL query string
        String queryString = ServletUtil.buildSafeQueryStringFromMap(propertiesMap);
        
        // complete URL
        URL urlObj = new URL(httpRequest.getRequestURL().toString());

        String protocol = urlObj.getProtocol();
        String host = urlObj.getHost();
        int port = urlObj.getPort();
        
        String url = protocol + "://" + host;
        if(port > 0) {
            url += ":" + port;
        }
        url += "/spring/reports/simple/" + viewType + "?def=" + definitionName + queryString;

        url = ServletUtil.createSafeUrl(httpRequest, url);
        url = StringEscapeUtils.escapeHtml(url);
        
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
    public Map<String, String> extractPropertiesFromAttributesMap(InputRoot inputRoot, Map<String, Object> attributes){
        
        HashMap<String,String> propertiesMap = new HashMap<String,String>();
        Map<String, ? extends InputSource> inputs = inputRoot.getInputMap();
        for (Entry<String, ? extends InputSource> entry : inputs.entrySet()) {
            
            String field = entry.getKey();
            
            if(attributes.containsKey(field)) {
                
                InputType type = entry.getValue().getType();
                
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
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }

    @Required
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
    
    @Required
    public void setReportDefinitionFactory(
            YukonReportDefinitionFactory<BareReportModel> reportDefinitionFactory) {
        this.reportDefinitionFactory = reportDefinitionFactory;
    }

    @Required
    public void setPointFormattingService(
            PointFormattingService pointFormattingService) {
        this.pointFormattingService = pointFormattingService;
    }
    

//  @Override
//  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
//      this.beanFactory = beanFactory;
//  }
    
}
