#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_mcsimpsched
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/INCLUDE/tbl_mcsimpsched.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2007/08/07 21:04:32 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
/*---------------------------------------------------------------------------

        Filename:  tbl_mcsimpsched.h

        Programmer:  Aaron Lauinger

        Description:    Header file for CtiMCSimpleSchedule.

        Initial Date:  1/11/01


        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 1999, 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786)

#ifndef __TBL_MCSIMPSCHED_H__
#define __TBL_MCSIMPSCHED_H__

#include <string>

#include <rw/db/db.h>

#include "mutex.h"
#include "guard.h"
#include "logger.h"

class CtiTableMCSimpleSchedule
{
public:
    CtiTableMCSimpleSchedule(   long schedule_id = 0,
                                const string& target_select = "",
                                const string& start_command = "",
                                const string& stop_command = "",
                                long repeat_interval = 0 );

    virtual ~CtiTableMCSimpleSchedule();

    long getScheduleID() const;
    const string& getTargetSelect() const;
    const string& getStartCommand() const;
    const string& getStopCommand() const;
    long getRepeatInterval() const;

    CtiTableMCSimpleSchedule& setScheduleID(long schedule_id);
    CtiTableMCSimpleSchedule& setTargetSelect(const string& target_select);
    CtiTableMCSimpleSchedule& setStartCommand(const string& start_command);
    CtiTableMCSimpleSchedule& setStopCommand(const string& stop_command);
    CtiTableMCSimpleSchedule& setRepeatInterval(long repeat_interval);

    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    bool DecodeDatabaseReader(RWDBReader &rdr);

    //database operations
    bool Update();
    bool Insert();
    bool Delete();

private:

    //CtiMutex _mux;

    static const char* _table_name;

    long    _schedule_id;
    string  _target_select;
    string  _start_command;
    string  _stop_command;
    long    _repeat_interval;
};
#endif
