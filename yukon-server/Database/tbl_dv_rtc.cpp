#include "yukon.h"

/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_rtc
*
* Date:   3/18/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/02/10 23:23:48 $
*
* HISTORY      :
* $Log: tbl_dv_rtc.cpp,v $
* Revision 1.2  2005/02/10 23:23:48  alauinger
* Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build
*
* Revision 1.1  2004/03/18 19:50:34  cplender
* Initial Checkin
* Builds, but not too complete.
*
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <windows.h>

#include <rw/cstring.h>
#include <rw/rwtime.h>

//#include <rw/db/table.h>
//#include <rw/db/reader.h>

#include "dbaccess.h"
#include "dlldefs.h"
#include "dllbase.h"
#include "logger.h"
#include "tbl_dv_rtc.h"

CtiTableDeviceRTC::CtiTableDeviceRTC() :
    _deviceID(-1),
    _rtcAddress(0),
    _lbtMode(CtiTableDeviceRTC::NoLBT),
    _responseBit(true)
{}

CtiTableDeviceRTC::~CtiTableDeviceRTC()
{
}

LONG CtiTableDeviceRTC::getDeviceID() const
{
    return _deviceID;
}

int CtiTableDeviceRTC::getRTCAddress() const
{
    return _rtcAddress;
}

bool CtiTableDeviceRTC::getResponseBit() const
{
    return _responseBit;
}
int  CtiTableDeviceRTC::getLBTMode() const
{
    return _lbtMode;
}


void CtiTableDeviceRTC::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable devTbl = db.table(getTableName() );

    selector << devTbl["rtcaddress"] <<
        devTbl["response"] <<
        devTbl["lbtmode"];

    selector.from(devTbl);

    selector.where( keyTable["paobjectid"] == devTbl["deviceid"] && selector.where() );  //later: == getDeviceID());
}

void CtiTableDeviceRTC::DecodeDatabaseReader(RWDBReader &rdr)
{
    RWCString rwsTemp;

    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        if(getDebugLevel() & 0x0800)
            dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"]      >> _deviceID;
    rdr["rtcaddress"] >> _rtcAddress;
    rdr["response"]  >> rwsTemp;

    if(rwsTemp.contains("Y",RWCString::ignoreCase)) _responseBit = true;
    else _responseBit = false;

    rdr["lbtmode"]  >> _lbtMode;
}

RWCString CtiTableDeviceRTC::getTableName()
{
    return "DeviceRTC";
}

RWDBStatus CtiTableDeviceRTC::Restore()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return RWDBStatus::notSupported;
}

RWDBStatus CtiTableDeviceRTC::Insert()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return RWDBStatus::notSupported;
}

RWDBStatus CtiTableDeviceRTC::Update()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return RWDBStatus::notSupported;
}

RWDBStatus CtiTableDeviceRTC::Delete()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return RWDBStatus::notSupported;
}


