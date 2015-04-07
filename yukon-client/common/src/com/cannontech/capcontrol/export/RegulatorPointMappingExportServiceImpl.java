package com.cannontech.capcontrol.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.RegulatorPointMapping;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.google.common.collect.ImmutableSet;

public class RegulatorPointMappingExportServiceImpl implements RegulatorPointMappingExportService {

    @Autowired PaoDao paoDao;
    
    private final static char separator = '-';
    private final static String extension = ".csv";
    
    @Override
    public File generateCsv(String filename, List<Integer> regulatorIds) throws IOException {
        
        DateTimeFormatter fmt = DateTimeFormat.forPattern("_yyyyMMdd_HHmmss");
        String completeFilename = filename + fmt.print(new Instant()) + extension;

        File csvFile = new File(completeFilename);
        FileOutputStream out = new FileOutputStream(csvFile);
        OutputStreamWriter writer = new OutputStreamWriter(out);
        
        Map<PaoIdentifier, LiteYukonPAObject> regulators = paoDao.getLiteYukonPaosById(paoDao.getPaoIdentifiersForPaoIds(regulatorIds));
        for (PaoIdentifier regulatorIdentifier : regulators.keySet()) {
            LiteYukonPAObject regulator = regulators.get(regulatorIdentifier);
            if (regulator.getPaoType().isRegulator()) {
                String regulatorName = regulator.getPaoName();
                
                ImmutableSet<RegulatorPointMapping> pointMappingsForPaoType = 
                        RegulatorPointMapping.getPointMappingsForPaoType(regulator.getPaoType());
                
                // Filter the list?
                // Sort the list?
                
                for (RegulatorPointMapping pointMapping : pointMappingsForPaoType) {
                    writer.write(regulatorName + separator + pointMapping.getMappingString() + '\n');
                }
            }
        }
        writer.close();
        out.close();
        
        return csvFile;
    }
}
