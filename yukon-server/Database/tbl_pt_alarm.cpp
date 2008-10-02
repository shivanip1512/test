/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_alarm
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_pt_alarm.cpp-arc  $
* REVISION     :  $Revision: 1.16 $
* DATE         :  $Date: 2008/10/02 18:27:30 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>


#include "dbaccess.h"
#include "logger.h"
#include "tbl_pt_alarm.h"

#include "rwutil.h"

CtiTablePointAlarming& CtiTablePointAlarming::operator=(const CtiTablePointAlarming& aRef)
{
    if(this != &aRef)
    {
        //setPointID( aRef.getPointID() );

        for(int i = 0; i < ALARM_STATE_SIZE; i++)
        {
            setAlarmCategory( i, aRef.getAlarmCategory(i) );
        }

        setExcludeNotifyStates( aRef.getExcludeNotifyStates() );
        setNotifyOnAcknowledge( aRef.getNotifyOnAcknowledge() );
        setRecipientID( aRef.getRecipientID() );
        setNotificationGroupID( aRef.getNotificationGroupID() );
    }
    return *this;
}

UINT CtiTablePointAlarming::resolveExcludeStates( string &str )
{
    int i;
    UINT states = 0;

    if( !str.empty() )
    {
        std::transform(str.begin(), str.end(), str.begin(), tolower);

        for(i = 0 ; i < str.length() - 1 && i < 32; i++ )
        {
            if(str[i] == 'y' || str[i] == 'e' || str[i] == 'b')
            {
                states |= ( 0x00000001 << i );      // Put the bit in there
            }
        }
    }

    return states;
}

UINT CtiTablePointAlarming::resolveAutoAcknowledgeStates( string &str )
{
    int i;
    UINT states = 0;

    if( !str.empty() )
    {
        std::transform(str.begin(), str.end(), str.begin(), tolower);

        for(i = 0 ; i < str.length() - 1 && i < 32; i++ )
        {
            if(str[i] == 'a' || str[i] == 'b')
            {
                states |= ( 0x00000001 << i );      // Put the bit in there
            }
        }
    }

    return states;
}

LONG CtiTablePointAlarming::getRecipientID() const
{
    return _recipientID;
}

UINT CtiTablePointAlarming::getAlarmCategory(const INT offset) const
{
    return _alarmCategory[offset];
}
UINT CtiTablePointAlarming::getExcludeNotifyStates() const
{
    return _excludeNotifyStates;
}
UINT CtiTablePointAlarming::getAutoAckStates() const
{
    return _autoAckStates;
}
UINT CtiTablePointAlarming::getNotificationGroupID() const
{
    return _notificationGroupID;
}

BOOL CtiTablePointAlarming::getNotifyOnAcknowledge() const
{
    return _notifyOnAcknowledge;
}

BOOL CtiTablePointAlarming::getNotifyOnClear() const
{
    return _notifyOnClear;
}

CtiTablePointAlarming& CtiTablePointAlarming::setRecipientID( const LONG &aLong )
{
    _recipientID = aLong;
    return *this;
}

CtiTablePointAlarming& CtiTablePointAlarming::setAlarmCategory( const INT offset, const UINT &aInt )
{
    _alarmCategory[offset] = aInt;
    return *this;
}

CtiTablePointAlarming& CtiTablePointAlarming::setAlarmCategory( const string str )
{
    unsigned char tchar;

    for(int i = 0; i < str.length() && i < ALARM_STATE_SIZE; i++)
    {
        tchar = (unsigned char)str[(size_t)i];
        setAlarmCategory(i, (UINT)tchar);
    }

    return *this;
}

CtiTablePointAlarming& CtiTablePointAlarming::setExcludeNotifyStates( const UINT &aInt )
{
    _excludeNotifyStates = aInt;
    return *this;
}

CtiTablePointAlarming& CtiTablePointAlarming::setAutoAckStates( const UINT &aInt )
{
    _autoAckStates = aInt;
    return *this;
}

CtiTablePointAlarming& CtiTablePointAlarming::setNotifyOnAcknowledge( const BOOL &aBool )
{
    _notifyOnAcknowledge = aBool;
    return *this;
}

CtiTablePointAlarming& CtiTablePointAlarming::setNotifyOnClear( const BOOL &aBool )
{
    _notifyOnClear = aBool;
    return *this;
}

CtiTablePointAlarming& CtiTablePointAlarming::setNotificationGroupID( const UINT &aInt )
{
    _notificationGroupID = aInt;
    return *this;
}

string CtiTablePointAlarming::getTableName()
{
    return string("PointAlarming");
}


bool CtiTablePointAlarming::operator<(const CtiTablePointAlarming &rhs) const
{
    return _pointID < rhs._pointID;
}


//This SQL is only proper when we assume some things about alarmstates
void CtiTablePointAlarming::getSQL(string &sql, LONG pointID, LONG paoID)
{

   sql = "select pointid, alarmstates, excludenotifystates, notifyonacknowledge,"
         " recipientid, notificationgroupid from pointalarming";

   if(pointID != 0)
   {
      sql += " where pointid = " + CtiNumStr(pointID);
   }
   else
   {
       sql += " where alarmstates != '\001\001\001\001\001\001\001\001\001\001\001\001\001\001\001\001\001\001\001\001\001\001\001\001\001\001\001\001\001\001\001\001'";
   }

   if(paoID != 0)
   {
       sql += " AND pointid in (select pointid from point where paobjectid = " + CtiNumStr(paoID) + ")";
   }
}

CtiTablePointAlarming::~CtiTablePointAlarming()
{
}

bool CtiTablePointAlarming::alarmOn( int alarm ) const
{
    return( _alarmCategory[alarm] > 0 );
}

INT CtiTablePointAlarming::alarmPriority( int alarm ) const
{
    return _alarmCategory[alarm];
}

bool CtiTablePointAlarming::isNotifyExcluded( int alarm) const
{
    return(_excludeNotifyStates & (0x00000001 << alarm));
}

bool CtiTablePointAlarming::isAutoAcked( int alarm) const
{
    return(_autoAckStates & (0x00000001 << alarm));
}


CtiTablePointAlarming::CtiTablePointAlarming(RWDBReader& rdr)
{
    static const RWCString pointid = "pointid";
    string temp;

    rdr[pointid] >> _pointID;
    rdr >> temp;
    setAlarmCategory( temp );

    rdr >> temp;
    setExcludeNotifyStates( resolveExcludeStates( temp ) );
    setAutoAckStates( resolveAutoAcknowledgeStates( temp ) );

    rdr >> temp;
    std::transform(temp.begin(), temp.end(), temp.begin(), tolower);
    setNotifyOnAcknowledge( temp[0] == 'a' || temp[0] == 'b' || temp[0] == 'y' );
    setNotifyOnClear( temp[0] == 'c' || temp[0] == 'b' );

    rdr >> _recipientID;
    rdr >> _notificationGroupID;
}

CtiTablePointAlarming::CtiTablePointAlarming( LONG pid) :
_pointID( pid ),
_excludeNotifyStates( 0 ),
_autoAckStates( 0 ),
_notifyOnAcknowledge(FALSE),
_notifyOnClear(FALSE),
_recipientID(0),
_notificationGroupID( 0 )
{
    for(int i = 0; i < ALARM_STATE_SIZE; i++)
    {
        _alarmCategory[i] = 1;            // This is event caliber!
    }
}

CtiTablePointAlarming::CtiTablePointAlarming(const CtiTablePointAlarming& aRef)
{
    *this = aRef;
}


