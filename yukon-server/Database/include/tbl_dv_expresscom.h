
#pragma warning( disable : 4786)
#ifndef __TBL_DV_EXPRESSCOM_H__
#define __TBL_DV_EXPRESSCOM_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_expresscom
*
* Class:  CtiTableExpresscomLoadGroup
* Date:   9/23/2002
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/12/20 17:16:08 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <windows.h>

#include <rw/db/select.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>
#include <rw/db/nullind.h>
#include <rw/db/db.h>
#include <rw/db/datetime.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "yukon.h"
#include "vcomdefs.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "dllbase.h"
#include "dbaccess.h"
#include "resolvers.h"

class IM_EX_CTIYUKONDB CtiTableExpresscomLoadGroup : public CtiMemDBObject
{
protected:

    LONG _lmGroupId;
    LONG _routeId;

    UINT _serial;                   // 1 - 4294967295
    USHORT _serviceProvider;        // 1 - 65534
    USHORT _geo;                    // 1 - 65534
    USHORT _substation;             // 1 - 65534
    USHORT _feeder;                 // 1 - 65534            // Bit-wise or'd against switch config in FW.
    UINT _zip;                      // 1 - 16777214
    USHORT _uda;                    // 1 - 65534            // User Defined Address
    UCHAR _program;                 // 1 - 65534
    UCHAR _splinter;

    USHORT _addressUsage;             // bit indicators.  LSB is SPID.  No bits set indicates serial.
    USHORT _loads;             // 0 indicates all loads.  Otherwise, one load per message!

private:

public:

    typedef CtiMemDBObject Inherited;

    CtiTableExpresscomLoadGroup();
    CtiTableExpresscomLoadGroup(const CtiTableExpresscomLoadGroup& aRef);
    virtual ~CtiTableExpresscomLoadGroup();

    CtiTableExpresscomLoadGroup& operator=(const CtiTableExpresscomLoadGroup& aRef);

    LONG getId() const;
    CtiTableExpresscomLoadGroup& setId(LONG id);
    LONG getRouteId() const;
    CtiTableExpresscomLoadGroup& setRouteId(LONG id);
    UINT getSerial() const;
    CtiTableExpresscomLoadGroup&  setSerial(UINT sid);
    USHORT getServiceProvider() const;
    CtiTableExpresscomLoadGroup& setServiceProvider(USHORT spid);
    USHORT getGeo() const;
    CtiTableExpresscomLoadGroup& setGeo(USHORT geo);
    USHORT getSubstation() const;
    CtiTableExpresscomLoadGroup& setSubstation(USHORT sub);
    USHORT getFeeder() const;
    CtiTableExpresscomLoadGroup& setFeeder(USHORT feed);
    UINT getZip() const;
    CtiTableExpresscomLoadGroup& setZip(UINT zip);
    USHORT getUda() const;
    CtiTableExpresscomLoadGroup& setUda(USHORT user);
    UCHAR getProgram() const;
    CtiTableExpresscomLoadGroup& setProgram(UCHAR prog);
    UCHAR getSplinter() const;
    CtiTableExpresscomLoadGroup& setSplinter(UCHAR splinter);

    USHORT getAddressUsage() const;
    CtiTableExpresscomLoadGroup& setAddressUsage(USHORT addrussage);
    BYTE getLoadMask() const;
    CtiTableExpresscomLoadGroup& setLoadMask(BYTE load);
    BOOL useRelay(const INT r) const;

    static string getTableName();

    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);
    virtual RWDBStatus Restore();
    virtual RWDBStatus Insert();
    virtual RWDBStatus Update();
    virtual RWDBStatus Delete();

};
#endif // #ifndef __TBL_DV_EXPRESSCOM_H__
