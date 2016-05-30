package com.cannontech.capcontrol.creation.model;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;
import org.jdom2.Namespace;

import com.cannontech.capcontrol.creation.CapControlImporterHierarchyField;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class HierarchyImportMissingDataResult implements HierarchyImportResult {
    private List<CapControlImporterHierarchyField> missingFields = Lists.newArrayList();
    private final HierarchyImportResultType resultType = HierarchyImportResultType.MISSING_DATA;
    
    private static Function<CapControlImporterHierarchyField, String> colNameOfField =
            new Function<CapControlImporterHierarchyField, String>() {
                @Override
                public String apply(CapControlImporterHierarchyField input) {
                    return input.getColumnName();
                }
            };
    
    public HierarchyImportMissingDataResult(CapControlImporterHierarchyField missingField) {
        missingFields.add(missingField);
    }
    
    public HierarchyImportMissingDataResult(Collection<CapControlImporterHierarchyField> missingFields) {
        this.missingFields.addAll(missingFields);
    }
    
    @Override
    public Element getResponseElement(Namespace ns) {
        Element response = new Element("hierarchyImportResponse", ns);
        
        Element resultTypeElement = new Element("hierarchyImportResultType", ns);
        resultTypeElement.addContent(resultType.getDbString());
        response.addContent(resultTypeElement);

        Element detailElement = new Element("resultDetail", ns);
        String detail = "Hierarchy Import failed because it was missing required field(s): ";
        Iterable<String> columnNames = Iterables.transform(missingFields, colNameOfField);
        detail += StringUtils.join(columnNames.iterator(), ", ");
        detailElement.addContent(detail);

        response.addContent(detailElement);
        
        return response;
    }

    @Override
    public YukonMessageSourceResolvable getMessage() {
        String key = "yukon.web.modules.capcontrol.import.missingDataResult";
        Iterable<String> columnNames = Iterables.transform(missingFields, colNameOfField);
        String missingFields = StringUtils.join(columnNames.iterator(), ", ");
        
        return new YukonMessageSourceResolvable(key, resultType.getDbString(), missingFields);
    }

    @Override
    public HierarchyImportResultType getResultType() {
        return resultType;
    }

}
