package com.cannontech.common.login.ldap;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.ldap.InitialLdapContext;

public class LDAPService {
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
    
}
