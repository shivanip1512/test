#include "yukon.h"



/*-----------------------------------------------------------------------------*
 *
 * File:   tbl_alm_ngroup
 *
 * Date:   7/16/2001
 *
 * PVCS KEYWORDS:
 * ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_alm_ngroup.cpp-arc  $
 * REVISION     :  $Revision: 1.6 $
 * DATE         :  $Date: 2005/07/19 22:48:52 $
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
        getGroupName() <<
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
        table["disableflag"];

    selector.where( table["notificationgroupid"] == getGroupID() );

    RWDBReader reader = selector.reader( conn );

    dbstat = selector.status();

    if( reader() )
    {
        DecodeDatabaseReader( reader );
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
        keyTable["disableflag"];

    selector.from(keyTable);
}

void CtiTableNotificationGroup::DecodeDatabaseReader(RWDBReader& rdr)
{
    RWCString temp;

{

    rdr["notificationgroupid"] >> _notificationGroupID;
    rdr["groupname"]           >> _groupName;
    rdr["disableflag"]         >> temp;
}

temp.toLower();
setDisabled( (temp((size_t)0) == 'y') ? TRUE : FALSE );

setDirty(false);  // Not dirty anymore

return;
}

void CtiTableNotificationGroup::dump() const
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << "Group ID: " << getGroupID() << endl;
    dout << "Group Name: " << getGroupName() << endl;
    dout << "is Disabled: " << isDisabled() << endl;
}

CtiTableNotificationGroup& CtiTableNotificationGroup::setDirty( bool dirt )
{
    _isDirty = dirt;
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
        setDisabled( aRef.isDisabled() );

        setDirty(aRef.isDirty());
    }

    return *this;
}
