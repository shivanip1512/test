/*-----------------------------------------------------------------------------*
*
* File:   port_dialout
*
* Class:  CtiPortDialout
* Date:   9/17/2002
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2004/05/05 15:31:41 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PORT_DIALOUT_H__
#define __PORT_DIALOUT_H__
#pragma warning( disable : 4786)


#include <windows.h>
#include <iostream>
using namespace std;

#include <rw\cstring.h>

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
#endif // #ifndef __PORT_DIALOUT_H__
