
#pragma warning( disable : 4786)
#ifndef __TBL_DV_CBC_H__
#define __TBL_DV_CBC_H__

/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_cbc
*
* Class:  CtiTableDeviceCBC
* Date:   8/24/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_dv_cbc.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/12/20 17:16:07 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

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

class IM_EX_CTIYUKONDB CtiTableDeviceCBC : public CtiMemDBObject
{

protected:

   LONG     _deviceID;
   INT      _serial;             // Versacom Serial number
   LONG     _routeID;            // the route (macro) which defines this device.

private:

public:

   CtiTableDeviceCBC();

   CtiTableDeviceCBC(const CtiTableDeviceCBC& aRef);

   virtual ~CtiTableDeviceCBC();

   CtiTableDeviceCBC& operator=(const CtiTableDeviceCBC& aRef);

   INT  getSerial() const;

   CtiTableDeviceCBC& setSerial( const INT a_ser );

   LONG  getRouteID() const;

   CtiTableDeviceCBC& setRouteID( const LONG a_routeID );

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

   static string getTableName();

   LONG getDeviceID() const;

   CtiTableDeviceCBC& setDeviceID( const LONG did);

   virtual bool Insert();
   virtual bool Update();
   virtual bool Delete();
};
#endif // #ifndef __TBL_DV_CBC_H__
