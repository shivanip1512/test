package com.cannontech.web.cbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dao.YukonImageDao;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.yukon.cbc.CapControlConst;

public class OnelineLegendController implements Controller {
    private StateDao stateDao;
    private YukonImageDao yukonImageDao;

    @SuppressWarnings("unchecked")
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        
        LiteStateGroup capBankState = stateDao.getLiteStateGroup(CapControlConst.CAPBANKSTATUS_STATEGROUP_ID);
        List<LiteState> capBankStateList = capBankState.getStatesList();
        
        List<LiteState> onelineStateList = new ArrayList<LiteState>();

        LiteStateGroup onelineSubstateList = stateDao.getLiteStateGroup(CapControlConst.ONELINE_SUBSTATE_STATEGROUP_ID);
        if (onelineSubstateList != null) onelineStateList.addAll(onelineSubstateList.getStatesList());
        
        LiteStateGroup onelineVerifyStateGroupList = stateDao.getLiteStateGroup(CapControlConst.ONELINE_VERIFY_STATEGROUP_ID);
        if (onelineVerifyStateGroupList != null) onelineStateList.addAll(onelineVerifyStateGroupList.getStatesList());

        Map<Integer,String> imageNameMap = createImageNameMap(capBankStateList, onelineStateList);
        
        mav.addObject("imageNameMap", imageNameMap);
        mav.addObject("capBankStateList", capBankStateList);
        mav.addObject("onelineStateList", onelineStateList);
        mav.setViewName("oneline/cbcOnelineLegend");
        return mav;
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
    
    public void setStateDao(StateDao stateDao) {
        this.stateDao = stateDao;
    }
    
    public void setYukonImageDao(YukonImageDao yukonImageDao) {
        this.yukonImageDao = yukonImageDao;
    }

}
