#pragma once

#include "row_reader.h"

#include <limits.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include <boost/shared_ptr.hpp>

#include <map>
#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "yukon.h"

class IM_EX_CTIYUKONDB CtiTablePointProperty : public CtiMemDBObject
{
protected:

private:
    long         _pointID;
    unsigned int _propertyID;
    float        _floatAttributeValue;

    CtiTablePointProperty(const CtiTablePointProperty& aRef);

    CtiTablePointProperty& operator=(const CtiTablePointProperty& aRef);

public:

    CtiTablePointProperty(Cti::RowReader &rdr);
    CtiTablePointProperty(long pointID, unsigned int propertyID, float attributeValue);
    virtual ~CtiTablePointProperty();

    bool operator<(const CtiTablePointProperty &rhs) const;

    long         getPointID()    const;
    unsigned int getPropertyID() const;

    float  getFloatProperty() const;
    int    getIntProperty  () const;

    static std::string getSQLCoreStatement();

    void          dump() const;
    static std::string getTableName();

    enum
    {
        STALE_ALARM_TIME = 1,
        STALE_UPDATE_TYPE,

        ARCHIVE_ON_TIMER, //The number of this does not matter as this is not stored in the database
    };

    enum
    {
        UPDATE_ALWAYS = 1,
        UPDATE_ON_CHANGE,
    };
};

typedef boost::shared_ptr< CtiTablePointProperty > CtiTablePointPropertySPtr;
