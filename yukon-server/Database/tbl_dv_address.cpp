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
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/12/20 17:16:05 $
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_dv_address.h"

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

void CtiTableDeviceAddress::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable devTbl = db.table(getTableName().c_str() );

    selector << devTbl["masteraddress"] <<
                devTbl["slaveaddress"]  <<
                devTbl["postcommwait"];

    selector.from(devTbl);

    selector.where( keyTable["paobjectid"] == devTbl["deviceid"] && selector.where() );  //later: == getDeviceID());
    // selector.where( selector.where() && keyTable["deviceid"] == devTbl["deviceid"] );
}

void CtiTableDeviceAddress::DecodeDatabaseReader(RWDBReader &rdr)
{
    string rwsTemp;

    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        if(getDebugLevel() & DEBUGLEVEL_DATABASE)
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

RWDBStatus CtiTableDeviceAddress::Restore()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return RWDBStatus::notSupported;
}

RWDBStatus CtiTableDeviceAddress::Insert()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return RWDBStatus::notSupported;
}

RWDBStatus CtiTableDeviceAddress::Update()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return RWDBStatus::notSupported;
}

RWDBStatus CtiTableDeviceAddress::Delete()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return RWDBStatus::notSupported;
}


