#pragma once

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>
#include <iostream>

#include "dsm2.h"
#include "port_base.h"
#include "port_dialable.h"


class CtiPortDialin : public CtiPortDialable
{
protected:

private:

    INT modemReset(USHORT Trace, BOOL dcdTest = TRUE);
    INT modemSetup(USHORT Trace, BOOL dcdTest = TRUE);

public:

    CtiPortDialin();
    CtiPortDialin(const CtiPortDialin& aRef);
    virtual ~CtiPortDialin();
    CtiPortDialin& operator=(const CtiPortDialin& aRef);

    INT  connectToDevice(CtiDeviceSPtr Device, LONG &LastDeviceId, INT trace);

    INT reset(INT trace);
    INT setup(INT trace);
    INT close(INT trace);
};
