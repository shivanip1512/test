
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_carrier
*
* Date:   8/18/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_carrier.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:18:40 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __TBL_CARRIER_H__
#define __TBL_CARRIER_H__

#include <rw/db/reader.h>
#include <rw\cstring.h>
#include <limits.h>
#include <rw/db/nullind.h>
#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/datetime.h>
#include <rw/rwtime.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"


class IM_EX_CTIYUKONDB CtiTableDeviceCarrier : public CtiMemDBObject
{
protected:

   LONG     _deviceID;
   INT      _address;

public:

   CtiTableDeviceCarrier();

   CtiTableDeviceCarrier(const CtiTableDeviceCarrier& aRef);

   virtual ~CtiTableDeviceCarrier();

   CtiTableDeviceCarrier& operator=(const CtiTableDeviceCarrier& aRef);

   INT  getAddress() const;
   CtiTableDeviceCarrier& setAddress( const INT aAddress );

   LONG getDeviceID() const;
   CtiTableDeviceCarrier& setDeviceID( const LONG did );

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   static RWCString getTableName();

   virtual void DecodeDatabaseReader(RWDBReader &rdr);
   virtual RWDBStatus Restore();
   virtual RWDBStatus Insert();
   virtual RWDBStatus Update();
   virtual RWDBStatus Delete();
};

#endif // #ifndef __TBL_CARRIER_H__
