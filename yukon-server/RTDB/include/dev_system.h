
#pragma warning( disable : 4786)
#ifndef __DEV_SYSTEM_H__
#define __DEV_SYSTEM_H__

#include "dev_base.h"

/*-----------------------------------------------------------------------------*
*
* File:   dev_system
*
* Class:  CtiDeviceSystem
* Date:   3/21/2000
*
* Author: Corey Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_system.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:19:53 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

class CtiDeviceSystem : public CtiDeviceBase
{
protected:

private:

public:

   typedef CtiDeviceBase Inherited;

   CtiDeviceSystem();// {}

   CtiDeviceSystem(const CtiDeviceSystem& aRef);/*
   {
      *this = aRef;
   }                                              */

   virtual ~CtiDeviceSystem();// {}

   CtiDeviceSystem& operator=(const CtiDeviceSystem& aRef);/*
   {
      if(this != &aRef)
      {
         Inherited::operator=(aRef);
      }
      return *this;
   }                                                         */

   static void getSQL(RWCString &Columns, RWCString &Tables, RWCString &Conditions);/*
   {
      Inherited::getSQL(Columns, Tables, Conditions);
   }                                                                                  */

   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);/*
   {
      Inherited::getSQL(db, keyTable, selector);
   }                                                                                    */

   virtual void DecodeDatabaseReader(RWDBReader &rdr);/*
   {
      Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
   }                                                   */

   virtual INT ExecuteRequest(CtiRequestMsg               *pReq,
                              CtiCommandParser               &parse,
                              OUTMESS                        *&OutMessage,
                              RWTPtrSlist< CtiMessage >      &vgList,
                              RWTPtrSlist< CtiMessage >      &retList,
                              RWTPtrSlist< OUTMESS >         &outList);

   virtual RWCString getDescription(const CtiCommandParser &parse) const;

};
#endif // #ifndef __DEV_SYSTEM_H__
