/*-----------------------------------------------------------------------------*
*
* File:   tbl_rtversacom
*
* Date:   8/23/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_rtversacom.cpp-arc  $
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2006/07/19 19:02:01 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
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

CtiTableVersacomRoute::CtiTableVersacomRoute(const CtiTableVersacomRoute& aRef)
{
    *this = aRef;
}

CtiTableVersacomRoute::~CtiTableVersacomRoute()
{
}

CtiTableVersacomRoute& CtiTableVersacomRoute::operator=(const CtiTableVersacomRoute& aRef)
{
    if(this != &aRef)
    {
        RouteID        = aRef.getRouteID();
        UtilityID      = aRef.getUtilityID();
        Section        = aRef.getSection();
        Class          = aRef.getClass();
        Division       = aRef.getDivision();
        Individual     = aRef.getIndividual();
        Bus            = aRef.getBus();
        Amp            = aRef.getAmp();

    }
    return *this;
}

void CtiTableVersacomRoute::DumpData()
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << " Route ID                                   " << RouteID << endl;
    dout << " Utility ID                                 " << UtilityID << endl;
    dout << " Section                                    " << Section << endl;
    dout << " Class                                      " << Class << endl;
    dout << " Division                                   " << Division << endl;
    dout << " Bus                                        " << Bus << endl;
    dout << " Amp                                        " << Amp << endl;
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
    string rwsTemp;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
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


