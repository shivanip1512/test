package com.cannontech.common.bulk.processor;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.service.impl.FdrTranslationManagerServiceImpl;
import com.cannontech.common.csvImport.CsvImportResult;
import com.cannontech.common.csvImport.CsvImportResultType;
import com.cannontech.common.csvImport.ImportAction;
import com.cannontech.common.csvImport.ImportFileFormat;
import com.cannontech.common.csvImport.ImportFileValidator;
import com.cannontech.common.csvImport.ImportRow;
import com.cannontech.common.csvImport.ImportValidationResult;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.ImportPaoType;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.point.PointBuilderFactory;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.i18n.YukonMessageSourceResolvable;

import static com.cannontech.common.bulk.model.PointImportParameters.*;

public abstract class PointImportProcessor extends SingleProcessor<ImportRow> {
    protected static final Logger log = YukonLogManager.getLogger(FdrTranslationManagerServiceImpl.class);
    protected MessageSourceAccessor messageSourceAccessor;
    protected ImportFileFormat format;
    protected PaoDao paoDao;
    protected PointDao pointDao;
    protected PointBuilderFactory pointBuilderFactory;
    private DBPersistentDao dbPersistentDao;

    public PointImportProcessor(ImportFileFormat format,
            MessageSourceAccessor messageSourceAccessor,
            PaoDao paoDao,
            PointDao pointDao,
            DBPersistentDao dbPersistentDao,
            PointBuilderFactory pointBuilderFactory) {
        this.format = format;
        this.messageSourceAccessor = messageSourceAccessor;
        this.paoDao = paoDao;
        this.pointDao = pointDao;
        this.dbPersistentDao = dbPersistentDao;
        this.pointBuilderFactory = pointBuilderFactory;
    }

    @Override
    public void process(ImportRow row) throws ProcessingException {
        ImportValidationResult validationResult = ImportFileValidator.validateRow(row, format);
        if (validationResult.isFailed()) {
            throwProcessingException(validationResult);
        } else {
            ImportAction action = ImportAction.valueOf(row.getValue(ACTION.NAME));
            switch (action) {
            case ADD:
                createPoint(row);
                break;
            case REMOVE:
                removePoint(row);
                break;
            default:
                String error = messageSourceAccessor.getMessage("yukon.exception.processingException.unhandledAction", action);
                throw new ProcessingException(error, "unhandledAction", action);
            }
        }
    }

    protected void throwProcessingException(ImportValidationResult result) {
        CsvImportResultType resultType;
        if (result.isBadValue()) {
            resultType = CsvImportResultType.BAD_DATA;
        } else if (result.isMissingValue()) {
            resultType = CsvImportResultType.MISSING_DATA;
        } else {
            throw new ProcessingException("Unknown validation error: " + result.getType(),
                    "validationError",
                    result.getType());
        }

        YukonMessageSourceResolvable resolvable = CsvImportResult.getImportResultMessage(resultType, result.getInvalidColumns());
        String errorMessage = messageSourceAccessor.getMessage(resolvable);
        throw new ProcessingException(errorMessage, resultType.toString());
    }

    protected int validatePaoAndPoint(String deviceName, PaoType paoType, String pointName) {
        YukonPao pao = paoDao.findYukonPao(deviceName, paoType);

        Integer paoId = null;
        // make sure the pao we're attaching the point to exists
        if (pao != null) {
            paoId = pao.getPaoIdentifier().getPaoId();
        } else {
            String error = messageSourceAccessor.getMessage("yukon.exception.processingException.invalidDevice", deviceName);
            throw new ProcessingException(error, "invalidDevice", deviceName);
        }

        // make sure the Point name is not more than 60 chars
        if (pointName.length() > 60) {
            String error = messageSourceAccessor.getMessage("yukon.exception.processingException.pointNameMaxLength",
                    pointName, deviceName);
            throw new ProcessingException(error, "pointNameMaxLength", pointName, deviceName);
        }

        // point should not already exist
        if (pointDao.findPointByName(pao, pointName) != null) {
            String error = messageSourceAccessor.getMessage("yukon.exception.processingException.pointExists", pointName,
                    deviceName);
            throw new ProcessingException(error, "pointExists", pointName, deviceName);
        }

        return paoId;
    }

    protected void removePoint(ImportRow row) {
        // find the device
        String deviceName = row.getValue(DEVICE_NAME.NAME);
        PaoType paoType = ImportPaoType.valueOf(row.getValue(DEVICE_TYPE.NAME));
        YukonPao pao = paoDao.findYukonPao(deviceName, paoType);
        if (pao == null) {
            String error = messageSourceAccessor.getMessage("yukon.exception.processingException.invalidDevice", deviceName);
            throw new ProcessingException(error, "invalidDevice", deviceName);
        }

        // find the point
        String pointName = row.getValue(POINT_NAME.NAME);
        LitePoint litePoint = pointDao.findPointByName(pao, pointName);
        if (litePoint == null) {
            String error = messageSourceAccessor.getMessage("yukon.exception.processingException.pointDoesNotExist", pointName,
                    deviceName);
            throw new ProcessingException(error, "pointDoesNotExist", pointName, deviceName);
        }

        // delete it
        try {
            PointBase heavyPoint = PointFactory.findPoint(litePoint.getPointID());
            dbPersistentDao.performDBChange(heavyPoint, TransactionType.DELETE);
        } catch (PersistenceException e) {
            log.error("An unexpected error occurred deleting point \"" + pointName + "\" on device \"" + deviceName, e);
            String error = messageSourceAccessor.getMessage("yukon.exception.processingException.unknownError");
            throw new ProcessingException(error, "unknownError");
        }
    }

    protected abstract void createPoint(ImportRow row);
}
