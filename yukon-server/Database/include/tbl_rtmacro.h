
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   tbl_rtmacro
*
* Date:   9/30/1999
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_rtmacro.h-arc  $
* REVISION     :  $Revision: 1.4.24.1 $
* DATE         :  $Date: 2008/11/13 17:23:49 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __TBL_RTMACRO_H__
#define __TBL_RTMACRO_H__

#include <limits.h>

#include "row_reader.h"
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>
#include <limits.h>



#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"
#include "dbaccess.h"
#include "dllbase.h"
#include "desolvers.h"
#include "types.h"
#include "logger.h"
#include "yukon.h"
#include "ctibase.h"


class IM_EX_CTIYUKONDB CtiTableMacroRoute : public CtiMemDBObject
{

protected:

   LONG        RouteID;
   INT         _singleRouteID;
   INT         RouteOrder;

private:

public:

   CtiTableMacroRoute(LONG routeId = -1L, INT routeOrder = INT_MAX);

   CtiTableMacroRoute(const CtiTableMacroRoute& aRef);

   virtual ~CtiTableMacroRoute();

   CtiTableMacroRoute& operator=(const CtiTableMacroRoute& aRef);

   void DumpData();

   LONG getRouteID() const;
   CtiTableMacroRoute& setRouteID( const LONG aRouteID );

   INT getRouteOrder() const;
   CtiTableMacroRoute& setRouteOrder( const INT aRouteOrder );

   INT getSingleRouteID() const;
   CtiTableMacroRoute& setSingleRouteID( const INT srid );

   RWBoolean operator<(const CtiTableMacroRoute& t2);

   RWBoolean operator==(const CtiTableMacroRoute& t2);

   static std::string getTableName();

   static std::string getSQLCoreStatement();

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
};
#endif // #ifndef __TBL_RTMACRO_H__
