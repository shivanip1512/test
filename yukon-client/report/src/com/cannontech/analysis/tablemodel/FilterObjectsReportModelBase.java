package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.controller.FilterObjectsMapSource;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.filters.impl.UserAccessCacheFilter;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.capcontrol.LiteCapControlStrategy;
import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
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
            
            CapControlCache capControlCache = YukonSpringHook.getBean("capControlCache", CapControlCache.class);
            UserAccessCacheFilter cacheFilter = new UserAccessCacheFilter(liteUser);
            
            List<Area> areaList = capControlCache.getCbcAreas();
            List<Integer> areasToHide = new ArrayList<Integer>();
            
            for ( StreamableCapObject area : areaList ) {
                if ( !cacheFilter.valid(area) ) {
                    areasToHide.add(area.getCcId());
                }
            }
            
            //if areasToHide is empty then no validation is required
            if ( areasToHide.isEmpty() ) {
                for (ReportFilter filter : filterModelTypes) {
                    List<? extends Object> objectsByModelType = ReportFuncs.getObjectsByModelType(filter, userId);
                    result.put(filter, objectsByModelType);
                }
                return result;
            }
            
            //areasToHide is not empty and we need to hide objects from the user
            for (ReportFilter filter : filterModelTypes) {
                List<? extends Object> objectsByModelType = ReportFuncs.getObjectsByModelType(filter, userId);
                List<LiteYukonPAObject> objectsToRemove = new ArrayList<LiteYukonPAObject>();
                
                for ( Object obj : objectsByModelType ) {
                    int objId = 0;
                    if ( filter.equals(ReportFilter.STRATEGY) ) {
                        objId = ((LiteCapControlStrategy)obj).getStrategyId();
                    } else {
                        objId = ((LiteYukonPAObject)obj).getPaoIdentifier().getPaoId();
                    }
                    
                    try {
                        objId = capControlCache.getParentAreaID( objId );
                        if ( areasToHide.contains( objId ) ) {
                            objectsToRemove.add((LiteYukonPAObject)obj);
                        }
                    } catch (NotFoundException ignore) {} //orphan objects are shown by default
                }
                
                objectsByModelType.removeAll(objectsToRemove);
                result.put(filter, objectsByModelType);
            }
            
            return result;
        }
    }
}
