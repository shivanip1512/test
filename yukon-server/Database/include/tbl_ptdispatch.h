#pragma once

#include "dllbase.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "pointdefs.h"
#include "yukon.h"
#include "ctitime.h"
#include "row_reader.h"
#include "database_connection.h"
#include "database_util.h"
#include "row_writer.h"
#include "loggable.h"

#include <limits.h>


class IM_EX_CTIYUKONDB CtiTablePointDispatch : public CtiMemDBObject, public Cti::RowSource
{
    LONG           _pointID;

    CtiTime        _timeStamp;
    INT            _timeStampMillis;
    DOUBLE         _value;
    UINT           _quality;

    CtiTime        _nextArchiveTime;
    UINT           _tags;

public:

    typedef CtiMemDBObject Inherited;

    CtiTablePointDispatch();

    CtiTablePointDispatch(LONG pointid,
                          DOUBLE value = 0,
                          UINT quality = UnintializedQuality,
                          const CtiTime& timestamp = CtiTime(),
                          UINT millis = 0 );

    virtual ~CtiTablePointDispatch();

    virtual bool operator==(const CtiTablePointDispatch&) const;

    static std::string getTableName();

    virtual bool Restore();

    static std::string getSQLCoreStatement(long id = 0);

    void DecodeDatabaseReader(Cti::RowReader& rdr);

    LONG getPointID() const;
    CtiTablePointDispatch& setPointID(LONG pointID);

    const CtiTime& getTimeStamp() const;
    CtiTablePointDispatch& setTimeStamp(const CtiTime& timestamp);

    INT getTimeStampMillis() const;
    CtiTablePointDispatch& setTimeStampMillis(INT millis);

    UINT getQuality() const;
    CtiTablePointDispatch& setQuality(UINT quality);

    DOUBLE getValue() const;
    CtiTablePointDispatch& setValue(DOUBLE value);

    const CtiTime& getNextArchiveTime() const;
    CtiTablePointDispatch& setNextArchiveTime(const CtiTime& timestamp);

    ULONG getLastAlarmLogID() const;

    UINT getTags() const;
    UINT setTags(UINT tags);
    UINT resetTags(UINT mask = 0xffffffff);

    virtual std::string toString() const override;

    void fillRowWriter(Cti::RowWriter& writer) const override;

    static std::array<Cti::Database::ColumnDefinition, 7> getTempTableSchema();
};
