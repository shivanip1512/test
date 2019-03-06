package com.cannontech.common.util;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

import org.apache.http.HttpHost;
import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class YukonHttpProxy {
    private static final Logger log = YukonLogManager.getLogger(YukonHttpProxy.class);
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
    
    /**
     * @return A Java http host based on this object's settings.
     */
    public HttpHost getJavaHttpHost() {
        return new HttpHost(host, port);
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
                log.warn(e);
                //return the empty optional
            }
        }
        
        return oProxy;
    }
    
    public void setAsSystemProxy() {
        System.setProperty("http.proxyHost", host);
        System.setProperty("http.proxyPort", getPortString());
    }
    
    public static void addNonProxyHosts(String hostAddresses) {
        System.setProperty("http.nonProxyHosts", hostAddresses);
        log.info("Adding " + hostAddresses + " to JVM proxy bypass list.");
    }

    public static URLConnection getURLConnection(String url, GlobalSettingDao globalSettingDao) throws Exception {
        Optional<YukonHttpProxy> proxy = YukonHttpProxy.fromGlobalSetting(globalSettingDao);
        URLConnection urlConnection = null;
        try {
            URL connectionUrl = new URL(url);
            if (proxy.isPresent()) {
                urlConnection = connectionUrl.openConnection(proxy.get().getJavaHttpProxy());
            } else {
                urlConnection = connectionUrl.openConnection();
            }
            urlConnection.connect();
        } catch (Exception e) {
            log.error("Unable to connect with proxy server or URL is not correct", e);
            throw new Exception("Unable to connect with proxy server or URL is not correct");
        }
        return urlConnection;
    }
    

}
