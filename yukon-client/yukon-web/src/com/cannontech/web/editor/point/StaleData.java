package com.cannontech.web.editor.point;

import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.point.alarm.dao.PointPropertyValueDao;
import com.cannontech.common.point.alarm.model.PointPropertyValue;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.cannontech.spring.YukonSpringHook;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StaleData {
    
    public static enum UpdateStyle implements DisplayableEnum, DatabaseRepresentationSource {
        ALWAYS(1),
        ON_CHANGE(2);
        
        private int updateType;
        
        UpdateStyle(int updateType) {
            this.updateType = updateType;
        }

        @Override
        public Object getDatabaseRepresentation() {
            return updateType;
        }

        @Override
        public String getFormatKey() {
            
            switch (this) {
            case ALWAYS:
                return "yukon.common.always";
            case ON_CHANGE:
                return "yukon.common.point.archiveType.ON_CHANGE";
            default:
                throw new IllegalArgumentException("UpdateStyle " + this.name() + " not internationalized" );
            }
        }
    }
    
    //private SelectItem[] updateStyles = null;
    private Integer updateStyle;
    private Integer time;
    private boolean enabled = false;
    private Integer pointId = null;
  
    public static final int TIME_PROPERTY = 1;
    public static final int UPDATE_PROPERTY = 2;

    @JsonCreator
    public StaleData(@JsonProperty("pointId") Integer pointId, @JsonProperty("updateStyle") Integer updateStyle, @JsonProperty ("time") Integer time) {
        this.pointId = pointId;
        this.updateStyle = updateStyle;
        this.time = time;
    }
    
    public StaleData() {
    }
    
    public StaleData(Integer pointId) {
        this.pointId = pointId;
        this.updateStyle = UpdateStyle.ALWAYS.updateType;
        this.time = 5;
    }
   
    public void update() {
        PointPropertyValue attribTime = new PointPropertyValue();
        PointPropertyValue attribUpdate = new PointPropertyValue();
        
        attribTime.setPointId(pointId);
        attribTime.setPointPropertyCode(1);
        attribTime.setFloatValue(getTime().floatValue());
        
        attribUpdate.setPointId(pointId);
        attribUpdate.setPointPropertyCode(2);
        attribUpdate.setFloatValue(getUpdateStyle()+1);
        PointPropertyValueDao dao = YukonSpringHook.getBean( "pointPropertyValueDao", PointPropertyValueDao.class);
        
        //remove from database
        dao.remove(attribTime);
        dao.remove(attribUpdate);
        
        //add
        if( isEnabled() ) {
            try{
                dao.add(attribTime);
                dao.add(attribUpdate);
            } catch( DataAccessException e) {   
                CTILogger.error(e.getMessage(), e);
            }
        }
    }

    public Integer getUpdateStyle() {
    	return updateStyle;
    }

    public void setUpdateStyle(Integer updateStyle) {
        this.updateStyle = updateStyle;
    }

    public Integer getTime() {
    	return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }
    
    @JsonIgnore
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    } 
    
    @JsonIgnore
    public Integer getPointId() {
        return pointId;
    }
    
    public void setPointId(Integer pointId) {
        this.pointId = pointId;
    }
    
    public StaleData overwriteWith(StaleData staleData) {
        if (getTime() != null) {
            staleData.setTime(getTime());
        }
        if (getUpdateStyle() != null) {
            staleData.setUpdateStyle(getUpdateStyle());
        }
        if (getTime() != null || getUpdateStyle() != null) {
            staleData.setEnabled(true);
        }

        return staleData;
    }
}
