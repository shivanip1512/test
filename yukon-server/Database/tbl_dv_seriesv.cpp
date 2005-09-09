/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_seriesv
*
* Date:   8/23/2005
*
* Author: Matt Fisher
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/09/09 10:54:07 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <rw/db/reader.h>

#include "tbl_dv_seriesv.h"
#include "dllbase.h"
#include "logger.h"

CtiTableDeviceSeriesV::CtiTableDeviceSeriesV()
{
}


CtiTableDeviceSeriesV::CtiTableDeviceSeriesV( const CtiTableDeviceSeriesV& aRef )
{
    *this = aRef;
}


CtiTableDeviceSeriesV::~CtiTableDeviceSeriesV()
{
}


CtiTableDeviceSeriesV &CtiTableDeviceSeriesV::operator=( const CtiTableDeviceSeriesV& aRef )
{
    if( this != &aRef )
    {
    }

    return *this;
}


RWCString CtiTableDeviceSeriesV::getTableName()
{
    return RWCString("deviceseries5rtu");
}


long CtiTableDeviceSeriesV::getDeviceID() const
{
    return _device_id;
}

int CtiTableDeviceSeriesV::getTickTime() const
{
    return _tick_time;
}

int CtiTableDeviceSeriesV::getTimeOffset() const
{
    return _transmit_offset;
}

int CtiTableDeviceSeriesV::getTransmitterLow() const
{
    return _power_value_low;
}

int CtiTableDeviceSeriesV::getTransmitterHigh() const
{
    return _power_value_high;
}

string CtiTableDeviceSeriesV::getStartCode() const
{
    return _start_code;
}

string CtiTableDeviceSeriesV::getStopCode() const
{
    return _stop_code;
}

void CtiTableDeviceSeriesV::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable devTbl       = db.table(getTableName());

    selector << devTbl["deviceid"] <<
                devTbl["ticktime"] <<
                devTbl["transmitoffset"] <<
                devTbl["savehistory"] <<
                devTbl["powervaluehighlimit"] <<
                devTbl["powervaluelowlimit"] <<
                devTbl["powervaluemultiplier"] <<
                devTbl["powervalueoffset"] <<
                devTbl["startcode"] <<
                devTbl["stopcode"] <<
                devTbl["retries"];

    selector.from(devTbl);

    selector.where(keyTable["paobjectid"] == devTbl["deviceid"] && selector.where());
}


void CtiTableDeviceSeriesV::DecodeDatabaseReader(RWDBReader &rdr)
{
    RWCString tmp;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"]       >> _device_id;
    rdr["ticktime"]       >> _tick_time;
    rdr["transmitoffset"] >> _transmit_offset;

    rdr["savehistory"] >> tmp;

    if( tmp == "Y" )
    {
        _save_history = true;
    }
    else
    {
        _save_history = false;
    }

    rdr["powervaluehighlimit"]       >> _power_value_high;
    rdr["powervaluelowlimit"]        >> _power_value_low;
    rdr["powervaluemultiplier"] >> _power_value_multiplier;
    rdr["powervalueoffset"]     >> _power_value_offset;

    rdr["startcode"] >> _start_code;
    rdr["stopcode"]  >> _stop_code;
    rdr["retries"]   >> _retries;
}


RWDBStatus CtiTableDeviceSeriesV::Restore()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return RWDBStatus::notSupported;
}


RWDBStatus CtiTableDeviceSeriesV::Insert()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return RWDBStatus::notSupported;
}


RWDBStatus CtiTableDeviceSeriesV::Update()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return RWDBStatus::notSupported;
}


RWDBStatus CtiTableDeviceSeriesV::Delete()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return RWDBStatus::notSupported;
}

