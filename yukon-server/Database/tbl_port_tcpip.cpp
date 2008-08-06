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
#include "yukon.h"

#include "tbl_port_tcpip.h"
#include "logger.h"

#include "rwutil.h"

CtiTablePortTCPIP::CtiTablePortTCPIP() :
_ipPort(1000)
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
    }
    return *this;
}


CtiTablePortTCPIP&   CtiTablePortTCPIP::setIPAddress(const string &str)  {  _ipAddress = str;  return *this;  }
CtiTablePortTCPIP&   CtiTablePortTCPIP::setIPPort   (const INT i)        {  _ipPort    = i;    return *this;  }

const string &CtiTablePortTCPIP::getIPAddress() const  {  return _ipAddress;  }
INT           CtiTablePortTCPIP::getIPPort()    const  {  return _ipPort;     }


void CtiTablePortTCPIP::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable portTbl = db.table(getTableName().c_str());

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

        if(getDebugLevel() & DEBUGLEVEL_DATABASE)
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
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

string CtiTablePortTCPIP::getTableName()
{
    return "PortTerminalServer";
}

