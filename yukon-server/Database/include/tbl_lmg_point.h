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
