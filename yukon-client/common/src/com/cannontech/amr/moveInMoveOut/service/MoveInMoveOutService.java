package com.cannontech.amr.moveInMoveOut.service;

import java.util.Date;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.amr.moveInMoveOut.bean.MoveInFormObj;
import com.cannontech.amr.moveInMoveOut.bean.MoveInResultObj;
import com.cannontech.amr.moveInMoveOut.bean.MoveOutFormObj;
import com.cannontech.amr.moveInMoveOut.bean.MoveOutResultObj;
import com.cannontech.core.dynamic.PointValueHolder;

public interface MoveInMoveOutService {

    /**
     * @param moveInFormObj
     * @return
     */
    public MoveInResultObj moveIn(MoveInFormObj moveInFormObj);

    /**
     * @param moveOutFormObj
     * @return
     */
    public MoveOutResultObj moveOut(MoveOutFormObj moveOutFormObj);

    /**
     * @param moveInFormObj
     * @return
     */
    public MoveInResultObj scheduleMoveIn(MoveInFormObj moveInFormObj);

    /**
     * @param moveInFormObj
     * @param moveInDefinition
     * @return
     */
    public MoveOutResultObj scheduleMoveOut(MoveOutFormObj moveOutFormObj);

    /**
     * @param pvh
     * @param calculatedValue
     * @param calculatedDate
     */
    public void insertDataPoint(PointValueHolder pvh, double calculatedValue,
            Date calculatedDate);

    /**
     * @param oldMeter
     * @param newMeter
     */
    public void updateMeter(Meter oldMeter, Meter newMeter);

    public void sendDBChangeMsg(Meter meter);

    // These methods are used to keep the email and the page very similiar.
    public String createMoveInErrorMsg(String deviceNameRepresentation);

    public String createMoveOutErrorMsg(String deviceNameRepresentation);

    public String createMoveInSuccessMsg(String deviceNameRepresentation);

    public String createMoveOutSuccessMsg(String deviceNameRepresentation);
}
