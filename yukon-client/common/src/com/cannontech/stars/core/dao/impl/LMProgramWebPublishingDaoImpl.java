package com.cannontech.stars.core.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.cannontech.stars.core.dao.LMProgramWebPublishingDao;
import com.cannontech.stars.database.data.event.LMProgramEvent;
import com.cannontech.stars.database.data.lite.LiteLMProgramEvent;
import com.cannontech.stars.database.data.lite.LiteLMProgramWebPublishing;
import com.cannontech.stars.database.data.lite.LiteStarsAppliance;
import com.cannontech.stars.database.data.lite.LiteStarsCustAccountInformation;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteStarsLMProgram;
import com.cannontech.stars.database.data.lite.StarsLiteFactory;

public class LMProgramWebPublishingDaoImpl implements LMProgramWebPublishingDao {
    
    @Override
    @Transactional(readOnly = true)
    public List<LiteLMProgramEvent> getProgramHistory(final int accountId, LiteStarsEnergyCompany energyCompany) {
        
        final LMProgramEvent[] events = LMProgramEvent.getAllLMProgramEvents(accountId);
        if (events == null) return Collections.emptyList();
        
        final Iterable<LiteLMProgramWebPublishing> allProgs = energyCompany.getAllPrograms();

        final Map<Integer, LiteLMProgramWebPublishing> programIdMap = toProgramIdMap(allProgs);
        final Set<Integer> programIdKeySet = programIdMap.keySet();

        final List<LiteLMProgramEvent> progHist = new ArrayList<LiteLMProgramEvent>(events.length);

        for (final LMProgramEvent event : events) {
            LiteLMProgramEvent liteEvent = (LiteLMProgramEvent) StarsLiteFactory.createLite(event);
            Integer programId = liteEvent.getProgramID();
            
            boolean foundProgramId = programIdKeySet.contains(programId);
            if (foundProgramId) progHist.add(liteEvent);
        }
        
        return progHist;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LiteStarsLMProgram> getPrograms(LiteStarsCustAccountInformation account,
            LiteStarsEnergyCompany energyCompany) {
        
        final Iterable<LiteLMProgramWebPublishing> allPrograms = energyCompany.getAllPrograms();
        
        final Map<Integer, LiteLMProgramWebPublishing> allProgramsMap = toProgramIdMap(allPrograms);

        final List<LiteLMProgramEvent> progHist = account.getProgramHistory();
        
        final List<LiteStarsLMProgram> programs = new ArrayList<LiteStarsLMProgram>();
                
            //TODO Refactor - this can be simplified.
            for (final LiteStarsAppliance appliance : account.getAppliances()) {
                int progID = appliance.getProgramID();
                if (progID == 0) continue;
                    
                boolean progExists = false;
                for (int j = 0; j < programs.size(); j++) {
                    if ((programs.get(j)).getProgramID() == progID) {
                         progExists = true;
                         break;
                    }
                }
                if (progExists) continue;
                    
                LiteLMProgramWebPublishing liteProg = allProgramsMap.get(progID);
                LiteStarsLMProgram prog = new LiteStarsLMProgram( liteProg );
                    
                prog.setGroupID( appliance.getAddressingGroupID() );
                prog.updateProgramStatus( progHist );
                    
                programs.add( prog );
        }
        return programs;
    }

    private Map<Integer, LiteLMProgramWebPublishing> toProgramIdMap(Iterable<LiteLMProgramWebPublishing> list) {
        final Map<Integer, LiteLMProgramWebPublishing> resultMap = 
            new HashMap<Integer, LiteLMProgramWebPublishing>();
        
        for (final LiteLMProgramWebPublishing program : list) {
            Integer programId = program.getProgramID();
            resultMap.put(programId, program);
        }
        
        return resultMap;
    }
    
}
