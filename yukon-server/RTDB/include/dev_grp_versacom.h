
#pragma warning( disable : 4786)
#ifndef __DEV_GRP_VERSACOM_H__
#define __DEV_GRP_VERSACOM_H__

/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_versacom
*
* Class:  CtiDeviceGroupVersacom
* Date:   12/14/1999
*
* Author: Corey Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/INCLUDE/tbl_alm_nloc.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:47 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include <rw/tpslist.h>

#include "dev_base.h"
#include "dev_grp.h"
#include "tbl_dv_versacom.h"

class CtiDeviceGroupVersacom : public CtiDeviceGroupBase
{
protected:

   CtiTableVersacomLoadGroup     VersacomGroup;

private:

public:

   typedef CtiDeviceGroupBase Inherited;

   CtiDeviceGroupVersacom();

   CtiDeviceGroupVersacom(const CtiDeviceGroupVersacom& aRef);

   virtual ~CtiDeviceGroupVersacom();

   CtiDeviceGroupVersacom& operator=(const CtiDeviceGroupVersacom& aRef);

   CtiTableVersacomLoadGroup   getVersacomGroup() const;
   CtiTableVersacomLoadGroup&  getVersacomGroup();

   CtiDeviceGroupVersacom&     setVersacomGroup(const CtiTableVersacomLoadGroup& aRef);

   virtual LONG getRouteID();
   virtual RWCString getDescription(const CtiCommandParser & parse) const;


   static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   static void getSQL(RWCString &Columns, RWCString &Tables, RWCString &Conditions);

   virtual void DecodeDatabaseReader(RWDBReader &rdr);

   virtual INT ExecuteRequest(CtiRequestMsg               *pReq,
                              CtiCommandParser               &parse,
                              OUTMESS                        *&OutMessage,
                              RWTPtrSlist< CtiMessage >      &vgList,
                              RWTPtrSlist< CtiMessage >      &retList,
                              RWTPtrSlist< OUTMESS >         &outList);

   virtual RWCString getPutConfigAssignment(UINT level = UINT_MAX);

};
#endif // #ifndef __DEV_GRP_VERSACOM_H__
