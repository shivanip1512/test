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
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2003/03/13 19:36:17 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __RTE_MACRO_H__
#define __RTE_MACRO_H__


#include <rw/tpordvec.h>
#include <rw/tvordvec.h>
#include <rw/tpslist.h>

#include "boost/shared_ptr.hpp"
using namespace std;
using boost::shared_ptr;



#include "tbl_rtmacro.h"
#include "rte_base.h"
#include "cmdparse.h"
#include "yukon.h"

class IM_EX_DEVDB CtiRouteMacro : public CtiRouteBase
{
protected:

   // All I really do is hint at REAL Routes.... Which had better exist somewhere else.. I think.
   RWTValOrderedVector< CtiTableMacroRoute >  RouteList;
   RWTValOrderedVector< shared_ptr< CtiRoute > >  RoutePtrList;    // Not responsible for these route pointer's memory...

private:

public:

   typedef CtiRouteBase Inherited;

   typedef RWTValOrderedVector< CtiTableMacroRoute > CtiRouteList_t;
   typedef RWTValOrderedVector< shared_ptr< CtiRoute > > CtiRoutePtrList_t;

   CtiRouteMacro();
   CtiRouteMacro(const CtiRouteMacro& aRef);
   ~CtiRouteMacro();

   CtiRouteMacro& operator=(const CtiRouteMacro& aRef);
   virtual void DumpData();

   CtiRouteList_t& CtiRouteMacro::getRouteList();
   CtiRouteList_t   CtiRouteMacro::getRouteList() const;

   CtiRoutePtrList_t & getRoutePtrList();
   CtiRoutePtrList_t   getRoutePtrList() const;


   virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
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
