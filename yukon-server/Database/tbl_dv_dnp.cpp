#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_dnp
*
* Date:   2002-aug-27
*
* Author : Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_dv_idlcremote.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/08/29 16:31:12 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "tbl_dv_dnp.h"

CtiTableDeviceDNP::CtiTableDeviceDNP() :
_deviceID(-1),
_masterAddress(-1),
_slaveAddress(-1),
_postdelay(-1)
{}

CtiTableDeviceDNP::CtiTableDeviceDNP(const CtiTableDeviceDNP& aRef)
{
    *this = aRef;
}

CtiTableDeviceDNP::~CtiTableDeviceDNP()
{
}

CtiTableDeviceDNP& CtiTableDeviceDNP::operator=(const CtiTableDeviceDNP& aRef)
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

LONG CtiTableDeviceDNP::getDeviceID() const
{
    return _deviceID;
}

CtiTableDeviceDNP& CtiTableDeviceDNP::setDeviceID( const LONG deviceID )
{
    _deviceID = deviceID;
    return *this;
}

LONG CtiTableDeviceDNP::getMasterAddress() const
{
    return _masterAddress;
}

void CtiTableDeviceDNP::setMasterAddress(LONG a)
{
    _masterAddress = a;
}

LONG CtiTableDeviceDNP::getSlaveAddress() const
{
    return _slaveAddress;
}

void CtiTableDeviceDNP::setSlaveAddress(LONG a)
{
    _slaveAddress = a;
}

INT  CtiTableDeviceDNP::getPostDelay() const
{
    return _postdelay;
}

void CtiTableDeviceDNP::setPostDelay(int d)
{
    _postdelay = d;
}

void CtiTableDeviceDNP::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable devTbl = db.table(getTableName() );

    selector << devTbl["masteraddress"] <<
                devTbl["slaveaddress"]  <<
                devTbl["postcommwait"];

    selector.from(devTbl);

    selector.where( keyTable["paobjectid"] == devTbl["deviceid"] && selector.where() );  //later: == getDeviceID());
    // selector.where( selector.where() && keyTable["deviceid"] == devTbl["deviceid"] );
}

void CtiTableDeviceDNP::DecodeDatabaseReader(RWDBReader &rdr)
{
    RWCString rwsTemp;

    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        if(getDebugLevel() & 0x0800)
            dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"]      >> _deviceID;
    rdr["masteraddress"] >> _masterAddress;
    rdr["slaveaddress"]  >> _slaveAddress;
    rdr["postcommwait"]  >> _postdelay;
}

RWCString CtiTableDeviceDNP::getTableName()
{
    return "DeviceDNP";
}

RWDBStatus CtiTableDeviceDNP::Restore()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return RWDBStatus::notSupported;
}

RWDBStatus CtiTableDeviceDNP::Insert()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return RWDBStatus::notSupported;
}

RWDBStatus CtiTableDeviceDNP::Update()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return RWDBStatus::notSupported;
}

RWDBStatus CtiTableDeviceDNP::Delete()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return RWDBStatus::notSupported;
}


