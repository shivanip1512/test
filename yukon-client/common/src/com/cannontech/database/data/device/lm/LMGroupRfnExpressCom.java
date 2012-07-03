package com.cannontech.database.data.device.lm;

public class LMGroupRfnExpressCom extends com.cannontech.database.data.device.lm.LMGroupExpressCom
{
    public LMGroupRfnExpressCom()
    {
        super();
        lmGroupExpressComm = new com.cannontech.database.db.device.lm.LMGroupExpressCom();
        getYukonPAObject().setType( com.cannontech.database.data.pao.PAOGroups.STRING_RFN_EXPRESSCOMM_GROUP[0] );
        getLMGroupExpressComm().setRouteID( 0 );
    }
    
    //Route ID is unused for RFN Expresscom group, so do nothing when change is attempted
    public void setRouteID( Integer rtID_ ) {  
    }
}