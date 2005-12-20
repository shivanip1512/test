#ifndef __TBL_DIRECT_H__
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_direct
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_direct.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/12/20 17:16:07 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#define __TBL_DIRECT_H__

#include <rw/db/reader.h>
#include <limits.h>
#include <rw/db/nullind.h>
#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/datetime.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dbmemobject.h"
#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"


class IM_EX_CTIYUKONDB CtiTableDeviceDirectComm : public CtiMemDBObject
{

protected:

   LONG           _deviceID;
   RWMutexLock    DirectCommMux;
   LONG           PortID;

public:

   CtiTableDeviceDirectComm();
   virtual ~CtiTableDeviceDirectComm();
   CtiTableDeviceDirectComm(const CtiTableDeviceDirectComm &aRef);
   CtiTableDeviceDirectComm& operator=(const CtiTableDeviceDirectComm &aRef);

   LONG getPortID() const;
   void setPortID(LONG id);

   LONG getDeviceID() const;
   CtiTableDeviceDirectComm& setDeviceID( const LONG did);

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);
   static string getTableName();

   virtual RWDBStatus Restore();
   virtual RWDBStatus Insert();
   virtual RWDBStatus Update();
   virtual RWDBStatus Delete();
};

#endif

