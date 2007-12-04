package com.cannontech.amr.moveInMoveOut.service;

import com.cannontech.amr.moveInMoveOut.bean.MoveInFormObj;
import com.cannontech.amr.moveInMoveOut.bean.MoveInResultObj;
import com.cannontech.amr.moveInMoveOut.bean.MoveOutFormObj;
import com.cannontech.amr.moveInMoveOut.bean.MoveOutResultObj;

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

}
