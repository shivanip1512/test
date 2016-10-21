package com.cannontech.common.util;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Optional;

import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class YukonHttpProxy {
    private final String host;
    private final int port;
    
    /**
     * @param hostAndPort A String representing the proxy host and port in the format <code>host:port</code>.
     * @throws IllegalArgumentException if <code>hostAndPort</code> is not formatted correctly.
     */
    public YukonHttpProxy(String hostAndPort) {
        String[] hostPortArray = hostAndPort.split(":");
        if(hostPortArray.length != 2) {
            throw new IllegalArgumentException("Invalid proxy value: " + hostAndPort + ". Unable to setup proxy.");
        } else {
            host = hostPortArray[0];
            try {
                port = Integer.parseInt(hostPortArray[1]);
            } catch(NumberFormatException e) {
                throw new IllegalArgumentException("Invalid proxy value: " + hostAndPort + ". Unable to setup proxy.");
            }
        }
    }
    
    /**
     * @return A Java http proxy based on this object's settings.
     */
    public Proxy getJavaHttpProxy() {
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
    }
    
    public String getHost() {
        return host;
    }
    
    public int getPort() {
        return port;
    }
    
    public String getPortString() {
        return Integer.toString(port);
    }
    
    /**
     * @return An Optional, containing a <code>YukonHttpProxy</code>
     */
    public static Optional<YukonHttpProxy> fromGlobalSetting(GlobalSettingDao globalSettingDao) {
        String proxySetting = globalSettingDao.getString(GlobalSettingType.HTTP_PROXY);
        
        Optional<YukonHttpProxy> oProxy = Optional.ofNullable(null);
        
        if (!proxySetting.equals("none")) {
            try {
                YukonHttpProxy proxy = new YukonHttpProxy(proxySetting);
                oProxy = Optional.of(proxy);
            } catch (IllegalArgumentException e) {
                //return the empty optional
            }
        }
        
        return oProxy;
    }
    
}
