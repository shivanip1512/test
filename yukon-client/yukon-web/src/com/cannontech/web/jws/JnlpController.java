package com.cannontech.web.jws;

import java.net.URL;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.checker.UserChecker;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.JAVA_WEB_START_LAUNCHER_ENABLED)
public class JnlpController extends JnlpControllerBase {
    @Autowired private ConfigurationSource configurationSource;

    private String appMainClass;
    private String appMainClassJar;
    private String appIcon;
    private UserChecker userChecker;

    public JnlpController() {
        setCacheSeconds(0);
        setRequireSession(true);
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setContentType("application/x-java-jnlp-file");
        response.setHeader("Cache-Control", "private");
        response.setHeader("Pragma", "");
        
        Document doc = new Document();
        Element jnlpElem = new Element("jnlp");
        doc.addContent(jnlpElem);
        // determine code base
        String url = request.getRequestURL().toString();
        String codebase = url.substring(0, url.lastIndexOf("/"));
        jnlpElem.setAttribute("codebase", codebase);
        
        Element infoElem = new Element("information");
        jnlpElem.addContent(infoElem);
        infoElem.addContent(new Element("title").setText("Yukon® " + title));
        infoElem.addContent(new Element("vendor").setText("Cooper Industries plc."));
        infoElem.addContent(new Element("homepage").setAttribute("href", "http://www.cannontech.com/"));
        infoElem.addContent(new Element("description").setText(description));
        if (appIcon != null) {
            String safeIconUrl = ServletUtil.createSafeUrl(request, appIcon);
            infoElem.addContent(new Element("icon").setAttribute("href", safeIconUrl));
        }

        String safeSplashUrl = ServletUtil.createSafeUrl(request, "/WebConfig/yukon/ApplicationLoading.gif");
        infoElem.addContent(new Element("icon").setAttribute("kind", "splash").setAttribute("href", safeSplashUrl));

        Element securityElem = new Element("security");
        jnlpElem.addContent(securityElem);
        securityElem.addContent(new Element("all-permissions"));

        Element resourcesElem = new Element("resources");
        jnlpElem.addContent(resourcesElem);
        Element j2seElem = new Element("j2se");
        j2seElem.setAttribute("version", "1.7");
        j2seElem.setAttribute("initial-heap-size", configurationSource.getString("JNLP_INIT_HEAP_SIZE", "128m"));
        j2seElem.setAttribute("max-heap-size", configurationSource.getString("JNLP_MAX_HEAP_SIZE", "384m"));
        resourcesElem.addContent(j2seElem);        

        // add main class jar
        Element mainJarElem = new Element("jar");
        resourcesElem.addContent(mainJarElem);
        mainJarElem.setAttribute("href", appMainClassJar);

        for (String jarFile : jars) {
            Element jarElem = new Element("jar");
            jarElem.setAttribute("href", jarFile);
            resourcesElem.addContent(jarElem);
        }

        addExtension(request, resourcesElem, "bc");
        addExtension(request, resourcesElem, "client_libs");

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

    private void addExtension(HttpServletRequest request, Element resourcesElem, String extensionName) {
        Element extensionElem = new Element("extension");
        String extensionUrl = ServletUtil.createSafeUrl(request, "/jws/" + extensionName + ".jnlp");
        extensionElem.setAttribute("href", extensionUrl);
        resourcesElem.addContent(extensionElem);
    }

    public void setAppMainClass(String appMainClass) {
        this.appMainClass = appMainClass;
    }

    @Required
    public void setAppMainClassJar(String appMainClassJar) {
        this.appMainClassJar = appMainClassJar;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    public UserChecker getUserChecker() {
        return userChecker;
    }

    @Required
    public void setUserChecker(UserChecker userChecker) {
        this.userChecker = userChecker;
    }
}
