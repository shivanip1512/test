/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_address
*
* Date:   2004-apr-14
*
* Author : Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive: $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2006/09/26 13:53:24 $
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "tbl_dv_address.h"

using std::endl;
using std::string;

CtiTableDeviceAddress::CtiTableDeviceAddress() :
_deviceID(-1),
_masterAddress(-1),
_slaveAddress(-1),
_postdelay(-1)
{}

CtiTableDeviceAddress::CtiTableDeviceAddress(const CtiTableDeviceAddress& aRef)
{
    *this = aRef;
}

CtiTableDeviceAddress::~CtiTableDeviceAddress()
{
}

CtiTableDeviceAddress& CtiTableDeviceAddress::operator=(const CtiTableDeviceAddress& aRef)
{
    if(this != &aRef)
    {
        _deviceID      = aRef._deviceID;
        _masterAddress = aRef._masterAddress;
        _slaveAddress  = aRef._slaveAddress;
        _postdelay     = aRef._postdelay;
    }

    return *this;
}

LONG CtiTableDeviceAddress::getDeviceID() const
{
    return _deviceID;
}

CtiTableDeviceAddress& CtiTableDeviceAddress::setDeviceID( const LONG deviceID )
{
    _deviceID = deviceID;
    return *this;
}

LONG CtiTableDeviceAddress::getMasterAddress() const
{
    return _masterAddress;
}

void CtiTableDeviceAddress::setMasterAddress(LONG a)
{
    _masterAddress = a;
}

LONG CtiTableDeviceAddress::getSlaveAddress() const
{
    return _slaveAddress;
}

void CtiTableDeviceAddress::setSlaveAddress(LONG a)
{
    _slaveAddress = a;
}

INT  CtiTableDeviceAddress::getPostDelay() const
{
    return _postdelay;
}

void CtiTableDeviceAddress::setPostDelay(int d)
{
    _postdelay = d;
}

void CtiTableDeviceAddress::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    string rwsTemp;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"]      >> _deviceID;
    rdr["masteraddress"] >> _masterAddress;
    rdr["slaveaddress"]  >> _slaveAddress;
    rdr["postcommwait"]  >> _postdelay;
}

string CtiTableDeviceAddress::getTableName()
{
    return "DeviceAddress";
}

bool CtiTableDeviceAddress::Insert()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return false;
}

bool CtiTableDeviceAddress::Update()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return false;
}

bool CtiTableDeviceAddress::Delete()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return false;
}


