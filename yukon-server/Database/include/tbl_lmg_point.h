#pragma once

#include <windows.h>


#include "yukon.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "dllbase.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "row_reader.h"


class IM_EX_CTIYUKONDB CtiTablePointGroup : public CtiMemDBObject, private boost::noncopyable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTablePointGroup(const CtiTablePointGroup&);
    CtiTablePointGroup& operator=(const CtiTablePointGroup&);

protected:

    LONG _lmGroupId;
    LONG _controlDevice;
    LONG _controlPoint;
    LONG _controlStartRawState;

    std::string _rawstate[2];             // These are the state strings.

public:

    CtiTablePointGroup();
    virtual ~CtiTablePointGroup();

    LONG getLmGroupId() const;
    LONG getControlDevice() const;
    LONG getControlPoint() const;
    LONG getControlStartRawState() const;
    std::string getControlStartString() const;
    std::string getControlStopString() const;

    static std::string getTableName( void );

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
};
