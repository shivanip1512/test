
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
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/12/20 17:20:29 $
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
protected:

    CtiTableExpresscomLoadGroup     _expresscomGroup;

private:

    // This method makes a gripe if any addressing level or load is predefined when submitting a request to a group
    bool checkForEmptyParseAddressing( CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &retList );


public:

    typedef CtiDeviceGroupBase Inherited;

    CtiDeviceGroupEnergyPro();
    CtiDeviceGroupEnergyPro(const CtiDeviceGroupEnergyPro& aRef);
    virtual ~CtiDeviceGroupEnergyPro();

    CtiDeviceGroupEnergyPro& operator=(const CtiDeviceGroupEnergyPro& aRef);

    CtiTableExpresscomLoadGroup   getExpresscomGroup() const;
    CtiTableExpresscomLoadGroup&  getExpresscomGroup();
    CtiDeviceGroupEnergyPro&     setExpresscomGroup(const CtiTableExpresscomLoadGroup& aRef);

    virtual INT ProcessResult(INMESS*, CtiTime&, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
    virtual LONG getRouteID();
    virtual string getDescription(const CtiCommandParser & parse) const;
    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
    virtual string getPutConfigAssignment(UINT modifier = 0);

};

#endif // #ifndef __DEV_GRP_ENERGYPRO_H__
