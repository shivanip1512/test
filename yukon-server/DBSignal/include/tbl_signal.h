/*-----------------------------------------------------------------------------*
*
* File:   tbl_signal
*
* Date:   4/12/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DBSIGNAL/INCLUDE/tbl_signal.h-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2007/03/22 17:22:40 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_SIGNAL_H__
#define __TBL_SIGNAL_H__

#include <windows.h>
#include "ctitime.h"
#include <rw/db/datetime.h>
#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>


#include "dbmemobject.h"
#include "dlldefs.h"
#include "pointdefs.h"
#include "utility.h"
#include "yukon.h"

class IM_EX_SIGNAL CtiTableSignal : public CtiMemDBObject
{
protected:

    LONG     _logID;
    LONG     _pointID;
    CtiTime  _time;
    INT      _millis;
    INT      _soe;
    INT      _logType;
    INT      _logPriority;
    string   _text;
    string   _additional;
    string   _user;


private:

    public:

    CtiTableSignal();

    CtiTableSignal(LONG            id,
                   const CtiTime  &tme    = CtiTime(),
                   INT             millis = 0,
                   const string   &text   = string(),
                   const string   &addl   = string(),
                   INT             lp     = SignalEvent,
                   INT             lt     = GeneralLogType,
                   INT             soe    = 0,
                   const string   &user   = string(""),
                   const INT       lid    = SystemLogIdGen());

    CtiTableSignal(const CtiTableSignal& aRef);

    virtual ~CtiTableSignal();

    CtiTableSignal& operator=(const CtiTableSignal& aRef);

    unsigned long operator()(const CtiTableSignal& aRef) const;
    bool operator<(const CtiTableSignal& aRef) const;
    BOOL operator==(const CtiTableSignal& right) const;

    void DecodeDatabaseReader( RWDBReader& rdr );
    virtual void Insert(RWDBConnection &conn);
    virtual void Insert();
    virtual void Restore();
    virtual void Update();
    virtual void Delete();
    virtual string getTableName() const;

    CtiTableSignal *replicate() const;

    LONG     getLogID()   const;
    LONG     getPointID() const;
    CtiTime  getTime()    const;
    INT      getMillis()  const;

    INT      getPriority() const;
    string   getText()     const;
    string   getUser()     const;
    INT      getSOE()      const;
    INT      getLogType()  const;
    string   getAdditionalInfo() const;

    CtiTableSignal &setLogID(LONG id);
    CtiTableSignal &setPointID(LONG id);
    CtiTableSignal &setTime(const CtiTime rwt);
    CtiTableSignal &setMillis(INT millis);
    CtiTableSignal &setPriority(INT cls);
    CtiTableSignal &setText(const string &str);
    CtiTableSignal &setUser(const string &str);
    CtiTableSignal &setSOE(const INT &i);
    CtiTableSignal &setLogType(const INT &i);
    CtiTableSignal &setAdditionalInfo(const string &str);

    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    static void getSQLMaxID(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector, LONG id);
    virtual void dump() const;

};
#endif // #ifndef __TBL_SIGNAL_H__
