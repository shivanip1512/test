#include "yukon.h"


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
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/02/10 23:23:48 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "tbl_port_tcpip.h"
#include "logger.h"

CtiTablePortTCPIP::CtiTablePortTCPIP() :
_ipPort(TSDEFAULTPORT)
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
        _ipPort           = aRef.getIPPort();
        _ipAddress        = aRef.getIPAddress();
    }
    return *this;
}

INT                  CtiTablePortTCPIP::getIPPort() const
{
    return _ipPort;
}

INT&                 CtiTablePortTCPIP::getIPPort()
{
    return _ipPort;
}

CtiTablePortTCPIP&   CtiTablePortTCPIP::setIPPort(const INT i)
{
    _ipPort = i;
    return *this;
}

RWCString CtiTablePortTCPIP::getIPAddress() const
{
    return _ipAddress;
}

RWCString& CtiTablePortTCPIP::getIPAddress()
{
    return _ipAddress;
}

CtiTablePortTCPIP&   CtiTablePortTCPIP::setIPAddress(const RWCString &str)
{
    _ipAddress = str;
    return *this;
}

void CtiTablePortTCPIP::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable portTbl = db.table(getTableName());

    selector <<
    portTbl["ipaddress"] <<
    portTbl["socketportnumber"];

    selector.from(portTbl);

    selector.where( selector.where() && keyTable["paobjectid"] == portTbl["portid"] );
}

void CtiTablePortTCPIP::DecodeDatabaseReader(RWDBReader &rdr)
{
    try
    {
        rdr["ipaddress"]        >> _ipAddress;
        rdr["socketportnumber"] >> _ipPort;

        if(getDebugLevel() & 0x00000800)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " IP Address           : " << _ipAddress << endl;
            dout << " IP Port              : " << _ipPort << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

RWCString CtiTablePortTCPIP::getTableName()
{
    return "PortTerminalServer";
}
