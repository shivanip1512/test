#ifndef __TBL_DV_DNP_H__
#define __TBL_DV_DNP_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_dnp
*
* Date:   2002-aug-27
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/08/29 16:31:12 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)

#include <rw/db/reader.h>
#include <rw/cstring.h>
#include <limits.h>
#include <rw/db/nullind.h>
#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/datetime.h>
#include <rw/rwtime.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>
#include <windows.h>

#include "types.h"
#include "logger.h"
#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "desolvers.h"
#include "yukon.h"

class IM_EX_CTIYUKONDB CtiTableDeviceDNP : public CtiMemDBObject
{
protected:

   LONG     _deviceID;
   LONG     _masterAddress,
            _slaveAddress;
   INT      _postdelay;

private:

public:

   CtiTableDeviceDNP();
   CtiTableDeviceDNP(const CtiTableDeviceDNP& aRef);

   virtual ~CtiTableDeviceDNP();

   CtiTableDeviceDNP& operator=(const CtiTableDeviceDNP& aRef);

   LONG getMasterAddress() const;
   void setMasterAddress(LONG a);

   LONG getSlaveAddress() const;
   void setSlaveAddress(LONG a);

   INT  getPostDelay() const;
   void setPostDelay(int d);

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   static RWCString getTableName();

   LONG getDeviceID() const;
   CtiTableDeviceDNP& setDeviceID(const LONG did);

   virtual RWDBStatus Restore();
   virtual RWDBStatus Insert();
   virtual RWDBStatus Update();
   virtual RWDBStatus Delete();

};
#endif // #ifndef __TBL_DV_DNP_H__
