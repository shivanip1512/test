package com.cannontech.servlet;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.cannontech.cbc.oneline.CapControlDrawingUpdater;
import com.cannontech.cbc.oneline.OnelineCBCBroker;
import com.cannontech.cbc.oneline.CapControlSVGGenerator;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.view.CapControlOnelineCanvas;
import com.cannontech.cbc.web.CBCWebUtils;
import com.cannontech.cbc.web.CapControlCache;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.svg.SVGOptions;
import com.cannontech.util.ParamUtil;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.SubBus;

public class OnelineCBCServlet extends HttpServlet {

    private Integer currentSubId;
    private CapControlCache cache = null;
    private CapControlSVGGenerator svgGenerator;

    public Integer getCurrentSubId() {
        return currentSubId;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        cache = (CapControlCache) req.getSession()
                                     .getServletContext()
                                     .getAttribute("capControlCache");
        currentSubId = ParamUtil.getInteger(req, "id");

        SubBus subBusMsg = cache.getSubBus(currentSubId);
        String absPath = req.getSession()
                            .getServletContext()
                            .getRealPath(CBCWebUtils.ONE_LINE_DIR);
        OnelineCBCBroker util = new OnelineCBCBroker();
        util.setDirBase(absPath);
        String subName = subBusMsg.getCcName();
        String dirAndFileExt = util.createFileName(subName);
        Dimension d = OnelineUtil.getDrawingDimension(subBusMsg);
        CapControlOnelineCanvas emptyCanvas = new CapControlOnelineCanvas(d);
        emptyCanvas.createDrawing(subBusMsg,
                                  CBCWebUtils.ONE_LINE_DIR + subName.trim() + ".html");

        util.createHTMLFile(dirAndFileExt, emptyCanvas);

        resp.sendRedirect(subName + ".html");

    }





    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        cache = (CapControlCache) req.getSession()
                                     .getServletContext()
                                     .getAttribute("capControlCache");
        currentSubId = ParamUtil.getInteger(req, "id");

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
        Drawing d = view.createDrawing(msg, CBCWebUtils.ONE_LINE_DIR + htmlFile);
        resp.setContentType("text/xml");

        svgGenerator = new CapControlSVGGenerator(getSVGOptions(), d);
        CapControlDrawingUpdater drawingUpdater = svgGenerator.getDrawingUpdater();
        drawingUpdater.setOnelineDrawing(view.getDrawing());
        drawingUpdater.setSubBusMsg(msg);
        svgGenerator.generate(resp.getWriter(), d);

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
