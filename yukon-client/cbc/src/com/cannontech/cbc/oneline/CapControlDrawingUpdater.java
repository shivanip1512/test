package com.cannontech.cbc.oneline;

import com.cannontech.cbc.oneline.elements.DynamicLineElement;
import com.cannontech.cbc.oneline.elements.SubDynamicImage;
import com.cannontech.cbc.oneline.view.OneLineDrawing;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.element.LineElement;
import com.cannontech.esub.element.StateImage;
import com.cannontech.esub.util.ESubDrawingUpdater;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.loox.jloox.LxComponent;

public class CapControlDrawingUpdater extends ESubDrawingUpdater {

    // The drawing to update
    private OneLineDrawing onelineDrawing;
    private SubBus subBusMsg;

    public SubBus getSubBusMsg() {
        return subBusMsg;
    }

    @Override
    public boolean updateLineElement(boolean change, LxComponent lxComponent) {
        LineElement le = ((LineElement) lxComponent);
        if (le.getColorPointID() != -1) {
            return super.updateLineElement(change, lxComponent);
        } else if (le instanceof DynamicLineElement) {
            DynamicLineElement dynamicLine = (DynamicLineElement) le;
            dynamicLine.update(change);
        }
        return change;

    }

    public CapControlDrawingUpdater(Drawing d) {
        super(d);
    }

    public void setSubBusMsg(SubBus msg) {
        subBusMsg = msg;
    }

    @Override
    public boolean updateStateImage(boolean change, LxComponent lxComponent) {
        StateImage si = (StateImage) lxComponent;
        LitePoint lp = si.getPoint();
        if (lp != null) {
            return super.updateStateImage(change, si);
        } else if (si instanceof SubDynamicImage) {
            SubDynamicImage img = (SubDynamicImage) si;
            img.update(change);
            return change;
        }
        return change;
    }

    public OneLineDrawing getOnelineDrawing() {
        return onelineDrawing;
    }

    public void setOnelineDrawing(OneLineDrawing d) {
        onelineDrawing = d;
    }

}
