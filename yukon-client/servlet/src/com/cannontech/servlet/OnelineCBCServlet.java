package com.cannontech.servlet;

import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.oneline.CapControlDrawingUpdater;
import com.cannontech.cbc.oneline.CapControlSVGGenerator;
import com.cannontech.cbc.oneline.OneLineParams;
import com.cannontech.cbc.oneline.OnelineCBCBroker;
import com.cannontech.cbc.oneline.tag.CBCTagHandler;
import com.cannontech.cbc.oneline.tag.OnelineTags;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.view.CapControlOnelineCanvas;
import com.cannontech.cbc.web.CBCWebUtils;
import com.cannontech.common.constants.LoginController;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.svg.SVGOptions;
import com.cannontech.servlet.nav.CBCNavigationUtil;
import com.cannontech.util.ParamUtil;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.SubBus;

@SuppressWarnings("serial")
public class OnelineCBCServlet extends HttpServlet {

    public static final String TAGHANDLER = "TAGHANDLER";
    private Integer currentSubId;
    private CapControlCache cache = null;
    private CapControlSVGGenerator svgGenerator;

    public Integer getCurrentSubId() {
        return currentSubId;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ServletContext config = req.getSession().getServletContext();
        initHandler(config);
        cache = (CapControlCache) config.getAttribute("capControlCache");
        currentSubId = ParamUtil.getInteger(req, "id");
        String redirectURL = ParamUtil.getString(req, "redirectURL", null);
        SubBus subBusMsg = cache.getSubBus(currentSubId);
        String absPath = config.getRealPath(CBCWebUtils.ONE_LINE_DIR);
        registerPointsWithDispatch(config, subBusMsg);
        
        LiteYukonUser user = (LiteYukonUser) req.getSession(false).getAttribute(LoginController.YUKON_USER);
        

        String subName = createSubBusDrawing(redirectURL, subBusMsg, absPath, user);
        String busHTML = subName + ".html";
        //remember the location
        String subOnelineURL = "/capcontrol/oneline/" + busHTML;
        CBCNavigationUtil.bookmarkLocationAndRedirect(subOnelineURL, req.getSession(false));
        resp.sendRedirect(busHTML);
        
        
    }

    private void initHandler(ServletContext config) {
        CBCTagHandler handler = (CBCTagHandler) config.getAttribute(TAGHANDLER);
        if (handler != null) {
            config.removeAttribute(TAGHANDLER);
        }
    }

    private void registerPointsWithDispatch(ServletContext config, SubBus subBusMsg) {
        CBCTagHandler handler;
        List<LitePoint> pointsToRegister = getPointsToRegister(subBusMsg);
        handler = OnelineTags.createTagHandler(pointsToRegister);
        config.setAttribute(TAGHANDLER, handler);
    }

    private String createSubBusDrawing(String redirectURL, SubBus subBusMsg, String absPath, LiteYukonUser user) {
        OnelineCBCBroker util = new OnelineCBCBroker();
        //set dir base
        util.setDirBase(absPath);
        //create file name
        String subName = subBusMsg.getCcName();
        String dirAndFileExt = util.createFileName(subName);
        //create lay out params
        Dimension d = OnelineUtil.getDrawingDimension(subBusMsg);
        boolean isSingleFeeder = subBusMsg.getCcFeeders().size() == 1;
        int height = (int)d.getHeight();
        int width = (int)d.getWidth();
        OneLineParams param = new OneLineParams(height, width, isSingleFeeder);
        param.setUser(user);
        param.setRedirectURL(redirectURL);
        //create drawing
        CapControlOnelineCanvas emptyCanvas = new CapControlOnelineCanvas(d);
        emptyCanvas.setLayoutParams(param);
        emptyCanvas.createDrawing(subBusMsg,
                                  CBCWebUtils.ONE_LINE_DIR + subName.trim() + ".html");
        util.createHTMLFile(dirAndFileExt, emptyCanvas);
        return subName;
    }

    private List<LitePoint> getPointsToRegister(SubBus subBusMsg) {
        List<LitePoint> pointsToRegister = new ArrayList<LitePoint>();
        addPointsForPAO(pointsToRegister, subBusMsg.getCcId());
        Vector feeders = subBusMsg.getCcFeeders();
        for (int i = 0; i < feeders.size(); i++) {
            Feeder feeder = (Feeder) feeders.get(i);
            addPointsForPAO(pointsToRegister, feeder.getCcId().intValue());
            Vector caps = feeder.getCcCapBanks();
            for (int j = 0; j < caps.size(); j++) {
                CapBankDevice cap = (CapBankDevice) caps.get(j);
                addPointsForPAO(pointsToRegister, cap.getCcId().intValue());
            }
        }
        return pointsToRegister;
    }

    private void addPointsForPAO(List<LitePoint> pointsToRegister, Integer subId) {
        pointsToRegister.addAll(DaoFactory.getPointDao()
                                          .getLitePointsByPaObjectId(subId.intValue()));
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        cache = (CapControlCache) req.getSession()
                                     .getServletContext()
                                     .getAttribute("capControlCache");
        currentSubId = ParamUtil.getInteger(req, "id");
        
        LiteYukonUser user = (LiteYukonUser) req.getSession(false).getAttribute(LoginController.YUKON_USER);
        
        SubBus msg = cache.getSubBus(currentSubId);

        String absPath = req.getSession()
                            .getServletContext()
                            .getRealPath(CBCWebUtils.ONE_LINE_DIR);
        OnelineCBCBroker util = new OnelineCBCBroker();
        util.setDirBase(absPath);

        String subName = msg.getCcName();
        String fileName = util.createFileName(subName);
        String htmlFile = StringUtils.replace(fileName, ".jlx", ".html");

        htmlFile = StringUtils.substringAfter(htmlFile,
                                              CBCWebUtils.ONE_LINE_DIR);

        CapControlOnelineCanvas view = new CapControlOnelineCanvas();
        view.setUser (user);
        Drawing d = view.createDrawing(msg, CBCWebUtils.ONE_LINE_DIR + htmlFile);
        resp.setContentType("text/xml");

        svgGenerator = new CapControlSVGGenerator(getSVGOptions(), d);
        CapControlDrawingUpdater drawingUpdater = svgGenerator.getDrawingUpdater();
        drawingUpdater.setOnelineDrawing(view.getDrawing());
        drawingUpdater.setSubBusMsg(msg);
        svgGenerator.generate(resp.getWriter(), d);
        resp.getWriter().flush();
    }

    private SVGOptions getSVGOptions() {
        SVGOptions svgOptions = new SVGOptions();
        svgOptions.setStaticSVG(false);
        svgOptions.setScriptingEnabled(true);
        svgOptions.setEditEnabled(false);
        svgOptions.setControlEnabled(false);
        svgOptions.setAudioEnabled(false);
        return svgOptions;
    }

   

}
