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
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2006/02/24 00:19:13 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_GRP_EXPRESSCOM_H__
#define __DEV_GRP_EXPRESSCOM_H__
#pragma warning( disable : 4786)


#include <rw/tpslist.h>

#include "dev_base.h"
#include "dev_grp.h"
#include "tbl_dv_expresscom.h"

class IM_EX_DEVDB CtiDeviceGroupExpresscom : public CtiDeviceGroupBase
{
protected:

    CtiTableExpresscomLoadGroup     _expresscomGroup;

private:

    // This method makes a gripe if any addressing level or load is predefined when submitting a request to a group
    bool checkForEmptyParseAddressing( CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &retList );


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
    virtual string getDescription(const CtiCommandParser & parse) const;
    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, list< OUTMESS* > &outList);
    virtual string getPutConfigAssignment(UINT modifier = 0);

};
#endif // #ifndef __DEV_GRP_EXPRESSCOM_H__
