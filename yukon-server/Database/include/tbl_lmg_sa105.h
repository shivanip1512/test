
/*-----------------------------------------------------------------------------*
*
* File:   tbl_lmg_sa105
*
* Class:  CtiTableSA105LoadGroup
* Date:   2/3/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/02/17 15:08:03 $
* HISTORY      :
* $Log: tbl_lmg_sa105.h,v $
* Revision 1.1  2004/02/17 15:08:03  cplender
* New files for GRE/SA support
*
*
* Copyright (c) 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_LMG_SA105_H__
#define __TBL_LMG_SA105_H__

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


class IM_EX_CTIYUKONDB CtiTableSA105LoadGroup : public CtiMemDBObject
{
protected:

    LONG _lmGroupId;
    LONG _routeId;

    RWCString _operationalAddress;
    int _function;

private:

    public:

    CtiTableSA105LoadGroup();
    CtiTableSA105LoadGroup(const CtiTableSA105LoadGroup& aRef);

    virtual ~CtiTableSA105LoadGroup();

    CtiTableSA105LoadGroup& operator=(const CtiTableSA105LoadGroup& aRef);

    LONG getLmGroupId() const;
    LONG getRouteId() const;
    RWCString getOperationalAddress() const;          // bitmask for functions to operate upon bit 0 is function 1.  Bit 3 is function 4.
    int getFunction() const;

    CtiTableSA105LoadGroup& setLmGroupId(LONG newVal);
    CtiTableSA105LoadGroup& setRouteId(LONG newVal);
    CtiTableSA105LoadGroup& setOperationalAddress(RWCString newVal);
    CtiTableSA105LoadGroup& setFunction(int newVal);          // bitmask for functions to operate upon bit 0 is function 1.  Bit 3 is function 4.

    static RWCString getTableName();

    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);
    virtual RWDBStatus Restore();
    virtual RWDBStatus Insert();
    virtual RWDBStatus Update();
    virtual RWDBStatus Delete();
};


#endif // #ifndef __TBL_LMG_SA105_H__
