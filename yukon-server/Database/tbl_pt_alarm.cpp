

#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_alarm
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_pt_alarm.cpp-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:58:06 $
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
#include "logger.h"
#include "tbl_pt_alarm.h"


CtiTablePointAlarming& CtiTablePointAlarming::operator=(const CtiTablePointAlarming& aRef)
{
    if(this != &aRef)
    {
        setPointID( aRef.getPointID() );

        for(int i = 0; i < ALARM_STATE_SIZE; i++)
        {
            setAlarmStates( i, aRef.getAlarmStates(i) );
        }

        setExcludeNotifyStates( aRef.getExcludeNotifyStates() );
        setNotifyOnAcknowledge( aRef.getNotifyOnAcknowledge() );
        setRecipientID( aRef.getRecipientID() );
        setNotificationGroupID( aRef.getNotificationGroupID() );
    }
    return *this;
}

UINT CtiTablePointAlarming::resolveStates( RWCString &str )
{
    int i;
    UINT states = 0;

    if( !str.isNull() )
    {
        str.toLower();

        for(i = 0 ; i < str.length() - 1 && i < 32; i++ )
        {
            if(str(i) == 'y')
            {
                states |= ( 0x00000001 << i );      // Put the bit in there
            }
        }
    }

    return states;
}

RWCString CtiTablePointAlarming::statesAsString( )
{
    char temp[40];

    RWCString str( (char)1 , ALARM_STATE_SIZE);

    for(int i = 0; i < ALARM_STATE_SIZE - 1; i++)
    {
        str[(size_t)i] = (BYTE)getAlarmStates(i);
    }

    return str;
}

RWCString CtiTablePointAlarming::excludeAsString( )
{
    RWCString str('N', ALARM_STATE_SIZE);

    for(int i = 0; i < str.length() - 1 && i < ALARM_STATE_SIZE; i++)
    {
        if( _excludeNotifyStates & (0x00000001 << i) )
        {
            str(i) = 'Y';
        }
    }

    return str;
}


LONG CtiTablePointAlarming::getPointID() const
{
    return _pointID;
}

LONG CtiTablePointAlarming::getRecipientID() const
{
    return _recipientID;
}

UINT CtiTablePointAlarming::getAlarmStates(const INT offset) const
{
    return _alarmStates[offset];
}
UINT CtiTablePointAlarming::getExcludeNotifyStates() const
{
    return _excludeNotifyStates;
}
UINT CtiTablePointAlarming::getNotificationGroupID() const
{
    return _notificationGroupID;
}

BOOL CtiTablePointAlarming::getNotifyOnAcknowledge() const
{
    return _notifyOnAcknowledge;
}

CtiTablePointAlarming& CtiTablePointAlarming::setPointID( const LONG &aLong )
{
    _pointID = aLong;
    return *this;
}

CtiTablePointAlarming& CtiTablePointAlarming::setRecipientID( const LONG &aLong )
{
    _recipientID = aLong;
    return *this;
}

CtiTablePointAlarming& CtiTablePointAlarming::setAlarmStates( const INT offset, const UINT &aInt )
{
    _alarmStates[offset] = aInt;
    return *this;
}

CtiTablePointAlarming& CtiTablePointAlarming::setAlarmStates( const RWCString str )
{
    unsigned char tchar;

    for(int i = 0; i < str.length() && i < ALARM_STATE_SIZE; i++)
    {
        tchar = (unsigned char)str[(size_t)i];
        setAlarmStates(i, (UINT)tchar);
    }

    return *this;
}

CtiTablePointAlarming& CtiTablePointAlarming::setExcludeNotifyStates( const UINT &aInt )
{
    _excludeNotifyStates = aInt;
    return *this;
}

CtiTablePointAlarming& CtiTablePointAlarming::setNotifyOnAcknowledge( const BOOL &aBool )
{
    _notifyOnAcknowledge = aBool;
    return *this;
}

CtiTablePointAlarming& CtiTablePointAlarming::setNotificationGroupID( const UINT &aInt )
{
    _notificationGroupID = aInt;
    return *this;
}

RWCString CtiTablePointAlarming::getTableName()
{
    return RWCString("PointAlarming");
}

RWDBStatus CtiTablePointAlarming::Insert()
{
    RWDBConnection conn = getConnection();
    RWLockGuard<RWDBConnection> conn_guard(conn);

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBInserter inserter = table.inserter();

    inserter <<
    getPointID() <<
    statesAsString() <<
    getExcludeNotifyStates() <<
    ( getNotifyOnAcknowledge() ? 'Y' : 'N' ) <<
    getRecipientID() <<
    getNotificationGroupID();

    inserter.execute( conn );

    return inserter.status();
}

RWDBStatus CtiTablePointAlarming::Update()
{
    RWDBConnection conn = getConnection();
    RWLockGuard<RWDBConnection> conn_guard(conn);

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBUpdater updater = table.updater();

    updater.where( table["pointid"] == getPointID() );

    updater <<
    table["alarmstates"].assign( statesAsString( ) ) <<
    table["excludenotifystates"].assign( excludeAsString( )) <<
    table["notifyonacknowledge"].assign(( getNotifyOnAcknowledge() ? 'Y' : 'N' )) <<
    table["recipientid"].assign(getRecipientID()) <<
    table["notificationgroupid"].assign(getNotificationGroupID());

    updater.execute( conn );

    return updater.status();
}

RWDBStatus CtiTablePointAlarming::Restore()
{
    RWDBConnection conn = getConnection();
    RWLockGuard<RWDBConnection> conn_guard(conn);

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBSelector selector = getDatabase().selector();

    selector <<
    table["pointid"] <<
    table["alarmstates"] <<
    table["excludenotifystates"] <<
    table["notifyonacknowledge"] <<
    table["recipientid"] <<
    table["notificationgroupid"];

    selector.where( table["pointid"] == getPointID() );

    RWDBReader reader = selector.reader( conn );

    /*
     *  If we are in the database, we reload and ARE NOT dirty... otherwise, we are dirty and need to be
     *  written into the database
     */
    if( reader() )
    {
        DecodeDatabaseReader( reader );
    }

    return reader.status();
}

RWDBStatus CtiTablePointAlarming::Delete()
{
    RWDBConnection conn = getConnection();
    RWLockGuard<RWDBConnection> conn_guard(conn);

    RWDBTable table = getDatabase().table( getTableName() );
    RWDBDeleter deleter = table.deleter();

    deleter.where( table["pointid"] == getPointID() );

    return deleter.execute( conn ).status();
}

void CtiTablePointAlarming::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable tbl = db.table( getTableName() );

    selector <<
    tbl["pointid"] <<
    tbl["alarmstates"] <<
    tbl["excludenotifystates"] <<
    tbl["notifyonacknowledge"] <<
    tbl["recipientid"] <<
    tbl["notificationgroupid"];

    selector.from(tbl);
    selector.where( selector.where() );
}

void CtiTablePointAlarming::DecodeDatabaseReader(RWDBReader& rdr)
{
    RWCString temp;

    rdr["alarmstates"]            >> temp;
    setAlarmStates( temp );

    rdr["excludenotifystates"]    >> temp;
    setExcludeNotifyStates( resolveStates( temp ) );

    rdr["notifyonacknowledge"]    >> temp;
    temp.toLower();
    setNotifyOnAcknowledge( temp(0) == 'y' );

    rdr["recipientid"]             >> _recipientID;
    rdr["notificationgroupid"]    >> _notificationGroupID;
}


CtiTablePointAlarming::~CtiTablePointAlarming()
{
}

bool CtiTablePointAlarming::alarmOn( int alarm ) const
{
    return( _alarmStates[alarm] > 0 );
}

INT CtiTablePointAlarming::alarmPriority( int alarm ) const
{
    return _alarmStates[alarm];
}

bool CtiTablePointAlarming::isExcluded( int alarm) const
{
    return(_excludeNotifyStates & (0x00000001 << alarm));
}

CtiTablePointAlarming::CtiTablePointAlarming( LONG pid) :
_pointID( pid ),
_excludeNotifyStates( UINT_MAX ),
_notifyOnAcknowledge(FALSE),
_recipientID(0),
_notificationGroupID( 0 )
{
    for(int i = 0; i < ALARM_STATE_SIZE; i++)
    {
        _alarmStates[i] = 1;            // This is event caliber!
    }
}

CtiTablePointAlarming::CtiTablePointAlarming(const CtiTablePointAlarming& aRef)
{
    *this = aRef;
}


