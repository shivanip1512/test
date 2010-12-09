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
    ZoneType,                   // added Type suffix to remove warnings from compiler
    VoltageRegulatorType
};

CtiRequestMsg* createPorterRequestMsg(long controllerId,const string& commandString);
bool isQualityOk(unsigned quality);
