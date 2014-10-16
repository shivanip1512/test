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
* REVISION     :  $Revision: 1.13 $
* DATE         :  $Date: 2006/09/26 13:53:24 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "tbl_dv_mctiedport.h"
#include "logger.h"

#include "utility.h"

using std::string;
using std::endl;

CtiTableDeviceMCTIEDPort::CtiTableDeviceMCTIEDPort() :
_deviceID(-1),
_iedScanRate(-1),
_defaultDataClass(-1),
_defaultDataOffset(-1),
_realTimeScan(-1)
{
}

CtiTableDeviceMCTIEDPort::~CtiTableDeviceMCTIEDPort() {}

long CtiTableDeviceMCTIEDPort::getDeviceID() const
{

    return _deviceID;
}

void CtiTableDeviceMCTIEDPort::setDeviceID(long aLong)
{

    _deviceID = aLong;
}

string CtiTableDeviceMCTIEDPort::getPassword() const
{

    return _password;
}

string& CtiTableDeviceMCTIEDPort::getPassword()
{

    return _password;
}

void CtiTableDeviceMCTIEDPort::setPassword(string &aStr)
{

    _password = aStr;
}

int CtiTableDeviceMCTIEDPort::getIEDType() const
{

    return _connectedIED;
}

int &CtiTableDeviceMCTIEDPort::getIEDType()
{

    return _connectedIED;
}

void CtiTableDeviceMCTIEDPort::setIEDType(IEDTypes type)
{

    _connectedIED = type;
}

int CtiTableDeviceMCTIEDPort::getIEDScanRate() const
{

    return _iedScanRate;
}

int &CtiTableDeviceMCTIEDPort::getIEDScanRate()
{

    return _iedScanRate;
}

void CtiTableDeviceMCTIEDPort::setIEDScanRate(int scanrate)
{

    _iedScanRate = scanrate;
}

int CtiTableDeviceMCTIEDPort::getDefaultDataClass() const
{

    return _defaultDataClass;
}

int &CtiTableDeviceMCTIEDPort::getDefaultDataClass()
{

    return _defaultDataClass;
}

void CtiTableDeviceMCTIEDPort::setDefaultDataClass(int dataclass)
{

    _defaultDataClass = dataclass;
}

int CtiTableDeviceMCTIEDPort::getDefaultDataOffset() const
{

    return _defaultDataOffset;
}

int &CtiTableDeviceMCTIEDPort::getDefaultDataOffset()
{

    return _defaultDataOffset;
}

void CtiTableDeviceMCTIEDPort::setDefaultDataOffset(int dataoffset)
{
    _defaultDataOffset = dataoffset;
}

int CtiTableDeviceMCTIEDPort::getRealTimeScanFlag() const
{

    return _realTimeScan;
}

int &CtiTableDeviceMCTIEDPort::getRealTimeScanFlag()
{

    return _realTimeScan;
}


void CtiTableDeviceMCTIEDPort::setRealTimeScanFlag(int flag)
{

    _realTimeScan = flag;
}

void CtiTableDeviceMCTIEDPort::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    string temp;

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CTILOG_DEBUG(dout, "Decoding DB read from "<< getTableName());
    }

    rdr["deviceid"]     >> _deviceID;
    rdr["connectedied"] >> temp;
    std::transform(temp.begin(), temp.end(), temp.begin(), tolower);


    if( temp == "landis and gyr s4" )
        _connectedIED = LandisGyrS4;
    else if( temp == "alpha power plus" )
        _connectedIED = AlphaPowerPlus;
    else if( temp == "general electric kv" )
        _connectedIED = GeneralElectricKV;
    else
        _connectedIED = InvalidIEDType;

    rdr["password"] >> temp;
    std::transform(temp.begin(), temp.end(), temp.begin(), tolower);
    if( temp.empty() || ciStringEqual(temp, "none") || ciStringEqual(temp, "(none)") )
        _password = "0000";
    else
        _password = temp;

    rdr["iedscanrate"]       >> _iedScanRate;
    rdr["defaultdataclass"]  >> _defaultDataClass;
    rdr["defaultdataoffset"] >> _defaultDataOffset;

    rdr["realtimescan"]      >> temp;
    std::transform(temp.begin(), temp.end(), temp.begin(), tolower);

    if( temp == "y" )
        _realTimeScan = 1;
    else
        _realTimeScan = 0;

}

string CtiTableDeviceMCTIEDPort::getTableName()
{
    return "DeviceMCTIEDPort";
}

bool CtiTableDeviceMCTIEDPort::Insert()
{
    CTILOG_ERROR(dout, "function unimplemented");

    return false;
}

bool CtiTableDeviceMCTIEDPort::Update()
{
    CTILOG_ERROR(dout, "function unimplemented");

    return false;
}

bool CtiTableDeviceMCTIEDPort::Delete()
{
    CTILOG_ERROR(dout, "function unimplemented");

    return false;
}

