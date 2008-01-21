package com.cannontech.simplereport;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.report.ColumnLayoutData;
import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.input.InputRoot;

public interface SimpleReportService {

    public abstract String formatData(String format,
                                      Object data,
                                      YukonUserContext userContext,
                                      PointFormattingService cachedPointFormatter);

    /**
     * Get report Definition using def parameter from request.
     * 
     * @param request
     * @return
     * @throws Exception
     */
    public abstract YukonReportDefinition<BareReportModel> getReportDefinition(
            HttpServletRequest request) throws Exception;

    /**
     * Get the report definition name from the request, create definition, model.
     * Return Fully loaded model.
     * 
     * @param request
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public abstract BareReportModel getReportModel(
            YukonReportDefinition<BareReportModel> reportDefinition,
            Map<String, String> parameterMap) throws Exception;
    
    public BareReportModel getStringReportModel(final YukonReportDefinition<BareReportModel> reportDefinition,
            final BareReportModel reportModel,
            Map<String, String> parameterMap,
            final YukonUserContext userContext) throws Exception;

    /**
     * Return a list of lists of the report data that has been formatted.
     * 
     * @param reportModel
     * @param columnFormats
     * @param user
     * @return
     */
    public abstract List<List<String>> getFormattedData(
            BareReportModel reportModel, List<ColumnInfo> ColumnInfos,
            YukonUserContext userContext);

    public abstract List<ColumnInfo> buildColumnInfoListFromColumnLayoutData(
            ColumnLayoutData[] bodyColumns);

    public abstract Map<String, String> extractPropertiesFromAttributesMap(InputRoot inputRoot,
                                                                           Map<String, Object> attributes);

    public abstract String getReportUrl(HttpServletRequest httpRequest,
                                        String definitionName,
                                        Map<String, Object> inputValues,
                                        Map<String, String> optionalAttributeDefaults,
                                        String viewType) throws Exception;
}