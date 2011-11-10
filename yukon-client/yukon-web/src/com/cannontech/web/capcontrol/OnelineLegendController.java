package com.cannontech.web.capcontrol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dao.YukonImageDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.util.CapControlConst;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller("/oneline/legend/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class OnelineLegendController {
    private StateDao stateDao;
    private YukonImageDao yukonImageDao;
    private String[] excludeStateList = {"Verify All", "Verify Stop"};

    @SuppressWarnings("unchecked")
    @RequestMapping
    public String legend(ModelMap model) {
        LiteStateGroup capBankState = stateDao.getLiteStateGroup(CapControlConst.CAPBANKSTATUS_STATEGROUP_ID);
        List<LiteState> capBankStateList = capBankState.getStatesList();
        
        List<LiteState> onelineStateList = new ArrayList<LiteState>();

        LiteStateGroup onelineSubstateList = stateDao.getLiteStateGroup(CapControlConst.ONELINE_SUBSTATE_STATEGROUP_ID);
        if (onelineSubstateList != null) onelineStateList.addAll(onelineSubstateList.getStatesList());
        
        LiteStateGroup onelineVerifyStateGroupList = stateDao.getLiteStateGroup(CapControlConst.ONELINE_VERIFY_STATEGROUP_ID);
        if (onelineVerifyStateGroupList != null) onelineStateList.addAll(onelineVerifyStateGroupList.getStatesList());

        onelineStateList = cleanStateList(onelineStateList);
        
        Map<Integer,String> imageNameMap = createImageNameMap(capBankStateList, onelineStateList);
        
        model.addAttribute("imageNameMap", imageNameMap);
        model.addAttribute("capBankStateList", capBankStateList);
        model.addAttribute("onelineStateList", onelineStateList);
        
        return "oneline/cbcOnelineLegend.jsp";
    }

    private Map<Integer,String> createImageNameMap(final List<LiteState>... lists) {
        final Map<Integer,String> imageNameMap = new HashMap<Integer,String>();
        for (final List<LiteState> list : lists) {
            for (final LiteState state : list) {
                int key = state.getImageID();
                LiteYukonImage image = yukonImageDao.getLiteYukonImage(key);
                String value = (image != null) ? image.getImageName() : "X.gif";
                imageNameMap.put(key, value);
            }
        }
        return imageNameMap;
    }
    
    private List<LiteState> cleanStateList(final List<LiteState> onelineStateList) {
        final List<LiteState> tempList = new ArrayList<LiteState>(onelineStateList.size());
        for (final LiteState state : onelineStateList) {
            String stateText = state.getStateText();
            if (!isExcluded(stateText)) tempList.add(state);
        }
        return tempList;
    }
    
    private boolean isExcluded(final String stateText) {
        for (final String excludeName : excludeStateList) {
            if (excludeName.equalsIgnoreCase(stateText)) return true;
        }
        return false;
    }
    
    @Autowired
    public void setStateDao(StateDao stateDao) {
        this.stateDao = stateDao;
    }
    
    @Autowired
    public void setYukonImageDao(YukonImageDao yukonImageDao) {
        this.yukonImageDao = yukonImageDao;
    }

}