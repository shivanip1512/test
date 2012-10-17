package com.cannontech.web.multispeak;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.multispeak.dao.MspLMInterfaceMappingDao;
import com.cannontech.multispeak.db.MspLMInterfaceMapping;
import com.cannontech.multispeak.db.MspLMInterfaceMappingStrategyNameComparator;
import com.cannontech.multispeak.db.MspLmInterfaceMappingColumnEnum;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.web.security.annotation.CheckGlobalSetting;
import com.cannontech.web.util.JsonView;

@CheckGlobalSetting(GlobalSettingType.MSP_LM_MAPPING_SETUP)
public class LMMappingsController extends  MultiActionController {

	private PaoDao paoDao;
	private MspLMInterfaceMappingDao mspLMInterfaceMappingDao;
	
	private static final MspLmInterfaceMappingColumnEnum defaultOrderedColumn = MspLmInterfaceMappingColumnEnum.STRATEGY;
	private static final boolean defaultAscending = true;
	
	// HOME
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws Exception {
       
    	ModelAndView mav = new ModelAndView();
        mav.setViewName("setup/lmMappings/home.jsp");
        
        // add all mappings
    	addAllMapppingToMav(mav, defaultOrderedColumn, defaultAscending);
        
        return mav;
    }
    
    // FIND MAPPING
    public ModelAndView findMapping(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
    	ModelAndView mav = new ModelAndView(new JsonView());
        
        String strategyName = ServletRequestUtils.getStringParameter(request, "strategyName", "");
        String substationName = ServletRequestUtils.getStringParameter(request, "substationName", "");
        
        String mappedName = findMappedName(strategyName, substationName);
        mav.addObject("mappedName", mappedName);
        
        return mav;
    }
    
    // ADD MAPPING
    public void addOrUpdateMapping(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        String strategyName = ServletRequestUtils.getStringParameter(request, "strategyName", "");
        String substationName = ServletRequestUtils.getStringParameter(request, "substationName", "");
        int mappedNameId = ServletRequestUtils.getIntParameter(request, "mappedNameId", -1);
        
        Integer existingMspLmInterfaceid = mspLMInterfaceMappingDao.findIdForStrategyAndSubstation(strategyName, substationName);
        
        if (mappedNameId > 0) {
        	if (existingMspLmInterfaceid == null) {
        		mspLMInterfaceMappingDao.add(strategyName, substationName, mappedNameId);
        	} else {
        		mspLMInterfaceMappingDao.updatePaoIdForStrategyAndSubstation(strategyName, substationName, mappedNameId);
        	}
        }
    }
    
    // RELOAD ALL MAPPINGS TABLE
    public ModelAndView reloadAllMappingsTable(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
    	ModelAndView mav = new ModelAndView();
        mav.setViewName("setup/lmMappings/allMappingsTable.jsp");
        
        String col = ServletRequestUtils.getStringParameter(request, "col", MspLmInterfaceMappingColumnEnum.STRATEGY.toString());
    	boolean ascending = ServletRequestUtils.getBooleanParameter(request, "ascending", true);
    	
    	// add all mappings
    	addAllMapppingToMav(mav, MspLmInterfaceMappingColumnEnum.valueOf(col), ascending);
    	
        return mav;
    }
    
    // REMOVE MAPPING
    public void removeMapping(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
    	int mspLMInterfaceMappingId = ServletRequestUtils.getRequiredIntParameter(request, "mspLMInterfaceMappingId");
    	mspLMInterfaceMappingDao.remove(mspLMInterfaceMappingId);
    }
    
    // HELPERS
    // returns null if mapping is not found or mapped paoId is not found
    private String findMappedName(String strategyName, String substationName) {
    	
    	String mappedName = null;
        try{
        	MspLMInterfaceMapping mapping = mspLMInterfaceMappingDao.getForStrategyAndSubstation(strategyName, substationName);
        	int paoId = mapping.getPaobjectId();
        	try {
        		mappedName = paoDao.getYukonPAOName(paoId);
        	} catch (NotFoundException e) {
        	}
        } catch (NotFoundException e) {
        }
        
        return mappedName;
    }
    
    private void addAllMapppingToMav(ModelAndView mav, MspLmInterfaceMappingColumnEnum col, boolean ascending) {
    	
    	List<MspLMInterfaceMapping> allMappings = mspLMInterfaceMappingDao.getAllMappings();
        Collections.sort(allMappings, new MspLMInterfaceMappingStrategyNameComparator(col, ascending));
        
        mav.addObject("orderedColumnName", col.toString());
        mav.addObject("ascending", ascending);
        mav.addObject("allMappings", allMappings);
    }

    
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
		this.paoDao = paoDao;
	}
    
    @Autowired
    public void setMspLMInterfaceMappingDao(
			MspLMInterfaceMappingDao mspLMInterfaceMappingDao) {
		this.mspLMInterfaceMappingDao = mspLMInterfaceMappingDao;
	}
}
