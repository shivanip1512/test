package com.cannontech.cbc.oneline;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.cbc.oneline.view.CapControlOnelineCanvas;
import com.cannontech.cbc.oneline.view.OneLineDrawing;
import com.cannontech.cbc.web.CBCWebUtils;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.svg.SVGOptions;
import com.cannontech.esub.util.HTMLOptions;
import com.cannontech.esub.util.ImageExporter;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.cbc.CBCClientConnection;
import com.cannontech.yukon.cbc.CBCCommand;
import com.cannontech.yukon.cbc.CBCSubstationBuses;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
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
    private static final Executor service = YukonSpringHook.getGlobalExecutor();
    private static final PointDao pointDao = YukonSpringHook.getBean("pointDao", PointDao.class);
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
            final CBCSubstationBuses subBusMsg = (CBCSubstationBuses) msg;
            if (subBusMsg.isAllSubs()) {
                final List<Integer> paObjectIdList = new ArrayList<Integer>();
                final List<SubBus> subBusMsgList = new ArrayList<SubBus>(subBusMsg.getSubBuses());
                
                for (final SubBus subBusAt : subBusMsgList) {
                    paObjectIdList.add(subBusAt.getCcId());
                    List<Feeder> feeders = subBusAt.getCcFeeders();
                    for (final Feeder feeder : feeders) {
                        paObjectIdList.add(feeder.getCcId());
                        List<CapBankDevice> banks = feeder.getCcCapBanks();
                        for (final CapBankDevice device : banks) {
                            paObjectIdList.add(device.getCcId());
                        }
                    }
                }
                
                final Map<Integer,List<LitePoint>> pointCache = pointDao.getLitePointsByPaObject(paObjectIdList);
                
                final List<Pair<String,CapControlOnelineCanvas>> canvasList = new ArrayList<Pair<String,CapControlOnelineCanvas>>(subBusMsg.getNumberOfBuses());

                for (final SubBus subBus : subBusMsgList) {
                    canvasList.add(createDrawing(subBus, pointCache));
                }
                
                service.execute(new Runnable() {
                    public void run() {
                        try {
                            for (final Pair<String,CapControlOnelineCanvas> pair : canvasList) {
                                String dirAndFileExt = (String) pair.getFirst();
                                CapControlOnelineCanvas canvas = (CapControlOnelineCanvas) pair.getSecond();
                                createHTMLFile(dirAndFileExt, canvas);
                                CTILogger.debug("...generation complete for " + canvas.getDrawing().getFileName());
                            }
                        } finally {
                            pointCache.clear();
                        }
                    }
                });
            }
            // generate only once if running in static mode
            if (isStaticMode())
                stop();
        }
    }

    private Pair<String,CapControlOnelineCanvas> createDrawing(final SubBus subBusAt, final Map<Integer,List<LitePoint>> pointCache) {
        final String subName = subBusAt.getCcName();
        CTILogger.info("Generating SubBus: " + getDirBase() + "/" + subName);

        final String dirAndFileExt = createFileName(subName);
        final CapControlOnelineCanvas emptyCanvas = new CapControlOnelineCanvas(800,
                                                                                1200);
        emptyCanvas.createDrawing(subBusAt,
                                  CAPCONTROL_ONELINE + subName.trim() + ".html", pointCache);

        return new Pair<String,CapControlOnelineCanvas>(dirAndFileExt,emptyCanvas);
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
            CTILogger.info("CBC One-Line successfully stopped.");
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
