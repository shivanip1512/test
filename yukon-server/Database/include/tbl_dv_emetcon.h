
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_emetcon
*
* Date:   10/4/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_dv_emetcon.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:39 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __TBL_DV_EMETCON_H__
#define __TBL_DV_EMETCON_H__

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

#define SILVERADDRESS  0x0000000f
#define GOLDADDRESS    0x00000030

class IM_EX_CTIYUKONDB CtiTableEmetconLoadGroup : public CtiMemDBObject
{
protected:

   LONG  _deviceID;
   INT   _silver;             // Zero based once in here!
   INT   _gold;               // Zero based once in here!
   INT   _addressUsage;       // This better be a nice little mask
   INT   _relay;

   LONG  _routeID;            // the route (macro) which defines this device.


private:


public:

   CtiTableEmetconLoadGroup();

   CtiTableEmetconLoadGroup(const CtiTableEmetconLoadGroup& aRef);

   virtual ~CtiTableEmetconLoadGroup();

   CtiTableEmetconLoadGroup& operator=(const CtiTableEmetconLoadGroup& aRef);

   INT getEmetconAddress() const;

   INT  getSilver() const;

   CtiTableEmetconLoadGroup& setSilver( const INT a_silver );

   INT  getGold() const;

   CtiTableEmetconLoadGroup& setGold( const INT a_gold );

   INT  getAddressUsage() const;

   CtiTableEmetconLoadGroup& setAddressUsage( const INT a_addressUsage );

   INT  getRelay() const;

   CtiTableEmetconLoadGroup& setRelay( const INT a_relay );

   LONG  getRouteID() const;

   CtiTableEmetconLoadGroup& setRouteID( const LONG a_routeID );

   LONG  getDeviceID() const;

   CtiTableEmetconLoadGroup& setDeviceID( const LONG did );

   static RWCString getTableName();

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);
   virtual RWDBStatus Restore();
   virtual RWDBStatus Insert();
   virtual RWDBStatus Update();
   virtual RWDBStatus Delete();

};
#endif // #ifndef __TBL_DV_EMETCON_H__
