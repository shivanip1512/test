
#ifndef __DEV_GRP_EMETCON_H__
#define __DEV_GRP_EMETCON_H__

/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_emetcon
*
* Class:  CtiDeviceGroupEmetcon
* Date:   10/4/1999
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_grp_emetcon.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:47 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/tpslist.h>

#include "dev_base.h"
#include "dev_grp.h"
#include "tbl_dv_emetcon.h"

class IM_EX_DEVDB CtiDeviceGroupEmetcon : public CtiDeviceGroupBase
{
protected:

   CtiTableEmetconLoadGroup      EmetconGroup;


private:

public:

   typedef CtiDeviceGroupBase Inherited;

   CtiDeviceGroupEmetcon();//   {}

   CtiDeviceGroupEmetcon(const CtiDeviceGroupEmetcon& aRef);/*
   {
      *this = aRef;
   }                                                          */

   virtual ~CtiDeviceGroupEmetcon();// {}

   CtiDeviceGroupEmetcon& operator=(const CtiDeviceGroupEmetcon& aRef);

   CtiTableEmetconLoadGroup   getEmetconGroup() const;//      { return EmetconGroup; }
   CtiTableEmetconLoadGroup&  getEmetconGroup();//            { return EmetconGroup; }

   CtiDeviceGroupEmetcon&     setEmetconGroup(const CtiTableEmetconLoadGroup& aRef);
   virtual LONG getRouteID();
   virtual RWCString getDescription(const CtiCommandParser & parse) const;


   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   virtual INT ExecuteRequest(CtiRequestMsg                  *pReq,
                              CtiCommandParser               &parse,
                              OUTMESS                        *&OutMessage,
                              RWTPtrSlist< CtiMessage >      &vgList,
                              RWTPtrSlist< CtiMessage >      &retList,
                              RWTPtrSlist< OUTMESS >         &outList);

};
#endif // #ifndef __DEV_GRP_EMETCON_H__
