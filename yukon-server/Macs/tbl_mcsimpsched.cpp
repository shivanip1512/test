/*-----------------------------------------------------------------------------*
*
* File:   tbl_mcsimpsched
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/tbl_mcsimpsched.cpp-arc  $
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2006/01/16 21:09:12 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
/*---------------------------------------------------------------------------

        Filename:  tbl_mcsimpsched.cpp

        Programmer:  Aaron Lauinger

        Description:    Source file for class CtiMCSimpleSchedule


        Initial Date:  1/11/01


        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 1999, 2001
---------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_mcsimpsched.h"
#include "dbaccess.h"
#include "rwutil.h"

//Name of the database table
const char* CtiTableMCSimpleSchedule::_table_name = "MACSimpleSchedule";

CtiTableMCSimpleSchedule::CtiTableMCSimpleSchedule(
                                                  long schedule_id,
                                                  long target_select,
                                                  const string& start_command,
                                                  const string& stop_command,
                                                  long repeat_interval )
:
_schedule_id(schedule_id),
_target_id(target_select),
_start_command(start_command),
_stop_command(stop_command),
_repeat_interval(repeat_interval)
{

}

long CtiTableMCSimpleSchedule::getScheduleID() const
{
    return _schedule_id;
}

long CtiTableMCSimpleSchedule::getTargetPaoId() const
{
    return _target_id;
}

const string& CtiTableMCSimpleSchedule::getStartCommand() const
{
    return _start_command;
}

const string& CtiTableMCSimpleSchedule::getStopCommand() const
{
    return _stop_command;
}

long CtiTableMCSimpleSchedule::getRepeatInterval() const
{
    return _repeat_interval;
}

CtiTableMCSimpleSchedule&
CtiTableMCSimpleSchedule::setScheduleID(long schedule_id)
{
    _schedule_id = schedule_id;
    return *this;
}

CtiTableMCSimpleSchedule&
CtiTableMCSimpleSchedule::setTargetSelect(const int target_select)
{
    _target_id = target_select;
    return *this;
}

CtiTableMCSimpleSchedule&
CtiTableMCSimpleSchedule::setStartCommand(const string& start_command)
{
    _start_command = start_command;
    return *this;
}

CtiTableMCSimpleSchedule&
CtiTableMCSimpleSchedule::setStopCommand(const string& stop_command)
{
    _stop_command = stop_command;
    return *this;
}

CtiTableMCSimpleSchedule&
CtiTableMCSimpleSchedule::setRepeatInterval(long repeat_interval)
{
    _repeat_interval = repeat_interval;
    return *this;
}

void CtiTableMCSimpleSchedule::getSQL(  RWDBDatabase &db,
                                        RWDBTable &keyTable,
                                        RWDBSelector &selector)
{
    keyTable = db.table(_table_name);

    selector                            <<
    keyTable["scheduleid"]          <<
    keyTable["paobjectid"]        <<
    keyTable["startcommand"]        <<
    keyTable["stopcommand"]         <<
    keyTable["repeatinterval"];

    selector.from(keyTable);

}

bool CtiTableMCSimpleSchedule::DecodeDatabaseReader(RWDBReader &rdr)
{
    //CtiLockGuard< CtiMutex > guard( _mux );

    string temp;

    rdr["scheduleid"]       >> _schedule_id;

    rdr["paobjectid"]     >> _target_id;

    rdr["startcommand"]    >> _start_command;
    rdr["stopcommand"]     >> _stop_command;

    rdr["repeatinterval"]  >> _repeat_interval;

    return true;
}

bool CtiTableMCSimpleSchedule::Update()
{
    bool ret_val = false;
    string sql;

    try
    {
        {
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();

            RWDBTable t = conn.database().table( _table_name );

            RWDBUpdater updater = t.updater();

            updater.where( t["ScheduleID"] == getScheduleID() );

            updater << t["PAObjectId"].assign(getTargetPaoId());

            updater << t["StartCommand"].assign((const char*) getStartCommand().c_str());

            updater << t["StopCommand"].assign((const char*) getStopCommand().c_str());

            updater << t["RepeatInterval"].assign(getRepeatInterval());

            sql = (const char*) updater.asString().data();

            RWDBResult result = updater.execute(conn);

            ret_val = ( result.status().errorCode() == RWDBStatus::ok );
        }
    }
    catch(...)
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << CtiTime()
        << " An exception occured updating table \""
        << _table_name
        << "\""
        << endl;
    }

    if( !ret_val )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << CtiTime()
        << " "
        << sql
        << endl;
    }

    return ret_val;
}

bool CtiTableMCSimpleSchedule::Insert()
{
    bool ret_val = false;
    string sql;

    try
    {
        {
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();

            RWDBTable t = conn.database().table( _table_name );
            RWDBInserter inserter = t.inserter();

            inserter << getScheduleID();

            inserter << getTargetPaoId();

            inserter << (const char*) getStartCommand().c_str();

            inserter << (const char*) getStopCommand().c_str();

            inserter << getRepeatInterval();

            sql = (const char*) inserter.asString().data();
            RWDBResult result = inserter.execute();

            ret_val = ( result.status().errorCode() == RWDBStatus::ok );

        }
    }
    catch(...)
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << CtiTime()
        << " An exception occured inserting to table \""
        << _table_name
        << "\""
        << endl;
    }

    if( !ret_val )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << CtiTime()
        << " "
        << sql
        << endl;
    }

    return ret_val;
}

bool CtiTableMCSimpleSchedule::Delete()
{
    bool ret_val = false;
    string sql;

    try
    {
        {
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();

            RWDBTable t = conn.database().table( _table_name );
            RWDBDeleter deleter = t.deleter();

            deleter.where( t["ScheduleID"] == getScheduleID() );

            sql = (const char*) deleter.asString().data();

            RWDBResult result = deleter.execute();
            ret_val = ( result.status().errorCode() == RWDBStatus::ok );
        }
    }
    catch(...)
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << CtiTime()
        << " An exception occured deleting from table \""
        << _table_name
        << "\""
        << endl;
    }

    if( !ret_val )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << CtiTime()
        << " "
        << sql
        << endl;
    }

    return ret_val;
}
CtiTableMCSimpleSchedule::~CtiTableMCSimpleSchedule() {};

