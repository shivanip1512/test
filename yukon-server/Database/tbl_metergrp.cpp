#include "precompiled.h"

#include "logger.h"
#include "tbl_metergrp.h"

#include "database_connection.h"
#include "database_writer.h"

using std::string;
using std::endl;

CtiTableDeviceMeterGroup::CtiTableDeviceMeterGroup():
_deviceID(-1)
{}

CtiTableDeviceMeterGroup::~CtiTableDeviceMeterGroup() {}

LONG CtiTableDeviceMeterGroup::getDeviceID() const
{
    return _deviceID;
}

string CtiTableDeviceMeterGroup::getMeterNumber() const
{
    return _meterNumber;
}

CtiTableDeviceMeterGroup& CtiTableDeviceMeterGroup::setMeterNumber( const string &meterNumber )
{
    _meterNumber = meterNumber;

    return *this;
}

void CtiTableDeviceMeterGroup::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB read from DeviceMeterGroup");
    }

    rdr["deviceid"] >> _deviceID;
    rdr["meternumber"] >> _meterNumber;
}

