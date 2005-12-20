/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_tappaging
*
* Date:   5/9/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_dv_tappaging.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/12/20 17:16:08 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TBL_DV_TAPPAGING_H__
#define __TBL_DV_TAPPAGING_H__

#include <rw/db/reader.h>
#include <limits.h>
#include <rw/db/nullind.h>
#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/datetime.h>
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
   string      _pagerNumber;                    // a.k.a. CAPCODE

   string    _senderID;
   string    _securityCode;
   string    _postPath;

private:

public:

   CtiTableDeviceTapPaging(string pn = string());

   CtiTableDeviceTapPaging(const CtiTableDeviceTapPaging& aRef);

   virtual ~CtiTableDeviceTapPaging();

   CtiTableDeviceTapPaging& operator=(const CtiTableDeviceTapPaging& aRef);

   string                  getPagerNumber() const;
   string&                 getPagerNumber();
   CtiTableDeviceTapPaging&   setPagerNumber(const string &aStr);

   string                  getSenderID() const;
   string                  getSecurityCode() const;
   string                  getPOSTPath() const;


   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   LONG getDeviceID() const;
   CtiTableDeviceTapPaging& setDeviceID(const LONG did);

   static string getTableName();
   virtual RWDBStatus Restore();
   virtual RWDBStatus Update();
   virtual RWDBStatus Insert();
   virtual RWDBStatus Delete();
};
#endif // #ifndef __TBL_DV_TAPPAGING_H__
