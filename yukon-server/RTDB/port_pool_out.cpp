/*-----------------------------------------------------------------------------*
*
* File:   port_pool_out
*
* Date:   3/4/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2003/03/13 19:36:05 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)


#include "port_pool_out.h"

INT CtiPortPoolDialout::openPort(INT rate, INT bits, INT parity, INT stopbits)
{
    INT status = NORMAL;

    return status;
}
INT CtiPortPoolDialout::inMess(CtiXfer& Xfer, CtiDevice* Dev, RWTPtrSlist< CtiMessage > &traceList)
{
    INT status = NORMAL;

    return status;
}

INT CtiPortPoolDialout::outMess(CtiXfer& Xfer, CtiDevice* Dev, RWTPtrSlist< CtiMessage > &traceList)
{
    INT status = NORMAL;

    return status;
}


void CtiPortPoolDialout::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
    selector.where( selector.where() && keyTable["type"] == "Dialout Pool");

#if 0
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << selector.asString() << endl;
    }
#endif
}

void CtiPortPoolDialout::DecodeDatabaseReader(RWDBReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);
}


