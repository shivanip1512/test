/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_ied
*
* Date:   2/25/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:39 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_DV_IED_H__
#define __TBL_DV_IED_H__

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

class IM_EX_CTIYUKONDB CtiTableDeviceIED : public CtiMemDBObject
{
protected:

   LONG           _deviceID;
   RWCString      _password;
   INT            _slaveAddress;

private:

public:

   CtiTableDeviceIED();

   CtiTableDeviceIED(const CtiTableDeviceIED& aRef);

   virtual ~CtiTableDeviceIED();

   CtiTableDeviceIED& operator=(const CtiTableDeviceIED& aRef);

   INT                  getSlaveAddress() const;
   INT&                 getSlaveAddress();
   CtiTableDeviceIED    setSlaveAddress(INT &aInt);

   RWCString            getPassword() const;
   RWCString&           getPassword();
   CtiTableDeviceIED    setPassword(RWCString &aStr);

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   void DecodeDatabaseReader(const INT DeviceType, RWDBReader &rdr);

   static RWCString getTableName();

   LONG getDeviceID() const;

   CtiTableDeviceIED& setDeviceID( const LONG did);

   virtual RWDBStatus Restore();
   virtual RWDBStatus Insert();
   virtual RWDBStatus Update();
   virtual RWDBStatus Delete();

};
#endif // #ifndef __TBL_DV_IED_H__
