/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_versacom
*
* Date:   12/14/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:58:14 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_DV_VERSACOM_H__
#define __TBL_DV_VERSACOM_H__

#include <rw/db/select.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>
#include <rw\cstring.h>
#include <limits.h>
#include <rw/db/nullind.h>
#include <rw/db/db.h>
#include <rw/db/datetime.h>
#include <rw/rwtime.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "yukon.h"
#include "vcomdefs.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"

class IM_EX_CTIYUKONDB CtiTableVersacomLoadGroup : public CtiMemDBObject
{
protected:

   LONG     _deviceID;
   INT      _utilityID;           // 1-256
   INT      _section;             // 1-256
   INT      _class;               // 16-bit bit mask
   INT      _division;            // 16-bit bit mask
   INT      _serial;

   INT      _addressUsage;       // This IS a mask 3 - 0 bits mean USCD bit fields!
   INT      _relayMask;              // This IS a mask 31 - 0 bits means 32 - 1 relays!

   LONG     _routeID;            // the route (macro) which defines this device.

private:

public:
   CtiTableVersacomLoadGroup();

   CtiTableVersacomLoadGroup(const CtiTableVersacomLoadGroup& aRef);

   virtual ~CtiTableVersacomLoadGroup();

   CtiTableVersacomLoadGroup& operator=(const CtiTableVersacomLoadGroup& aRef);

   INT  getSerial() const;
   CtiTableVersacomLoadGroup& setSerial( const INT a_ser );

   INT  getUtilityID() const;
   CtiTableVersacomLoadGroup& setUtilityID( const INT a_uid );
   INT  getSection() const;
   CtiTableVersacomLoadGroup& setSection( const INT aSection );

   INT  getClass() const;
   CtiTableVersacomLoadGroup& setClass( const INT aClass );

   INT  getDivision() const;
   CtiTableVersacomLoadGroup& setDivision( const INT aDivision );

   INT  getAddressUsage() const;
   BOOL useUtilityID() const;
   BOOL useSection() const;
   BOOL useClass() const;
   BOOL useDivision() const;

   LONG getDeviceID() const;
   CtiTableVersacomLoadGroup& setDeviceID( const LONG did );

   CtiTableVersacomLoadGroup& setAddressUsage( const INT a_addressUsage );

   INT  getRelayMask() const;
   BOOL useRelay(const INT r) const;
   CtiTableVersacomLoadGroup& setRelayMask( const INT a_relayMask );

   LONG  getRouteID() const;
   CtiTableVersacomLoadGroup& setRouteID( const LONG a_routeID );

   static RWCString getTableName();

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   virtual void DecodeDatabaseReader(RWDBReader &rdr);
   virtual RWDBStatus Restore();
   virtual RWDBStatus Insert();
   virtual RWDBStatus Update();
   virtual RWDBStatus Delete();

};
#endif // #ifndef __TBL_DV_VERSACOM_H__
