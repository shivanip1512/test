#include "precompiled.h"

#include "dbaccess.h"
#include "logger.h"
#include "tbl_rtversacom.h"
#include "database_connection.h"
#include "database_reader.h"

using std::string;
using std::endl;

CtiTableVersacomRoute::CtiTableVersacomRoute() :
    RouteID(0),
    UtilityID(0),
    Section(0),
    Division(0),
    Bus(0),
    Amp(0),
    Individual(0)
{
}

CtiTableVersacomRoute::~CtiTableVersacomRoute()
{
}

std::string CtiTableVersacomRoute::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiTableVersacomRoute";
    itemList.add("Route ID")   << RouteID;
    itemList.add("Utility ID") << UtilityID;
    itemList.add("Section")    << Section;
    itemList.add("Class")      << Class;
    itemList.add("Division")   << Division;
    itemList.add("Bus")        << Bus;
    itemList.add("Amp")        << Amp;

    return itemList.toString();
}

INT CtiTableVersacomRoute::getUtilityID() const
{


    return UtilityID;
}

CtiTableVersacomRoute& CtiTableVersacomRoute::setUtilityID( const INT aUtilityID )
{


    UtilityID = aUtilityID;
    return *this;
}

INT CtiTableVersacomRoute::getSection() const
{


    return Section;
}

CtiTableVersacomRoute& CtiTableVersacomRoute::setSection( const INT aSection )
{


    Section = aSection;
    return *this;
}

INT CtiTableVersacomRoute::getClass() const
{


    return Class;
}

CtiTableVersacomRoute& CtiTableVersacomRoute::setClass( const INT aClass )
{


    Class = aClass;
    return *this;
}

INT CtiTableVersacomRoute::getDivision() const
{


    return Division;
}

CtiTableVersacomRoute& CtiTableVersacomRoute::setDivision( const INT aDivision )
{


    Division = aDivision;
    return *this;
}

INT CtiTableVersacomRoute::getBus() const
{


    return Bus;
}

CtiTableVersacomRoute& CtiTableVersacomRoute::setBus( const INT aBus )
{


    Bus = aBus;
    return *this;
}

INT CtiTableVersacomRoute::getAmp() const
{


    return Amp;
}

CtiTableVersacomRoute& CtiTableVersacomRoute::setAmp( const INT aAmp )
{


    Amp = aAmp;
    return *this;
}

INT  CtiTableVersacomRoute::getSPID() const
{
    return UtilityID;
}

INT  CtiTableVersacomRoute::getGeo() const
{
    return Section;
}

INT  CtiTableVersacomRoute::getSubstation() const
{
    return Class;
}

UINT  CtiTableVersacomRoute::getIndividual() const
{
    return Individual;
}

string CtiTableVersacomRoute::getSQLCoreStatement()
{
    static const string sql =  "SELECT VR.routeid, VR.utilityid, VR.sectionaddress, VR.classaddress, VR.divisionaddress, "
                                   "VR.busnumber, VR.ampcardset "
                               "FROM VersacomRoute VR";

    return sql;
}

void CtiTableVersacomRoute::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB read from "<< getTableName());
    }

    rdr["routeid"]          >> RouteID;
    rdr["utilityid"]        >> UtilityID;
    rdr["sectionaddress"]   >> Section;
    rdr["classaddress"]     >> Class;
    rdr["divisionaddress"]  >> Division;
    rdr["divisionaddress"]  >> Individual;
    rdr["busnumber"]        >> Bus;
    rdr["ampcardset"]       >> Amp;

    // These are zero based in my code...
    Bus--;
    Amp--;
}

LONG CtiTableVersacomRoute::getRouteID() const
{


    return RouteID;
}

CtiTableVersacomRoute& CtiTableVersacomRoute::setRouteID( const LONG routeID )
{


    RouteID = routeID;
    return *this;
}

string CtiTableVersacomRoute::getTableName()
{
    return "VersacomRoute";
}


