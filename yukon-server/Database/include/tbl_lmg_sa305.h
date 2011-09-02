#pragma once

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

class IM_EX_CTIYUKONDB CtiTableSA305LoadGroup : public CtiMemDBObject
{
protected:

    LONG _lmGroupId;
    LONG _routeId;

    int _utility;           // 4 bit address
    int _group;             // 6 bit address
    int _division;          // 6 bit address
    int _substation;        // 10 bit address
    int _individual;        // 22 bit serial number.  4173802 = 3FAFEA is an all call.

    int _rateFamily;        // 3 bits
    int _rateMember;        // 4 bits
    int _hierarchy;         // 1 bit

    int _function;          // bitmask for functions to operate upon bit 0 is function 1.  Bit 3 is function 4.
    std::string _addressUsage;      // Identifies which addressing components to use.


private:

public:

    typedef CtiMemDBObject Inherited;


    CtiTableSA305LoadGroup();
    CtiTableSA305LoadGroup(const CtiTableSA305LoadGroup& aRef);

    virtual ~CtiTableSA305LoadGroup();

    CtiTableSA305LoadGroup& operator=(const CtiTableSA305LoadGroup& aRef);

    LONG getLmGroupId() const;
    LONG getRouteId() const;
    int getUtility() const;           // 4 bit address
    int getGroup() const;             // 6 bit address
    int getDivision() const;          // 6 bit address
    int getSubstation() const;        // 10 bit address
    int getIndividual() const;        // 22 bit serial number.  4173802 = 3FAFEA is an all call.
    int getRateFamily() const;        // 3 bits
    int getRateMember() const;        // 4 bits
    int getHierarchy() const;         // 1 bit
    int getFunction() const;          // bitmask for functions to operate upon bit 0 is function 1.  Bit 3 is function 4.
    std::string  getAddressUsage() const;

    CtiTableSA305LoadGroup& setLmGroupId(LONG newVal);
    CtiTableSA305LoadGroup& setRouteId(LONG newVal);
    CtiTableSA305LoadGroup& setUtility(int newVal);           // 4 bit address
    CtiTableSA305LoadGroup& setGroup(int newVal);             // 6 bit address
    CtiTableSA305LoadGroup& setDivision(int newVal);          // 6 bit address
    CtiTableSA305LoadGroup& setSubstation(int newVal);        // 10 bit address
    CtiTableSA305LoadGroup& setIndividual(int newVal);        // 22 bit serial number.  4173802 = 3FAFEA is an all call.
    CtiTableSA305LoadGroup& setRateFamily(int newVal);        // 3 bits
    CtiTableSA305LoadGroup& setRateMember(int newVal);        // 4 bits
    CtiTableSA305LoadGroup& setHierarchy(int newVal);         // 1 bit
    CtiTableSA305LoadGroup& setFunction(int newVal);          // bitmask for functions to operate upon bit 0 is function 1.  Bit 3 is function 4.
    CtiTableSA305LoadGroup& setAddressUsage(std::string  newVal);      //

    static std::string getTableName();

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
};
