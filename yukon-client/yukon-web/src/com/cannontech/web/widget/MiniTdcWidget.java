package com.cannontech.web.widget;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

@Controller
@RequestMapping("/miniTdcWidget/*")
public class MiniTdcWidget extends WidgetControllerBase {
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private PointDao pointDao;
    @Autowired private PaoDao paoDao;
    
    
   
    public MiniTdcWidget() {
        
        SimpleWidgetInput simpleWidget = new SimpleWidgetInput();
        simpleWidget.setName("displayNumber");
        simpleWidget.setDescription("the number of a TDC display");
        simpleWidget.setRequired(true);
        
        addInput(simpleWidget);
        this.setIdentityPath("miniTdcWidget/identity.jsp");
    }
    
    @Override
    @RequestMapping("identity")
    public ModelAndView identity(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView(getIdentityPath());
        
        int displayNumber = WidgetParameterHelper.getRequiredIntParameter(request, "displayNumber");
        
        String sql = "select name, title from display where displaynum = ?";
        Map<String, Object> result = jdbcTemplate.queryForMap(sql, displayNumber);
        
        mav.addObject("identity", result);
        
        return mav;
    }

    @Override
    @RequestMapping("render")
    public ModelAndView render(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView();
        
        int displayNumber = WidgetParameterHelper.getRequiredIntParameter(request, "displayNumber");
        
        String sql = "select pointid from display2waydata where displaynum = ? order by ordering";
        List<Integer> pointIdList = jdbcTemplate.query(sql, new ParameterizedRowMapper<Integer>() {
            @Override
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
}
