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
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2008/10/28 19:21:44 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_SYSTEM_H__
#define __DEV_SYSTEM_H__
#pragma warning( disable : 4786)


#include "dev_base.h"


class CtiDeviceSystem : public CtiDeviceBase
{
private:

    typedef CtiDeviceBase Inherited;

protected:

public:

   CtiDeviceSystem();

   CtiDeviceSystem(const CtiDeviceSystem& aRef);

   virtual ~CtiDeviceSystem();

   CtiDeviceSystem& operator=(const CtiDeviceSystem& aRef);

   virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector) const;
   virtual void DecodeDatabaseReader(RWDBReader &rdr);
   virtual INT ExecuteRequest(CtiRequestMsg               *pReq,
                              CtiCommandParser               &parse,
                              OUTMESS                        *&OutMessage,
                              list< CtiMessage* >      &vgList,
                              list< CtiMessage* >      &retList,
                              list< OUTMESS* >         &outList);

   virtual string getDescription(const CtiCommandParser &parse) const;

};
#endif // #ifndef __DEV_SYSTEM_H__
