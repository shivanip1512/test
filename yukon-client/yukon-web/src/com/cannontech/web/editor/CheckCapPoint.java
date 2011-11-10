package com.cannontech.web.editor;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller("/checkCapPoint/*")
public class CheckCapPoint {
    
    private JdbcTemplate jdbcTemplate;
    
    @RequestMapping
    public void checkPoint(HttpServletResponse response, int pointID) throws Exception {
        StringBuffer query  = new StringBuffer();
        query.append("select count(*) from capbank, point "); 
        query.append("where controldeviceid = paobjectid "); 
        query.append("and "); 
        query.append("pointid=?");
        int numberUsingThisPoint = jdbcTemplate.queryForInt(query.toString(), new Integer[] {new Integer (pointID)});
        PrintWriter writer = response.getWriter();
        writer.write("" + numberUsingThisPoint);
        writer.flush();
    }
    
    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}