/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_tappaging
*
* Date:   5/9/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_dv_tappaging.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/06/13 13:47:19 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_DV_TAPPAGING_H__
#define __TBL_DV_TAPPAGING_H__

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

class IM_EX_CTIYUKONDB CtiTableDeviceTapPaging : public CtiMemDBObject
{
protected:

   LONG           _deviceID;
   RWCString      _pagerNumber;                    // a.k.a. CAPCODE

   RWCString    _senderID;
   RWCString    _securityCode;
   RWCString    _postPath;

private:

public:

   CtiTableDeviceTapPaging(RWCString pn = RWCString());

   CtiTableDeviceTapPaging(const CtiTableDeviceTapPaging& aRef);

   virtual ~CtiTableDeviceTapPaging();

   CtiTableDeviceTapPaging& operator=(const CtiTableDeviceTapPaging& aRef);

   RWCString                  getPagerNumber() const;
   RWCString&                 getPagerNumber();
   CtiTableDeviceTapPaging&   setPagerNumber(const RWCString &aStr);

   RWCString                  getSenderID() const;
   RWCString                  getSecurityCode() const;
   RWCString                  getPOSTPath() const;


   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   LONG getDeviceID() const;
   CtiTableDeviceTapPaging& setDeviceID(const LONG did);

   static RWCString getTableName();
   virtual RWDBStatus Restore();
   virtual RWDBStatus Update();
   virtual RWDBStatus Insert();
   virtual RWDBStatus Delete();
};
#endif // #ifndef __TBL_DV_TAPPAGING_H__
