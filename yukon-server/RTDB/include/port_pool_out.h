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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2003/03/13 19:36:16 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PORT_POOL_OUT_H__
#define __PORT_POOL_OUT_H__
#pragma warning( disable : 4786)

#include "port_base.h"

class IM_EX_PRTDB CtiPortPoolDialout : public CtiPort
{
protected:

    vector< CtiPortSPtr > _ports;

private:

public:

    typedef CtiPort Inherited;

    CtiPortPoolDialout() {}

    CtiPortPoolDialout(const CtiPortPoolDialout& aRef)
    {
        *this = aRef;
    }

    virtual ~CtiPortPoolDialout() {}

    CtiPortPoolDialout& operator=(const CtiPortPoolDialout& aRef)
    {
        if(this != &aRef)
        {
        }
        return *this;
    }

    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);

    virtual INT openPort(INT rate = 0, INT bits = 8, INT parity = NOPARITY, INT stopbits = ONESTOPBIT);
    virtual INT inMess(CtiXfer& Xfer, CtiDevice* Dev, RWTPtrSlist< CtiMessage > &traceList);
    virtual INT outMess(CtiXfer& Xfer, CtiDevice* Dev, RWTPtrSlist< CtiMessage > &traceList);

};
#endif // #ifndef __PORT_POOL_OUT_H__
