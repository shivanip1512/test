
#pragma warning( disable : 4786)
#ifndef __DEV_GRP_EXPRESSCOM_H__
#define __DEV_GRP_EXPRESSCOM_H__

/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_expresscom
*
* Class:  CtiDeviceGroupExpresscom
* Date:   9/23/2002
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/09/30 14:21:26 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dev_base.h"
#include "dev_grp.h"
#include "tbl_dv_expresscom.h"

class CtiDeviceGroupExpresscom : public CtiDeviceGroupBase
{
protected:

    CtiTableExpresscomLoadGroup     _expresscomGroup;

private:

public:

    typedef CtiDeviceGroupBase Inherited;

    CtiDeviceGroupExpresscom();
    CtiDeviceGroupExpresscom(const CtiDeviceGroupExpresscom& aRef);
    virtual ~CtiDeviceGroupExpresscom();

    CtiDeviceGroupExpresscom& operator=(const CtiDeviceGroupExpresscom& aRef);


    CtiTableExpresscomLoadGroup   getExpresscomGroup() const;
    CtiTableExpresscomLoadGroup&  getExpresscomGroup();

    CtiDeviceGroupExpresscom&     setExpresscomGroup(const CtiTableExpresscomLoadGroup& aRef);

    virtual LONG getRouteID();
    virtual RWCString getDescription(const CtiCommandParser & parse) const;


    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);

    virtual INT ExecuteRequest(CtiRequestMsg               *pReq,
                               CtiCommandParser               &parse,
                               OUTMESS                        *&OutMessage,
                               RWTPtrSlist< CtiMessage >      &vgList,
                               RWTPtrSlist< CtiMessage >      &retList,
                               RWTPtrSlist< OUTMESS >         &outList);

    virtual RWCString getPutConfigAssignment(UINT level = UINT_MAX);



};
#endif // #ifndef __DEV_GRP_EXPRESSCOM_H__
