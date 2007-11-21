package com.cannontech.esub.svg;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import com.cannontech.esub.Drawing;
import com.cannontech.esub.util.DrawingUpdater;
import com.cannontech.esub.util.ESubDrawingUpdater;
import com.loox.jloox.LxAbstractImage;
import com.loox.jloox.LxAbstractText;
import com.loox.jloox.LxComponent;

public class ESubSVGGenerator extends BaseSVGGenerator {

    public ESubSVGGenerator(SVGOptions options) {
        super(options);
    }

    public ESubSVGGenerator() {
        super();
    }
    
    public void addBehavior(LxComponent comp, Element elem) {
        elem.removeAttributeNS(null, "onclick");
        
        if(comp instanceof LxAbstractText && getGenOptions().isScriptingEnabled()) {
            elem.setAttributeNS(null,"onmouseover", "underLine(evt.getTarget())");
            elem.setAttributeNS(null,"onmouseout", "noUnderLine(evt.getTarget())");
        }
        else 
        if(comp instanceof LxAbstractImage && getGenOptions().isScriptingEnabled()){                     
            elem.setAttributeNS(null,"onmouseover", "addBorder(evt.getTarget())");
            elem.setAttributeNS(null,"onmouseout", "noBorder(evt.getTarget())");
        }
    }
    
    public void addJSFiles(SVGDocument doc, Element svgRoot)  {
        svgRoot.insertBefore(createScriptElement(doc, "refresh.js"), svgRoot.getFirstChild());
        svgRoot.insertBefore(createScriptElement(doc, "updateGraph.js"), svgRoot.getFirstChild());
        svgRoot.insertBefore(createScriptElement(doc, "action.js"), svgRoot.getFirstChild());
        svgRoot.insertBefore(createScriptElement(doc, "point.js"), svgRoot.getFirstChild());
        svgRoot.insertBefore(createScriptElement(doc, "xmlhttp.js"), svgRoot.getFirstChild());
        svgRoot.insertBefore(createScriptElement(doc, "cgui_lib.js"), svgRoot.getFirstChild());
    }

    @Override
    public DrawingUpdater initDrawingUpdater(Drawing d) {
        ESubDrawingUpdater updater = new ESubDrawingUpdater(d);
        updater.setUpdateGraphs(false);
        return updater;
    }

}
