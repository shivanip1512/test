
#ifndef __RTE_MACRO_H__
#define __RTE_MACRO_H__

/*-----------------------------------------------------------------------------*
*
* File:   rte_macro
*
* Class:  CtiRouteMacro
* Date:   9/30/1999
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/rte_macro.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 16:00:32 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/tpordvec.h>
#include <rw/tvordvec.h>
#include <rw/tpslist.h>

#include "tbl_rtmacro.h"
#include "rte_base.h"
#include "cmdparse.h"
#include "yukon.h"

class IM_EX_DEVDB CtiRouteMacro : public CtiRouteBase
{
protected:

   // All I really do is hint at REAL Routes.... Which had better exist somewhere else.. I think.
   RWTValOrderedVector< CtiTableMacroRoute >  RouteList;
   RWTPtrOrderedVector< CtiRoute           >  RoutePtrList;    // Not responsible for these route pointer's memory...

private:

public:

   typedef CtiRouteBase Inherited;

   typedef RWTValOrderedVector< CtiTableMacroRoute > CtiRouteList_t;
   typedef RWTPtrOrderedVector< CtiRoute > CtiRoutePtrList_t;

   CtiRouteMacro();
   CtiRouteMacro(const CtiRouteMacro& aRef);
   ~CtiRouteMacro();

   CtiRouteMacro& operator=(const CtiRouteMacro& aRef);
   virtual void DumpData();

   CtiRouteList_t& CtiRouteMacro::getRouteList();
   CtiRouteList_t   CtiRouteMacro::getRouteList() const;

   CtiRoutePtrList_t & getRoutePtrList();
   CtiRoutePtrList_t   getRoutePtrList() const;


   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   static void getSQL(RWCString &Columns, RWCString &Tables, RWCString &Conditions);
   virtual void DecodeDatabaseReader(RWDBReader &rdr);
   virtual void DecodeMacroReader(RWDBReader &rdr);

   virtual INT ExecuteRequest(CtiRequestMsg               *pReq,
                              CtiCommandParser               &parse,
                              OUTMESS                        *&OutMessage,
                              RWTPtrSlist< CtiMessage >      &vgList,
                              RWTPtrSlist< CtiMessage >      &retList,
                              RWTPtrSlist< OUTMESS >         &outList);

   virtual bool processAdditionalRoutes( INMESS *InMessage ) const;

};
#endif // #ifndef __RTE_MACRO_H__
