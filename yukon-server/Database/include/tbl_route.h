
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_route
*
* Date:   8/18/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_route.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/12/20 17:16:08 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __TBL_ROUTE_H__
#define __TBL_ROUTE_H__

#include <limits.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>


#include "dllbase.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "row_reader.h"


class IM_EX_CTIYUKONDB CtiTableDeviceRoute : public CtiMemDBObject
{
protected:

   LONG        RouteID;

public:

   CtiTableDeviceRoute();

   CtiTableDeviceRoute(const CtiTableDeviceRoute& aRef);

   virtual ~CtiTableDeviceRoute();

   CtiTableDeviceRoute& operator=(const CtiTableDeviceRoute& aRef);

   LONG  getRouteID() const;

   LONG  getID() const;

   CtiTableDeviceRoute& setRouteID( const LONG aRouteID );

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

   static std::string getTableName();
};
#endif // #ifndef __TBL_ROUTE_H__
