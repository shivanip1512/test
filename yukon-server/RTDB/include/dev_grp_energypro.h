/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_energypro
*
* Class:  CtiDeviceGroupEnergyPro
* Date:   6/19/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2008/10/28 19:21:43 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __DEV_GRP_ENERGYPRO_H__
#define __DEV_GRP_ENERGYPRO_H__

#include "dev_base.h"
#include "dev_grp.h"
#include "tbl_dv_expresscom.h"

class IM_EX_DEVDB CtiDeviceGroupEnergyPro : public CtiDeviceGroupBase
{
private:

    typedef CtiDeviceGroupBase Inherited;

    // This method makes a gripe if any addressing level or load is predefined when submitting a request to a group
    bool checkForEmptyParseAddressing( CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &retList );

protected:

    CtiTableExpresscomLoadGroup     _expresscomGroup;

public:

    CtiDeviceGroupEnergyPro();
    CtiDeviceGroupEnergyPro(const CtiDeviceGroupEnergyPro& aRef);
    virtual ~CtiDeviceGroupEnergyPro();

    CtiDeviceGroupEnergyPro& operator=(const CtiDeviceGroupEnergyPro& aRef);

    CtiTableExpresscomLoadGroup   getExpresscomGroup() const;
    CtiTableExpresscomLoadGroup&  getExpresscomGroup();
    CtiDeviceGroupEnergyPro&     setExpresscomGroup(const CtiTableExpresscomLoadGroup& aRef);

    virtual INT ProcessResult(INMESS*, CtiTime&, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    virtual LONG getRouteID();
    virtual string getDescription(const CtiCommandParser & parse) const;
    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector) const;
    virtual void DecodeDatabaseReader(RWDBReader &rdr);
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    virtual string getPutConfigAssignment(UINT modifier = 0);

};

#endif // #ifndef __DEV_GRP_ENERGYPRO_H__
