
/*-----------------------------------------------------------------------------*
*
* File:   tbl_lmg_golay
*
* Class:  CtiTableGolayGroup
* Date:   2/4/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/02/17 15:08:03 $
* HISTORY      :
* $Log: tbl_lmg_golay.h,v $
* Revision 1.1  2004/02/17 15:08:03  cplender
* New files for GRE/SA support
*
*
* Copyright (c) 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_LMG_GOLAY_H__
#define __TBL_LMG_GOLAY_H__

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


class IM_EX_CTIYUKONDB CtiTableGolayGroup : public CtiMemDBObject
{
protected:

    LONG _lmGroupId;
    LONG _routeId;

    RWCString _operationalAddress;
    int _nominalTimeout;        // Switch is hardcoded to be off for this duration in seconds!
    int _virtualTimeout;        // Group is desired to control for this duration in seconds.  If these are not equal multiple control messages must be sent to make it occur (master cycle-like)

public:

    CtiTableGolayGroup();
    CtiTableGolayGroup(const CtiTableGolayGroup& aRef);
    virtual ~CtiTableGolayGroup();

    CtiTableGolayGroup& operator=(const CtiTableGolayGroup& aRef);

    LONG getLmGroupId() const;
    LONG getRouteId() const;
    RWCString getOperationalAddress() const;          // bitmask for functions to operate upon bit 0 is function 1.  Bit 3 is function 4.
    int getFunction() const;
    int getNominalTimeout() const;
    int getVirtualTimeout() const;

    CtiTableGolayGroup& setLmGroupId(LONG newVal);
    CtiTableGolayGroup& setRouteId(LONG newVal);
    CtiTableGolayGroup& setOperationalAddress(RWCString newVal);
    CtiTableGolayGroup& setFunction(int newVal);          // bitmask for functions to operate upon bit 0 is function 1.  Bit 3 is function 4.
    CtiTableGolayGroup& setNominalTimeout(int newVal);
    CtiTableGolayGroup& setVirtualTimeout(int newVal);

    static RWCString getTableName();

    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);
    virtual RWDBStatus Restore();
    virtual RWDBStatus Insert();
    virtual RWDBStatus Update();
    virtual RWDBStatus Delete();

};
#endif // #ifndef __TBL_LMG_GOLAY_H__
