#pragma warning( disable : 4786 )
/*-----------------------------------------------------------------------------*
*
* File:   mgr_route
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/mgr_route.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:19:44 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <rw/db/db.h>

#include "mgr_route.h"
#include "rte_xcu.h"
#include "rte_ccu.h"
#include "rte_versacom.h"
#include "rte_macro.h"

#include "tbl_rtversacom.h"
#include "tbl_rtmacro.h"

#include "dbaccess.h"
#include "yukon.h"

CtiRouteManager::CtiRouteManager() {}
CtiRouteManager::~CtiRouteManager() {}


void CtiRouteManager::DeleteList(void)   { Map.clearAndDestroy(); }



inline RWBoolean isRouteIdStaticId(CtiRouteBase *pRoute, void* d)
{
   CtiRouteBase *pSp = (CtiRouteBase *)d;

   return(pRoute->getRouteID() == pSp->getRouteID());
}

inline RWBoolean  isRouteNotUpdated(CtiRouteBase *pRoute, void* d)
{
   // Return TRUE if it is NOT SET
   return(RWBoolean(!pRoute->getUpdatedFlag()));
}


void ApplyRouteResetUpdated(const CtiHashKey *key, CtiRouteBase *&pRoute, void* d)
{
   pRoute->resetUpdatedFlag();

   if(pRoute->getType() == MacroRouteType)
   {
      ((CtiRouteMacro*)pRoute)->getRouteList().clear();   // We will refill this one as we go!
   }
   if(pRoute->getType() == CCURouteType)
   {
      ((CtiRouteCCU*)pRoute)->getRepeaterList().clear();  //  ditto
   }

   return;
}

void ApplyRepeaterSort(const CtiHashKey *key, CtiRouteBase *&pRoute, void* d)
{
   if(pRoute->getType() == CCURouteType)  //  used to be RepeaterRouteType
   {
      ((CtiRouteCCU*)pRoute)->getRepeaterList().sort();
   }
   return;
}

void ApplyMacroRouteSort(const CtiHashKey *key, CtiRouteBase *&pRoute, void* d)
{
   if(pRoute->getType() == MacroRouteType)
   {
      ((CtiRouteMacro*)pRoute)->getRouteList().sort();
   }
   return;
}

void ApplyInvalidateNotUpdated(const CtiHashKey *key, CtiRouteBase *&pPt, void* d)
{
   if(!pPt->getUpdatedFlag())
   {
      pPt->setValid(FALSE);   //   NOT NOT NOT Valid
   }
   return;
}

void CtiRouteManager::RefreshList(CtiRouteBase* (*Factory)(RWDBReader &),
                                  BOOL (*testFunc)(CtiRouteBase*,void*),
                                  void *arg)
{
   CtiRouteBase *pTempCtiRoute = NULL;

   try
   {
      {
         LockGuard guard(monitor());

         // Reset everyone's Updated flag.
         Map.apply(ApplyRouteResetUpdated, NULL);

         /*
          *  093099 CGP: Look for items starting at the bottom of the inheritance hierarchy.
          *  The way the selects are, we should get an entry fro EACH route in the MAIN route
          *  table based upon the setting in the type field.
          *
          *  The items not collected in the first call are collected in subsequent calls
          *  at the table level.. i.e. CtiTableVersacomRoute::getSQLColumns(column) ....
          *
          *  This allows the data to be collected in just four calls to the SQL DB.
          */
         {
            // Make sure all objects that that store results
            RWDBConnection conn = getConnection();
             // are out of scope when the release is called
            RWLockGuard<RWDBConnection> conn_guard(conn);
            RWDBDatabase db = conn.database();
            RWDBSelector selector = conn.database().selector();

            RWDBTable   keyTable;

            if(DebugLevel & 0x00040000)  { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for CCU, LCU, TCU, & CCURPT Routes" << endl; }
            CtiRouteCCU::getSQL( db, keyTable, selector );
            selector.where( keyTable["category"] == RWDBExpr("ROUTE") && selector.where());
            RWDBReader  rdr = selector.reader(conn);
            if(DebugLevel & 0x00040000 || selector.status().errorCode() != RWDBStatus::ok) { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl; }
            RefreshRoutes(rdr, Factory, testFunc, arg);
            if(DebugLevel & 0x00040000)  { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for CCU, LCU, TCU, & CCURPT Routes" << endl; }
         }

         {
            // Make sure all objects that that store results
            RWDBConnection conn = getConnection();
             // are out of scope when the release is called
            RWLockGuard<RWDBConnection> conn_guard(conn);
            RWDBDatabase db = conn.database();
            RWDBSelector selector = conn.database().selector();

            RWDBTable   keyTable;

            if(DebugLevel & 0x00040000)  { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Versacom Routes" << endl; }
            CtiTableVersacomRoute::getSQL( db, keyTable, selector );
            RWDBReader  rdr = selector.reader(conn);
            if(DebugLevel & 0x00040000 || selector.status().errorCode() != RWDBStatus::ok) { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl; }
            RefreshVersacomRoutes(rdr, testFunc, arg);
            if(DebugLevel & 0x00040000)  { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Versacom Routes" << endl; }
         }

         {
            // Make sure all objects that that store results
            RWDBConnection conn = getConnection();
             // are out of scope when the release is called
            RWLockGuard<RWDBConnection> conn_guard(conn);
            RWDBDatabase db = conn.database();
            RWDBSelector selector = conn.database().selector();

            RWDBTable   keyTable;

            if(DebugLevel & 0x00040000)  { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Repeater Information" << endl; }
            CtiTableRepeaterRoute::getSQL( db, keyTable, selector );
            RWDBReader  rdr = selector.reader(conn);
            if(DebugLevel & 0x00040000 || selector.status().errorCode() != RWDBStatus::ok) { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl; }
            RefreshRepeaterRoutes(rdr, testFunc, arg);
            if(DebugLevel & 0x00040000)  { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Repeater Information" << endl; }
         }

         {
            // Make sure all objects that that store results
            RWDBConnection conn = getConnection();
             // are out of scope when the release is called
            RWLockGuard<RWDBConnection> conn_guard(conn);
            RWDBDatabase db = conn.database();
            RWDBSelector selector = conn.database().selector();

            RWDBTable   keyTable;
            if(DebugLevel & 0x00040000)  { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Macro Routes" << endl; }
            CtiTableMacroRoute::getSQL( db, keyTable, selector );
            RWDBReader  rdr = selector.reader(conn);
            if(DebugLevel & 0x00040000 || selector.status().errorCode() != RWDBStatus::ok) { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << selector.asString() << endl; }
            RefreshMacroRoutes(rdr, testFunc, arg);
            if(DebugLevel & 0x00040000)  { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done looking for Macro Routes" << endl; }
         }

         // Now I need to check for any Route removals based upon the
         // Updated Flag being NOT set

         Map.apply(ApplyInvalidateNotUpdated, NULL);

      }   // Temporary results are destroyed to free the connection
   }
   catch(RWExternalErr e )
   {
      //Make sure the list is cleared
      { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Attempting to clear point list..." << endl; }
      Map.clearAndDestroy();

      { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "getRoutes:  " << e.why() << endl; }
      RWTHROW(e);
   }
}


void CtiRouteManager::RefreshRoutes(RWDBReader& rdr, CtiRouteBase* (*Factory)(RWDBReader &), BOOL (*testFunc)(CtiRouteBase*,void*), void *arg)
{
   LONG              lTemp = 0;
   CtiRouteBase*    pTempCtiRoute = NULL;

   while( (rdr.status().errorCode() == RWDBStatus::ok) && rdr() )
   {
      rdr["paobjectid"] >> lTemp;            // get the RouteID
      CtiHashKey key(lTemp);


      if( Map.entries() > 0 && ((pTempCtiRoute = Map.findValue(&key)) != NULL) )
      {
         /*
          *  The point just returned from the rdr already was in my list.  We need to
          *  update my list entry to the new settings!
          */

         pTempCtiRoute->DecodeDatabaseReader(rdr);  // Fills himself in from the reader
         pTempCtiRoute->setUpdatedFlag();           // Mark it updated
      }
      else
      {
         CtiRouteBase* pSp = (*Factory)(rdr);  // Use the reader to get me an object of the proper type

         if(pSp)
         {
            pSp->DecodeDatabaseReader(rdr);        // Fills himself in from the reader

            if(((*testFunc)(pSp, arg)))            // If I care about this point in the db in question....
            {
               pSp->setUpdatedFlag();               // Mark it updated
               Map.insert( new CtiHashKey(pSp->getRouteID()), pSp ); // Stuff it in the list
            }
            else
            {
               delete pSp;                         // I don't want it!
            }
         }
      }
   }
}

void CtiRouteManager::RefreshRepeaterRoutes(RWDBReader& rdr, BOOL (*testFunc)(CtiRouteBase*,void*), void *arg)
{
   LONG        lTemp = 0;
   CtiRouteBase*   pTempCtiRoute = NULL;

   while( (rdr.status().errorCode() == RWDBStatus::ok) && rdr() )
   {
      CtiRouteBase* pSp = NULL;

      rdr["routeid"] >> lTemp;            // get the RouteID
      CtiHashKey key(lTemp);

      if( Map.entries() > 0 && ((pTempCtiRoute = Map.findValue(&key)) != NULL) )
      {
         if(pTempCtiRoute->getType() == CCURouteType)  //  used to be RepeaterRouteType
         {
            /*
             *  The route just returned from the rdr already was in my list.  We need to
             *  add this repeater to the route entry... It had better be a repeater route.
             */

            ((CtiRouteCCU*)pTempCtiRoute)->DecodeRepeaterDatabaseReader(rdr);        // Fills himself in from the reader
         }
      }
   }

   // Sort all the repeater listings based on repeater order
   Map.apply(ApplyRepeaterSort, NULL);
}

void CtiRouteManager::RefreshMacroRoutes(RWDBReader& rdr, BOOL (*testFunc)(CtiRouteBase*,void*), void *arg)
{
   LONG        lTemp = 0;
   CtiRouteBase*   pTempCtiRoute = NULL;

   while( (rdr.status().errorCode() == RWDBStatus::ok) && rdr() )
   {
      CtiRouteBase* pSp = NULL;

      rdr["routeid"] >> lTemp;            // get the RouteID
      CtiHashKey key(lTemp);

      if( Map.entries() > 0 && ((pTempCtiRoute = Map.findValue(&key)) != NULL) )
      {
         if(pTempCtiRoute->getType() == MacroRouteType)
         {
            /*
             *  The route just returned from the rdr already was in my list.  We need to
             *  add this route to the macro route entry... It had better be a macro route.
             */

            ((CtiRouteMacro*)pTempCtiRoute)->DecodeMacroReader(rdr);        // Fills himself in from the reader
         }
      }
   }

   Map.apply(ApplyMacroRouteSort, NULL);
}

void CtiRouteManager::RefreshVersacomRoutes(RWDBReader& rdr, BOOL (*testFunc)(CtiRouteBase*,void*), void *arg)
{
   LONG        lTemp = 0;
   CtiRouteBase*   pTempCtiRoute = NULL;

   while( (rdr.status().errorCode() == RWDBStatus::ok) && rdr() )
   {
      CtiRouteBase* pSp = NULL;

      rdr["routeid"] >> lTemp;            // get the RouteID
      CtiHashKey key(lTemp);

      if( Map.entries() > 0 && ((pTempCtiRoute = Map.findValue(&key)) != NULL) )
      {
         if(pTempCtiRoute->getType() == VersacomRouteType)
         {
            /*
             *  The route just returned from the rdr already was in my list.  We need to
             *  add this versacom data to the route entry... It had better be a versacom route.
             */

            ((CtiRouteVersacom*)pTempCtiRoute)->DecodeVersacomDatabaseReader(rdr);        // Fills himself in from the reader
         }
      }
   }
}

CtiRouteBase *CtiRouteManager::getEqual( LONG Rte )
{
   CtiHashKey key(Rte);
   return Map.findValue(&key);

}



CtiRouteBase *CtiRouteManager::RouteGetEqualByName( RWCString &rname )
{
    CtiRTDBIterator itr(Map);
    CtiRouteBase *tmpRoute = NULL;
    RWCString tmpName;

    rname.toLower();

    //  go through the list looking for a matching name
    for( ; itr(); )
    {
        tmpName = (itr.value())->getName();
        tmpName.toLower();

        if( tmpName == rname )
        {
            tmpRoute = itr.value();
            break;  //  look, ma, this makes us O(n/2) instead of O(n) - ha ha.
        }
    }

    return tmpRoute;
}


void CtiRouteManager::DumpList(void)
{
   CtiRoute *p = NULL;
   try
   {
      CtiRTDBIterator itr(Map);

      for(; itr() ;)
      {
         p = itr.value();
         p->DumpData();
      }
   }
   catch(RWExternalErr e )
   {
      //Make sure the list is cleared
      { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Attempting to clear port list..." << endl; }
      Map.clearAndDestroy();
      { CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "DumpRoutes:  " << e.why() << endl; }
      RWTHROW(e);

   }
}


