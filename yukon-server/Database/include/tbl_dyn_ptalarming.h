#pragma once

#include "dllbase.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "pointdefs.h"
#include "yukon.h"
#include "row_reader.h"
#include "database_connection.h"
#include "database_util.h"
#include "row_writer.h"
#include "loggable.h"


#define CTITABLEDYNAMICPOINTALARMING_MAX_ACTION         60
#define CTITABLEDYNAMICPOINTALARMING_MAX_DESCRIPTION    120


class IM_EX_CTIYUKONDB CtiTableDynamicPointAlarming : public CtiMemDBObject, private boost::noncopyable, public Cti::RowSource
{
    LONG        _pointID;
    UINT        _alarmCondition;       // 0-31     Telling us which alarm this represents  Directly tied to point type & found in class CtiTablePointAlarming
    UINT        _categoryID;           // 0-255    Indicates AlarmCategory.AlarmCategoryID and ties over to Notification Group!
    CtiTime     _alarmTime;            // DateTime of the alarm.
    std::string _action;
    std::string _description;
    UINT _tags;                 // Tags indicating ONLY Active (0x8000000) and/or Acknowledged (0x4000000) for this CONDITION
    UINT _logID;                // SystemLog row in where this alarm was written

    INT          _soe;
    INT          _logType;
    std::string  _user;

public:

    CtiTableDynamicPointAlarming();
    virtual ~CtiTableDynamicPointAlarming();

    virtual int operator==(const CtiTableDynamicPointAlarming&) const;

    static std::string getTableName();

    bool Insert(Cti::Database::DatabaseConnection &conn);
    bool Update(Cti::Database::DatabaseConnection &conn);

    static bool Delete(long pointid, int alarm_condition);

    virtual std::string getSQLCoreStatement() const;

    void DecodeDatabaseReader(Cti::RowReader& rdr);

    LONG              getPointID() const;
    CtiTableDynamicPointAlarming&   setPointID(LONG id);

    UINT              getAlarmCondition() const;        // Same as Priority in the systemlog.priority column
    CtiTableDynamicPointAlarming&   setAlarmCondition(INT cnd);

    UINT              getCategoryID() const;        // Same as Priority in the systemlog.priority column
    CtiTableDynamicPointAlarming&   setCategoryID(UINT cls);

    CtiTime getAlarmTime() const;
    CtiTableDynamicPointAlarming& setAlarmTime(const CtiTime &newTime);
    CtiTime getAlarmDBTime() const;
    CtiTableDynamicPointAlarming& setAlarmDBTime(const CtiTime &newTime);

    std::string         getAction() const;
    CtiTableDynamicPointAlarming&   setAction(const std::string &str);

    std::string         getDescription() const;
    CtiTableDynamicPointAlarming&   setDescription(const std::string &str);

    UINT getTags() const;
    UINT setTags(UINT tags);
    UINT resetTags(UINT mask = 0xffffffff);

    LONG getLogID() const;
    CtiTableDynamicPointAlarming& setLogID(UINT id);

    INT getSOE() const;
    CtiTableDynamicPointAlarming& setSOE(const INT &i);

    INT getLogType() const;
    CtiTableDynamicPointAlarming& setLogType(const INT &i);

    std::string getUser() const;
    CtiTableDynamicPointAlarming& setUser(const std::string &str);

    virtual std::string toString() const override;

    void fillRowWriter(Cti::RowWriter& writer) const override;

    static std::array<Cti::Database::ColumnDefinition, 11> getTempTableSchema();
};

