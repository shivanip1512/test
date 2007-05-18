package com.cannontech.cbc.oneline.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.StateDao;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.esub.element.StaticText;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.StreamableCapObject;
import com.cannontech.yukon.cbc.SubBus;
import com.loox.jloox.LxComponent;

public class OnelineUtil {
    public static final int MIN_SUBINJLINE_LENGTH = 150;
    public static final int PXL_LARGE_FONT = 10;
    public static final int DEFAULT_HEIGHT = 800;
    public static final int DEFAULT_WIDTH = 1200;
    @SuppressWarnings("cast")
    private static final double ASPECT_RATIO = (double) DEFAULT_WIDTH / (double) DEFAULT_HEIGHT;
    //public static final int OFFSET_TO_RIGHT = 210;
    public static final int PXLS_PER_FEEDER = 130;
    public static final int PXLS_PER_CAPBANK = 130;
    public static final int PXLS_PER_CHAR = 7;
    public static final int SUB_ST_EN = 0;
    public static final int SUB_ST_DIS = 1;
    public static final int SUB_ST_PENDING = 2;
    public static final int SUB_ST_EN_ALBUS = 3;

    public static final int VERIFY_EN = 0;
    public static final int VERIFY_DIS = 1;

    public static final double SUB_IMG_WIDTH = 30;
    public static final double CAP_IMG_WIDTH = 20;
    public static final int SUB_IMG_HEIGHT = 60;

    public static final String IMG_YUKON_LOGO_WHITE = "YukonLogoWhite.gif";
    public static final String IMG_CAP_CONTROL_LOGO = "CapControlLogo.gif";
    public static final String IMG_BACKBUTTON = "arrow.gif";
    public static final String IMG_EDITOR = "edit.gif";
    public static final String IMG_QUESTION = "question.gif";
    public static final String IMG_PTTIMESTAMP = "magnifier.gif";
    public static final String IMG_REGENERATE = "regenerate.gif";

    

    public static final String ONELN_STATE_GROUP = "1LNSUBSTATE";
    public static final String ONELN_VERIFY_GROUP = "1LNVERIFY";
    
    public static final Font SMALL_FONT = new java.awt.Font("arial",
                                                            java.awt.Font.BOLD,
                                                            10);
    public static final Font MEDIUM_FONT = new java.awt.Font("arial",
                                                            java.awt.Font.BOLD,
                                                            12);

    public static final Font LARGE_FONT = new java.awt.Font("arial",
                                                            java.awt.Font.BOLD,
                                                            14);
    public static final Color TAG_COLOR = new Color(159, 187, 172);
    
    public static final int EDITOR_CAPCONTROL = 2;
    public static final Color PURPLISH = new Color(115, 79, 182);

    public static LiteStateGroup getOnelineStateGroup(String groupName) {
        LiteStateGroup[] groups = DaoFactory.getStateDao().getAllStateGroups();
        for (int i = 0; i < groups.length; i++) {
            LiteStateGroup group = groups[i];
            if (group.getStateGroupName().equalsIgnoreCase(groupName))
                return group;

        }
        return null;
    }

    public static List stringToList(String s) {
        String[] temp = StringUtils.split(s, ':');
        List<String> l = new ArrayList(0);
        for (int i = 0; i < temp.length; i++) {
            String string = temp[i];
            if (string.equalsIgnoreCase("null")) {
                l.add(null);
            } else {
                l.add(string);
            }
        }
        return l;
    }

    public static List<LiteYukonImage> getAdditionalImages() {
        StateDao stateDao = DaoFactory.getStateDao();
        // we need images from state groups: CapBankStatus, 1LNSUBSTATE,
        // 1LNVERIFY
        List<LiteYukonImage> additionalImages = new ArrayList<LiteYukonImage>(0);
        LiteStateGroup[] allStateGroups = stateDao.getAllStateGroups();
        for (int i = 0; i < allStateGroups.length; i++) {
            LiteStateGroup group = allStateGroups[i];
            String stateGroupName = group.getStateGroupName();
            int stateGroupID = group.getStateGroupID();
            if (stateGroupName.equalsIgnoreCase(OnelineUtil.ONELN_STATE_GROUP) || stateGroupName.equalsIgnoreCase(OnelineUtil.ONELN_VERIFY_GROUP) || stateGroupID == StateGroupUtils.STATEGROUPID_CAPBANK) {
                LiteState[] liteStates = stateDao.getLiteStates(stateGroupID);
                for (int j = 0; j < liteStates.length; j++) {
                    LiteState state = liteStates[j];
                    int imageID = state.getImageID();
                    LiteYukonImage liteYukonImage = DaoFactory.getYukonImageDao()
                                                              .getLiteYukonImage(imageID);
                    additionalImages.add(liteYukonImage);
                }

            }

        }
        return additionalImages;

    }

    public static Dimension getDrawingDimension(SubBus subBusMsg) {

        // size determining stuff
        Vector ccFeeders = subBusMsg.getCcFeeders();
        int maxCapNum = 0;
        String maxCapFeederName = "";
        for (Iterator iter = ccFeeders.iterator(); iter.hasNext();) {
            Feeder f = (Feeder) iter.next();
            if (f.getCcCapBanks().size() > maxCapNum) {
                maxCapNum = f.getCcCapBanks().size();
                maxCapFeederName = f.getCcName();
            }
        }

        Dimension feederOptimized = optimizeForFeeders(ccFeeders.size());
        Dimension capOptimized = optimizeForCaps(maxCapNum, maxCapFeederName, subBusMsg.getCcName());
        int heght = (int) Math.max(feederOptimized.getHeight(),
                                   capOptimized.getHeight());
        int width = (int) Math.max(feederOptimized.getWidth(),
                                   capOptimized.getWidth());
        return new Dimension(width, heght);

    }

    private static Dimension optimizeForCaps(int maxCapNum,
            String maxCapFeederName, String subName) {

        int height = DEFAULT_HEIGHT;
        int width = DEFAULT_WIDTH;
        int fdrOffset = getFeederOffset(maxCapFeederName);
        int temp = ((DEFAULT_WIDTH - OnelineUtil.getInjLineLength(subName)) - (fdrOffset + (PXLS_PER_CAPBANK * (maxCapNum + 1))));
        width = (temp < 0) ? DEFAULT_WIDTH + (temp * -1) : DEFAULT_WIDTH;
        height = (int) ((width > DEFAULT_WIDTH) ? (width / ASPECT_RATIO)
                : DEFAULT_HEIGHT);
        return new Dimension(width, height);
    }

    public static int getFeederOffset(String maxCapFeederName) {
        int fdrOffset = (PXLS_PER_CHAR * maxCapFeederName.length() < PXLS_PER_CAPBANK) ? PXLS_PER_FEEDER
                : PXLS_PER_CHAR * maxCapFeederName.length();
        return fdrOffset;
    }

    private static Dimension optimizeForFeeders(int numOfFeeders) {
        int width = DEFAULT_WIDTH;
        int height = DEFAULT_HEIGHT;

        if (numOfFeeders >= 7) {
            height = numOfFeeders * PXLS_PER_FEEDER;
            width = (int) (height * ASPECT_RATIO);
        }
        return new Dimension(width, height);
    }

    public static StaticText createColoredTextElement(String sep,
            Point2D p, Integer optionXOffset, Integer optionYOffset, Color c) {
        StaticText text = new StaticText();
        text.setText(sep);
        double pX = (optionXOffset != null) ? (optionXOffset.intValue() + p.getX()) : p.getX();
        text.setX(pX);
        double pY = (optionYOffset != null) ? (optionYOffset.intValue() + p.getY()) : p.getY(); 
        text.setY(pY);
        text.setFont(SMALL_FONT);
        text.setPaint(c);
        return text;
    }
   
    public static StaticText createTextElement(String sep,
            Point2D p, Integer optionXOffset, Integer optionYOffset) {
        StaticText text = new StaticText();
        text.setText(sep);
        double pX = (optionXOffset != null) ? (optionXOffset.intValue() + p.getX()) : p.getX();
        text.setX(pX);
        double pY = (optionYOffset != null) ? (optionYOffset.intValue() + p.getY()) : p.getY(); 
        text.setY(pY);
        text.setFont(SMALL_FONT);
        text.setPaint(Color.LIGHT_GRAY);
        return text;
    }

    public static Point2D getStartPoint(LxComponent component) {
        return new Point ((int)component.getX(), (int)component.getY());
    }

    public static int getInjLineLength(String subName) {
        int length = Math.max((int) (subName.length() * PXL_LARGE_FONT - OnelineUtil.SUB_IMG_WIDTH), MIN_SUBINJLINE_LENGTH);
        return length;
    }

    

    public static String extractObjectIdFromString(String str) {
        String[] allStrings = StringUtils.split(str, "_");
        if (allStrings.length == 2)
            return allStrings[1];
        return "ID_PLACEHOLDER";
    }

    public static String createEditLink(int id) {
        String link = "/editor/cbcBase.jsf?type=" + EDITOR_CAPCONTROL + "&itemid=" + id;
        return link;
    }
    
    public static String createBookmarkLink(int id) {
        String link = "/capcontrol/oneline/OnelineBookmarkServlet?itemid=" + id;
        return link;
    }
    
    public static int getYukonType (StreamableCapObject obj) {
        if (obj instanceof SubBus)
        {
            return PAOGroups.CAP_CONTROL_SUBBUS;
        }
        else if (obj instanceof Feeder)
        {
            return PAOGroups.CAP_CONTROL_FEEDER;
        }
        else if(obj instanceof CapBankDevice)
        {
            return PAOGroups.CAPBANK;
        }
        return 0;
    }

}
