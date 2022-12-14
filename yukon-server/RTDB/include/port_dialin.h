#pragma once

#include "port_base.h"
#include "port_dialable.h"

class IM_EX_PRTDB CtiPortDialin : public CtiPortDialable
{
    YukonError_t modemReset(USHORT Trace, BOOL dcdTest = TRUE);
    INT modemSetup(USHORT Trace, BOOL dcdTest = TRUE);

public:

    CtiPortDialin();

    YukonError_t connectToDevice(CtiDeviceSPtr Device, LONG &LastDeviceId, INT trace);

    YukonError_t reset(INT trace);
    INT setup(INT trace);
    INT close(INT trace);
};
