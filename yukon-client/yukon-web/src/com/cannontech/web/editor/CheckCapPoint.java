package com.cannontech.web.editor;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("/checkCapPoint/*")
@RequestMapping("/checkCapPoint/*")
public class CheckCapPoint {
    
    private JdbcTemplate jdbcTemplate;
    
    @RequestMapping("checkPoint")
    public void checkPoint(HttpServletResponse response, int pointID) throws Exception {
        StringBuffer query  = new StringBuffer();
        query.append("select count(*) from capbank, point "); 
        query.append("where controldeviceid = paobjectid "); 
        query.append("and "); 
        query.append("pointid=?");
        int numberUsingThisPoint = jdbcTemplate.queryForInt(query.toString(), pointID);
        PrintWriter writer = response.getWriter();
        writer.write("" + numberUsingThisPoint);
        writer.flush();
    }
    
    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}