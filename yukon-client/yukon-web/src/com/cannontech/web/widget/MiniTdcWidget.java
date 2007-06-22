package com.cannontech.web.widget;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

public class MiniTdcWidget extends WidgetControllerBase {
    private SimpleJdbcOperations jdbcTemplate;
    private PointDao pointDao;
    private PaoDao paoDao;
    
    @Override
    public ModelAndView identity(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView(getIdentityPath());
        
        int displayNumber = WidgetParameterHelper.getRequiredIntParameter(request, "displayNumber");
        
        String sql = "select name, title from display where displaynum = ?";
        Map<String, Object> result = jdbcTemplate.queryForMap(sql, displayNumber);
        
        mav.addObject("identity", result);
        
        return mav;
    }

    @Override
    public ModelAndView render(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView();
        
        int displayNumber = WidgetParameterHelper.getRequiredIntParameter(request, "displayNumber");
        
        String sql = "select pointid from display2waydata where displaynum = ? order by ordering";
        List<Integer> pointIdList = jdbcTemplate.query(sql, new ParameterizedRowMapper<Integer>() {
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt(1);
            }
        }, displayNumber);
        
        
        List<Map<String, Object>> pointList = new ArrayList<Map<String,Object>>(pointIdList.size());
        for (Integer pointId : pointIdList) {
            HashMap<String, Object> data = new HashMap<String, Object>();
            
            LitePoint litePoint = pointDao.getLitePoint(pointId);
            data.put("pointId", litePoint.getPointID());
            data.put("pointName", litePoint.getPointName());
            LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(litePoint.getPaobjectID());
            String formattedDeviceName = liteYukonPAO.getPaoName();
            data.put("deviceName", formattedDeviceName);
            
            pointList.add(data);
        }
        
        mav.addObject("pointList", pointList);
        
        return mav;
    }
    
    @Required
    final public void setJdbcTemplate(SimpleJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Required
    final public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
    
    @Required
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
}
