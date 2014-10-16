/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_seriesv
*
* Date:   8/23/2005
*
* Author: Matt Fisher
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/12/20 17:16:06 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "row_reader.h"

#include "tbl_dv_seriesv.h"
#include "dllbase.h"
#include "logger.h"

using std::string;
using std::endl;

CtiTableDeviceSeriesV::CtiTableDeviceSeriesV() :
    _device_id(0),
    _save_history(false),
    _tick_time(0),
    _transmit_offset(0),
    _power_value_high(0),
    _power_value_low(0),
    _power_value_multiplier(0),
    _power_value_offset(0),
    _retries(0)
{
}

CtiTableDeviceSeriesV::~CtiTableDeviceSeriesV()
{
}

string CtiTableDeviceSeriesV::getTableName()
{
    return string("deviceseries5rtu");
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

unsigned CtiTableDeviceSeriesV::getStartCode() const
{
    return _start_code;
}

unsigned CtiTableDeviceSeriesV::getStopCode() const
{
    return _stop_code;
}

void CtiTableDeviceSeriesV::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    string tmp;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB read from "<< getTableName());
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

