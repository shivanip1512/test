
#pragma warning( disable : 4786)
#ifndef __TBL_DV_CBC_H__
#define __TBL_DV_CBC_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_cbc
*
* Class:  CtiTableDeviceCBC
* Date:   8/24/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_dv_cbc.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:58:13 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

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

class IM_EX_CTIYUKONDB CtiTableDeviceCBC : public CtiMemDBObject
{

protected:

   LONG     _deviceID;
   INT      _serial;             // Versacom Serial number
   LONG     _routeID;            // the route (macro) which defines this device.

private:

public:

   CtiTableDeviceCBC();

   CtiTableDeviceCBC(const CtiTableDeviceCBC& aRef);

   virtual ~CtiTableDeviceCBC();

   CtiTableDeviceCBC& operator=(const CtiTableDeviceCBC& aRef);

   INT  getSerial() const;

   CtiTableDeviceCBC& setSerial( const INT a_ser );

   LONG  getRouteID() const;

   CtiTableDeviceCBC& setRouteID( const LONG a_routeID );

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   static RWCString getTableName();

   LONG getDeviceID() const;

   CtiTableDeviceCBC& setDeviceID( const LONG did);

   virtual RWDBStatus Restore();
   virtual RWDBStatus Insert();
   virtual RWDBStatus Update();
   virtual RWDBStatus Delete();
};
#endif // #ifndef __TBL_DV_CBC_H__
