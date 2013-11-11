package com.cannontech.common.login.ldap;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
public class LDAPService {
    private CustomisedSSLSocketFactory customisedSSLSocketFactory;

    public void getSSLContext(final String url, final String user, final String password,
                              final String timeout) throws NamingException, IOException {
        
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, url);
        LdapContext ctx = new InitialLdapContext(env, null);
        StartTlsResponse tls =
            (StartTlsResponse) ctx.extendedOperation(new StartTlsRequest());
        tls.setHostnameVerifier(new CustomsiedhostnameVerifier());
        tls.negotiate(customisedSSLSocketFactory);
        ctx.addToEnvironment(Context.SECURITY_AUTHENTICATION, "simple");
        ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, user);
        ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
        ctx.close();
    }

    final static class CustomsiedhostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            try {
                Certificate cert = session.getPeerCertificates()[0];
                byte[] encoded = cert.getEncoded();
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                ByteArrayInputStream bais = new ByteArrayInputStream(encoded);
                X509Certificate xcert = (X509Certificate) cf.generateCertificate(bais);
                String cn = getCanonicalName(xcert.getSubjectDN().getName());
                if (cn.endsWith(hostname)) {
                    return true;
                }
                else
                {
                    return false;
                }
            } catch (SSLPeerUnverifiedException | CertificateException e) {
                return false;
            }

        }
    }

    private static String getCanonicalName(String subjectDN) {
        Pattern pattern = Pattern.compile("CN=([-.*aA-zZ0-9]*)");
        Matcher matcher = pattern.matcher(subjectDN);

        if (matcher.find())
            return matcher.group(1);
        return subjectDN;
    }

     public Context getContext(final String url, final String user, final String password, final String timeout)
        throws NamingException {        
         
        Hashtable<String,String> env = new Hashtable<String,String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, url);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, user);
        env.put(Context.SECURITY_CREDENTIALS, password);
        env.put("com.sun.jndi.ldap.connect.timeout", Integer.toString(Integer.parseInt(timeout) * 1000));       
        DirContext ctx = new InitialLdapContext(env, null);
        return ctx;
    }
    
    public void close(final Context ctx) {
        if (ctx != null) {
            try {
                ctx.close();
            } catch (NamingException ignore) { } 
        }
    }
    public void setCustomisedSSLSocketFactory(CustomisedSSLSocketFactory customisedSSLSocketFactory) {
        this.customisedSSLSocketFactory = customisedSSLSocketFactory;
    }

    
}
