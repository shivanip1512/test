
#ifndef __RTE_REPEATER_H__
#define __RTE_REPEATER_H__

/*-----------------------------------------------------------------------------*
*
* File:   rte_repeater
*
* Class:  CtiRouteRepeater
* Date:   9/30/1999
*
* Author: Corey Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/rte_repeater.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:19:58 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <rw/tvordvec.h>
#include "tbl_rtrepeater.h"
#include "rte_ccu.h"

class IM_EX_DEVDB CtiRouteRepeater : public CtiRouteCCU
{
protected:

   // This is a vector of repeaters 0 to 7 in length... currently we rely on DBEditor to assure this...
   RWTValOrderedVector< CtiTableRepeaterRoute >  RepeaterList;


private:

public:

   typedef CtiRouteCCU Inherited;
   typedef RWTValOrderedVector< CtiTableRepeaterRoute > CtiRepeaterList_t;

   CtiRouteRepeater() {}

   CtiRouteRepeater(const CtiRouteRepeater& aRef)
   {
      *this = aRef;
   }

   ~CtiRouteRepeater() {}

   CtiRouteRepeater& operator=(const CtiRouteRepeater& aRef)
   {
      if(this != &aRef)
      {
         Inherited::operator=(aRef);
         RepeaterList = aRef.getRepeaterList();
      }
      return *this;
   }

   virtual void DumpData()
   {
      Inherited::DumpData();

      for (int i = 0; i < RepeaterList.length(); i++)
         RepeaterList[i].DumpData();
   }


   CtiRepeaterList_t&    getRepeaterList()         { return RepeaterList; }
   CtiRepeaterList_t     getRepeaterList() const   { return RepeaterList; }

   virtual INT  getStages() const                  { return RepeaterList.entries(); }


   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
   {
      Inherited::getSQL(db, keyTable, selector);
      // Collect CtiTableRepeaterRoute in a later directed call specific to this table...
   }

   static void getSQL(RWCString &Columns, RWCString &Tables, RWCString &Conditions)
   {
      if(getDebugLevel() & 0x0800) cout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;

      Inherited::getSQL(Columns, Tables, Conditions);

      // Collect CtiTableRepeaterRoute in a later directed call specific to this table...
   }

   virtual void DecodeDatabaseReader(RWDBReader &rdr)
   {
      Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
   }

   virtual void DecodeRepeaterDatabaseReader(RWDBReader &rdr)
   {
      CtiTableRepeaterRoute   Rpt;

      if(Type == RepeaterRouteType)   // Just make darn sure.
      {
         if(getDebugLevel() & 0x0800) cout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
         Rpt.DecodeDatabaseReader(rdr);
         RepeaterList.insert(Rpt);
      }
   }


};
#endif // #ifndef __RTE_REPEATER_H__
