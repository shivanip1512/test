package com.cannontech.web.jws;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.ClientAbortException;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.util.ServletUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

@Controller
public class JwsController {
    private final static Logger log = YukonLogManager.getLogger(JwsController.class);

    @Autowired private ConfigurationSource configurationSource;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private RolePropertyDao rolePropertyDao;

    private Cache<String, String> jarDownloadTokens =
        CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build();

    private Path jarFileBase = Paths.get(CtiUtilities.getYukonBase(), "Client/bin");

    @RequestMapping("/applications")
    public String applications(ModelMap model, LiteYukonUser user) {
        rolePropertyDao.verifyProperty(YukonRoleProperty.JAVA_WEB_START_LAUNCHER_ENABLED, user);
        model.addAttribute("jreInstaller", CtiUtilities.getJREInstaller());
        return "applications.jsp";
    }
    
    /**
     * This is exposed without login filter. No user available
     */
    @RequestMapping("/{requestedJar:.+\\.jar}")
    public void getJar(HttpServletRequest request, HttpServletResponse response, @PathVariable String requestedJar,
            @RequestParam("version-id") String requestedVersion) throws IOException {
        if (jarDownloadTokens.getIfPresent(request.getRemoteHost()) == null) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return;
        }

        Path jarFile = jarFileBase.resolve(requestedJar);
        if (!Files.isReadable(jarFile)) {
            log.error("Requested jar: " + requestedJar + " is not readable.");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }

        String jarVersionId = Long.toString(Files.getLastModifiedTime(jarFile).toMillis());
        if (!requestedVersion.equals(jarVersionId)) {
            String errorMsg = requestedJar + " is at version " + jarVersionId + ". Requested version ("
                + requestedVersion + ") is not valid.";
            log.error(errorMsg);
            response.setContentType("application/x-java-jnlp-error");
            response.getWriter().write(errorMsg);
            return;
        }

        response.setContentType("application/java-archive");
        response.setHeader("Cache-Control", "public");
        response.setContentLength((int) Files.size(jarFile));
        response.setHeader("x-java-jnlp-version-id", jarVersionId);

        try {
            Files.copy(jarFile, response.getOutputStream());
        } catch (ClientAbortException e) {
            // JWS downloads about 95% of the jar then terminates the connection
            // before we are able to send the entire file. JWS somehow
            // rebuilds the jar's central directory ... explain that one to me
            log.debug("Got Exception while downloading Web Start JAR (this can be ignored): " + e);
        }
    }

    /**
     * This is exposed without login filter. No user available
     */
    @RequestMapping("/{requestedJnlp:(?:client_libs|bc|sqlserver_jdbc)\\.jnlp}")
    public void getExtensionJnlp(HttpServletRequest request, HttpServletResponse response,
            @PathVariable JwsJnlp requestedJnlp, @RequestParam("version-id") String requestedVersion)
            throws IOException {
        Document doc = new Document();
        Element jnlpElem = new Element("jnlp");
        doc.addContent(jnlpElem);
        jnlpElem.setAttribute("spec", "1.0+");
        // determine code base
        String url = request.getRequestURL().toString();
        String codebase = url.substring(0, url.lastIndexOf("/")) + "/";
        jnlpElem.setAttribute("codebase", codebase);

        Element securityElem = new Element("security");
        jnlpElem.addContent(securityElem);
        securityElem.addContent(new Element("all-permissions"));

        Element infoElem = new Element("information");
        jnlpElem.addContent(infoElem);
        infoElem.addContent(new Element("title").setText("Yukon\u00AE " + requestedJnlp.getTitle()));
        infoElem.addContent(new Element("vendor").setText("Cooper Power Systems by Eaton"));
        infoElem.addContent(new Element("homepage").setAttribute("href", "http://www.eaton.com"));
        infoElem.addContent(new Element("description").setText(requestedJnlp.getDescription()));

        Element resourcesElem = new Element("resources");
        jnlpElem.addContent(resourcesElem);

        long jnlpVersion = 0;
        for (String jar : requestedJnlp.getAppJars()) {
            long jarVersionNum = addJarToElement(jar, resourcesElem);
            jnlpVersion = Math.max(jnlpVersion, jarVersionNum);
        }

        String jnlpVersionId = Long.toString(jnlpVersion);
        if (!requestedVersion.equals(jnlpVersionId)) {
            String errorMsg = requestedJnlp.name() + " is at version " + jnlpVersionId + ". Requested version ("
                + requestedVersion + ") is not valid.";
            log.error(errorMsg);
            response.setContentType("application/x-java-jnlp-error");
            response.getWriter().write(errorMsg);
            return;
        }

        response.setHeader("x-java-jnlp-version-id", jnlpVersionId);
        response.setContentType("application/x-java-jnlp-file");
        response.setHeader("Cache-Control", "private");
        response.setHeader("Pragma", "");

        Element compElem = new Element("component-desc");
        jnlpElem.addContent(compElem);

        XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
        ServletOutputStream responseOutStream = response.getOutputStream();
        out.output(doc, responseOutStream);
    }

    @RequestMapping("/{requestedJnlp:(?:dbeditor|tdc|trending|commander)\\.jnlp}")
    public void getApplicationJnlp(HttpServletRequest request, HttpServletResponse response,
            @PathVariable JwsJnlp requestedJnlp, LiteYukonUser user) throws IOException {
        jarDownloadTokens.put(request.getRemoteHost(), requestedJnlp.getTitle());

        response.setContentType("application/x-java-jnlp-file");
        response.setHeader("Cache-Control", "private");
        response.setHeader("Pragma", "");

        Document body = new Document();
        Element jnlpElem = new Element("jnlp");
        body.addContent(jnlpElem);
        jnlpElem.setAttribute("spec", "1.0+");
        // determine code base url
        String url = request.getRequestURL().toString();
        String codebase = url.substring(0, url.lastIndexOf("/")) + "/";
        jnlpElem.setAttribute("codebase", codebase);

        Element infoElem = new Element("information");
        jnlpElem.addContent(infoElem);
        infoElem.addContent(new Element("title").setText("Yukon\u00AE " + requestedJnlp.getTitle()));
        infoElem.addContent(new Element("vendor").setText("Eaton Corporation plc."));
        infoElem.addContent(new Element("homepage").setAttribute("href", "http://www.cooperindustries.com/content/public/en/power_systems/brands/yukon.html"));
        infoElem.addContent(new Element("description").setText(requestedJnlp.getDescription()));
        String safeIconUrl = ServletUtil.createSafeUrl(request, requestedJnlp.getAppIcon());
        infoElem.addContent(new Element("icon").setAttribute("href", safeIconUrl));

        String safeSplashUrl = ServletUtil.createSafeUrl(request, "/WebConfig/yukon/ApplicationLoading.png");
        infoElem.addContent(new Element("icon").setAttribute("kind", "splash").setAttribute("href", safeSplashUrl));

        Element securityElem = new Element("security");
        jnlpElem.addContent(securityElem);
        securityElem.addContent(new Element("all-permissions"));

        Element resourcesElem = new Element("resources");
        jnlpElem.addContent(resourcesElem);
        Element j2seElem = new Element("j2se");
        j2seElem.setAttribute("version", "1.8");
        j2seElem.setAttribute("initial-heap-size", configurationSource.getString("JNLP_INIT_HEAP_SIZE", "128m"));
        j2seElem.setAttribute("max-heap-size", configurationSource.getString("JNLP_MAX_HEAP_SIZE", "384m"));
        resourcesElem.addContent(j2seElem);

        long versionNum = addJarToElement(requestedJnlp.getAppMainClassJar(), resourcesElem);
        for (String jar : requestedJnlp.getAppJars()) {
            long jarVersionNum = addJarToElement(jar, resourcesElem);
            versionNum = Math.max(versionNum, jarVersionNum);
        }

        addExtension(request, resourcesElem, JwsJnlp.BOUNCY_CASTLE);
        addExtension(request, resourcesElem, JwsJnlp.SQL_SERVER_JDBC);
        addExtension(request, resourcesElem, JwsJnlp.CLIENT_LIBS);

        setJnlpProperty("user", user.getUsername(), resourcesElem);
        setJnlpProperty("host", ServletUtil.getHostURL(request).toString() + request.getContextPath(), resourcesElem);
        setJnlpProperty("rememberMe", globalSettingDao.getString(GlobalSettingType.CLIENT_APPLICATIONS_REMEMBER_ME),
            resourcesElem);
        setJnlpProperty("version", VersionTools.getYUKON_VERSION(), resourcesElem);
        setJnlpProperty("version.details", VersionTools.getYukonDetails(), resourcesElem);

        Element appElem = new Element("application-desc");
        jnlpElem.addContent(appElem);
        appElem.setAttribute("main-class", requestedJnlp.getAppMainClass());

        XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
        ServletOutputStream responseOutStream = response.getOutputStream();
        out.output(body, responseOutStream);
    }

    private void setJnlpProperty(String name, String value, Element element) {
        Element propertyElement = new Element("property");
        propertyElement.setAttribute("name", "jnlp.yukon." + name);
        propertyElement.setAttribute("value", value);
        element.addContent(propertyElement);
    }

    private long addJarToElement(String jarToAdd, Element element) throws IOException {
        Path jarFile = jarFileBase.resolve(jarToAdd);
        long versionNum = Files.getLastModifiedTime(jarFile).toMillis();
        Element jarElem = new Element("jar");
        jarElem.setAttribute("href", jarToAdd);
        jarElem.setAttribute("size", Long.toString(Files.size(jarFile)));
        jarElem.setAttribute("version", Long.toString(versionNum));
        element.addContent(jarElem);
        return versionNum;
    }

    private void addExtension(HttpServletRequest request, Element resourcesElem, JwsJnlp extension) throws IOException {
        Element extensionElem = new Element("extension");
        String extensionUrl = ServletUtil.createSafeUrl(request, extension.getPath());
        extensionElem.setAttribute("href", extensionUrl);
        extensionElem.setAttribute("version", Long.toString(getExtensionJnlpVersionNumber(extension)));
        resourcesElem.addContent(extensionElem);
    }

    private long getExtensionJnlpVersionNumber(JwsJnlp jnlp) throws IOException {
        long versionNum = 0;
        for (String jar : jnlp.getAppJars()) {
            Path jarFile = jarFileBase.resolve(jar);
            long jarVersionNum = Files.getLastModifiedTime(jarFile).toMillis();
            versionNum = Math.max(versionNum, jarVersionNum);
        }
        return versionNum;
    }

    @InitBinder
    public void initialize(WebDataBinder webDataBinder) {
        webDataBinder.registerCustomEditor(JwsJnlp.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                setValue(JwsJnlp.getFromPath(value));
            }
        });
    }
}
