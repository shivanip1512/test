package com.cannontech.common.login.ldap;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
/**
 * This CustomisedSSLSocketFactory is used to get window-keyStore and doing simple authentication with TLS.
 *
 */
public class CustomisedSSLSocketFactory extends SSLSocketFactory {
    private SSLSocketFactory socketFactory;

    public CustomisedSSLSocketFactory() {
        try {
            KeyStore keyStore = KeyStore.getInstance("WINDOWS-ROOT");
            keyStore.load(null, null);
            SSLContext ctx = SSLContext.getInstance("TLS");            
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());    
            tmf.init(keyStore);     
            ctx.init(null, tmf.getTrustManagers(), null);
            socketFactory = ctx.getSocketFactory();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

   
    public static SocketFactory getDefault() {
        return new CustomisedSSLSocketFactory();
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return socketFactory.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return socketFactory.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(Socket socket, String string, int i, boolean bln) throws IOException {
        return socketFactory.createSocket(socket, string, i, bln);
    }

    @Override
    public Socket createSocket(String string, int i) throws IOException, UnknownHostException {
        return socketFactory.createSocket(string, i);
    }

    @Override
    public Socket createSocket(String string, int i, InetAddress ia, int i1) throws IOException,
            UnknownHostException {
        return socketFactory.createSocket(string, i, ia, i1);
    }

    @Override
    public Socket createSocket(InetAddress ia, int i) throws IOException {
        return socketFactory.createSocket(ia, i);
    }

    @Override
    public Socket createSocket(InetAddress ia, int i, InetAddress ia1, int i1) throws IOException {
        return socketFactory.createSocket(ia, i, ia1, i1);
    }
    @Override
    public Socket createSocket() throws IOException {
        return socketFactory.createSocket();
    }
}
