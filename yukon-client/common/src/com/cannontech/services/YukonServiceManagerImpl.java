package com.cannontech.services;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.spring.YukonBaseXmlApplicationContext;
import com.google.common.collect.Lists;

public class YukonServiceManagerImpl implements YukonServiceManager, ApplicationContextAware {
    private Logger log = YukonLogManager.getLogger(YukonServiceManagerImpl.class);
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    private ApplicationContext applicationContext;
    private CountDownLatch shutdownLatch = new CountDownLatch(1);
    private List<ConfigurableApplicationContext> contexts = Lists.newArrayList();
    
    @Override
    @PostConstruct
    public void loadServices() {
        // Load "optional" services from the database
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ServiceID, ServiceName, ServiceClass, ServiceType"); 
        sql.append("FROM YukonServices");
        sql.append("WHERE ServiceID > 0 and AppName = ").appendArgument(BootstrapUtils.getApplicationName());
        
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rset) throws SQLException {
            	String displayName = rset.getString("ServiceName");
            	String path = rset.getString("ServiceClass");
            	ServiceType serviceType = rset.getEnum("ServiceType", ServiceType.class);
            	if (startService(path, serviceType)) {
                    log.info("Successful start of the " + displayName + " service" );
                } else {
                    log.error("Unable to load the " + displayName + " service: " + path);
                }
            }
        });
        
        // Load "required" services from enum
        for (ManagedService service : ManagedService.values()) {
            if (startService(service.getPath(), service.getType())) {
                log.info("Successful start of the " + service.getName() + " service" );
            } else {
                log.error("Unable to load the " + service.getName() + " service: " + service.getPath());
            }
        }
                      
    }

    private boolean startService(String servicePath, ServiceType serviceType) {
        servicePath = servicePath.trim();

        Exception e1, e2;
        // try as context file
        try {
            ConfigurableApplicationContext context2 = new YukonBaseXmlApplicationContext(applicationContext, "classpath:com/cannontech/services/server/sharedServiceManagerContext.xml", servicePath);
            log.debug("loaded as context: " + context2);
            contexts.add(context2);
            return true;
        } catch (Exception e) {
            e1 = e;
            log.trace("unable to load as context: " + servicePath, e);
        }

        try {
            Object service = null;
            Class<?> clazz = Class.forName(servicePath);
            service = clazz.newInstance();
            //better have a start() method defined in the class!
            service.getClass().getMethod("start").invoke(service);
            log.debug("loaded as class: " + service);
            return true;
        } catch (Exception e) {
            e2 = e;
            log.trace("unable to load as class: " + servicePath, e);
        }
        
		if (ServiceType.CONTEXT_FILE_TYPE == serviceType) {
			log.warn("Service was unable to load as context", e1);
		} else if (ServiceType.CLASS_NAME_TYPE == serviceType) {
			log.warn("Service was unable to load as class", e2);
		}

		return false;
    }
    
    @Override
    public void shutdownServiceManager() {
        log.info("Service Manager is shutting down");
    	for (ConfigurableApplicationContext context : contexts) {
            context.close();
            log.debug("Closed Configurable Application Context: "+ context);
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
        finally
        {
            log.info("Service Manager has been shutdown successfully");
        }
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
        
    }
}
