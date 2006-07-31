package com.cannontech.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.search.PointDeviceLuceneInitializer;
import com.cannontech.spring.YukonSpringHook;

public class LuceneIndexingServlet extends HttpServlet {
    boolean initialIndexDone = false;
    public LuceneIndexingServlet() {
        super();
    }
    
    @Override
    public void init(ServletConfig arg0) throws ServletException {
        final PointDeviceLuceneInitializer pdli = 
            (PointDeviceLuceneInitializer) YukonSpringHook.getBean("pointDeviceSearchInitializer");
        
        new Thread(new Runnable() {
            public void run() {
                pdli.createInitialIndex();
                initialIndexDone = true;
                CTILogger.info("Point Device index has been constructed.");
            }
        }, "LuceneIndexer").start();
    }
}
