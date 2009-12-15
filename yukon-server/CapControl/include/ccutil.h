#pragma once
#pragma warning( disable : 4786)

#include "devicetypes.h"

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
