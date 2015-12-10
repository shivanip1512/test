package com.cannontech.capcontrol.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.RegulatorPointMapping;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;

public class RegulatorPointMappingExportServiceImpl implements RegulatorPointMappingExportService {

    @Autowired private IDatabaseCache dbCache;
    @Autowired private DateFormattingService dateFormattingService;
    
    private final static char separator = '-';
    private final static String extension = ".csv";
    
    @Override
    public File generateCsv(String filename, Collection<Integer> regulatorIds, YukonUserContext userContext) throws IOException {

        String timestamp = dateFormattingService.format(new Instant(), DateFormatEnum.FILE_TIMESTAMP, userContext);
        String completeFilename = filename + '_' + timestamp + extension;

        Map<Integer, LiteYukonPAObject> allPaos = dbCache.getAllPaosMap();
        List<LiteYukonPAObject> regulators = new ArrayList<>();
        for (Integer id : regulatorIds) {
            LiteYukonPAObject pao = allPaos.get(id);
            if (pao.getPaoType().isRegulator()) {
                regulators.add(pao);
            }
        }
        
        File csvFile = new File(completeFilename);
        try (FileOutputStream out = new FileOutputStream(csvFile);
                OutputStreamWriter writer = new OutputStreamWriter(out);) {
            
            for (LiteYukonPAObject regulator : regulators) {
                String regulatorName = StringEscapeUtils.escapeCsv(regulator.getPaoName());
                
                Set<RegulatorPointMapping> pointMappingsForPaoType = 
                        RegulatorPointMapping.getPointMappingsForPaoType(regulator.getPaoType());
                
                for (RegulatorPointMapping pointMapping : pointMappingsForPaoType) {
                    writer.write(regulatorName + separator + pointMapping.getMappingString() + '\n');
                }
            }
        }
        
        return csvFile;
    }
}
