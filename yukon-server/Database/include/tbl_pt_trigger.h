#pragma once

#include "yukon.h"

#include "row_reader.h"
#include <limits.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"

class IM_EX_CTIYUKONDB CtiTablePointTrigger : public CtiMemDBObject
{
protected:

    long      _pointID;
    long      _triggerID;
    DOUBLE    _triggerDeadband;
    long      _verificationID;
    DOUBLE    _verificationDeadband;
    int       _commandTimeOut;

private:

    public:
    CtiTablePointTrigger();

    CtiTablePointTrigger(const CtiTablePointTrigger& aRef);
    virtual ~CtiTablePointTrigger();

    CtiTablePointTrigger& operator=(const CtiTablePointTrigger& aRef);

    static std::string getSQLCoreStatement(long pointID = 0);

    void DecodeDatabaseReader(Cti::RowReader &rdr);
    void dump() const;

    long             getPointID()                const;
    long             getTriggerID()              const;
    DOUBLE           getTriggerDeadband()        const;
    long             getVerificationID()         const;
    DOUBLE           getVerificationDeadband()   const;
    int              getCommandTimeOut()         const;
};

