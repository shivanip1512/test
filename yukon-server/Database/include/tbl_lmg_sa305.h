
/*-----------------------------------------------------------------------------*
*
* File:   tbl_lmg_sa305
*
* Class:  CtiTableSA305LoadGroup
* Date:   1/14/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/02/17 15:08:03 $
* HISTORY      :
* $Log: tbl_lmg_sa305.h,v $
* Revision 1.1  2004/02/17 15:08:03  cplender
* New files for GRE/SA support
*
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_LMG_SA305_H__
#define __TBL_LMG_SA305_H__

#include <windows.h>

#include <rw/db/select.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>
#include <rw/db/db.h>

#include "yukon.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "dllbase.h"
#include "dbaccess.h"
#include "resolvers.h"

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
    int _addressUsage;      // bit indicators.  Identifies which addressing components to use.


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
    int getAddressUsage() const;

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
    CtiTableSA305LoadGroup& setAddressUsage(int newVal);      //

    static RWCString getTableName();

    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);
    virtual RWDBStatus Restore();
    virtual RWDBStatus Insert();
    virtual RWDBStatus Update();
    virtual RWDBStatus Delete();

};
#endif // #ifndef __TBL_LMG_SA305_H__
