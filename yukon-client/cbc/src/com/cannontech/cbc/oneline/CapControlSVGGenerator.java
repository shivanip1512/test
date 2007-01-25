package com.cannontech.cbc.oneline;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import com.cannontech.cbc.oneline.model.cap.CapControlPanel;
import com.cannontech.cbc.oneline.model.cap.OnelineCap;
import com.cannontech.cbc.oneline.model.feeder.FeederControlPanel;
import com.cannontech.cbc.oneline.model.feeder.OnelineFeeder;
import com.cannontech.cbc.oneline.model.sub.SubControlPanel;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.element.LineElement;
import com.cannontech.esub.element.StateImage;
import com.cannontech.esub.svg.BaseSVGGenerator;
import com.cannontech.esub.svg.SVGOptions;
import com.cannontech.esub.util.DrawingUpdater;
import com.loox.jloox.LxAbstractImage;
import com.loox.jloox.LxAbstractText;
import com.loox.jloox.LxComponent;

public class CapControlSVGGenerator extends BaseSVGGenerator {



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
            elem.setAttributeNS(null,
                                "onmouseover",
                                "underLine(evt.getTarget())");
            elem.setAttributeNS(null,
                                "onmouseout",
                                "noUnderLine(evt.getTarget())");

        } else if (comp instanceof LxAbstractImage && getGenOptions().isScriptingEnabled() && !isCap(comp)) {
            if (comp instanceof StateImage) {
                StateImage newImage = (StateImage) comp;
                if (isSub(newImage)) {
                    setOnelineAttributes(elem,
                                         SubControlPanel.PANEL_NAME,
                                         false);
                }
            }
        } else if (isFeeder(comp) && getGenOptions().isScriptingEnabled()) {
            boolean isFdrTextName = comp instanceof LxAbstractText;
            String[] temp = StringUtils.split(compName, '_');
            setOnelineAttributes(elem,
                                 FeederControlPanel.PANEL_NAME + "_" + temp[1],
                                 isFdrTextName);
        } else if (isCap(comp) && getGenOptions().isScriptingEnabled()) {
            String[] temp = StringUtils.split(compName, '_');
            setOnelineAttributes(elem,
                                 CapControlPanel.PANEL_NAME + "_" + temp[1],
                                 false);
        }

    }

    private boolean isCap(LxComponent comp) {
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

    private void setOnelineAttributes(Element elem, String panelName,
            boolean addUnderline) {
        String toggleOn = "toggleControlPanel(evt.getTarget(), 'on','" + panelName + "')";
        String toggleOff = "toggleControlPanel(evt.getTarget(), 'off','" + panelName + "')";
        String toggleClick = "tglOnClick(evt.getTarget(), '" + panelName + "')";
        if (addUnderline) {
            toggleOn = ("underLine(evt.getTarget());" + toggleOn);
            toggleOff = ("noUnderLine(evt.getTarget());" + toggleOff);
        }
        elem.setAttributeNS(null, "onmouseover", toggleOn);
        elem.setAttributeNS(null, "onmouseout", toggleOff);

        elem.setAttributeNS(null, "onclick", toggleClick);
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
        svgRoot.insertBefore(createScriptElement(doc, "prototype.js"),
                             svgRoot.getFirstChild());
        svgRoot.insertBefore(createScriptElement(doc, "cc.js"),
                             svgRoot.getFirstChild());

    }

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
