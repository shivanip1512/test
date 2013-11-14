package com.cannontech.common.login.ldap;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * This CustomisedSSLSocketFactory is used to get window-keyStore and doing simple authentication
 * with TLS.
 * 
 */
public class CustomisedSSLSocketFactory extends SSLSocketFactory {
    private SSLSocketFactory socketFactory;

    public CustomisedSSLSocketFactory() throws KeyStoreException, NoSuchAlgorithmException, CertificateException,IOException, KeyManagementException {
        KeyStore keyStore = KeyStore.getInstance("WINDOWS-ROOT");
        keyStore.load(null, null);
        SSLContext ctx = SSLContext.getInstance("TLS");
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);
        ctx.init(null, tmf.getTrustManagers(), null);
        socketFactory = ctx.getSocketFactory();
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
    public Socket createSocket(Socket socketdetail, String host, int port, boolean autoClose) throws IOException {
        return socketFactory.createSocket(socketdetail, host, port, autoClose);
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return socketFactory.createSocket(host, port);
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress address, int localPort) throws IOException,
            UnknownHostException {
        return socketFactory.createSocket(host, port, address, localPort);
    }

    @Override
    public Socket createSocket(InetAddress address, int port) throws IOException {
        return socketFactory.createSocket(address, port);
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return socketFactory.createSocket(address, port, localAddress, localPort);
    }

    @Override
    public Socket createSocket() throws IOException {
        return socketFactory.createSocket();
    }
}
