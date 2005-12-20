
/*-----------------------------------------------------------------------------*
*
* File:   tbl_dyn_ptalarming
*
* Class:  CtiTableDynamicPointAlarming
* Date:   8/7/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/12/20 17:16:08 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_DYN_PTALARMING_H__
#define __TBL_DYN_PTALARMING_H__

#include <rw/db/db.h>

#include "ctibase.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "pointdefs.h"
#include "yukon.h"


#define CTITABLEDYNAMICPOINTALARMING_MAX_ACTION         60
#define CTITABLEDYNAMICPOINTALARMING_MAX_DESCRIPTION    120

class IM_EX_CTIYUKONDB CtiTableDynamicPointAlarming : public CtiMemDBObject
{
protected:

    LONG _pointID;
    UINT _alarmCondition;       // 0-31     Telling us which alarm this represents  Directly tied to point type & found in class CtiTablePointAlarming
    UINT _categoryID;           // 0-255    Indicates AlarmCategory.AlarmCategoryID and ties over to Notification Group!
    CtiTime _alarmTime;    // DateTime of the alarm.
    string _action;
    string _description;
    UINT _tags;                 // Tags indicating ONLY Active (0x8000000) and/or Acknowledged (0x4000000) for this CONDITION
    UINT _logID;                // SystemLog row in where this alarm was written

    INT         _soe;
    INT         _logType;
    string   _user;

private:

public:

    CtiTableDynamicPointAlarming();
    CtiTableDynamicPointAlarming(const CtiTableDynamicPointAlarming& aRef);
    virtual ~CtiTableDynamicPointAlarming();

    CtiTableDynamicPointAlarming& operator=(const CtiTableDynamicPointAlarming& aRef);
    virtual int operator==(const CtiTableDynamicPointAlarming&) const;

    static string getTableName();

    RWDBStatus Insert(RWDBConnection &conn);
    RWDBStatus Update(RWDBConnection &conn);

    virtual RWDBStatus Insert();
    virtual RWDBStatus Update();
    virtual RWDBStatus Restore();
    virtual RWDBStatus Delete();

    static RWDBStatus Delete(long pointid, int alarm_condition);

    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    void DecodeDatabaseReader(RWDBReader& rdr);

    LONG              getPointID() const;
    CtiTableDynamicPointAlarming&   setPointID(LONG id);

    UINT              getAlarmCondition() const;        // Same as Priority in the systemlog.priority column
    CtiTableDynamicPointAlarming&   setAlarmCondition(INT cnd);

    UINT              getCategoryID() const;        // Same as Priority in the systemlog.priority column
    CtiTableDynamicPointAlarming&   setCategoryID(UINT cls);

    CtiTime getAlarmTime() const;
    CtiTableDynamicPointAlarming& setAlarmTime(const CtiTime &rwt);
    CtiTime getAlarmDBTime() const;
    CtiTableDynamicPointAlarming& setAlarmDBTime(const CtiTime &rwt);

    string         getAction() const;
    CtiTableDynamicPointAlarming&   setAction(const string &str);

    string         getDescription() const;
    CtiTableDynamicPointAlarming&   setDescription(const string &str);

    UINT getTags() const;
    UINT setTags(UINT tags);
    UINT resetTags(UINT mask = 0xffffffff);

    LONG getLogID() const;
    CtiTableDynamicPointAlarming& setLogID(UINT id);

    INT getSOE() const;
    CtiTableDynamicPointAlarming& setSOE(const INT &i);

    INT getLogType() const;
    CtiTableDynamicPointAlarming& setLogType(const INT &i);

    string getUser() const;
    CtiTableDynamicPointAlarming& setUser(const string &str);

    virtual void dump();
};
#endif // #ifndef __TBL_DYN_PTALARMING_H__
