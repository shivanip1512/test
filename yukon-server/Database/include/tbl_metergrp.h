
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_metergrp
*
* Date:   8/18/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_metergrp.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:58:15 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __TBL_METERGRP_H__
#define __TBL_METERGRP_H__

#include <rw\rwtime.h>
#include <rw\cstring.h>
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



class IM_EX_CTIYUKONDB CtiTableDeviceMeterGroup : public CtiMemDBObject
{
protected:

   LONG        _deviceID;
   RWCString   _collectionGroup;
   RWCString   _testCollectionGroup;
   RWCString   _meterNumber;     //did Ryan add this to his schtuff?
   RWCString   _billingGroup;

public:

   CtiTableDeviceMeterGroup();

   CtiTableDeviceMeterGroup(const CtiTableDeviceMeterGroup& aRef);

   virtual ~CtiTableDeviceMeterGroup();

   CtiTableDeviceMeterGroup& operator=(const CtiTableDeviceMeterGroup& aRef);

   RWCString getCollectionGroup() const;
   CtiTableDeviceMeterGroup& setCollectionGroup( const RWCString &aCycleGroup );

   RWCString getTestCollectionGroup() const;
   CtiTableDeviceMeterGroup& setTestCollectionGroup( const RWCString &aAreaCodeGroup );

   RWCString getBillingGroup() const;
   CtiTableDeviceMeterGroup& setBillingGroup( const RWCString &cGroup );

   RWCString getMeterNumber() const;
   CtiTableDeviceMeterGroup& setMeterNumber( const RWCString &mNum );

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   LONG getDeviceID() const;
   CtiTableDeviceMeterGroup& setDeviceID( const LONG did );
   static RWCString getTableName();

   virtual RWDBStatus Restore();
   virtual RWDBStatus Update();
   virtual RWDBStatus Insert();
   virtual RWDBStatus Delete();

};
#endif // #ifndef __TBL_METERGRP_H__
