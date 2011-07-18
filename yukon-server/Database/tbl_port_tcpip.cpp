/*-----------------------------------------------------------------------------*
*
* File:   tbl_port_tcpip
*
* Date:   8/28/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_port_tcpip.cpp-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2008/08/06 18:26:49 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "tbl_port_tcpip.h"
#include "logger.h"

using std::string;
using std::endl;

CtiTablePortTCPIP::CtiTablePortTCPIP() :
    _ipPort(1000),
    _portID(0)
{
}

CtiTablePortTCPIP::CtiTablePortTCPIP(const CtiTablePortTCPIP& aRef)
{
    *this = aRef;
}

CtiTablePortTCPIP::~CtiTablePortTCPIP()
{
}

CtiTablePortTCPIP& CtiTablePortTCPIP::operator=(const CtiTablePortTCPIP& aRef)
{
    if(this != &aRef)
    {
        _ipPort    = aRef.getIPPort();
        _ipAddress = aRef.getIPAddress();
        _encodingKey = aRef.getEncodingKey();
        _encodingType = aRef.getEncodingType();
    }
    return *this;
}


void CtiTablePortTCPIP::setIPAddress(const string &str)
{
    _ipAddress = str;
}

void CtiTablePortTCPIP::setIPPort(const INT i)
{
    _ipPort = i;
}

const string &CtiTablePortTCPIP::getIPAddress() const
{
    return _ipAddress;
}

INT CtiTablePortTCPIP::getIPPort() const
{
    return _ipPort;
}

const string & CtiTablePortTCPIP::getEncodingKey() const
{
    return _encodingKey;
}

void CtiTablePortTCPIP::setEncodingKey(const string& encodingKey)
{
    _encodingKey = encodingKey;
}

const string & CtiTablePortTCPIP::getEncodingType() const
{
    return _encodingType;
}

void CtiTablePortTCPIP::setEncodingType(const string& encodingType)
{
    _encodingType = encodingType;
}

void CtiTablePortTCPIP::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    try
    {
        rdr["ipaddress"] >> _ipAddress;
        rdr["socketportnumber"] >> _ipPort;
        rdr["encodingkey"] >> _encodingKey;
        rdr["encodingtype"] >> _encodingType;

        if(getDebugLevel() & DEBUGLEVEL_DATABASE)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " IP Address           : " << _ipAddress << endl;
            dout << " IP Port              : " << _ipPort << endl;
            dout << " Encoding Key         : " << _encodingKey << endl;
            dout << " Encoding Type        : " << _encodingType << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

string CtiTablePortTCPIP::getTableName()
{
    return "PortTerminalServer";
}
