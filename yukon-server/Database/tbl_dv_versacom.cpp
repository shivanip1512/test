/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_versacom
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_dv_versacom.cpp-arc  $
* REVISION     :  $Revision: 1.11.24.1 $
* DATE         :  $Date: 2008/11/13 17:23:49 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_dv_versacom.h"
#include "resolvers.h"
#include "logger.h"

#include "database_connection.h"
#include "database_writer.h"

CtiTableVersacomLoadGroup::CtiTableVersacomLoadGroup() :
_deviceID(-1),
_serial(0),
_utilityID(0),
_section(0),
_class(0),
_division(0),
_addressUsage(0),
_relayMask(0),
_routeID(-1)
{}

CtiTableVersacomLoadGroup::CtiTableVersacomLoadGroup(const CtiTableVersacomLoadGroup& aRef)
{
    *this = aRef;
}

CtiTableVersacomLoadGroup::~CtiTableVersacomLoadGroup() {}

CtiTableVersacomLoadGroup& CtiTableVersacomLoadGroup::operator=(const CtiTableVersacomLoadGroup& aRef)
{
    if(this != &aRef)
    {
        _deviceID      = aRef.getDeviceID();
        _serial        = aRef.getSerial();
        _utilityID     = aRef.getUtilityID();
        _section       = aRef.getSection();
        _class         = aRef.getClass();
        _division      = aRef.getDivision();
        _addressUsage  = aRef.getAddressUsage();
        _relayMask     = aRef.getRelayMask();
        _routeID       = aRef.getRouteID();
    }
    return *this;
}

INT  CtiTableVersacomLoadGroup::getSerial() const
{

    return _serial;
}

CtiTableVersacomLoadGroup& CtiTableVersacomLoadGroup::setSerial( const INT a_ser )
{

    _serial = a_ser;
    return *this;
}

INT  CtiTableVersacomLoadGroup::getUtilityID() const
{

    return _utilityID;
}

CtiTableVersacomLoadGroup& CtiTableVersacomLoadGroup::setUtilityID( const INT a_uid )
{

    _utilityID = a_uid;
    return *this;
}

INT  CtiTableVersacomLoadGroup::getSection() const
{

    return _section;
}

CtiTableVersacomLoadGroup& CtiTableVersacomLoadGroup::setSection( const INT aSection )
{

    _section = aSection;
    return *this;
}

INT  CtiTableVersacomLoadGroup::getClass() const
{

    return _class;
}

CtiTableVersacomLoadGroup& CtiTableVersacomLoadGroup::setClass( const INT aClass )
{

    _class = aClass;
    return *this;
}

INT  CtiTableVersacomLoadGroup::getDivision() const
{

    return _division;
}

CtiTableVersacomLoadGroup& CtiTableVersacomLoadGroup::setDivision( const INT aDivision )
{

    _division = aDivision;
    return *this;
}


INT  CtiTableVersacomLoadGroup::getAddressUsage() const
{

    return _addressUsage;
}

BOOL CtiTableVersacomLoadGroup::useUtilityID() const
{

    return((_addressUsage & U_MASK) ? TRUE : FALSE );
}

BOOL CtiTableVersacomLoadGroup::useSection() const
{

    return((_addressUsage & S_MASK) ? TRUE : FALSE );
}

BOOL CtiTableVersacomLoadGroup::useClass() const
{

    return((_addressUsage & C_MASK) ? TRUE : FALSE );
}

BOOL CtiTableVersacomLoadGroup::useDivision() const
{

    return((_addressUsage & D_MASK) ? TRUE : FALSE );
}

CtiTableVersacomLoadGroup& CtiTableVersacomLoadGroup::setAddressUsage( const INT a_addressUsage )
{

    _addressUsage = a_addressUsage;
    return *this;
}

INT  CtiTableVersacomLoadGroup::getRelayMask() const
{

    return _relayMask;
}

BOOL CtiTableVersacomLoadGroup::useRelay(const INT r) const
{

    return((_relayMask & (0x00000001 << (r - 1))) ? TRUE : FALSE);
}

CtiTableVersacomLoadGroup& CtiTableVersacomLoadGroup::setRelayMask( const INT a_relayMask )
{

    _relayMask = a_relayMask;
    return *this;
}

LONG  CtiTableVersacomLoadGroup::getRouteID() const
{

    return _routeID;
}

CtiTableVersacomLoadGroup& CtiTableVersacomLoadGroup::setRouteID( const LONG a_routeID )
{

    _routeID = a_routeID;
    return *this;
}

void CtiTableVersacomLoadGroup::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    string rwsTemp;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["paobjectid"]       >> _deviceID;

    rdr["serialaddress"]    >> rwsTemp;
    _serial = atoi(rwsTemp.c_str());
    rdr["utilityaddress"]   >> _utilityID;
    rdr["sectionaddress"]   >> _section;
    rdr["classaddress"]     >> _class;
    rdr["divisionaddress"]  >> _division;
    rdr["routeid"]          >> _routeID;

    rdr["addressusage"]     >> rwsTemp;
    std::transform(rwsTemp.begin(), rwsTemp.end(), rwsTemp.begin(), tolower);

    _addressUsage = resolveAddressUsage(rwsTemp, Cti::AddressUsage_Versacom);

    rdr["relayusage"]       >> rwsTemp;
    _relayMask = resolveRelayUsage(rwsTemp);
}

LONG CtiTableVersacomLoadGroup::getDeviceID() const
{
    return _deviceID;
}

CtiTableVersacomLoadGroup& CtiTableVersacomLoadGroup::setDeviceID( const LONG deviceID )
{
    _deviceID = deviceID;
    return *this;
}

bool CtiTableVersacomLoadGroup::Insert()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return false;
}

bool CtiTableVersacomLoadGroup::Update()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return false;
}

bool CtiTableVersacomLoadGroup::Delete()
{
    static const std::string sql = "delete from " + getTableName() + " where deviceid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       deleter(conn, sql);

    deleter << getDeviceID();

    return deleter.execute();
}

string CtiTableVersacomLoadGroup::getTableName()
{
    return "LMGroupVersacom";
}
