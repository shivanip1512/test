

#pragma once

#include "ctitime.h"
#include "row_reader.h"
#include "database_connection.h"
#include "dbmemobject.h"
#include "dlldefs.h"
#include "utility.h"
#include "yukon.h"

#include <string>


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
    std::string   _text;
    std::string   _additional;
    std::string   _user;

public:

    CtiTableSignal();

    CtiTableSignal(LONG                 id,
                   const CtiTime       &tme    = CtiTime(),
                   INT                  millis = 0,
                   const std::string   &text   = std::string(),
                   const std::string   &addl   = std::string(),
                   INT                  lp     = SignalEvent,
                   INT                  lt     = GeneralLogType,
                   INT                  soe    = 0,
                   const std::string   &user   = std::string(),
                   const INT            lid    = SystemLogIdGen());

    CtiTableSignal(const CtiTableSignal& aRef);

    virtual ~CtiTableSignal();

    CtiTableSignal& operator=(const CtiTableSignal& aRef);

    unsigned long operator()(const CtiTableSignal& aRef) const;
    bool operator<(const CtiTableSignal& aRef) const;
    BOOL operator==(const CtiTableSignal& right) const;

    virtual void Insert(Cti::Database::DatabaseConnection &conn);
    virtual void Insert();
    virtual std::string getTableName() const;

    CtiTableSignal *replicate() const;

    LONG     getLogID()   const;
    LONG     getPointID() const;
    CtiTime  getTime()    const;
    INT      getMillis()  const;

    INT         getPriority() const;
    std::string getText()     const;
    std::string getUser()     const;
    INT         getSOE()      const;
    INT         getLogType()  const;
    std::string getAdditionalInfo() const;

    CtiTableSignal &setLogID(LONG id);
    CtiTableSignal &setPointID(LONG id);
    CtiTableSignal &setTime(const CtiTime rwt);
    CtiTableSignal &setMillis(INT millis);
    CtiTableSignal &setPriority(INT cls);
    CtiTableSignal &setText(const std::string &str);
    CtiTableSignal &setUser(const std::string &str);
    CtiTableSignal &setSOE(const INT &i);
    CtiTableSignal &setLogType(const INT &i);
    CtiTableSignal &setAdditionalInfo(const std::string &str);

    virtual void dump() const;

};

