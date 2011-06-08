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
* REVISION     :  $Revision: 1.13 $
* DATE         :  $Date: 2008/10/28 19:21:44 $
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
   virtual std::string getDescription(const CtiCommandParser & parse) const;

   virtual std::string getSQLCoreStatement() const;

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
   virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
   virtual std::string getPutConfigAssignment(UINT modifier = 0);

};
#endif // #ifndef __DEV_GRP_VERSACOM_H__
