
/*-----------------------------------------------------------------------------*
*
* File:   tbl_lmg_sa205
*
* Class:  CtiTableSA205LoadGroup
* Date:   2/3/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/02/17 15:08:03 $
* HISTORY      :
* $Log: tbl_lmg_sa205.h,v $
* Revision 1.1  2004/02/17 15:08:03  cplender
* New files for GRE/SA support
*
*
* Copyright (c) 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_LMG_SA205_H__
#define __TBL_LMG_SA205_H__

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


class IM_EX_CTIYUKONDB CtiTableSA205LoadGroup : public CtiMemDBObject
{
protected:

    LONG _lmGroupId;
    LONG _routeId;

    RWCString _operationalAddress;
    int _function;

private:

public:

    CtiTableSA205LoadGroup();
    CtiTableSA205LoadGroup(const CtiTableSA205LoadGroup& aRef);

    virtual ~CtiTableSA205LoadGroup();

    CtiTableSA205LoadGroup& operator=(const CtiTableSA205LoadGroup& aRef);

    LONG getLmGroupId() const;
    LONG getRouteId() const;
    RWCString getOperationalAddress() const;          // bitmask for functions to operate upon bit 0 is function 1.  Bit 3 is function 4.
    int getFunction() const;          //
    int getAddressUsage() const;

    CtiTableSA205LoadGroup& setLmGroupId(LONG newVal);
    CtiTableSA205LoadGroup& setRouteId(LONG newVal);
    CtiTableSA205LoadGroup& setOperationalAddress(RWCString newVal);
    CtiTableSA205LoadGroup& setFunction(int newVal);          // bitmask for functions to operate upon bit 0 is function 1.  Bit 3 is function 4.
    CtiTableSA205LoadGroup& setAddressUsage(int newVal);          //

    static RWCString getTableName();

    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);
    virtual RWDBStatus Restore();
    virtual RWDBStatus Insert();
    virtual RWDBStatus Update();
    virtual RWDBStatus Delete();
};
#endif // #ifndef __TBL_LMG_SA205_H__
