/*-----------------------------------------------------------------------------*
*
* File:   tbl_signal
*
* Date:   4/12/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DBSIGNAL/INCLUDE/tbl_signal.h-arc  $
* REVISION     :  $Revision: 1.7.20.1 $
* DATE         :  $Date: 2008/11/13 17:23:49 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_SIGNAL_H__
#define __TBL_SIGNAL_H__


#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>
#include "ctitime.h"
#include "row_reader.h"
#include "database_connection.h"


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

    void DecodeDatabaseReader( Cti::RowReader& rdr );
    virtual void Insert(Cti::Database::DatabaseConnection &conn);
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

    virtual void dump() const;

};
#endif // #ifndef __TBL_SIGNAL_H__
