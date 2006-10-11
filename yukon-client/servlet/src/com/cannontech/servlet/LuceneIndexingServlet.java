package com.cannontech.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.cannontech.common.search.index.IndexBuilder;
import com.cannontech.spring.YukonSpringHook;

public class LuceneIndexingServlet extends HttpServlet {
    public LuceneIndexingServlet() {
        super();
    }
    
    @Override
    public void init(ServletConfig arg0) throws ServletException {
        final IndexBuilder builder = (IndexBuilder) YukonSpringHook.getBean("indexBuilder");

        new Thread(new Runnable() {
            public void run() {
                builder.buildAllIndexes(false);
            }
        }, "LuceneIndexer").start();
    }
}
