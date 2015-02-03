/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_lmvcserial
*
* Date:   8/13/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_dv_lmvcserial.cpp-arc  $
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2005/12/20 17:16:06 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "tbl_dv_lmvcserial.h"
#include "logger.h"

#include "database_connection.h"
#include "database_reader.h"

using std::string;
using std::endl;

CtiTableLMGroupVersacomSerial::CtiTableLMGroupVersacomSerial() :
_deviceID(-1),
_serial(0),
_groupID(0),
_addressUsage(0),
_relayMask(0),
_routeID(-1)
{}

CtiTableLMGroupVersacomSerial::~CtiTableLMGroupVersacomSerial() {}

INT  CtiTableLMGroupVersacomSerial::getSerial() const
{

    return _serial;
}

CtiTableLMGroupVersacomSerial& CtiTableLMGroupVersacomSerial::setSerial( const INT a_ser )
{

    _serial = a_ser;
    return *this;
}

INT  CtiTableLMGroupVersacomSerial::getGroupID() const
{

    return _groupID;
}

CtiTableLMGroupVersacomSerial& CtiTableLMGroupVersacomSerial::setGroupID( const INT i )
{

    _groupID = i;
    return *this;
}

INT  CtiTableLMGroupVersacomSerial::getAddressUsage() const
{

    return _addressUsage;
}

BOOL CtiTableLMGroupVersacomSerial::useUtilityID() const
{

    return((_addressUsage & U_MASK) ? TRUE : FALSE );
}

BOOL CtiTableLMGroupVersacomSerial::useSection() const
{

    return((_addressUsage & S_MASK) ? TRUE : FALSE );
}

BOOL CtiTableLMGroupVersacomSerial::useClass() const
{

    return((_addressUsage & C_MASK) ? TRUE : FALSE );
}

BOOL CtiTableLMGroupVersacomSerial::useDivision() const
{

    return((_addressUsage & D_MASK) ? TRUE : FALSE );
}

CtiTableLMGroupVersacomSerial& CtiTableLMGroupVersacomSerial::setAddressUsage( const INT a_addressUsage )
{

    _addressUsage = a_addressUsage;
    return *this;
}

INT  CtiTableLMGroupVersacomSerial::getRelayMask() const
{

    return _relayMask;
}

BOOL CtiTableLMGroupVersacomSerial::useRelay(const INT r) const
{

    return((_relayMask & (0x00000001 << (r - 1))) ? TRUE : FALSE);
}

CtiTableLMGroupVersacomSerial& CtiTableLMGroupVersacomSerial::setRelayMask( const INT a_relayMask )
{

    _relayMask = a_relayMask;
    return *this;
}

LONG  CtiTableLMGroupVersacomSerial::getRouteID() const
{

    return _routeID;
}

CtiTableLMGroupVersacomSerial& CtiTableLMGroupVersacomSerial::setRouteID( const LONG a_routeID )
{

    _routeID = a_routeID;
    return *this;
}

string CtiTableLMGroupVersacomSerial::getTableName()
{
    return "LMGroupVersacomSerial";
}

LONG CtiTableLMGroupVersacomSerial::getDeviceID() const
{

    return _deviceID;
}

CtiTableLMGroupVersacomSerial& CtiTableLMGroupVersacomSerial::setDeviceID( const LONG deviceID)
{

    _deviceID = deviceID;
    return *this;
}

void CtiTableLMGroupVersacomSerial::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    string usageStr;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB read from "<< getTableName());
    }

    rdr["deviceid"]         >> _deviceID;
    rdr["serialnumber"]     >> _serial;
    rdr["deviceidofgroup"]  >> _groupID;
    rdr["routeid"]          >> _routeID;

    rdr["relayusage"]       >> usageStr;
    _relayMask = resolveRelayUsage(usageStr);

    _addressUsage = 0;                     // This is a serial number ok!

}
