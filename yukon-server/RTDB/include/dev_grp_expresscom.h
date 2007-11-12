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
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2007/11/12 16:56:45 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_GRP_EXPRESSCOM_H__
#define __DEV_GRP_EXPRESSCOM_H__
#pragma warning( disable : 4786)


#include "dev_base.h"
#include "dev_grp.h"
#include "tbl_dv_expresscom.h"

class IM_EX_DEVDB CtiDeviceGroupExpresscom : public CtiDeviceGroupBase
{
private:

    typedef CtiDeviceGroupBase Inherited;

    // This method makes a gripe if any addressing level or load is predefined when submitting a request to a group
    bool checkForEmptyParseAddressing( CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &retList );

protected:

    CtiTableExpresscomLoadGroup     _expresscomGroup;

public:

    CtiDeviceGroupExpresscom();
    CtiDeviceGroupExpresscom(const CtiDeviceGroupExpresscom& aRef);
    virtual ~CtiDeviceGroupExpresscom();

    CtiDeviceGroupExpresscom& operator=(const CtiDeviceGroupExpresscom& aRef);


    CtiTableExpresscomLoadGroup   getExpresscomGroup() const;
    CtiTableExpresscomLoadGroup&  getExpresscomGroup();
    CtiDeviceGroupExpresscom&     setExpresscomGroup(const CtiTableExpresscomLoadGroup& aRef);

    virtual LONG getRouteID();
    virtual string getDescription(const CtiCommandParser & parse) const;
    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    virtual string getPutConfigAssignment(UINT modifier = 0);

};
#endif // #ifndef __DEV_GRP_EXPRESSCOM_H__
