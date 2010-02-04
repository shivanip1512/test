package com.cannontech.web.bulk.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.database.data.point.PointType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PaoTypeMasks {
    private PaoType paoType;
    private Map<PointTemplate, Boolean> pointTemplateMaskMap;

    public PaoType getPaoType() {
        return paoType;
    }
    public void setPaoType(PaoType paoType) {
        this.paoType = paoType;
    }
    
    public Map<PointTemplate, Boolean> getPointTemplateMaskMap() {
        return pointTemplateMaskMap;
    }
    public void setPointTemplateMaskMap(Map<PointTemplate, Boolean> pointTemplateMaskMap) {
        this.pointTemplateMaskMap = pointTemplateMaskMap;
    }
    
    public HashMap<PointType, List<PointTemplateMask>> getPointTypeToPointTemplateMap() {
        HashMap<PointType, List<PointTemplateMask>> results = Maps.newHashMap();

        for (Entry<PointTemplate, Boolean> pointTemplateMaskEntry : pointTemplateMaskMap.entrySet()) {

            PointTemplateMask pointTemplateMask = new PointTemplateMask();
            pointTemplateMask.setPointTemplate(pointTemplateMaskEntry.getKey());
            pointTemplateMask.setMasked(pointTemplateMaskEntry.getValue());
            
            List<PointTemplateMask> pointTemplateMaskEntryList = 
                results.get(pointTemplateMaskEntry.getKey().getPointType());
            if (pointTemplateMaskEntryList == null){
                pointTemplateMaskEntryList = Lists.newArrayList();
                results.put(pointTemplateMaskEntry.getKey().getPointType(), pointTemplateMaskEntryList);
            }

            pointTemplateMaskEntryList.add(pointTemplateMask);
        }

        return results;
    }
    
    public static class PointTemplateMask {
        private PointTemplate pointTemplate;
        private Boolean masked;
        
        public PointTemplate getPointTemplate() {
            return pointTemplate;
        }
        public void setPointTemplate(PointTemplate pointTemplate) {
            this.pointTemplate = pointTemplate;
        }

        public Boolean getMasked() {
            return masked;
        }
        public void setMasked(Boolean masked) {
            this.masked = masked;
        }
    }
    
}