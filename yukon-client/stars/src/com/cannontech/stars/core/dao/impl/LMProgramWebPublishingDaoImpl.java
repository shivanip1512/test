package com.cannontech.stars.core.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.cannontech.database.data.lite.stars.LiteLMProgramEvent;
import com.cannontech.database.data.lite.stars.LiteLMProgramWebPublishing;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.data.stars.event.LMProgramEvent;
import com.cannontech.stars.core.dao.LMProgramWebPublishingDao;

public class LMProgramWebPublishingDaoImpl implements LMProgramWebPublishingDao {
    
    @Override
    @Transactional(readOnly = true)
    public List<LiteLMProgramEvent> getProgramHistory(final int accountId, List<Integer> energyCompanyIds) {
        
        final LMProgramEvent[] events = LMProgramEvent.getAllLMProgramEvents(accountId);
        if (events == null) return Collections.emptyList();
        
        final List<LiteLMProgramWebPublishing> allProgs = new ArrayList<LiteLMProgramWebPublishing>();
        for (Integer energyCompanyId : energyCompanyIds) {
            allProgs.addAll(getAllWebPublishing(energyCompanyId));
        }

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
    public List<LiteStarsLMProgram> getPrograms(
            LiteStarsCustAccountInformation account,
            List<Integer> energyCompanyIds) {
        
        final List<LiteLMProgramWebPublishing> allPrograms = new ArrayList<LiteLMProgramWebPublishing>();
        for (Integer energyCompanyId : energyCompanyIds) {
            allPrograms.addAll(getAllWebPublishing(energyCompanyId));
        }
        final Map<Integer, LiteLMProgramWebPublishing> allProgramsMap = toProgramIdMap(allPrograms);

        final List<LiteLMProgramEvent> progHist = getProgramHistory(account.getAccountID(), energyCompanyIds);
        
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
    
    @Override
    @Transactional(readOnly = true)
    public List<LiteLMProgramWebPublishing> getAllWebPublishing(int energyCompanyId) {

        final List<LiteLMProgramWebPublishing> pubPrograms = new ArrayList<LiteLMProgramWebPublishing>();
        
        com.cannontech.database.db.stars.appliance.ApplianceCategory[] appCats =
                com.cannontech.database.db.stars.appliance.ApplianceCategory.getAllApplianceCategories(energyCompanyId);
                
        for (int i = 0; i < appCats.length; i++) {
            
            com.cannontech.database.db.stars.LMProgramWebPublishing[] pubProgs =
                    com.cannontech.database.db.stars.LMProgramWebPublishing.getAllLMProgramWebPublishing( appCats[i].getApplianceCategoryID().intValue() );
            
            for (int j = 0; j < pubProgs.length; j++) {
                LiteLMProgramWebPublishing program = (LiteLMProgramWebPublishing) StarsLiteFactory.createLite(pubProgs[j]);
                if (program != null){
                    pubPrograms.add( program );
                }
            }
        }
        
        return pubPrograms;
    }

    private Map<Integer, LiteLMProgramWebPublishing> toProgramIdMap(List<LiteLMProgramWebPublishing> list) {
        final Map<Integer, LiteLMProgramWebPublishing> resultMap = 
            new HashMap<Integer, LiteLMProgramWebPublishing>(list.size());
        
        for (final LiteLMProgramWebPublishing program : list) {
            Integer programId = program.getProgramID();
            resultMap.put(programId, program);
        }
        
        return resultMap;
    }
    
}
