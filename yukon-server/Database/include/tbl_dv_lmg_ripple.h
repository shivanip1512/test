
#pragma warning( disable : 4786)
#ifndef __TBL_DV_LMG_RIPPLE_H__
#define __TBL_DV_LMG_RIPPLE_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_lmg_ripple
*
* Class:  CtiTableRippleLoadGroup
* Date:   9/25/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_dv_lmg_ripple.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:58:14 $
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

class IM_EX_CTIYUKONDB CtiTableRippleLoadGroup : public CtiMemDBObject
{
protected:

   LONG           _deviceID;
   RWCString      _controlBits;     // The control command transmitted to this group.
   RWCString      _restoreBits;     // The restore command transmitted to this group.
   LONG           _routeID;         // the route
   LONG           _shedTime;

private:

public:

   CtiTableRippleLoadGroup();

   CtiTableRippleLoadGroup(const CtiTableRippleLoadGroup& aRef);

   virtual ~CtiTableRippleLoadGroup();

   CtiTableRippleLoadGroup& operator=(const CtiTableRippleLoadGroup& aRef);

   LONG  getRouteID() const;

   CtiTableRippleLoadGroup& setRouteID( const LONG a_routeID );

   RWCString getControlBits() const;
   BYTE  getControlBit(INT i);

   CtiTableRippleLoadGroup& setControlBits( const RWCString str );
   CtiTableRippleLoadGroup& setControlBit( INT pos, const BYTE ch );

   RWCString getRestoreBits() const;
   BYTE getRestoreBit(INT i);

   CtiTableRippleLoadGroup& setRestoreBits( const RWCString str );
   CtiTableRippleLoadGroup& setRestoreBit( INT pos, const BYTE ch );

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   LONG getDeviceID() const;
   LONG getShedTime() const;

   static RWCString getTableName();

   CtiTableRippleLoadGroup& setShedTime( const LONG stm);
   CtiTableRippleLoadGroup& setDeviceID( const LONG did);

   //corey's original .h has no decodedatabasereader()...
   void DecodeDatabaseReader( RWDBReader& rdr );
   virtual RWDBStatus Restore();
   virtual RWDBStatus Insert();
   virtual RWDBStatus Update();
   virtual RWDBStatus Delete();

   bool copyMessage(BYTE *bptr, bool shed) const;
};
#endif // #ifndef __TBL_DV_LMG_RIPPLE_H__
