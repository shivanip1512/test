/*-----------------------------------------------------------------------------*
*
* File:   port_serial
*
* Date:   3/3/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2005/02/10 23:24:02 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include "port_serial.h"

void CtiPortSerial::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
    CtiTablePortSettings::getSQL(db, keyTable, selector);
    CtiTablePortTimings::getSQL(db, keyTable, selector);
}

void CtiPortSerial::DecodeDatabaseReader(RWDBReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);

    LockGuard gd(monitor());
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


CtiPortSerial& CtiPortSerial::operator=(const CtiPortSerial& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
        _tblPortSettings = aRef.getTablePortSettings();
        _tblPortTimings = aRef.getTablePortTimings();
    }
    return *this;
}

