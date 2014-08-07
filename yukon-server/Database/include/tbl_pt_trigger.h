#pragma once

#include "yukon.h"

#include "row_reader.h"
#include <limits.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"

class IM_EX_CTIYUKONDB CtiTablePointTrigger : public CtiMemDBObject, private boost::noncopyable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTablePointTrigger(const CtiTablePointTrigger&);
    CtiTablePointTrigger& operator=(const CtiTablePointTrigger&);

protected:

    long      _pointID;
    long      _triggerID;
    DOUBLE    _triggerDeadband;
    long      _verificationID;
    DOUBLE    _verificationDeadband;
    int       _commandTimeOut;

public:

    CtiTablePointTrigger();
    virtual ~CtiTablePointTrigger();

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

