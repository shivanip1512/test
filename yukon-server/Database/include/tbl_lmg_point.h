#ifndef __TBL_LMG_POINT_H__
#define __TBL_LMG_POINT_H__


#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>


#include "yukon.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "dllbase.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "row_reader.h"


class IM_EX_CTIYUKONDB CtiTablePointGroup : public CtiMemDBObject
{
protected:

    LONG _lmGroupId;
    LONG _controlDevice;
    LONG _controlPoint;
    LONG _controlStartRawState;

    std::string _rawstate[2];             // These are the state strings.

private:

public:

    CtiTablePointGroup();
    CtiTablePointGroup(const CtiTablePointGroup& aRef);
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

#endif // #ifndef __TBL_LMG_POINT_H__
