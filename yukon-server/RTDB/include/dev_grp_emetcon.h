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
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2006/09/21 21:31:38 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_GRP_EMETCON_H__
#define __DEV_GRP_EMETCON_H__


#include <rw/tpslist.h>

#include "dev_base.h"
#include "dev_grp.h"
#include "tbl_dv_emetcon.h"

class IM_EX_DEVDB CtiDeviceGroupEmetcon : public CtiDeviceGroupBase
{
private:

    typedef CtiDeviceGroupBase Inherited;

protected:

   CtiTableEmetconLoadGroup      EmetconGroup;

public:

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
   virtual string getDescription(const CtiCommandParser & parse) const;


   virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   virtual INT ExecuteRequest(CtiRequestMsg                  *pReq,
                              CtiCommandParser               &parse,
                              OUTMESS                        *&OutMessage,
                              list< CtiMessage* >      &vgList,
                              list< CtiMessage* >      &retList,
                              list< OUTMESS* >         &outList);

   static string CtiDeviceGroupEmetcon::generateCommandString(OUTMESS *&OutMessage);

};
#endif // #ifndef __DEV_GRP_EMETCON_H__
