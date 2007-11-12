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
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2007/11/12 16:56:45 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_GRP_VERSACOM_H__
#define __DEV_GRP_VERSACOM_H__
#pragma warning( disable : 4786)


#include "dev_base.h"
#include "dev_grp.h"
#include "tbl_dv_versacom.h"

class IM_EX_DEVDB CtiDeviceGroupVersacom : public CtiDeviceGroupBase
{
private:

    typedef CtiDeviceGroupBase Inherited;

protected:

   CtiTableVersacomLoadGroup     VersacomGroup;

public:

   CtiDeviceGroupVersacom();

   CtiDeviceGroupVersacom(const CtiDeviceGroupVersacom& aRef);

   virtual ~CtiDeviceGroupVersacom();

   CtiDeviceGroupVersacom& operator=(const CtiDeviceGroupVersacom& aRef);

   CtiTableVersacomLoadGroup   getVersacomGroup() const;
   CtiTableVersacomLoadGroup&  getVersacomGroup();

   CtiDeviceGroupVersacom&     setVersacomGroup(const CtiTableVersacomLoadGroup& aRef);

   virtual LONG getRouteID();
   virtual string getDescription(const CtiCommandParser & parse) const;


   virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   virtual void DecodeDatabaseReader(RWDBReader &rdr);
   virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
   virtual string getPutConfigAssignment(UINT modifier = 0);

};
#endif // #ifndef __DEV_GRP_VERSACOM_H__
