package com.cannontech.common.bulk.processor;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.csvImport.ImportFileFormat;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.point.PointBuilderFactory;
import com.cannontech.common.point.PointCalculation;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;

/**
 * Factory for building various types of point import processors. The factory automatically provides 
 * the autowired dependencies that the processors require.
 */
public class PointImportProcessorFactory {
    @Autowired PaoDao paoDao;
    @Autowired PointDao pointDao;
    @Autowired DBPersistentDao dbPersistentDao;
    @Autowired PointBuilderFactory pointBuilderFactory;
    
    public AnalogPointImportProcessor getAnalogProcessor(ImportFileFormat format, MessageSourceAccessor messageSourceAccessor) {
        return new AnalogPointImportProcessor(format, messageSourceAccessor, paoDao, pointDao, dbPersistentDao, pointBuilderFactory);
    }
    
    public StatusPointImportProcessor getStatusProcessor(ImportFileFormat format, MessageSourceAccessor messageSourceAccessor) {
        return new StatusPointImportProcessor(format, messageSourceAccessor, paoDao, pointDao, dbPersistentDao, pointBuilderFactory);
    }
    
    public AccumulatorPointImportProcessor getAccumulatorProcessor(ImportFileFormat format, MessageSourceAccessor messageSourceAccessor) {
        return new AccumulatorPointImportProcessor(format, messageSourceAccessor, paoDao, pointDao, dbPersistentDao, pointBuilderFactory);
    }
    
    public CalcStatusPointImportProcessor getCalcStatusProcessor(ImportFileFormat format, Map<String, PointCalculation> calcMap, MessageSourceAccessor messageSourceAccessor) {
        return new CalcStatusPointImportProcessor(format, calcMap, messageSourceAccessor, paoDao, pointDao, dbPersistentDao, pointBuilderFactory);
    }
    
    public CalcAnalogPointImportProcessor getCalcAnalogProcessor(ImportFileFormat format, Map<String, PointCalculation> calcMap, MessageSourceAccessor messageSourceAccessor) {
        return new CalcAnalogPointImportProcessor(format, calcMap, messageSourceAccessor, paoDao, pointDao, dbPersistentDao, pointBuilderFactory);
    }
}
