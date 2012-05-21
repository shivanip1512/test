package com.cannontech.capcontrol.creation.model;

import org.jdom.Element;
import org.jdom.Namespace;

import com.cannontech.capcontrol.creation.CapControlImporterCbcField;
import com.cannontech.i18n.YukonMessageSourceResolvable;

public class CbcImportInvalidDataResult implements CbcImportResult {

    private final CapControlImporterCbcField invalidField;
    private final CbcImportResultType resultType;
    
    public CbcImportInvalidDataResult(CbcImportResultType resultType) {
        this.resultType = resultType;
        this.invalidField = getFieldForType(resultType);
    }
    
    private CapControlImporterCbcField getFieldForType(CbcImportResultType resultType) {
        switch(resultType) {
        case INVALID_IMPORT_ACTION:
            return CapControlImporterCbcField.IMPORT_ACTION;
        case INVALID_PARENT:
            return CapControlImporterCbcField.CAPBANK_NAME;
        case INVALID_TYPE:
            return CapControlImporterCbcField.CBC_TYPE;
        case INVALID_COMM_CHANNEL:
            return CapControlImporterCbcField.COMM_CHANNEL;
        case INVALID_SERIAL_NUMBER:
            return CapControlImporterCbcField.CBC_SERIAL_NUMBER;
        default:
            return null;
        }
    }
    
    @Override
    public Element getResponseElement(Namespace ns) {
        Element response = new Element("cbcImportResponse", ns);
        
        Element resultTypeElement = new Element("cbcImportResultType", ns);
        resultTypeElement.addContent(resultType.getDbString());
        response.addContent(resultTypeElement);

        Element detailElement = new Element("resultDetail", ns);
        String detail = "CBC Import failed because it has invalid data in field " + invalidField.getColumnName();
        detailElement.addContent(detail);

        response.addContent(detailElement);
        
        return response;
    }

    @Override
    public YukonMessageSourceResolvable getMessage() {
        String key = "yukon.web.modules.capcontrol.import.invalidDataResult";
        
        return new YukonMessageSourceResolvable(key, resultType.getDbString(), invalidField.getColumnName());
    }

    @Override
    public CbcImportResultType getResultType() {
        return resultType;
    }
}
