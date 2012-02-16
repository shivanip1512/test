package com.cannontech.capcontrol.creation.model;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jdom.Namespace;

import com.cannontech.capcontrol.creation.CapControlImporterCbcField;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class CbcImportMissingDataResult implements CbcImportResult {
    private List<CapControlImporterCbcField> missingFields = Lists.newArrayList();
    private final CbcImportResultType resultType = CbcImportResultType.MISSING_DATA;
    
    private static Function<CapControlImporterCbcField, String> colNameOfField =
        new Function<CapControlImporterCbcField, String>() {
            @Override
            public String apply(CapControlImporterCbcField input) {
                return input.getColumnName();
            }
        };
    
    public CbcImportMissingDataResult(CapControlImporterCbcField missingField) {
        missingFields.add(missingField);
    }
    
    public CbcImportMissingDataResult(Collection<CapControlImporterCbcField> fields) {
        missingFields.addAll(fields);
    }
    
    public List<CapControlImporterCbcField> getMissingFields() {
        return missingFields;
    }
    
    @Override
    public CbcImportResultType getResultType() {
        return resultType;
    }
    
    @Override
    public Element getResponseElement(Namespace ns) {
        Element response = new Element("cbcImportResponse", ns);
        
        Element cbcResult = new Element("cbcImportResultType", ns);
        cbcResult.addContent(resultType.getDbString());
        response.addContent(cbcResult);
        
        Element detailElement = new Element("resultDetail", ns);
        String detail = "CBC Import failed because it was missing required field(s): ";
        Iterable<String> columnNames = Iterables.transform(missingFields, colNameOfField);
        detail += StringUtils.join(columnNames.iterator(), ", ");
        detailElement.addContent(detail);
        
        response.addContent(detailElement);
        
        return response;
    }

    @Override
    public YukonMessageSourceResolvable getResolvable() {
        String key = "yukon.web.modules.capcontrol.import.missingDataResult";
        Iterable<String> columnNames = Iterables.transform(missingFields, colNameOfField);
        String missingFields = StringUtils.join(columnNames.iterator(), ", ");
        
        return new YukonMessageSourceResolvable(key, resultType.getDbString(), missingFields);
    }
}
