/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_mctiedport
*
* Date:   2002-jan-30
*
* Author : Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:     $
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2005/02/17 19:02:57 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_dv_mctiedport.h"
#include "logger.h"

CtiTableDeviceMCTIEDPort::CtiTableDeviceMCTIEDPort() :
_deviceID(-1),
_iedScanRate(-1),
_defaultDataClass(-1),
_defaultDataOffset(-1),
_realTimeScan(-1)
{
}

CtiTableDeviceMCTIEDPort::CtiTableDeviceMCTIEDPort(const CtiTableDeviceMCTIEDPort& aRef)
{
    *this = aRef;
}

CtiTableDeviceMCTIEDPort::~CtiTableDeviceMCTIEDPort() {}

CtiTableDeviceMCTIEDPort& CtiTableDeviceMCTIEDPort::operator=(const CtiTableDeviceMCTIEDPort& aRef)
{

    if( this != &aRef )
    {
        _deviceID           = aRef.getDeviceID();
        _password           = aRef.getPassword();
        _connectedIED       = aRef.getIEDType();
        _iedScanRate        = aRef.getIEDScanRate();
        _defaultDataClass   = aRef.getDefaultDataClass();
        _defaultDataOffset  = aRef.getDefaultDataOffset();
        _realTimeScan       = aRef.getRealTimeScanFlag();
    }
    return *this;
}

long CtiTableDeviceMCTIEDPort::getDeviceID() const
{

    return _deviceID;
}

CtiTableDeviceMCTIEDPort CtiTableDeviceMCTIEDPort::setDeviceID(long aLong)
{

    _deviceID = aLong;
    return *this;
}

RWCString CtiTableDeviceMCTIEDPort::getPassword() const
{

    return _password;
}

RWCString& CtiTableDeviceMCTIEDPort::getPassword()
{

    return _password;
}

CtiTableDeviceMCTIEDPort CtiTableDeviceMCTIEDPort::setPassword(RWCString &aStr)
{

    _password = aStr;
    return *this;
}

int CtiTableDeviceMCTIEDPort::getIEDType() const
{

    return _connectedIED;
}

int &CtiTableDeviceMCTIEDPort::getIEDType()
{

    return _connectedIED;
}

CtiTableDeviceMCTIEDPort CtiTableDeviceMCTIEDPort::setIEDType(IEDTypes type)
{

    _connectedIED = type;
    return *this;
}

int CtiTableDeviceMCTIEDPort::getIEDScanRate() const
{

    return _iedScanRate;
}

int &CtiTableDeviceMCTIEDPort::getIEDScanRate()
{

    return _iedScanRate;
}

CtiTableDeviceMCTIEDPort CtiTableDeviceMCTIEDPort::setIEDScanRate(int scanrate)
{

    _iedScanRate = scanrate;
    return *this;
}

int CtiTableDeviceMCTIEDPort::getDefaultDataClass() const
{

    return _defaultDataClass;
}

int &CtiTableDeviceMCTIEDPort::getDefaultDataClass()
{

    return _defaultDataClass;
}

CtiTableDeviceMCTIEDPort CtiTableDeviceMCTIEDPort::setDefaultDataClass(int dataclass)
{

    _defaultDataClass = dataclass;
    return *this;
}

int CtiTableDeviceMCTIEDPort::getDefaultDataOffset() const
{

    return _defaultDataOffset;
}

int &CtiTableDeviceMCTIEDPort::getDefaultDataOffset()
{

    return _defaultDataOffset;
}

CtiTableDeviceMCTIEDPort CtiTableDeviceMCTIEDPort::setDefaultDataOffset(int dataoffset)
{

    _defaultDataOffset = dataoffset;
    return *this;
}

int CtiTableDeviceMCTIEDPort::getRealTimeScanFlag() const
{

    return _realTimeScan;
}

int &CtiTableDeviceMCTIEDPort::getRealTimeScanFlag()
{

    return _realTimeScan;
}


CtiTableDeviceMCTIEDPort CtiTableDeviceMCTIEDPort::setRealTimeScanFlag(int flag)
{

    _realTimeScan = flag;
    return *this;
}

void CtiTableDeviceMCTIEDPort::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable devTbl = db.table(getTableName());

    selector << devTbl["deviceid"]
    << devTbl["connectedied"]
    << devTbl["password"]
    << devTbl["iedscanrate"]
    << devTbl["defaultdataclass"]
    << devTbl["defaultdataoffset"]
    << devTbl["realtimescan"];

    selector.from(devTbl);

    //  this select will be tacked on to the Carrier device
    selector.where( selector.where() && keyTable["paobjectid"].leftOuterJoin(devTbl["deviceid"]) );  //later: == getDeviceID());
}

void CtiTableDeviceMCTIEDPort::DecodeDatabaseReader(RWDBReader &rdr)
{
    RWCString temp;



    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        if( getDebugLevel() & 0x0800 ) dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["deviceid"]     >> _deviceID;
    rdr["connectedied"] >> temp;
    temp.toLower();

    if( temp == "landis and gyr s4" )
        _connectedIED = LandisGyrS4;
    else if( temp == "alpha power plus" )
        _connectedIED = AlphaPowerPlus;
    else if( temp == "general electric kv" )
        _connectedIED = GeneralElectricKV;
    else
        _connectedIED = InvalidIEDType;

    rdr["password"] >> temp;
    temp.toLower();
    if( temp.isNull() || !temp.compareTo("none", RWCString::ignoreCase) || !temp.compareTo("(none)", RWCString::ignoreCase) )
        _password = "0000";
    else
        _password = temp;

    rdr["iedscanrate"]       >> _iedScanRate;
    rdr["defaultdataclass"]  >> _defaultDataClass;
    rdr["defaultdataoffset"] >> _defaultDataOffset;

    rdr["realtimescan"]      >> temp;
    temp.toLower();

    if( temp == "y" )
        _realTimeScan = 1;
    else
        _realTimeScan = 0;

}

RWCString CtiTableDeviceMCTIEDPort::getTableName()
{
    return "DeviceMCTIEDPort";
}

RWDBStatus CtiTableDeviceMCTIEDPort::Restore()
{

    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable devTbl = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector << devTbl["deviceid"]
    << devTbl["connectedied"]
    << devTbl["password"]
    << devTbl["iedscanrate"]
    << devTbl["defaultdataclass"]
    << devTbl["defaultdataoffset"]
    << devTbl["realtimescan"];

    selector.where( devTbl["deviceid"] == getDeviceID() );

    RWDBReader reader = selector.reader( conn );

    if( reader() )
    {
        DecodeDatabaseReader( reader  );
        setDirty( false );
    }
    else
    {
        setDirty( true );
    }
    return reader.status();
}

RWDBStatus CtiTableDeviceMCTIEDPort::Insert()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "CtiTableDeviceMCTIEDPort::Insert() not implemented" << endl;
    }

    return RWDBStatus::noInserter;
}

RWDBStatus CtiTableDeviceMCTIEDPort::Update()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "CtiTableDeviceMCTIEDPort::Update() not implemented" << endl;
    }

    return RWDBStatus::noUpdater;
}

RWDBStatus CtiTableDeviceMCTIEDPort::Delete()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "CtiTableDeviceMCTIEDPort::Delete() not implemented" << endl;
    }

    return RWDBStatus::noDeleter;
}

