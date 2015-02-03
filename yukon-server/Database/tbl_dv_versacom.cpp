#include "precompiled.h"

#include "tbl_dv_versacom.h"
#include "resolvers.h"
#include "logger.h"
#include "database_connection.h"
#include "database_writer.h"
#include "database_util.h"

using std::string;
using std::endl;

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

CtiTableVersacomLoadGroup::~CtiTableVersacomLoadGroup() {}

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
    string tmpStr;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB read from "<< getTableName());
    }

    rdr["paobjectid"]       >> _deviceID;

    rdr["serialaddress"]    >> tmpStr;
    _serial = atoi(tmpStr.c_str());
    rdr["utilityaddress"]   >> _utilityID;
    rdr["sectionaddress"]   >> _section;
    rdr["classaddress"]     >> _class;
    rdr["divisionaddress"]  >> _division;
    rdr["routeid"]          >> _routeID;

    rdr["addressusage"]     >> tmpStr;
    std::transform(tmpStr.begin(), tmpStr.end(), tmpStr.begin(), tolower);

    _addressUsage = resolveAddressUsage(tmpStr, Cti::AddressUsage_Versacom);

    rdr["relayusage"]       >> tmpStr;
    _relayMask = resolveRelayUsage(tmpStr);
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
    CTILOG_ERROR(dout, "function unimplemented");

    return false;
}

bool CtiTableVersacomLoadGroup::Update()
{
    CTILOG_ERROR(dout, "function unimplemented");

    return false;
}

bool CtiTableVersacomLoadGroup::Delete()
{
    static const std::string sql = "delete from " + getTableName() + " where deviceid = ?";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       deleter(conn, sql);

    deleter << getDeviceID();

    return Cti::Database::executeCommand( deleter, __FILE__, __LINE__ );
}

string CtiTableVersacomLoadGroup::getTableName()
{
    return "LMGroupVersacom";
}
