
/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_lmvcserial
*
* Date:   5/9/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_dv_lmvcserial.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:18:42 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_DV_LMVCSERIAL_H__
#define __TBL_DV_LMVCSERIAL_H__

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

#include "vcomdefs.h"

class IM_EX_CTIYUKONDB CtiTableLMGroupVersacomSerial : public CtiMemDBObject
{
protected:

   LONG     _deviceID;
   INT      _serial;
   INT      _groupID;            // DEvice ID of the group this guy belongs to, bookkeeping.
   INT      _addressUsage;       // This IS a mask 3 - 0 bits mean USCD bit fields!
   INT      _relayMask;          // This IS a mask 31 - 0 bits means 32 - 1 relays!
   LONG     _routeID;            // the route (macro) which defines this device.

private:

public:

   CtiTableLMGroupVersacomSerial();

   CtiTableLMGroupVersacomSerial(const CtiTableLMGroupVersacomSerial& aRef);

   virtual ~CtiTableLMGroupVersacomSerial();

   CtiTableLMGroupVersacomSerial& operator=(const CtiTableLMGroupVersacomSerial& aRef);

   INT  getSerial() const;

   CtiTableLMGroupVersacomSerial& setSerial( const INT a_ser );

   INT  getGroupID() const;

   CtiTableLMGroupVersacomSerial& setGroupID( const INT i );

   LONG getDeviceID() const;
   CtiTableLMGroupVersacomSerial& setDeviceID( const LONG did);

   INT  getAddressUsage() const;
   BOOL useUtilityID() const;
   BOOL useSection() const;
   BOOL useClass() const;
   BOOL useDivision() const;

   CtiTableLMGroupVersacomSerial& setAddressUsage( const INT a_addressUsage );

   INT  getRelayMask() const;
   BOOL useRelay(const INT r) const;

   CtiTableLMGroupVersacomSerial& setRelayMask( const INT a_relayMask );

   LONG  getRouteID() const;
   CtiTableLMGroupVersacomSerial& setRouteID( const LONG a_routeID );

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   static RWCString getTableName();

   virtual void DecodeDatabaseReader(RWDBReader &rdr);
   virtual RWDBStatus Restore();
   virtual RWDBStatus Insert();
   virtual RWDBStatus Update();
   virtual RWDBStatus Delete();

};
#endif // #ifndef __TBL_DV_LMVCSERIAL_H__
