package com.cannontech.services.jms;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.google.common.base.Supplier;

public class LazyConnectionFactory implements ConnectionFactory {
    private static final Logger log = YukonLogManager.getLogger(LazyConnectionFactory.class);
    
    private final Supplier<ConnectionFactory> supplier;
    private AtomicBoolean setup = new AtomicBoolean(false);
    private ConnectionFactory delegate = null;

    private volatile RuntimeException savedException;

    public LazyConnectionFactory(Supplier<ConnectionFactory> supplier) {
        this.supplier = supplier;
    }

    @Override
    public Connection createConnection() throws JMSException {
        setup();
        return delegate.createConnection();
    }

    private void setup() {
        if (setup.compareAndSet(false, true)) {
            try {
                delegate = supplier.get();
            } catch (RuntimeException e) {
                log.warn("caught exception in setup", e);
                savedException = e;
            }
        }
        
        if (savedException != null) {
            throw savedException;
        }
        
    }

    @Override
    public Connection createConnection(String arg0, String arg1) throws JMSException {
        setup();
        return delegate.createConnection(arg0, arg1);
    }

}
