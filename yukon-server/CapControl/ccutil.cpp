/*---------------------------------------------------------------------------
        Filename:  ccstatsobject.cpp
        
        Programmer:  Julie Richter
                
        Description:    CCStatsObject
                        

        Initial Date:  2/24/2009
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2009
---------------------------------------------------------------------------*/

#include "yukon.h"
#include "ccutil.h"
#include "pointdefs.h"
 

extern ULONG _MSG_PRIORITY;

CtiRequestMsg* createPorterRequestMsg(long controllerId,const string& commandString) 
{

    CtiRequestMsg* reqMsg = new CtiRequestMsg(controllerId, commandString);
    reqMsg->setMessagePriority(_MSG_PRIORITY);
    return reqMsg;
};

bool isQualityOk(unsigned quality)
{
    if (quality == NormalQuality || quality == ManualQuality)
    {
        return true;
    }
    return false;
}

