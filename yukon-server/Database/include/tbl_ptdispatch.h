#pragma once

#include "ctibase.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "pointdefs.h"
#include "yukon.h"
#include "ctitime.h"
#include "row_reader.h"
#include "database_connection.h"
#include "loggable.h"

#include <limits.h>


class IM_EX_CTIYUKONDB CtiTablePointDispatch : public CtiMemDBObject, private boost::noncopyable, public Cti::Loggable
{
    LONG           _pointID;

    CtiTime        _timeStamp;
    INT            _timeStampMillis;
    DOUBLE         _value;
    UINT           _quality;

    CtiTime        _nextArchiveTime;
    UINT           _tags;

    bool _pointIdInvalid;

    void initInserter (Cti::Database::DatabaseWriter &inserter) const;
    void initUpdater  (Cti::Database::DatabaseWriter &updater) const;

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

    bool writeToDB(Cti::Database::DatabaseConnection &conn);

    bool isPointIdInvalid() const;

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

    UINT getStaleCount() const;

    virtual std::string toString() const override;
};
