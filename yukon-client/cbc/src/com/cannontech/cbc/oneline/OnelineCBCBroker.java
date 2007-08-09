package com.cannontech.cbc.oneline;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.view.CapControlOnelineCanvas;
import com.cannontech.cbc.oneline.view.OneLineDrawing;
import com.cannontech.cbc.web.CBCWebUtils;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.StateDao;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.element.YukonImageElement;
import com.cannontech.esub.svg.SVGOptions;
import com.cannontech.esub.util.HTMLGenerator;
import com.cannontech.esub.util.HTMLOptions;
import com.cannontech.esub.util.ImageExporter;
import com.cannontech.esub.util.Util;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.yukon.cbc.CBCClientConnection;
import com.cannontech.yukon.cbc.CBCCommand;
import com.cannontech.yukon.cbc.CBCSubstationBuses;
import com.cannontech.yukon.cbc.SubBus;
import com.cannontech.yukon.conns.ConnPool;

/**
 * @author eWally Generates OneLines for CapControl based on DBChanges messages
 *         heard from the CapControl server.
 */
/**
 * @author ekhazon generates files to the CBCWebUtils.ONE_LINE_DIR oneline files
 *         constist of 1.jlx 2.svg 3.html if staticMode is true than only one
 *         set of files will be regenerated The reason that it is practical is
 *         we don't want to regenerate all the files every time a message comes
 *         in ( which is a lot of work - 1000's of files every few seconds).
 *         What we want to do is to generate all the files on start up and then
 *         regenerate a set of files belonging to a single substation view the
 *         user chooses (just in case structural changes were done such as
 *         deleting an object)
 */
public class OnelineCBCBroker implements MessageListener {
    private static final String CAPCONTROL_ONELINE = CBCWebUtils.ONE_LINE_DIR;
    private String dirBase = null;
    // exists to start the generation in static mode - or generate all the
    // drawing only once on start up
    private boolean staticMode = false;

    public OnelineCBCBroker() {
        super();
        setDirBase(CtiUtilities.getYukonBase() + CBCWebUtils.ONE_LINE_DIR);
    }

    public OnelineCBCBroker(String dirBase) {
        super();
        setDirBase(dirBase + CBCWebUtils.ONE_LINE_DIR);
    }

    public void messageReceived(MessageEvent e) {
        handleMessage(e.getMessage());
    }

    private void handleMessage(com.cannontech.message.util.Message msg) {
        if (msg instanceof CBCSubstationBuses) {
            CBCSubstationBuses subBusMsg = (CBCSubstationBuses) msg;
            if (subBusMsg.isAllSubs()) {
                for (int i = 0; i < subBusMsg.getNumberOfBuses(); i++) {

                    SubBus subBusAt = subBusMsg.getSubBusAt(i);

                    String subName = subBusAt.getCcName();
                    CTILogger.info("Generating SubBus: " + getDirBase() + "/" + subName);

                    String dirAndFileExt = createFileName(subName);
                    CapControlOnelineCanvas emptyCanvas = new CapControlOnelineCanvas(800,
                                                                                      1200);
                    emptyCanvas.createDrawing(subBusAt,
                                              CAPCONTROL_ONELINE + subName.trim() + ".html");

                    createHTMLFile(dirAndFileExt, emptyCanvas);
                    CTILogger.debug("...generation complete for " + subName);

                }
            }
            // generate only once if running in static mode
            if (isStaticMode())
                stop();
        }
    }

    public String createFileName(String subName) {
        String dirAndFileExt = getDirBase() + "/" + subName.trim() + ".jlx";
        return dirAndFileExt;
    }

    public void createHTMLFile(String dirAndFileExt,
            CapControlOnelineCanvas canvas) {
        int retries = 3;
        Drawing drawing = canvas.getDrawing().getDrawing();

        drawing.setFileName(dirAndFileExt);
        do {
            try {
                writeJLX(dirAndFileExt, drawing);
                writeSVG(dirAndFileExt, canvas);
                writeHTML(dirAndFileExt, drawing);
                String parent = new File(dirAndFileExt).getParent();
                writeImages(drawing, parent);
                break;
            } catch (Exception e) {
                CTILogger.debug(e.getMessage());
            }
        } while (retries-- > 0);
    }

    private void writeImages(Drawing ccSubBusDrawing, String parent) {
        ImageExporter ie = new ImageExporter(ccSubBusDrawing);
        ie.exportImages(parent);
        ie.exportAllImages(parent, OnelineUtil.getAdditionalImages());
    }


    private void writeJLX(String dirAndFileExt, Drawing ccSubBusDrawing) {
        String jlxFileName = dirAndFileExt;

        if (!jlxFileName.endsWith(".jlx")) {
            jlxFileName = jlxFileName.concat(".jlx");
        }

        ccSubBusDrawing.getLxGraph().save(jlxFileName);
    }

    private void writeSVG(String dirAndFileExt, CapControlOnelineCanvas canvas) {
        String svgFileName = dirAndFileExt;
        if (svgFileName.endsWith(".jlx")) {
            svgFileName = svgFileName.substring(0, svgFileName.length() - 4);
        }

        svgFileName = svgFileName.concat(".svg");

        try {
            SVGOptions svgOptions = new SVGOptions();
            svgOptions.setControlEnabled(true);
            svgOptions.setEditEnabled(false);
            svgOptions.setScriptingEnabled(true);
            svgOptions.setStaticSVG(false);

            OneLineDrawing onelineDrawing = canvas.getDrawing();
            Drawing drawing = onelineDrawing.getDrawing();
            SubBus subBusMsg = onelineDrawing.getSub().getSubBusMsg();

            CapControlSVGGenerator gen = new CapControlSVGGenerator(svgOptions,
                                                                    drawing);
            gen.getDrawingUpdater().setSubBusMsg(subBusMsg);

            FileWriter fw = new FileWriter(svgFileName);
            CapControlDrawingUpdater drawingUpdater = gen.getDrawingUpdater();
            drawingUpdater.setOnelineDrawing(onelineDrawing);
            gen.generate(fw, drawing);
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeHTML(String dirAndFileExt, Drawing ccSubBusDrawing) {
        String htmlFileName = dirAndFileExt;

        if (htmlFileName.endsWith(".jlx")) {
            htmlFileName = htmlFileName.substring(0, htmlFileName.length() - 4);
        }

        htmlFileName = htmlFileName.concat(".html");

        try {
            OnelineHTMLGenerator gen = new OnelineHTMLGenerator();

            FileWriter fw = new FileWriter(htmlFileName);
            HTMLOptions options = new HTMLOptions();
            options.setStaticHTML(false);
            gen.setGenOptions(options);
            gen.generate(fw, ccSubBusDrawing);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Throwable {
        System.setProperty("cti.app.name", "cbcOneLineGen");

        OnelineCBCBroker thisIsThis = new OnelineCBCBroker();
        if (args.length > 0)
            thisIsThis.dirBase = args[0];

        thisIsThis.start();

        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                break;
            }
        }

        System.exit(0);

    }

    public String getDirBase() {
        return dirBase;
    }

    public void start() {
        CTILogger.info("Starting CBC One-Line ....");
        try {
            getConnection().addMessageListener(this);
            getConnection().executeCommand(0, CBCCommand.REQUEST_ALL_AREAS);
            CTILogger.info("CBC One-Line successfully started...");
        } catch (Exception e) {

            CTILogger.info("CBC One-Line could not be started: " + e.getMessage());

        }
    }

    /**
     * Stop us
     */
    public void stop() {
        CTILogger.info("Stopping CBC One-Line ....");
        try {
            getConnection().removeMessageListener(this);
            CTILogger.info("CBC One-Line successfully stoped.");
        } catch (Exception e) {
            CTILogger.info("CBC One-Line could not be stopped: " + e.getMessage());
        }
    }

    public boolean isRunning() {
        return getConnection().isValid();
    }

    private CBCClientConnection getConnection() {
        return (CBCClientConnection) ConnPool.getInstance()
                                             .getDefCapControlConn();
    }

    public void setDirBase(String dirBase) {
        this.dirBase = dirBase;
        CTILogger.debug(" Oneline generation output: " + dirBase);
    }

    public boolean isStaticMode() {
        return staticMode;
    }

    public void setStaticMode(boolean staticMode) {
        this.staticMode = staticMode;
    }

}
