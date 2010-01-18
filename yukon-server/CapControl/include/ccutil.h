#pragma once
#pragma warning( disable : 4786)

#include "devicetypes.h"
#include "msg_pcrequest.h"

enum CapControlType
{
    Undefined = 0,
    CapBank,
    Feeder,
    SubBus,
    Substation,
    Area,
    Strategy,
    Schedule,
    SpecialArea,
    Ltc = TYPELTC
};

CtiRequestMsg* createPorterRequestMsg(long controllerId,const string& commandString);
