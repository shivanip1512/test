package com.cannontech.web.editor.point;

import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.point.alarm.dao.PointPropertyValueDao;
import com.cannontech.common.point.alarm.model.PointPropertyValue;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.spring.YukonSpringHook;

public class StaleData {
    
    public static enum UpdateStyle implements DisplayableEnum, DatabaseRepresentationSource {
        ALWAYS(0),
        ON_CHANGE(1);
        
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
    private Integer updateStyle = 0;
    private Integer time = 5;
    private boolean enabled = false;
    private PointBase point = null;
    
    public static final int TIME_PROPERTY = 1;
    public static final int UPDATE_PROPERTY = 2;

    public StaleData() {}
    
    public StaleData(PointBase point) {
        this.point = point;
        init();
    }
    
    private void init() {
        PointPropertyValue attrib1;
        PointPropertyValue attrib2;
        boolean staleEnabled = false;
        PointPropertyValueDao dao = YukonSpringHook.getBean( "pointPropertyValueDao", PointPropertyValueDao.class);
        try{
            attrib1 = dao.getByIdAndPropertyId(point.getPoint().getPointID(), 1);
            attrib2 = dao.getByIdAndPropertyId(point.getPoint().getPointID(), 2);
            staleEnabled = true;
        }catch( DataAccessException e) {
            attrib1 = new PointPropertyValue(point.getPoint().getPointID(),1,5);
            attrib2 = new PointPropertyValue(point.getPoint().getPointID(),2,1);
        }
        setTime((int)attrib1.getFloatValue());
        setUpdateStyle((int)attrib2.getFloatValue()-1);
        
        if( staleEnabled ) {
            setEnabled(true);
        } else {
            setEnabled(false);
        }
    }
    
    public void update() {
        PointPropertyValue attribTime = new PointPropertyValue();
        PointPropertyValue attribUpdate = new PointPropertyValue();
        
        attribTime.setPointId(point.getPoint().getPointID());
        attribTime.setPointPropertyCode(1);
        attribTime.setFloatValue(getTime().floatValue());
        
        attribUpdate.setPointId(point.getPoint().getPointID());
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    } 
}
