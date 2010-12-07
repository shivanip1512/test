package com.cannontech.web.jws;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.checker.UserChecker;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.JAVA_WEB_START_LAUNCHER_ENABLED)
public class JnlpController extends AbstractController {

    private ConfigurationSource configurationSource;

    private String appTitle;
    private String appDescription;
    private String appMainClass;
    private String appMainClassJar;
    private String appIcon;
    private String path;
    private UserChecker userChecker;
    private Set<String> excludedJars = Collections.emptySet();
    
    public JnlpController() {
        setCacheSeconds(0);
        setRequireSession(true);
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/x-java-jnlp-file");
        response.setHeader("Cache-Control", "private");
        response.setHeader("Pragma", "");
        
        Document doc = new Document();
        Element jnlpElem = new Element("jnlp");
        doc.addContent(jnlpElem);
        //jnlpElem.setAttribute("spec", "1.5+");
        // determine code base
        String url = request.getRequestURL().toString();
        String codebase = url.substring(0, url.lastIndexOf("/"));
        jnlpElem.setAttribute("codebase", codebase);
        //jnlpElem.setAttribute("href", url);
        
        Element infoElem = new Element("information");
        jnlpElem.addContent(infoElem);
        infoElem.addContent(new Element("title").setText("Yukon® " + appTitle));
        infoElem.addContent(new Element("vendor").setText("Cooper Industries plc."));
        infoElem.addContent(new Element("homepage").setAttribute("href", "http://www.cannontech.com/"));
        infoElem.addContent(new Element("description").setText(appDescription));
        String safeIconUrl = ServletUtil.createSafeUrl(request, appIcon);
        infoElem.addContent(new Element("icon").setAttribute("href", safeIconUrl));
        String safeSplashUrl = ServletUtil.createSafeUrl(request, "/WebConfig/yukon/ApplicationLoading.gif");
        infoElem.addContent(new Element("icon").setAttribute("kind", "splash").setAttribute("href", safeSplashUrl));
        //infoElem.addContent(new Element("offline-allowed"));
        
        Element securityElem = new Element("security");
        jnlpElem.addContent(securityElem);
        securityElem.addContent(new Element("all-permissions"));
        
        Element resourcesElem = new Element("resources");
        jnlpElem.addContent(resourcesElem);
        Element j2seElem = new Element("j2se");
        //j2seElem.setAttribute("href", "http://java.sun.com/products/autodl/j2se");
        j2seElem.setAttribute("version", "1.6");
        j2seElem.setAttribute("initial-heap-size", configurationSource.getString("JNLP_INIT_HEAP_SIZE", "128m"));
        j2seElem.setAttribute("max-heap-size", configurationSource.getString("JNLP_MAX_HEAP_SIZE", "384m"));
        resourcesElem.addContent(j2seElem);        
        
        // add main class jar
        Element mainJarElem = new Element("jar");
        resourcesElem.addContent(mainJarElem);
        mainJarElem.setAttribute("href", appMainClassJar);
        
        // locate all jars under the client directory
        String yukonBase = CtiUtilities.getYukonBase();
        File clientDir = new File(yukonBase, "client/bin");
        
        Collection<String> allJars;
        try {
            allJars = CtiUtilities.getAllJars(clientDir, appMainClassJar);
        } catch (IOException e) {
            throw new RuntimeException("Unable to list JARs. Check that " + appMainClassJar + " exists in " + clientDir + ".", e);
        }
        
        for (String jarFile : allJars) {
            if (excludedJars.contains(jarFile)) {
                continue;
            }
            Element jarElem = new Element("jar");
            jarElem.setAttribute("href", jarFile);
            resourcesElem.addContent(jarElem);
        }
        
        // add some properties to ease log in
        LiteYukonUser user = ServletUtil.getYukonUser(request.getSession());
        if(user != null) {
            Element userPropElem = new Element("property");
            userPropElem.setAttribute("name", "yukon.jws.user");
            userPropElem.setAttribute("value", user.getUsername());
            resourcesElem.addContent(userPropElem);
        }
        
        Element userPropElem = new Element("property");
        userPropElem.setAttribute("name", "yukon.jws.server.base");
        userPropElem.setAttribute("value", CtiUtilities.getYukonBase());
        resourcesElem.addContent(userPropElem);
        
        // add server info
        URL hostUrl = ServletUtil.getHostURL(request);
        Element hostPropElem = new Element("property");
        hostPropElem.setAttribute("name", "yukon.jws.host");
        hostPropElem.setAttribute("value", hostUrl.toString());
        resourcesElem.addContent(hostPropElem);
        
        Element appElem = new Element("application-desc");
        jnlpElem.addContent(appElem);
        appElem.setAttribute("main-class", appMainClass);
        
        XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
        ServletOutputStream responseOutStream = response.getOutputStream();
        out.output(doc, responseOutStream);
        return null;
    }
    
    public void setAppTitle(String appTitle) {
        this.appTitle = appTitle;
    }

    public void setAppMainClass(String appMainClass) {
        this.appMainClass = appMainClass;
    }

    public void setAppMainClassJar(String appMainClassJar) {
        this.appMainClassJar = appMainClassJar;
    }

    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }

    public String getAppMainClass() {
        return appMainClass;
    }

    public String getAppMainClassJar() {
        return appMainClassJar;
    }

    public String getAppTitle() {
        return appTitle;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    public void setExcludedJars(Set<String> excludedJars) {
        this.excludedJars = excludedJars;
    }

    public void setAppDescription(String appDescription) {
        this.appDescription = appDescription;
    }

    public String getAppDescription() {
        return appDescription;
    }

    public UserChecker getUserChecker() {
        return userChecker;
    }

    public void setUserChecker(UserChecker userChecker) {
        this.userChecker = userChecker;
    }

    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }
}
