
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
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2002/04/22 19:47:17 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

class CtiDeviceSystem : public CtiDeviceBase
{
protected:

private:

public:

   typedef CtiDeviceBase Inherited;

   CtiDeviceSystem();

   CtiDeviceSystem(const CtiDeviceSystem& aRef);

   virtual ~CtiDeviceSystem();

   CtiDeviceSystem& operator=(const CtiDeviceSystem& aRef);

   virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   virtual void DecodeDatabaseReader(RWDBReader &rdr);
   virtual INT ExecuteRequest(CtiRequestMsg               *pReq,
                              CtiCommandParser               &parse,
                              OUTMESS                        *&OutMessage,
                              RWTPtrSlist< CtiMessage >      &vgList,
                              RWTPtrSlist< CtiMessage >      &retList,
                              RWTPtrSlist< OUTMESS >         &outList);

   virtual RWCString getDescription(const CtiCommandParser &parse) const;

};
#endif // #ifndef __DEV_SYSTEM_H__
