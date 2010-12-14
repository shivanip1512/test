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
namespace Cti           {
namespace CapControl    {

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

MissingPointAttribute::MissingPointAttribute(const long ID, const PointAttribute & attribute, string paoType)
    : std::exception(),
      _description("Missing Point Attribute: '")
{
    _description += attribute.name() + "' on "+ paoType +" with ID: " + CtiNumStr(ID);
}


const char * MissingPointAttribute::what( ) const
{
    return _description.c_str();
}
}
}
