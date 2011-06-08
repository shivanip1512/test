/*-----------------------------------------------------------------------------*
*
* File:   port_dialin
*
* Class:  CtiPortDialin
* Date:   11/13/2002
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.7.24.1 $
* DATE         :  $Date: 2008/11/13 17:23:39 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PORT_DIALIN_H__
#define __PORT_DIALIN_H__
#pragma warning( disable : 4786)



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
#endif // #ifndef __PORT_DIALIN_H__
