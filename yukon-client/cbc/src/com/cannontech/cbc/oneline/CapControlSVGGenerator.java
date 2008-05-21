package com.cannontech.cbc.oneline;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import com.cannontech.cbc.oneline.model.cap.OnelineCap;
import com.cannontech.cbc.oneline.model.feeder.OnelineFeeder;
import com.cannontech.cbc.oneline.util.OnelineUtil;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.element.LineElement;
import com.cannontech.esub.element.StateImage;
import com.cannontech.esub.element.StaticImage;
import com.cannontech.esub.svg.BaseSVGGenerator;
import com.cannontech.esub.svg.SVGOptions;
import com.cannontech.esub.util.DrawingUpdater;
import com.loox.jloox.LxAbstractImage;
import com.loox.jloox.LxAbstractText;
import com.loox.jloox.LxComponent;

public class CapControlSVGGenerator extends BaseSVGGenerator {

    @Override
    public CDATASection createCDATASection(SVGDocument doc) {
        getJsGenerator().addOnload("initCC();");
        String script = getJsGenerator().getScript();
        script = StringUtils.remove(script, "   refresh(evt);");
        CDATASection cdata = doc.createCDATASection(script);
        return cdata;
    }

    private CapControlDrawingUpdater drawingUpdater;

    public CapControlSVGGenerator() {
        super();
    }

    public CapControlSVGGenerator(SVGOptions options) {
        super(options);

    }

    public CapControlSVGGenerator(SVGOptions options, Drawing d) {
        super(options);
        if (!options.isStaticSVG()) {
            initDrawingUpdater(d);
        }
    }

    public void addBehavior(LxComponent comp, Element elem) {

        String compName = comp.getName();
        elem.setAttributeNS(null, "id", compName);

        elem.removeAttributeNS(null, "onclick");

        if (comp instanceof LxAbstractText && getGenOptions().isScriptingEnabled() && !isFeeder(comp)) {
            String paoID = OnelineUtil.extractObjectIdFromString(compName);
            elem.setAttributeNS(null,
                                "onmouseover",
                                "underLine(evt.getTarget())");
            elem.setAttributeNS(null,
                                "onmouseout",
                                "noUnderLine(evt.getTarget())");
            if (StringUtils.contains(comp.getName(), CommandPopups.SUB_TAG)) {

                elem.setAttributeNS(null,
                                    "onclick",
                                    "openPopupWin (evt.getTarget(), \"" + CommandPopups.SUB_TAG + "_" + paoID + "\")");
            } else if (StringUtils.contains(comp.getName(),
                                            CommandPopups.FEEDER_TAG)) {
                elem.setAttributeNS(null,
                                    "onclick",
                                    "openPopupWin (evt.getTarget(), \"" + CommandPopups.FEEDER_TAG + "_" + paoID + "\")");

            } else if (StringUtils.contains(comp.getName(), CommandPopups.CAP_TAG)) {
                elem.setAttributeNS(null,
                                    "onclick",
                                    "openPopupWin (evt.getTarget(), \"" + CommandPopups.CAP_TAG + "_" + paoID + "\")");

            } else if (StringUtils.contains(comp.getName(), "legend")) {
                elem.setAttributeNS(null,
                                    "onclick",
                                    "openPopupWin (evt.getTarget(), \"legend\")");
            } else if (StringUtils.contains(comp.getName(), CommandPopups.VAR_CHANGE_POPUP)) {
                elem.setAttributeNS(null,
                                    "onclick",
                                    "openPopupWin (evt.getTarget(), \"" + CommandPopups.VAR_CHANGE_POPUP + "_" + paoID + "\")");
            } else if (StringUtils.contains(comp.getName(), CommandPopups.BANK_MOVE_BACK)) {
            	elem.setAttributeNS(null,
                        "onclick",
                        "openPopupWin (evt.getTarget(), \"" + CommandPopups.BANK_MOVE_BACK + "_" + paoID + "\")");
            } else if (StringUtils.contains(comp.getName(), CommandPopups.BANK_MOVE)) {
            	elem.setAttributeNS(null,
                        "onclick",
                        "openPopupWin (evt.getTarget(), \"" + CommandPopups.BANK_MOVE + "_" + paoID + "\")");
            } else if ( StringUtils.contains(comp.getName(), CommandPopups.ALERTS_POPUP) ) {
            	addDynamicAttributes(elem,CommandPopups.ALERTS_POPUP);
            }

        } else if (comp instanceof LxAbstractImage && getGenOptions().isScriptingEnabled() && !isCapBankImage(comp)) {
            if (comp instanceof StateImage) {
                StateImage stateImage = (StateImage)comp;
                if(stateImage.getControlEnabled()) {
                    if (isSub(stateImage)) {
                        String id = OnelineUtil.extractObjectIdFromString(comp.getName());
                        addDynamicAttributes(elem, CommandPopups.SUB_COMMAND + "_" + id);
                    }
                }
            }
        } else if (isFeeder(comp) && getGenOptions().isScriptingEnabled()) {
            String id = OnelineUtil.extractObjectIdFromString(comp.getName());
            addDynamicAttributes(elem, CommandPopups.FEEDER_COMMAND +  "_" + id);
            elem.setAttributeNS(null,
                                "onmouseover",
                                "underLine(evt.getTarget())");
            elem.setAttributeNS(null,
                                "onmouseout",
                                "noUnderLine(evt.getTarget())");

        }
        else if (isCapBank(comp) && getGenOptions().isScriptingEnabled()) {
            String id = OnelineUtil.extractObjectIdFromString(comp.getName());
            addDynamicAttributes(elem, CommandPopups.CAP_COMMAND +  "_" + id);
            elem.setAttributeNS(null,
                                "onmouseover",
                                "underLine(evt.getTarget())");
            elem.setAttributeNS(null,
                                "onmouseout",
                                "noUnderLine(evt.getTarget())");

        }
        else if (isCapBankImage(comp) && getGenOptions().isScriptingEnabled()) {
            StateImage stateImage = (StateImage)comp;
            if(stateImage.getControlEnabled()) {
                String str = comp.getName();
                String id = OnelineUtil.extractObjectIdFromString(str);
                addDynamicAttributes(elem, CommandPopups.CAP_COMMAND + "_" + id);
            }
        }
        if (isWarningImage(comp) && getGenOptions().isScriptingEnabled()) {
        	String str = comp.getName();
            String id = OnelineUtil.extractObjectIdFromString(str);
        	addDynamicAttributes(elem,CommandPopups.WARNING_IMAGE + "_" + id);
        }
        if (isCapBankInfoImage(comp) && getGenOptions().isScriptingEnabled()) {
            String str = comp.getName();
            String id = OnelineUtil.extractObjectIdFromString(str);
            addDynamicAttributes(elem, CommandPopups.CAP_INFO + "_" + id);
        }
        if (isCapBankPointTimestampImage(comp) && getGenOptions().isScriptingEnabled()) {
            String str = comp.getName();
            String id = OnelineUtil.extractObjectIdFromString(str);
            addDynamicAttributes(elem, CommandPopups.CAP_TMSTMP + "_" + id);
        }

    }


    private boolean isCapBankPointTimestampImage(LxComponent comp) {
        if ((comp instanceof StaticImage) && StringUtils.contains(comp.getName(),
                                                                  CommandPopups.CAP_TMSTMP)) {
             return true;
         }
         return false;
     }

    private boolean isCapBankInfoImage(LxComponent comp) {
        if ((comp instanceof StaticImage) && StringUtils.contains(comp.getName(),
                                                                 CommandPopups.CAP_INFO)) {
            return true;
        }
        return false;
    }
    private boolean isWarningImage(LxComponent comp) {
        if ((comp instanceof StaticImage) && StringUtils.contains(comp.getName(),
                                                                 CommandPopups.WARNING_IMAGE)) {
            return true;
        }
        return false;
    }
    private void addDynamicAttributes(Element elem, String typeString) {
        elem.setAttributeNS(null,
                            "onclick",
                            "openPopupWin(evt.getTarget(), \"" + typeString + "\")");
        elem.setAttributeNS(null, "onmouseover", "addBorder(evt.getTarget())");
        elem.setAttributeNS(null, "onmouseout", "noBorder(evt.getTarget())");
    }

    private boolean isCapBankImage(LxComponent comp) {
        if ((comp instanceof StateImage) && StringUtils.contains(comp.getName(),
                                                                 OnelineCap.NAME_PREFIX)) {
            return true;
        }
        return false;
    }

    private boolean isFeeder(LxComponent comp) {
        return (((comp instanceof LineElement) || comp instanceof LxAbstractText) && StringUtils.contains(comp.getName(),
                                                                                                          OnelineFeeder.NAME_PREFIX));
    }
    private boolean isCapBank(LxComponent comp) {
        return (((comp instanceof LineElement) || comp instanceof LxAbstractText) && StringUtils.contains(comp.getName(),
                                                                                                          OnelineCap.NAME_PREFIX));
    }

    private boolean isSub(StateImage newImage) {
        LiteYukonImage yukonImage = newImage.getYukonImage();
        if (yukonImage != null) {
            String imageName = yukonImage.getImageName();
            return StringUtils.contains(imageName, "sub_");
        }
        return false;
    }

    public void addJSFiles(SVGDocument doc, Element svgRoot) {

        svgRoot.insertBefore(createScriptElement(doc, "xmlhttp.js"),
                             svgRoot.getFirstChild());
        svgRoot.insertBefore(createScriptElement(doc, "cgui_lib.js"),
                             svgRoot.getFirstChild());
        svgRoot.insertBefore(createScriptElement(doc, "prototype150.js"),
                             svgRoot.getFirstChild());
        svgRoot.insertBefore(createScriptElement(doc, "cc.js"),
                             svgRoot.getFirstChild());
        svgRoot.insertBefore(createScriptElement(doc, "cconelinepopup.js"),
                             svgRoot.getFirstChild());

    }

    @Override
    public DrawingUpdater initDrawingUpdater(Drawing d) {
        if (drawingUpdater == null) {
            drawingUpdater = new CapControlDrawingUpdater(d);
            drawingUpdater.setUpdateGraphs(false);
        }
        return drawingUpdater;
    }

    public CapControlDrawingUpdater getDrawingUpdater() {
        return drawingUpdater;
    }

}
