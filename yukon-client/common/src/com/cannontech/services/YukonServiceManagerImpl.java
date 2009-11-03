package com.cannontech.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcOperations;
import com.cannontech.spring.YukonBaseXmlApplicationContext;
import com.google.common.collect.Lists;

public class YukonServiceManagerImpl implements YukonServiceManager, ApplicationContextAware {
    private Logger log = YukonLogManager.getLogger(YukonServiceManagerImpl.class);
    private ApplicationContext applicationContext;
    private YukonJdbcOperations yukonJdbcOperations;
    private CountDownLatch shutdownLatch = new CountDownLatch(1);
    private List<ConfigurableApplicationContext> contexts = Lists.newArrayList();
    
    @PostConstruct
    public void loadCustomServices() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ServiceID, ServiceName, ServiceClass, ParamNames, ParamValues"); 
        sql.append("FROM YukonServices");
        sql.append("WHERE ServiceID > 0 and AppName = ").appendArgument(CtiUtilities.getApplicationName());
        
        yukonJdbcOperations.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rset) throws SQLException {
                String displayName = rset.getString(2);
                String path = rset.getString(3);
                if (startService(displayName, path)) {
                    log.info("successfull start of the " + displayName + " service" );
                } else {
                    log.error("Unable to load the " + displayName + " service: " + path);
                }
            }
        });
            
    }

    private boolean startService(String displayName, String serviceName) throws SQLException {
        serviceName = serviceName.trim();

        // try as bean name
        try {
            Object service = null;
            service = applicationContext.getBean(serviceName);
            //better have a start() method defined in the class!
            service.getClass().getMethod("start").invoke(service);
            log.debug("loaded as bean: " + service);
            return true;
        } catch (Exception e) {
            log.debug("unable to load as bean: " + serviceName + ": " + e.getMessage());
        }

        // try as context file
        try {
            ConfigurableApplicationContext context2 = new YukonBaseXmlApplicationContext(serviceName, applicationContext);
            log.debug("loaded as context: " + context2);
            contexts.add(context2);
            return true;
        } catch (Exception e) {
            log.debug("unable to load as context: " + serviceName + ": " + e.getMessage());
        }

        try {
            Object service = null;
            Class<?> clazz = Class.forName(serviceName);
            service = clazz.newInstance();
            //better have a start() method defined in the class!
            service.getClass().getMethod("start").invoke(service);
            log.debug("loaded as class: " + service);
            return true;
        } catch (Exception e) {
            log.debug("unable to load as class: " + serviceName + ": " + e.getMessage());
        }

        return false;
    }
    
    @Override
    public void shutdownServiceManager() {
        for (ConfigurableApplicationContext context : contexts) {
            context.close();
        }
        shutdownLatch.countDown();
    }
    
    @Override
    public void waitForShutdown() {
        try {
            shutdownLatch.await();
        } catch (InterruptedException e) {
            shutdownServiceManager();
        }
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
        
    }
    
    @Autowired
    public void setYukonJdbcOperations(YukonJdbcOperations yukonJdbcOperations) {
        this.yukonJdbcOperations = yukonJdbcOperations;
    }
}
