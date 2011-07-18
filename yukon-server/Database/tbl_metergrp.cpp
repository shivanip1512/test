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

CtiTableDeviceMeterGroup::CtiTableDeviceMeterGroup(const CtiTableDeviceMeterGroup& aRef)
{
    *this = aRef;
}

CtiTableDeviceMeterGroup::~CtiTableDeviceMeterGroup() {}

CtiTableDeviceMeterGroup& CtiTableDeviceMeterGroup::operator=(const CtiTableDeviceMeterGroup& aRef)
{
    if(this != &aRef)
    {
        _deviceID            = aRef.getDeviceID();
        _meterNumber         = aRef.getMeterNumber();
    }
    return *this;
}

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
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"] >> _deviceID;
    rdr["meternumber"] >> _meterNumber;
}

