#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_cicust
*
* Date:   4/2/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_dv_cicust.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/02/10 23:23:48 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>
#include <rw\rwtime.h>
#include <rw\cstring.h>

#include "dbaccess.h"
#include "logger.h"
#include "tbl_dv_cicust.h"

bool CtiTableCICustomerBase::operator<( const CtiTableCICustomerBase &rhs ) const
{
    return(getID() < rhs.getID());
}
bool CtiTableCICustomerBase::operator==( const CtiTableCICustomerBase &rhs ) const
{
    return(getID() == rhs.getID());
}
bool CtiTableCICustomerBase::operator()(const CtiTableCICustomerBase& aRef) const
{
    return operator<(aRef);
}


LONG CtiTableCICustomerBase::getID() const
{

    return _id;
}

CtiTableCICustomerBase& CtiTableCICustomerBase::setID( const LONG &aRef )
{

    _id = aRef;
    return *this;
}

RWCString CtiTableCICustomerBase::getTableName()
{
    return RWCString("CICustomerBase");
}

RWDBStatus CtiTableCICustomerBase::Insert()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();



    inserter << getID();


    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    inserter.execute( conn );

    return inserter.status();
}

RWDBStatus CtiTableCICustomerBase::Update()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();



    updater.where( table["deviceid"] == getID() );

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    updater.execute( conn );

    return updater.status();
}

RWDBStatus CtiTableCICustomerBase::Restore()
{
    int iTemp;
    int contactid;
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();
    pair< INTSET::iterator, bool >  findpair;

    RWDBStatus dbstat;

    {
        RWDBTable table = getDatabase().table( getTableName() );
        RWDBSelector selector = getDatabase().selector();

        selector << table["deviceid"];

        selector.where( table["deviceid"] == getID() );

        RWDBReader reader = selector.reader( conn );

        dbstat = selector.status();

        if( reader() )
        {
            DecodeDatabaseReader( reader );
        }
    }

    if( dbstat.errorCode() == RWDBStatus::ok )
    {

        _custContacts.clear();         // blank the list.

        RWDBDatabase db = getDatabase();
        RWDBTable table = db.table( "CICustContact" );
        RWDBSelector selector = db.selector();

        selector << table["deviceid"] << table["contactid"];
        selector.from(table);
        selector.where( table["deviceid"] == getID() );

        RWDBReader reader = selector.execute( conn ).table().reader();
        dbstat = selector.status();

        while( reader() )
        {
            reader["contactid"] >> contactid;

            findpair = _custContacts.insert( contactid );

            if(findpair.second == false)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Company lists contactid (tbl CICustomerContact)" << contactid << endl;
                dout << RWTime() << "    Multiple times.  Only one notification will be sent." << endl;
            }
        }
    }


    /*
     *  Now go out and load up the CustomerContact itself to get the locationid!
     */
    if( dbstat.errorCode() == RWDBStatus::ok )
    {
        INTSET::iterator itr;


        _custLocationIDs.clear();         // blank the list.

        RWDBDatabase db = getDatabase();
        RWDBTable table = db.table( "CustomerContact" );
        RWDBSelector selector = db.selector();

        selector << table["contactid"] << table["locationid"];
        selector.from(table);

        RWDBReader reader = selector.execute( conn ).table().reader();
        dbstat = selector.status();

        while( reader() )
        {
            reader["locationid"] >> iTemp;
            reader["contactid"] >> contactid;

            if( (itr = _custContacts.find(contactid)) != _custContacts.end() )
            {
                findpair = _custLocationIDs.insert( iTemp );   // Add it to our location ids then!

                if(findpair.second == false)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Company " << endl;
                    dout << "    Lists locationid (tbl CustomerContact) " << iTemp << endl;
                    dout << "    Multiple times with different contact names/ids." << endl;
                    dout << "    Only one notification will be sent. " << endl;
                    dout << "    Second contactid is " << contactid << endl;
                }
            }
        }
    }

    return dbstat;
}

RWDBStatus CtiTableCICustomerBase::Delete()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["deviceid"] == getID() );

    return deleter.execute( conn ).status();
}

void CtiTableCICustomerBase::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table( getTableName() );

    selector <<
    keyTable["deviceid"];

    selector.from(keyTable);
}

void CtiTableCICustomerBase::DecodeDatabaseReader(RWDBReader& rdr)
{
    RWCString temp;

    {

        rdr["deviceid"] >> _id;
    }

    return;
}

size_t CtiTableCICustomerBase::entries() const
{

    return _custLocationIDs.size();
}


void CtiTableCICustomerBase::dump() const
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << getID() << endl;
}

set< int > CtiTableCICustomerBase::getRecipientSet() const
{
    INTSET newset;

    INTSET::const_iterator   theIterator;



    try
    {
        for( theIterator = _custLocationIDs.begin(); theIterator != _custLocationIDs.end(); theIterator++ )
        {
            const int &aInt = *theIterator;
            newset.insert( aInt );
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }


    return newset;
}


void CtiTableCICustomerBase::dumpRecipients() const
{
    CtiTableCICustomerBase::CONST_INTSETITERATOR iter;

    try
    {
        for(iter = _custLocationIDs.begin(); iter != _custLocationIDs.end(); ++iter)
        {
            set<int>::const_reference locID = *iter;

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << " LocationID " << locID << endl;
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

CtiTableCICustomerBase& CtiTableCICustomerBase::setRecipientSet( const INTSET &rhs)
{
    _custLocationIDs = rhs;
    return *this;
}

vector<int> CtiTableCICustomerBase::getRecipientVector() const
{
    vector<int> vect;

    CtiTableCICustomerBase::CONST_INTSETITERATOR iter;

    try
    {
        for(iter = _custLocationIDs.begin(); iter != _custLocationIDs.end(); ++iter)
        {
            set<int>::const_reference locID = *iter;

            vect.push_back(locID);
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return vect;
}
CtiTableCICustomerBase::CtiTableCICustomerBase(LONG id) :
_id(id)
{}

CtiTableCICustomerBase::CtiTableCICustomerBase(const CtiTableCICustomerBase& aRef)
{
    if(this != &aRef)
    {
        _custContacts.clear();
        _custLocationIDs.clear();
    }

    *this = aRef;
}

CtiTableCICustomerBase::~CtiTableCICustomerBase() {}

CtiTableCICustomerBase& CtiTableCICustomerBase::operator=(const CtiTableCICustomerBase& aRef)
{
    if(this != &aRef)
    {
        setID(aRef.getID());
        setRecipientSet(aRef.getRecipientSet());
    }
    return *this;
}


