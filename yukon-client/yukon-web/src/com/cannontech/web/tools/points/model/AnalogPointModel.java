package com.cannontech.web.tools.points.model;

import com.cannontech.database.data.point.AnalogPoint;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class AnalogPointModel extends ScalarPointModel<AnalogPoint> {

    private PointAnalog pointAnalog;
    private PointAnalogControl pointAnalogControl;

    public PointAnalog getPointAnalog() {
        if (pointAnalog == null) {
            pointAnalog = new PointAnalog();
        }
        return pointAnalog;
    }

    public void setPointAnalog(PointAnalog pointAnalog) {
        this.pointAnalog = pointAnalog;
    }

    public PointAnalogControl getPointAnalogControl() {
        if (pointAnalogControl == null) {
            pointAnalogControl = new PointAnalogControl();
        }
        return pointAnalogControl;
    }

    public void setPointAnalogControl(PointAnalogControl pointAnalogControl) {
        this.pointAnalogControl = pointAnalogControl;
    }

    @Override
    public void buildDBPersistent(AnalogPoint point) {

        getPointAnalog().buildDBPersistent(point.getPointAnalog());

        getPointAnalogControl().buildDBPersistent(point.getPointAnalogControl());

        super.buildDBPersistent(point);
    }

    @Override
    public void buildModel(AnalogPoint point) {
        getPointAnalog().buildModel(point.getPointAnalog());
        getPointAnalogControl().buildModel(point.getPointAnalogControl());

        super.buildModel(point);
    }
}
