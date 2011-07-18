/*-----------------------------------------------------------------------------*
*
* File:   port_serial
*
* Date:   3/3/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.4.2.1 $
* DATE         :  $Date: 2008/11/20 16:49:20 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"


#include "port_serial.h"

void CtiPortSerial::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);

    CtiLockGuard<CtiMutex> guard(_classMutex);
    _tblPortSettings.DecodeDatabaseReader(rdr);       // get the base class handled
    _tblPortTimings.DecodeDatabaseReader(rdr);       // get the base class handled
}


CtiPort &CtiPortSerial::setBaudRate(INT baudRate)
{
    if(baudRate)
        _tblPortSettings.setBaudRate(baudRate);

    return *this;
}


ULONG CtiPortSerial::getDelay(int Offset) const
{
    return _tblPortTimings.getDelay(Offset);
}

CtiPort& CtiPortSerial::setDelay(int Offset, int D)
{
    _tblPortTimings.setDelay(Offset, D);
    return *this;
}

