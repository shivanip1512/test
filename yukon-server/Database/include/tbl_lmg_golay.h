#ifndef __TBL_LMG_GOLAY_H__
#define __TBL_LMG_GOLAY_H__


#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>

#include "row_reader.h"

#include "yukon.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "dllbase.h"
#include "dbaccess.h"
#include "resolvers.h"


class IM_EX_CTIYUKONDB CtiTableGolayGroup : public CtiMemDBObject
{
protected:

    LONG _lmGroupId;
    LONG _routeId;

    std::string _operationalAddress;
    int _nominalTimeout;        // Switch is hardcoded to be off for this duration in seconds!
    int _virtualTimeout;        // Group is desired to control for this duration in seconds.  If these are not equal multiple control messages must be sent to make it occur (master cycle-like)

public:

    CtiTableGolayGroup();
    CtiTableGolayGroup(const CtiTableGolayGroup& aRef);
    virtual ~CtiTableGolayGroup();

    CtiTableGolayGroup& operator=(const CtiTableGolayGroup& aRef);

    LONG getLmGroupId() const;
    LONG getRouteId() const;
    std::string getOperationalAddress() const;          // bitmask for functions to operate upon bit 0 is function 1.  Bit 3 is function 4.
    int getFunction() const;
    int getNominalTimeout() const;
    int getVirtualTimeout() const;

    CtiTableGolayGroup& setLmGroupId(LONG newVal);
    CtiTableGolayGroup& setRouteId(LONG newVal);
    CtiTableGolayGroup& setOperationalAddress(std::string newVal);
    CtiTableGolayGroup& setFunction(int newVal);          // bitmask for functions to operate upon bit 0 is function 1.  Bit 3 is function 4.
    CtiTableGolayGroup& setNominalTimeout(int newVal);
    CtiTableGolayGroup& setVirtualTimeout(int newVal);

    static std::string getTableName();

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
};
#endif // #ifndef __TBL_LMG_GOLAY_H__
