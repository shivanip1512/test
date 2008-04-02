package com.cannontech.servlet;

import java.awt.Dimension;
import java.io.IOException;
import java.io.PrintWriter;

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
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.view.CapControlOnelineCanvas;
import com.cannontech.cbc.web.CBCWebUtils;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.LoginController;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.svg.SVGOptions;
import com.cannontech.servlet.nav.CBCNavigationUtil;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.util.ParamUtil;
import com.cannontech.util.ServletUtil;
import com.cannontech.yukon.cbc.SubBus;

@SuppressWarnings("serial")
public class OnelineCBCServlet extends HttpServlet {
    private static final int MAX_RETRY = 3;
    private static final String separator = System.getProperty("file.separator");
    public static final String TAGHANDLER = "TAGHANDLER";
    private static final CapControlCache cache = YukonSpringHook.getBean("cbcCache", CapControlCache.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
        ServletContext config = req.getSession().getServletContext();
        LiteYukonUser user = (LiteYukonUser) req.getSession(false).getAttribute(LoginController.YUKON_USER);
        Integer currentSubId = ParamUtil.getInteger(req, "id");
        String redirectURL = ParamUtil.getString(req, "redirectURL", null);
        SubBus subBusMsg = cache.getSubBus(currentSubId);
        String absPath = config.getRealPath(CBCWebUtils.ONE_LINE_DIR);

        String subName = createSubBusDrawing(redirectURL, subBusMsg, absPath, user);
        String busHTML = subName + ".html";
        //remember the location
        String subOnelineURL = "/capcontrol/oneline/" + busHTML;
        CBCNavigationUtil.bookmarkLocationAndRedirect(subOnelineURL, req.getSession(false));
        resp.sendRedirect(busHTML);
    }

    private String createSubBusDrawing(String redirectURL, SubBus subBusMsg, String absPath, LiteYukonUser user) {
        String subName = subBusMsg.getCcName();
        
        for (int count = 0; count < MAX_RETRY; count++) {
            try {
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
                emptyCanvas.setUser(user);
                emptyCanvas.setLayoutParams(param);
                emptyCanvas.createDrawing(subBusMsg,
                                          CBCWebUtils.ONE_LINE_DIR + subName.trim() + ".html");
                
                String dirAndFileExt = absPath + separator + subName;
                OnelineUtil.createHTMLFile(dirAndFileExt, emptyCanvas);
                break;
            } catch (RuntimeException e) {
                if (count < MAX_RETRY) {
                    CTILogger.warn("Failure generating OneLine drawing: " + e.getClass());
                    CTILogger.debug(e.getStackTrace());
                    continue; 
                }
                throw e;
            }
        }
        return subName;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

        resp.setContentType("text/xml");

        Integer currentSubId = ParamUtil.getInteger(req, "id");
        LiteYukonUser user = ServletUtil.getYukonUser(req);

        SubBus msg = cache.getSubBus(currentSubId);

        String absPath = req.getSession().getServletContext().getRealPath(CBCWebUtils.ONE_LINE_DIR);

        String subName = msg.getCcName();
        String fileName = absPath + separator + subName;
        String htmlFile = StringUtils.replace(fileName, ".jlx", ".html");

        htmlFile = StringUtils.substringAfter(htmlFile,
                                              CBCWebUtils.ONE_LINE_DIR);

        for (int count = 0; count < MAX_RETRY; count++) {
            try {
            	SubBus bus = cache.getSubBus(currentSubId);
            	Dimension dim = OnelineUtil.getDrawingDimension(bus);
                CapControlOnelineCanvas view = new CapControlOnelineCanvas(dim);
                view.setUser(user);
                Drawing d = view.createDrawing(msg, CBCWebUtils.ONE_LINE_DIR + htmlFile);

                CapControlSVGGenerator svgGenerator = new CapControlSVGGenerator(getSVGOptions(), d);
                CapControlDrawingUpdater drawingUpdater = svgGenerator.getDrawingUpdater();
                drawingUpdater.setOnelineDrawing(view.getDrawing());
                drawingUpdater.setSubBusMsg(msg);


                PrintWriter writer = null;
                try {
                    writer = resp.getWriter();
                    svgGenerator.generate(writer, d);
                } finally {    
                    if (writer != null) {
                        writer.flush();
                        writer.close();
                    }
                }    
                break;

            } catch (RuntimeException e) {
                if (count < MAX_RETRY) {
                    CTILogger.warn("Failure generating OneLine drawing: " + e.getClass());
                    continue; 
                }
                throw e;
            }
        }
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
