/*-----------------------------------------------------------------------------*
*
* File:   port_pool_out
*
* Class:  CtiPortPoolDialout
* Date:   3/4/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2004/05/05 15:31:41 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PORT_POOL_OUT_H__
#define __PORT_POOL_OUT_H__
#pragma warning( disable : 4786)

#include "port_base.h"


#define PORTPOOL_DEBUGLEVL_CHILDAVAILABILITY            0x00000010
#define PORTPOOL_DEBUGLEVL_CHILDSELECTION               0x00000020
#define PORTPOOL_DEBUGLEVL_CHILDALLOCATION              0x00000040
#define PORTPOOL_DEBUGLEVL_CHILDADDITION                0x00000080

#define PORTPOOL_DEBUGLEVL_POOLQUEUE                    0x00010000  // Used on the porter side...
#define PORTPOOL_DEBUGLEVL_QUEUEDUMPS                   0x00020000
#define PORTPOOL_DEBUGLEVL_POSTSTOPARENT                0x00040000  // Used on the porter side...

class IM_EX_PRTDB CtiPortPoolDialout : public CtiPort
{
protected:

    typedef vector< CtiPortSPtr > CtiPortPoolVector;

    vector< long >          _portids;
    CtiPortPoolVector       _ports;

private:

    static int _poolDebugLevel;

public:

    enum
    {
        PPSC_ParentQueueEmpty,
        PPSC_AllChildrenBusy,
        PPSC_ChildReady
    }
    CtiPortPoolDialoutStatusCode;

    typedef CtiPort Inherited;

    CtiPortPoolDialout()
    {
    }

    CtiPortPoolDialout(const CtiPortPoolDialout& aRef)
    {
        *this = aRef;
    }

    virtual ~CtiPortPoolDialout()
    {
    }

    CtiPortPoolDialout& operator=(const CtiPortPoolDialout& aRef);

    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);

    virtual INT openPort(INT rate = 0, INT bits = 8, INT parity = NOPARITY, INT stopbits = ONESTOPBIT);
    virtual INT inMess(CtiXfer& Xfer, CtiDeviceSPtr  Dev, RWTPtrSlist< CtiMessage > &traceList);
    virtual INT outMess(CtiXfer& Xfer, CtiDeviceSPtr  Dev, RWTPtrSlist< CtiMessage > &traceList);

    virtual size_t addPort(CtiPortSPtr port);

    void DecodePooledPortsDatabaseReader(RWDBReader &rdr);
    static void getPooledPortsSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

    CtiPortSPtr getAvailableChildPort(CtiDeviceSPtr  Device);
    INT allocateQueueEntsToChildPort();

};
#endif // #ifndef __PORT_POOL_OUT_H__
