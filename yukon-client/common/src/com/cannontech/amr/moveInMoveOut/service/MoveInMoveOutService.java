package com.cannontech.amr.moveInMoveOut.service;

import com.cannontech.amr.meter.model.PlcMeter;
import com.cannontech.amr.moveInMoveOut.bean.MoveInForm;
import com.cannontech.amr.moveInMoveOut.bean.MoveInResult;
import com.cannontech.amr.moveInMoveOut.bean.MoveOutForm;
import com.cannontech.amr.moveInMoveOut.bean.MoveOutResult;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface MoveInMoveOutService {

    /**
     * @param moveInFormObj
     * @return
     */
    public MoveInResult moveIn(MoveInForm moveInFormObj);

    /**
     * @param moveOutFormObj
     * @return
     */
    public MoveOutResult moveOut(MoveOutForm moveOutFormObj);

    /**
     * @param moveInFormObj
     * @return
     */
    public MoveInResult scheduleMoveIn(MoveInForm moveInFormObj);

    /**
     * @param moveInFormObj
     * @param moveInDefinition
     * @return
     */
    public MoveOutResult scheduleMoveOut(MoveOutForm moveOutFormObj);
    
    /**
     * Determineif the user is authorized to execute a move in/move out related command.
     * @param liteYukonUser
     * @return
     */
    public boolean isAuthorized(LiteYukonUser liteYukonUser, PlcMeter meter);

}
