#include "yukon.h"



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
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/02/10 23:23:49 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dbaccess.h"
#include "logger.h"
#include "tbl_rtversacom.h"

CtiTableVersacomRoute::CtiTableVersacomRoute()
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

void CtiTableVersacomRoute::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
#if 1
    keyTable = db.table(getTableName() );

    selector <<
    keyTable["routeid"] <<
    keyTable["utilityid"] <<
    keyTable["sectionaddress"] <<
    keyTable["classaddress"] <<
    keyTable["divisionaddress"] <<
    keyTable["busnumber"] <<
    keyTable["ampcardset"];

    selector.from(keyTable);

    // selector.where( selector.where() && keyTable["routeid"] == routetbl["routeid"] );
#else
    keyTable = db.table("Route");
    RWDBTable routetbl = db.table(getTableName() );

    selector <<
    keyTable["routeid"] <<
    routetbl["utilityid"] <<
    routetbl["sectionaddress"] <<
    routetbl["classaddress"] <<
    routetbl["divisionaddress"] <<
    routetbl["busnumber"] <<
    routetbl["ampcardset"];

    selector.from(keyTable);
    selector.from(routetbl);

    selector.where( selector.where() && keyTable["routeid"] == routetbl["routeid"] );
#endif
}

void CtiTableVersacomRoute::DecodeDatabaseReader(RWDBReader &rdr)
{
    RWCString rwsTemp;

    if(getDebugLevel() & 0x0800)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["routeid"]          >> RouteID;
    rdr["utilityid"]        >> UtilityID;
    rdr["sectionaddress"]   >> Section;
    rdr["classaddress"]     >> Class;
    rdr["divisionaddress"]  >> Division;
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

RWCString CtiTableVersacomRoute::getTableName()
{
    return "VersacomRoute";
}

RWDBStatus CtiTableVersacomRoute::Restore()
{

    char temp[32];

    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["routeid"] <<
    table["utilityid"] <<
    table["sectionaddress"] <<
    table["classaddress"] <<
    table["divisionaddress"] <<
    table["busnumber"] <<
    table["ampcardset"];

    selector.where( table["routeid"] == getRouteID() );

    RWDBReader reader = selector.reader( conn );

    if( reader() )
    {
        DecodeDatabaseReader( reader );
        setDirty( false );
    }
    else
    {
        setDirty( true );
    }
    return reader.status();
}

RWDBStatus CtiTableVersacomRoute::Insert()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getRouteID() <<
    getUtilityID() <<
    getSection() <<
    getClass() <<
    getDivision() <<
    getBus() <<
    getAmp();

    if( inserter.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return inserter.status();
}

RWDBStatus CtiTableVersacomRoute::Update()
{
    char temp[32];



    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["routeid"] == getRouteID() );

    updater <<
    table["utilityid"].assign( getUtilityID() ) <<
    table["sectionaddress"].assign( getSection() ) <<
    table["classaddress"].assign( getClass() ) <<
    table["divisionaddress"].assign( getDivision() ) <<
    table["busnumber"].assign( getBus() ) <<
    table["ampcardset"].assign(getAmp());

    if( updater.execute( conn ).status().errorCode() == RWDBStatus::ok)
    {
        setDirty(false);
    }

    return updater.status();
}

RWDBStatus CtiTableVersacomRoute::Delete()
{


    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["routeid"] == getRouteID() );
    deleter.execute( conn );
    return deleter.status();
}



