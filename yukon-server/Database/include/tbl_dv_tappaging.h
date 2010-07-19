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

#include <limits.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"
#include "row_reader.h"

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

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

   LONG getDeviceID() const;
   CtiTableDeviceTapPaging& setDeviceID(const LONG did);

   static string getTableName();
   virtual bool Update();
   virtual bool Insert();
   virtual bool Delete();
};
#endif // #ifndef __TBL_DV_TAPPAGING_H__
