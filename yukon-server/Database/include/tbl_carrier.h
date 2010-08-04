
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_carrier
*
* Date:   8/18/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_carrier.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/12/20 17:16:07 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __TBL_CARRIER_H__
#define __TBL_CARRIER_H__

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


class IM_EX_CTIYUKONDB CtiTableDeviceCarrier : public CtiMemDBObject
{
protected:

   LONG     _deviceID;
   INT      _address;

public:

   CtiTableDeviceCarrier();

   CtiTableDeviceCarrier(const CtiTableDeviceCarrier& aRef);

   virtual ~CtiTableDeviceCarrier();

   CtiTableDeviceCarrier& operator=(const CtiTableDeviceCarrier& aRef);

   INT  getAddress() const;
   CtiTableDeviceCarrier& setAddress( const INT aAddress );

   LONG getDeviceID() const;
   CtiTableDeviceCarrier& setDeviceID( const LONG did );

   static string getTableName();

   bool isInitialized();

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
   virtual bool Insert();
   virtual bool Update();
   virtual bool Delete();
};

#endif // #ifndef __TBL_CARRIER_H__
