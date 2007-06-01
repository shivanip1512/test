package com.cannontech.web.editor;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class CheckCapPoint extends AbstractController {
    private JdbcTemplate jdbcTemplate;
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
      
        int pointID = ServletRequestUtils.getRequiredIntParameter(request,
        "pointID");
        StringBuffer query  = new StringBuffer();
        query.append("select count(*) from capbank, point "); 
        query.append("where controldeviceid = paobjectid "); 
        query.append("and "); 
        query.append("pointid=?");
        int numberUsingThisPoint = jdbcTemplate.queryForInt(query.toString(), new Integer[] {new Integer (pointID)});
        PrintWriter writer = response.getWriter();
        writer.write("" + numberUsingThisPoint);
        writer.flush();
        return null;
    }
    
    
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
