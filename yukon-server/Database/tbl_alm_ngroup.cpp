#include "yukon.h"



/*-----------------------------------------------------------------------------*
*
* File:   tbl_alm_ngroup
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_alm_ngroup.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/02/10 23:23:47 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>
#include <rw\rwtime.h>
#include <rw\cstring.h>

#include "dbaccess.h"
#include "tbl_alm_ngroup.h"
#include "logger.h"

bool CtiTableNotificationGroup::operator<( const CtiTableNotificationGroup &rhs ) const
{
    return(getGroupID() < rhs.getGroupID());
}
bool CtiTableNotificationGroup::operator==( const CtiTableNotificationGroup &rhs ) const
{
    return(getGroupID() == rhs.getGroupID());
}
bool CtiTableNotificationGroup::operator()(const CtiTableNotificationGroup& aRef) const
{
    return operator<(aRef);
}



LONG CtiTableNotificationGroup::getGroupID() const
{

    return _notificationGroupID;
}
RWCString CtiTableNotificationGroup::getGroupName() const
{

    return _groupName;
}
RWCString CtiTableNotificationGroup::getEmailFromAddress() const
{

    return _emailFromAddress;
}
RWCString CtiTableNotificationGroup::getEmailSubject() const
{

    return _emailSubject;
}
RWCString CtiTableNotificationGroup::getEmailMessage() const
{

    return _emailMessage;
}
RWCString CtiTableNotificationGroup::getNumericPagerMessage() const
{

    return _numericPagerMessage;
}
BOOL CtiTableNotificationGroup::isDisabled() const
{

    return _groupDisabled;
}

CtiTableNotificationGroup& CtiTableNotificationGroup::setGroupID( const LONG &aRef )
{

    _notificationGroupID = aRef;
    return *this;
}
CtiTableNotificationGroup& CtiTableNotificationGroup::setGroupName( const RWCString &aStr )
{

    _groupName = aStr;
    return *this;
}
CtiTableNotificationGroup& CtiTableNotificationGroup::setEmailFromAddress( const RWCString &aStr )
{

    _emailFromAddress = aStr;
    return *this;
}
CtiTableNotificationGroup& CtiTableNotificationGroup::setEmailSubject( const RWCString &aStr )
{

    _emailSubject = aStr;
    return *this;
}
CtiTableNotificationGroup& CtiTableNotificationGroup::setEmailMessage( const RWCString &aStr )
{

    _emailMessage = aStr;
    return *this;
}
CtiTableNotificationGroup& CtiTableNotificationGroup::setNumericPagerMessage( const RWCString &aStr )
{

    _numericPagerMessage = aStr;
    return *this;
}
CtiTableNotificationGroup& CtiTableNotificationGroup::setDisabled( const BOOL b )
{

    _groupDisabled = b;
    return *this;
}


RWCString CtiTableNotificationGroup::getTableName()
{
    return RWCString("NotificationGroup");
}

RWDBStatus CtiTableNotificationGroup::Insert()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();



    inserter <<
    getGroupID() <<
    getEmailFromAddress() <<
    getEmailSubject() <<
    getEmailMessage() <<
    getNumericPagerMessage() <<
    RWCString( ( isDisabled() ? 'Y': 'N' ) );

    inserter.execute( conn );

    return inserter.status();
}

RWDBStatus CtiTableNotificationGroup::Update()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();



    updater.where( table["notificationgroupid"] == getGroupID() );

    updater <<
    table["groupname"].assign( getGroupName() ) <<
    table["emailfromaddress"].assign( getEmailFromAddress() ) <<
    table["emailsubject"].assign( getEmailSubject() ) <<
    table["emailmessage"].assign( getEmailMessage() ) <<
    table["numericpagermessage"].assign( getNumericPagerMessage() ) <<
    table["disableflag"].assign( isDisabled() );

    updater.execute( conn );

    return updater.status();
}

RWDBStatus CtiTableNotificationGroup::Restore()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBStatus dbstat;

    {
        RWDBTable table = getDatabase().table( getTableName() );
        RWDBSelector selector = getDatabase().selector();

        selector <<
        table["notificationgroupid"] <<
        table["groupname"] <<
        table["emailsubject"] <<
        table["emailfromaddress"] <<
        table["emailmessage"] <<
        table["numericpagermessage"] <<
        table["disableflag"];

        selector.where( table["notificationgroupid"] == getGroupID() );

        RWDBReader reader = selector.reader( conn );

        dbstat = selector.status();

        if( reader() )
        {
            DecodeDatabaseReader( reader );
        }
    }


    if( dbstat.errorCode() == RWDBStatus::ok )
    {


        _destinationSet.clear();         // blank the list.

        RWDBTable table;
        RWDBDatabase db = getDatabase();
        RWDBSelector selector = db.selector();

        CtiTableNotificationDestination::getSQL(db, table, selector);

        selector.where( table["notificationgroupid"] == getGroupID() );

        RWDBReader reader = selector.reader(conn);
        dbstat = selector.status();


        if(dbstat.errorCode() != RWDBStatus::ok)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << selector.asString() << endl;
        }

        CtiTableNotificationDestination tempDest;

        while( reader() )
        {
            tempDest.DecodeDatabaseReader( reader );

            pair< DESTINATIONSET::iterator, bool >  findpair;

            {

                findpair = _destinationSet.insert( tempDest );

                if( findpair.second == false )
                {
                    CtiTableNotificationDestination &aDest = *(findpair.first);
                    aDest = tempDest;   // Use operator equal to re-set the value!
                }
            }
        }
    }

    return dbstat;
}

RWDBStatus CtiTableNotificationGroup::Delete()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["notificationgroupid"] == getGroupID() );

    return deleter.execute( conn ).status();
}

void CtiTableNotificationGroup::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    keyTable = db.table( getTableName() );

    selector <<
    keyTable["notificationgroupid"] <<
    keyTable["groupname"] <<
    keyTable["emailsubject"] <<
    keyTable["emailfromaddress"] <<
    keyTable["emailmessage"] <<
    keyTable["numericpagermessage"] <<
    keyTable["disableflag"];

    selector.from(keyTable);
}

void CtiTableNotificationGroup::DecodeDatabaseReader(RWDBReader& rdr)
{
    RWCString temp;

    {

        rdr["notificationgroupid"] >> _notificationGroupID;
        rdr["groupname"]           >> _groupName;
        rdr["emailsubject"]        >> _emailSubject;
        rdr["emailfromaddress"]    >> _emailFromAddress;
        rdr["emailmessage"]        >> _emailMessage;
        rdr["numericpagermessage"] >> _numericPagerMessage;
        rdr["disableflag"]         >> temp;
    }

    temp.toLower();
    setDisabled( (temp((size_t)0) == 'y') ? TRUE : FALSE );

    setDirty(false);  // Not dirty anymore

    return;
}

size_t CtiTableNotificationGroup::entries() const
{

    return _destinationSet.size();
}


void CtiTableNotificationGroup::dump() const
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << "Group ID: " << getGroupID() << endl;
    dout << "Group Name: " << getGroupName() << endl;
    dout << "Email From Address: " << getEmailFromAddress() << endl;
    dout << "Email Subject: " << getEmailSubject() << endl;
    dout << "Email Message: " << getEmailMessage() << endl;
    dout << "Numeric Pager Message: " << getNumericPagerMessage() << endl;
    dout << "is Disabled: " << isDisabled() << endl;
}

void CtiTableNotificationGroup::dumpDestinations() const
{
    DESTINATIONSET::const_iterator   theIterator;



    for( theIterator = _destinationSet.begin(); theIterator != _destinationSet.end(); theIterator++ )
    {
        const CtiTableNotificationDestination &aDest = *theIterator;
        aDest.dump();
    }
}


CtiTableNotificationGroup::DESTINATIONSET CtiTableNotificationGroup::getDestinations() const
{
    return _destinationSet;
}

CtiTableNotificationGroup& CtiTableNotificationGroup::setDestinations( const CtiTableNotificationGroup::DESTINATIONSET dest )
{
    _destinationSet = dest;
    return *this;
}

vector<int> CtiTableNotificationGroup::getRecipientVector()
{
    vector<int> vect;
    DESTINATIONSET::const_iterator   theIterator;



    for( theIterator = _destinationSet.begin(); theIterator != _destinationSet.end(); theIterator++ )
    {
        const CtiTableNotificationDestination &aDest = *theIterator;
        vect.push_back( aDest.getRecipientID() );
    }

    return vect;
}

CtiTableNotificationGroup& CtiTableNotificationGroup::setDirty( bool dirt )
{

    DESTINATIONSET::iterator   theIterator;

    _isDirty = dirt;

    if(dirt == true)     // They clean themselves based upon Restore only!
    {
        for( theIterator = _destinationSet.begin(); theIterator != _destinationSet.end(); theIterator++ )
        {
            CtiTableNotificationDestination &aDest = *theIterator;
            aDest.setDirty(dirt);
        }
    }

    return *this;
}

bool CtiTableNotificationGroup::isDirty() const
{


    return _isDirty;
}

CtiTableNotificationGroup::CtiTableNotificationGroup( LONG gid) :
_isDirty(true),
_notificationGroupID(gid),
_groupDisabled(FALSE)
{}

CtiTableNotificationGroup::CtiTableNotificationGroup(const CtiTableNotificationGroup& aRef)
{
    *this = aRef;
}

CtiTableNotificationGroup::~CtiTableNotificationGroup() {}

CtiTableNotificationGroup& CtiTableNotificationGroup::operator=(const CtiTableNotificationGroup& aRef)
{
    if(this != &aRef)
    {
        setGroupID( aRef.getGroupID() );
        setGroupName( aRef.getGroupName() );
        setEmailFromAddress( aRef.getEmailFromAddress() );
        setEmailSubject( aRef.getEmailSubject() );
        setEmailMessage( aRef.getEmailMessage() );
        setNumericPagerMessage( aRef.getNumericPagerMessage() );
        setDisabled( aRef.isDisabled() );

        setDirty(aRef.isDirty());

        setDestinations( aRef.getDestinations() );
    }

    return *this;
}
