#pragma once

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>
#include <iostream>

#include "dsm2.h"
#include "port_base.h"
#include "port_dialable.h"

class CtiPortDialout : public CtiPortDialable
{
protected:

private:

    INT modemReset(USHORT Trace, BOOL dcdTest = TRUE);
    INT modemSetup(USHORT Trace, BOOL dcdTest = TRUE);
    INT modemHangup(USHORT Trace, BOOL dcdTest = TRUE);
    INT modemConnect(PCHAR Message, USHORT Trace, BOOL dcdTest = TRUE);

public:

    CtiPortDialout();
    CtiPortDialout(const CtiPortDialout& aRef);
    virtual ~CtiPortDialout();

    CtiPortDialout& operator=(const CtiPortDialout& aRef);

    INT  connectToDevice(CtiDeviceSPtr Device, LONG &LastDeviceId, INT trace);
    INT  disconnect(CtiDeviceSPtr Device, INT trace);

    INT reset(INT trace);
    INT setup(INT trace);
    INT close(INT trace);

};
