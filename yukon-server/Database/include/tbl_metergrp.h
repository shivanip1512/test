/*-----------------------------------------------------------------------------*
*
* File:   tbl_metergrp
*
* Date:   8/18/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_metergrp.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2008/10/14 21:25:45 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)

#ifndef __TBL_METERGRP_H__
#define __TBL_METERGRP_H__


#include <rw/db/select.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/reader.h>
#include <limits.h>
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
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"



class IM_EX_CTIYUKONDB CtiTableDeviceMeterGroup : public CtiMemDBObject
{
protected:

   LONG     _deviceID;
   string   _meterNumber;

   static string getTableName();
   long getDeviceID() const;

public:

   CtiTableDeviceMeterGroup();

   CtiTableDeviceMeterGroup(const CtiTableDeviceMeterGroup& aRef);

   virtual ~CtiTableDeviceMeterGroup();

   CtiTableDeviceMeterGroup& operator=(const CtiTableDeviceMeterGroup& aRef);

   string getMeterNumber() const;
   CtiTableDeviceMeterGroup& setMeterNumber( const string &mNum );

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   virtual RWDBStatus Restore();
   virtual RWDBStatus Update();
   virtual RWDBStatus Insert();
   virtual RWDBStatus Delete();
};

#endif // #ifndef __TBL_METERGRP_H__
