package com.cannontech.web.jws;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.ClientAbortException;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.clientutils.ClientApplicationRememberMe;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.util.CtiUtilities;
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

    @Autowired private ConfigurationSource configurationSource;
    @Autowired  private GlobalSettingDao globalSettingDao;
    @Autowired private RolePropertyDao rolePropertyDao;

    private Cache<String, String> jarDownloadTokens
        = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build();

    private Path jarFileBase = Paths.get(CtiUtilities.getYukonBase(), "Client/bin");
    private Logger log = YukonLogManager.getLogger(JwsController.class);

    @RequestMapping("/applications")
    public String applications(ModelMap model, LiteYukonUser user) {
        rolePropertyDao.verifyProperty(YukonRoleProperty.JAVA_WEB_START_LAUNCHER_ENABLED, user);
        model.addAttribute("jreInstaller", CtiUtilities.getJREInstaller());
        return "applications.jsp";
    }

    /** Note: This is exposed without login filter. No user available*/
    @RequestMapping(value = "/{requestedJar:.+\\.jar}")
    public void getJar(HttpServletRequest request, HttpServletResponse response, @PathVariable String requestedJar)
            throws IOException {

        if (jarDownloadTokens.getIfPresent(request.getRemoteHost()) == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        Path jarFile = jarFileBase.resolve(requestedJar);
        if (!Files.isReadable(jarFile)) {
            log.error("Requested jar: " + requestedJar + " is not readable.");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }

        response.setContentType("application/java-archive");
        response.setHeader("Cache-Control", "public");
        response.setContentLength((int) Files.size(jarFile));
        response.setDateHeader("Last-Modified", Files.getLastModifiedTime(jarFile).toMillis());

        try {
            Files.copy(jarFile, response.getOutputStream());
        } catch (ClientAbortException e) {
            // JWS downloads only the first ~32KB of files it has cached locally. It terminates the connection 
            // before we are able to send the entire file which causes this exception. 
            // Interesting conundrum: JWS doesn't download the entire file even when it doesn't have it cached. 
            // It downloads about 95% of it and somehow rebuilds the jar's central directory
            // ... explain that one to me
            log.debug("Got Exception while downloading Web Start JAR (this can be ignored): " + e);
        }
    }

    /** Note: This is exposed without login filter. No user available */
    @RequestMapping(value = "/{requestedJnlp:client_libs\\.jnlp|bc\\.jnlp}")
    public void getExtensionJnlp(HttpServletRequest request, HttpServletResponse response,
            @PathVariable JwsJnlp requestedJnlp) throws IOException {

        response.setContentType("application/x-java-jnlp-file");
        response.setHeader("Cache-Control", "private");
        response.setHeader("Pragma", "");

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
        infoElem.addContent(new Element("vendor").setText("Cooper Industries plc."));
        infoElem.addContent(new Element("homepage").setAttribute("href", "http://www.cannontech.com/"));
        infoElem.addContent(new Element("description").setText(requestedJnlp.getDescription()));

        Element resourcesElem = new Element("resources");
        jnlpElem.addContent(resourcesElem);

        for (String jarFile : requestedJnlp.getAppJars()) {
            Element jarElem = new Element("jar");
            jarElem.setAttribute("href", jarFile);
            jarElem.setAttribute("size", Long.toString(Files.size(jarFileBase.resolve(jarFile))));
            resourcesElem.addContent(jarElem);
        }

        Element compElem = new Element("component-desc");
        jnlpElem.addContent(compElem);

        XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
        ServletOutputStream responseOutStream = response.getOutputStream();
        out.output(doc, responseOutStream);
    }

    @RequestMapping(value = "/{requestedJnlp:dbeditor\\.jnlp|tdc\\.jnlp|trending\\.jnlp|esub\\.jnlp|commander\\.jnlp}")
    public void getApplicationJnlp(HttpServletRequest request, HttpServletResponse response, @PathVariable JwsJnlp requestedJnlp)
            throws IOException {
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
        infoElem.addContent(new Element("vendor").setText("Cooper Industries plc."));
        infoElem.addContent(new Element("homepage").setAttribute("href", "http://www.cannontech.com/"));
        infoElem.addContent(new Element("description").setText(requestedJnlp.getDescription()));
        String safeIconUrl = ServletUtil.createSafeUrl(request, requestedJnlp.getAppIcon());
        infoElem.addContent(new Element("icon").setAttribute("href", safeIconUrl));

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
        mainJarElem.setAttribute("href", requestedJnlp.getAppMainClassJar());
        mainJarElem.setAttribute("size", Long.toString(Files.size(jarFileBase.resolve(requestedJnlp.getAppMainClassJar()))));
        for (String jarFile : requestedJnlp.getAppJars()) {
            Element jarElem = new Element("jar");
            jarElem.setAttribute("href", jarFile);
            jarElem.setAttribute("size", Long.toString(Files.size(jarFileBase.resolve(jarFile))));
            resourcesElem.addContent(jarElem);
        }

        addExtension(request, resourcesElem, "bc");
        addExtension(request, resourcesElem, "client_libs");

        // add some properties to ease log in
        LiteYukonUser user = ServletUtil.getYukonUser(request.getSession());
        if (user != null) {
            Element userPropElem = new Element("property");
            userPropElem.setAttribute("name", "jnlp.yukon.user");
            userPropElem.setAttribute("value", user.getUsername());
            resourcesElem.addContent(userPropElem);
        }

        Element userPropElem = new Element("property");
        userPropElem.setAttribute("name", "jnlp.yukon.server.base");
        userPropElem.setAttribute("value", CtiUtilities.getYukonBase());
        resourcesElem.addContent(userPropElem);

        // add server info
        URL hostUrl = ServletUtil.getHostURL(request);
        Element hostPropElem = new Element("property");
        hostPropElem.setAttribute("name", "jnlp.yukon.host");
        hostPropElem.setAttribute("value", hostUrl.toString());
        resourcesElem.addContent(hostPropElem);

        Element rememberMeSettingPropElem = new Element("property");
        ClientApplicationRememberMe rememberMeSetting = globalSettingDao.getEnum(GlobalSettingType.CLIENT_APPLICATIONS_REMEMBER_ME,
                                                                                 ClientApplicationRememberMe.class);
        rememberMeSettingPropElem.setAttribute("name", "jnlp.yukon.rememberMe");
        rememberMeSettingPropElem.setAttribute("value", rememberMeSetting.name());
        resourcesElem.addContent(rememberMeSettingPropElem);

        Element appElem = new Element("application-desc");
        jnlpElem.addContent(appElem);
        appElem.setAttribute("main-class", requestedJnlp.getAppMainClass());

        XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
        ServletOutputStream responseOutStream = response.getOutputStream();
        out.output(body, responseOutStream);
    }

    private void addExtension(HttpServletRequest request, Element resourcesElem, String extensionName) {
        Element extensionElem = new Element("extension");
        String extensionUrl = ServletUtil.createSafeUrl(request, extensionName + ".jnlp");
        extensionElem.setAttribute("href", extensionUrl);
        resourcesElem.addContent(extensionElem);
    }

    @InitBinder
    public void initialize(WebDataBinder webDataBinder) {
        webDataBinder.registerCustomEditor(JwsJnlp.class, new PropertyEditorSupport () {
            @Override
            public void setAsText(String value) {
                setValue(JwsJnlp.getFromPath(value));
            }
        });
    }
}
