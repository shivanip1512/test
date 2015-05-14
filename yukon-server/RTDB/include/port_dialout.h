#pragma once

#include "port_base.h"
#include "port_dialable.h"

class IM_EX_PRTDB CtiPortDialout : public CtiPortDialable
{
    YukonError_t modemReset(USHORT Trace, BOOL dcdTest = TRUE);
    INT modemSetup(USHORT Trace, BOOL dcdTest = TRUE);
    INT modemHangup(USHORT Trace, BOOL dcdTest = TRUE);
    YukonError_t modemConnect(PCHAR Message, USHORT Trace, BOOL dcdTest = TRUE);

public:

    YukonError_t connectToDevice(CtiDeviceSPtr Device, LONG &LastDeviceId, INT trace);
    INT  disconnect(CtiDeviceSPtr Device, INT trace);

    YukonError_t reset(INT trace);
    INT setup(INT trace);
    INT close(INT trace);
};
