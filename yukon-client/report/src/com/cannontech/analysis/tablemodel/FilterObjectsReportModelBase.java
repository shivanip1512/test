package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.controller.FilterObjectsMapSource;
import com.cannontech.cbc.dao.AreaDao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.UserUtils;

public abstract class FilterObjectsReportModelBase<E> extends ReportModelBase<E> implements FilterObjectsMapSource {
    
    public FilterObjectsReportModelBase() {
        super();
    }
    
    public FilterObjectsReportModelBase(Date start, Date stop) {
        super(start, stop);
    }
    
    public LinkedHashMap<ReportFilter,List<? extends Object>> getFilterObjectsMap(int userId) {
        LinkedHashMap<ReportFilter, List<? extends Object>> result = new LinkedHashMap<ReportFilter, List<? extends Object>>();
        ReportFilter[] filterModelTypes = getFilterModelTypes();
        return getFilteredCapControlObjectsMap(userId, result, filterModelTypes);
    }
    
    public static LinkedHashMap<ReportFilter, List<? extends Object>> getFilteredCapControlObjectsMap(int userId, LinkedHashMap<ReportFilter, List<? extends Object>> result, ReportFilter[] filterModelTypes) {
        if (filterModelTypes == null) {
            return result;
        } else {
            LiteYukonUser liteUser = null;
            YukonUserDao yukonUserDao = YukonSpringHook.getBean("yukonUserDao", YukonUserDao.class);
            if(userId > UserUtils.USER_DEFAULT_ID) {
                liteUser = yukonUserDao.getLiteYukonUser(userId);
            }
            PaoAuthorizationService paoAuthService = YukonSpringHook.getBean("paoAuthorizationService", PaoAuthorizationService.class);
            
            List<YukonPao> areasToHide = new ArrayList<YukonPao>();
            AreaDao areaDao = YukonSpringHook.getBean("areaDao", AreaDao.class);
            
            for ( PaoIdentifier area :  areaDao.getAllAreas()) {
                if ( !paoAuthService.isAuthorized(liteUser, Permission.PAO_VISIBLE, area) ) {
                    areasToHide.add(area);
                }
            }
            
            //if the user can see all the areas then just throw everything in result now.. skipping the filter logic below
            if ( areasToHide.isEmpty() ) {
                for (ReportFilter filter : filterModelTypes) {
                    List<? extends Object> objectsByModelType = ReportFuncs.getObjectsByModelType(filter, userId);
                    result.put(filter, objectsByModelType);
                }
                return result;
            }
            
            //filter out the CC objects the user shouldn't see
            for (ReportFilter filter : filterModelTypes) {
                List<? extends Object> objectsByModelType = ReportFuncs.getValidCapControlObjectsByModelType(filter, userId);
                result.put(filter, objectsByModelType);
            }
            
            return result;
        }
    }
}
