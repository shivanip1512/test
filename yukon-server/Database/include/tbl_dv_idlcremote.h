
/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_idlcremote
*
* Date:   2/25/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:18:41 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_DV_IDLCREMOTE_H__
#define __TBL_DV_IDLCREMOTE_H__

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

class IM_EX_CTIYUKONDB CtiTableDeviceIDLC : public CtiMemDBObject
{
protected:

   LONG        _deviceID;
   LONG        _address;       // IDLC Addressing, etc.
   INT         _postdelay;
   INT         _ccuAmpUseType; //FINISH!

private:

public:

   CtiTableDeviceIDLC();
   CtiTableDeviceIDLC(const CtiTableDeviceIDLC& aRef);

   virtual ~CtiTableDeviceIDLC();

   CtiTableDeviceIDLC& operator=(const CtiTableDeviceIDLC& aRef);

   LONG getAddress() const;
   void setAddress(LONG a);

   INT getAmp() const;

   INT  getPostDelay() const;
   void setPostDelay(int d);

   INT getCCUAmpUseType() const;
   CtiTableDeviceIDLC& setCCUAmpUseType( const INT aAmpUseType );

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   static RWCString getTableName();

   LONG getDeviceID() const;
   CtiTableDeviceIDLC& setDeviceID( const LONG did);

   virtual RWDBStatus Restore();
   virtual RWDBStatus Insert();
   virtual RWDBStatus Update();
   virtual RWDBStatus Delete();

};
#endif // #ifndef __TBL_DV_IDLCREMOTE_H__
