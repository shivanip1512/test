package com.cannontech.servlet;

/**
 * @author rneuharth
 * 
 * Replaces properties in a JNLP page 
 * 
 */
import javax.servlet.*;
import javax.servlet.http.*;

import com.cannontech.database.data.lite.LiteYukonUser;

import java.io.*;
import java.util.*;

public class DynamicJNLPServlet extends HttpServlet
{
    private HashMap jnlpMap = new HashMap();
    private ArrayList matchList;
    private boolean parseQueryString;


    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);

        this.initMatchList();

        this.parseQueryString = config.getInitParameter("parse.querystring")
                .equalsIgnoreCase("true") ? true : false;

        config.getServletContext().setAttribute("DynamicJNLPServlet", this);
    }

    private void initMatchList()
    {
        this.matchList = new ArrayList();
        String toMatch = getServletConfig().getInitParameter("match.list");

        StringTokenizer tok = new StringTokenizer(toMatch, "|");
        while (tok.hasMoreTokens())
            this.matchList.add(tok.nextToken());
    }


    private String getPropertyXML(String name, String value)
    {
        return "<property name=\"" + name + "\" value=\"" + value + "\"/>\n";
    }

    public void clearCache()
    {
        this.jnlpMap.clear();
    }

    private StringBuffer getJnlpFromDisk(HttpServletRequest req , String match )
            throws IOException
    {
        String uriSansContext = req.getRequestURI().substring(
                req.getRequestURI().indexOf(req.getContextPath())
                        + req.getContextPath().length());

        String filepath = this.getServletContext().getRealPath(uriSansContext);

        //System.out.println( " URI: " + req.getRequestURI() );
        //System.out.println( "Path: " + filepath );

        if (this.jnlpMap.containsKey(filepath))
            return (StringBuffer) this.jnlpMap.get(filepath);

        int ch;
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new FileReader(filepath));

        while ((ch = br.read()) > -1)
            sb.append((char) ch);

        br.close();

        this.replaceCodebase(req, sb);
        this.replaceDisplayName(match, sb);
        this.replaceHref(req, sb);

        this.jnlpMap.put(match, sb);

//        System.out.println("[" + new Date() + "] Found JNLP Template");
//        System.out.println("     URL: " + req.getRequestURL());
//        System.out.println("   Match: " + match);
//        System.out.println("    File: " + filepath);

        return sb;
    }

    private void replaceCodebase(HttpServletRequest req, StringBuffer sb)
    {
        int start = sb.toString().indexOf("$$codebase");
        int length = "$$codebase".length();

        String url = req.getRequestURL().toString();

        String codebase = url.substring(0, url.lastIndexOf("/"));

        if( start >= 0 )
            sb.replace(start, start + length, codebase);
    }

    private void replaceDisplayName(String matchName, StringBuffer sb)
    {
        int start = sb.toString().indexOf("$$title");
        int length = "$$title".length();

        String name = this.getServletConfig().getInitParameter(
                matchName + ".title");

        if( start >= 0 )
            sb.replace(start, start + length, name);
    }

    private void replaceProperties(HttpServletRequest req, StringBuffer sb)
    {
        if (sb.toString().indexOf("$$properties") < 0)
            return;

        StringBuffer propXmlBuffer = new StringBuffer();
        Enumeration enum = req.getParameterNames();
        while (enum.hasMoreElements())
        {
            String name = (String) enum.nextElement();
            propXmlBuffer.append(
                    this.getPropertyXML(name, req.getParameter(name)));
        }
        
        LiteYukonUser user = 
            (LiteYukonUser)req.getSession().getAttribute("YUKON_USER");
        if( user != null )
        {
            propXmlBuffer.append(
                    this.getPropertyXML("yukon.user", user.getUsername()));
            propXmlBuffer.append(
                    this.getPropertyXML("yukon.password", user.getPassword()));
        }
        

        int start = sb.toString().indexOf("$$properties");
        int length = "$$properties".length();

        if( start >= 0 )
            sb.replace(start, start + length, propXmlBuffer.toString());
    }

    private void replaceHref(HttpServletRequest req, StringBuffer sb)
    {
        int start = sb.toString().indexOf("$$href");
        int length = "$$href".length();

        String url = req.getRequestURL().toString();

        String href = url.substring(url.lastIndexOf("/") + 1);

        if( start >= 0 )
            sb.replace(start, start + length, href);
    }

    private String getJnlp(HttpServletRequest req) throws IOException
    {
        String matchName = "", url = req.getRequestURL().toString();
        boolean foundMatch = false;

        for (int i = 0; i < this.matchList.size(); i++)
        {
            matchName = (String) this.matchList.get(i);
            if (url.indexOf(matchName) > -1)
            {
                foundMatch = true;
                break;
            }
        }

        if (!foundMatch)
            return null;

        StringBuffer jnlpBuffer = this.getJnlpFromDisk(req, matchName);

        if (this.parseQueryString)
        {
            StringBuffer tmpBuffer = new StringBuffer(jnlpBuffer.toString());
            this.replaceProperties(req, tmpBuffer);
            return tmpBuffer.toString();
        }
        else
            return jnlpBuffer.toString();
    }

    protected void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException
    {
        try
        {
            String jnlp = this.getJnlp(req);
            if (jnlp == null)
                res.sendError(HttpServletResponse.SC_NOT_FOUND);
            else
            {
                res.setContentType("application/x-java-jnlp");
                res.getWriter().print(jnlp);
                res.flushBuffer();
            }
        }
        catch (IOException e)
        {
            System.err.println("DynamicJNLPServlet Error: " + e.getMessage());
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}