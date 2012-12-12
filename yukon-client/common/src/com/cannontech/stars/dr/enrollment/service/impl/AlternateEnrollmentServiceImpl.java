package com.cannontech.stars.dr.enrollment.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.common.util.Pair;
import com.cannontech.database.TransactionTemplateHelper;
import com.cannontech.stars.dr.displayable.dao.DisplayableEnrollmentDao;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.enrollment.service.AlternateEnrollmentService;
import com.cannontech.stars.dr.enrollment.service.EnrollmentHelperService;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.ProgramToAlternateProgramDao;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.hardware.model.ProgramToAlternateProgram;
import com.cannontech.stars.dr.program.dao.ProgramDao;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class AlternateEnrollmentServiceImpl implements AlternateEnrollmentService {
	
	@Autowired private DisplayableEnrollmentDao deDao;
    @Autowired private ProgramToAlternateProgramDao ptapDao;
    @Autowired private ProgramDao programDao;
    @Autowired private EnrollmentDao enrollmentDao;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private EnrollmentHelperService enrollmentHelperService;
    @Autowired private TransactionTemplate transactionTemplate;

	@Override
	public void buildEnrollmentMaps(int accountId, Map<HardwareSummary, Pair<Set<Program>, Set<Program>>> available, Map<HardwareSummary, Pair<Set<Program>, Set<Program>>> active) {
		
		List<ProgramToAlternateProgram> all = ptapDao.getAll();
        ImmutableBiMap.Builder<Program, Program> b = ImmutableBiMap.builder();
        Map<Integer, Program> normal = Maps.newHashMap(); 
        Map<Integer, Program> alternate = Maps.newHashMap(); 
        for (ProgramToAlternateProgram mapping : all) {
            Program normalProgram = programDao.getByProgramId(mapping.getAssignedProgramId());
            Program alternateProgram = programDao.getByProgramId(mapping.getAlternateProgramId());
            b.put(normalProgram, alternateProgram);
            normal.put(normalProgram.getProgramId(), normalProgram);
            alternate.put(alternateProgram.getProgramId(), alternateProgram);
        }
        /* normal program to alternate program */
        BiMap<Program, Program> mappings = b.build();
        
        for (ProgramEnrollment enrollment : enrollmentDao.getActiveEnrollmentsByAccountId(accountId)) {
        	if (normal.containsKey(enrollment.getAssignedProgramId())) {
        		HardwareSummary summary = inventoryDao.findHardwareSummaryById(enrollment.getInventoryId());
        		Pair<Set<Program>, Set<Program>> pair = available.get(summary);
        		if (pair == null) {
        			Set<Program> normalList = Sets.newHashSet();
        			Set<Program> alternateList = Sets.newHashSet();
        			pair = Pair.of(normalList, alternateList);
        		}
        		Program normalProgram = normal.get(enrollment.getAssignedProgramId());
        		pair.first.add(normalProgram);
        		pair.second.add(mappings.get(normalProgram));
        		available.put(summary, pair);
        	} else if (alternate.containsKey(enrollment.getAssignedProgramId())) {
        		HardwareSummary summary = inventoryDao.findHardwareSummaryById(enrollment.getInventoryId());
        		Pair<Set<Program>, Set<Program>> pair = active.get(summary);
        		if (pair == null) {
        			Set<Program> normalList = Sets.newHashSet();
        			Set<Program> alternateList = Sets.newHashSet();
        			pair = Pair.of(alternateList, normalList);
        		}
        		Program alternateProgram = alternate.get(enrollment.getAssignedProgramId());
        		pair.first.add(alternateProgram);
        		pair.second.add(mappings.inverse().get(alternateProgram));
        		active.put(summary, pair);
        	}
        }
		
	}
	
	@Override
	public void doEnrollment(final Integer[] alternate, final Integer[] normal, final int accountId, final YukonUserContext context) {
		List<ProgramToAlternateProgram> all = ptapDao.getAll();
        
        ImmutableBiMap.Builder<Program, Program> b = ImmutableBiMap.builder();
        final Map<Integer, Program> normalMappings = Maps.newHashMap(); 
        final Map<Integer, Program> alternateMappings = Maps.newHashMap(); 
        for (ProgramToAlternateProgram mapping : all) {
            Program normalProgram = programDao.getByProgramId(mapping.getAssignedProgramId());
            Program alternateProgram = programDao.getByProgramId(mapping.getAlternateProgramId());
            b.put(normalProgram, alternateProgram);
            normalMappings.put(normalProgram.getProgramId(), normalProgram);
            alternateMappings.put(alternateProgram.getProgramId(), alternateProgram);
        }
        /* normal program to alternate program */
        final BiMap<Program, Program> mappings = b.build();
        
        TransactionTemplateHelper.execute(transactionTemplate, new Callable<Object>() {
        	@Override
            public Object call() {
        		/* unenroll from normal programs and enroll in alternate programs */
            	if (alternate != null) {
            		List<ProgramEnrollment> unenrollFromNormal = Lists.newArrayList();
            		List<ProgramEnrollment> enrollInAlternate = Lists.newArrayList();
        	    	for (Integer inventoryId : alternate) {
        	    		for (ProgramEnrollment enrollment : enrollmentDao.getActiveEnrollmentsByInventory(inventoryId)) {
        	    			if (normalMappings.containsKey(enrollment.getAssignedProgramId())){
        	    				ProgramEnrollment unenrollFrom = enrollment.copyOf();
        	    				ProgramEnrollment enrollIn = enrollment.copyOf();
        	    				
        	    				unenrollFrom.setEnroll(false);
        	    				unenrollFromNormal.add(unenrollFrom);
        	    				
        	    				Program alternateProgram = mappings.get(normalMappings.get(enrollment.getAssignedProgramId()));
        						enrollIn.setAssignedProgramId(alternateProgram.getProgramId());
        						enrollIn.setApplianceCategoryId(alternateProgram.getApplianceCategoryId());
        						enrollIn.setLmGroupId(programDao.getLoadGroupIdForProgramId(alternateProgram.getProgramId()));
        						enrollInAlternate.add(enrollIn);
        	    			}
        	    		}
        			}
        	    	enrollmentHelperService.updateProgramEnrollments(unenrollFromNormal, accountId, context);
        	    	enrollmentHelperService.updateProgramEnrollments(enrollInAlternate, accountId, context);
            	}
            	
            	/* unenroll from alternate programs and enroll in normal programs */
            	if (normal != null) {
            		List<ProgramEnrollment> unenrollFromAlternate = Lists.newArrayList();
            		List<ProgramEnrollment> enrollInNormal = Lists.newArrayList();
        	    	for (Integer inventoryId : normal) {
        	    		for (ProgramEnrollment enrollment : enrollmentDao.getActiveEnrollmentsByInventory(inventoryId)) {
        	    			if (alternateMappings.containsKey(enrollment.getAssignedProgramId())){
        	    				ProgramEnrollment unenrollFrom = enrollment.copyOf();
        	    				ProgramEnrollment enrollIn = enrollment.copyOf();
        	    				
        	    				unenrollFrom.setEnroll(false);
        	    				unenrollFromAlternate.add(unenrollFrom);
        	    				
        	    				Program normalProgram = mappings.inverse().get(alternateMappings.get(enrollment.getAssignedProgramId()));
        						enrollIn.setAssignedProgramId(normalProgram.getProgramId());
        						enrollIn.setApplianceCategoryId(normalProgram.getApplianceCategoryId());
        						enrollIn.setLmGroupId(programDao.getLoadGroupIdForProgramId(normalProgram.getProgramId()));
        						enrollInNormal.add(enrollIn);
        	    			}
        	    		}
        			}
        	    	enrollmentHelperService.updateProgramEnrollments(unenrollFromAlternate, accountId, context);
        	    	enrollmentHelperService.updateProgramEnrollments(enrollInNormal, accountId, context);
            	}
            	
        		return null;
        	}
        });
	}
}