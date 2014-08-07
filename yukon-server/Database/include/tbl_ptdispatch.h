#pragma once

#include <limits.h>


#include "ctibase.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "pointdefs.h"
#include "yukon.h"
#include "ctitime.h"
#include "row_reader.h"
#include "database_connection.h"


class IM_EX_CTIYUKONDB CtiTablePointDispatch : public CtiMemDBObject, private boost::noncopyable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTablePointDispatch(const CtiTablePointDispatch&);
    CtiTablePointDispatch& operator=(const CtiTablePointDispatch&);

protected:

    LONG           _pointID;

    CtiTime        _timeStamp;
    INT            _timeStampMillis;
    DOUBLE         _value;
    UINT           _quality;

    CtiTime        _nextArchiveTime;
    UINT           _tags;

private:

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

    virtual void dump();
};
